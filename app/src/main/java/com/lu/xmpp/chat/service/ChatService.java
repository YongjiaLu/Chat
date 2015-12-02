package com.lu.xmpp.chat.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;

import com.lu.xmpp.chat.ChatConnectCallBack;
import com.lu.xmpp.chat.ChatControl;
import com.lu.xmpp.chat.async.SearchFriendsAsync;
import com.lu.xmpp.connect.ChatConnection;
import com.lu.xmpp.contacts.ChatContacts;
import com.lu.xmpp.modle.Friend;
import com.lu.xmpp.utils.Log;
import com.lu.xmpp.utils.NetUtil;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.io.IOException;

/**
 * 由Chat类调用，其他地方一律通过Chat类来通信获取数据
 * <br/>
 */
public class ChatService extends Service {

    private static final boolean SmackDebug = false;

    //Action
    //<===========================================================>
    public final String ActionConnect = "chat.service.connect";
    public final String ActionLogin = "chat.service.login";
    public final String ActionRegister = "chat.service.register";
    public final String ActionStartGetFriends = "chat.service.start_get_friends";
    public final String ActionOnReceiverFriends = "chat.service.on_receiver_friends";
    //<===========================================================>

    //Param
    //<===========================================================>
    public final String ParamFriendList = "friends";
    public final String ParamUserName = "username";
    public final String ParamPassword = "password";
    //<===========================================================>

    //BroadCastReceiver Action
    //<===========================================================>
    public final String BroadCast_Action_On_Receiver_Friends = "chat.broadcast.receiver_friends";
    //<===========================================================>
    private final static String Tag = "ChatService";

    private static ChatService mInstance;
    //XmppConnection Instance
    private ChatConnection connection;
    //Connect Manager
    private ChatConnectManager mChatConnectManager = ChatConnectManager.getInstance();
    //Register Manager
    private ChatRegisterManager mChatRegisterManager = ChatRegisterManager.getInstance();
    //Friends Observer
    private FriendManager mFriendManager;

    public static ChatService getInstance() {
        return mInstance;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mInstance = this;
        connection = new ChatConnection(SmackDebug, ChatService.this);
        //TODO Setting page get SubscriptionMode ( not implemented)
        Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            Log.e(Tag, intent.getAction());
            switch (intent.getAction()) {
                case ActionConnect: {
                    //创建连接
                    connect();
                }
                break;
                case ActionLogin: {
                    //登陆
                    String username = intent.getStringExtra(ParamUserName);
                    String password = intent.getStringExtra(ParamPassword);
                    login(username, password);
                }
                break;
                case ActionRegister: {
                    //注册
                    String username = intent.getStringExtra(ParamUserName);
                    String password = intent.getStringExtra(ParamPassword);
                    register(username, password);
                }
                break;
                case ActionStartGetFriends: {
                    //开始一个获取Roster的任务
                    mFriendManager = FriendManager.getInstance(connection, this);
                    mFriendManager.init();
                }
                break;

                case ActionOnReceiverFriends: {
                    //收到FriendsObserver处理好的好友列表，通过广播转发
                    Intent broadcast = new Intent();
                    broadcast.setAction(BroadCast_Action_On_Receiver_Friends);
                    broadcast.putExtra(ParamFriendList, intent.getParcelableArrayListExtra(ParamFriendList));
                    sendBroadcast(broadcast);
                    if (mFriendManager == null) {
                        mFriendManager = FriendManager.getInstance(connection, this);
                    }
                    try {
                        mFriendManager.startRosterPresenceAndMessageListener();
                    } catch (Exception e) {
                        Log.e(Tag, e.toString());
                    }
                }
                break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //release
        if (null != connection) {
            if (connection.isConnected()) {
                connection.disconnect();
            }
            connection = null;
        }

        if (null != mFriendManager) {
            try {
                mFriendManager.stopRosterPresenceListener();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mFriendManager.finish();
        }
    }

    /**
     * 连接由Chat调用
     */
    private void connect() {
        Log.e(Tag, "connect()");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!connection.isConnected()) {
                        //连接
                        Log.e(Tag, "start connect");
                        connection.connect();
                    } else {

                    }
                    Log.e(Tag, "connect complete");
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void login(final String username, final String password) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                //登陆
                try {
                    Log.e(Tag, "Start login username=" + username + "@" + ChatContacts.getHostName() + "    password=" + password);
                    connection.login(username, password);

                    if (connection.isAuthenticated()) {
                        //when login successful ,FriendManager start
                        mFriendManager = FriendManager.getInstance(connection, mInstance);
                        mFriendManager.init();
                    } else {
                        handleLoginError(new Exception("username or password error"));
                    }
                } catch (XMPPException e) {
                    handleLoginError(e);
                } catch (SmackException e) {
                    handleLoginError(e);
                } catch (IOException e) {
                    handleLoginError(e);
                }
            }
        }).start();
    }

    public boolean isLogin() {
        return connection.isAuthenticated();
    }

    private void handlerRegisterError(Exception e) {
        e.printStackTrace();
        mChatRegisterManager.executeCallbacks(false, e);
    }


    /**
     * 是否与服务器成功建立连接
     *
     * @return is connected
     */
    public boolean isConnected() {
        return connection.isConnected();
    }

    //<=========================================================================================>
    //<====================================  register  =========================================>
    //<=========================================================================================>


    public void register(final String username, final String password) {
        new Thread(new Runnable() {
            /**
             * Starts executing the active part of the class' code. This method is
             * called when a thread is started that has been created with a class which
             * implements {@code Runnable}.
             */
            @Override
            public void run() {
                try {
                    if (connection.isConnected()) {
                        AccountManager manager = AccountManager.getInstance(connection);

                        manager.createAccount(username, password);
                        //manager.createAccount(username, password);
                        mChatRegisterManager.executeCallbacks(true, null);
                    }
                } catch (SmackException.NoResponseException e) {
                    handlerRegisterError(e);
                } catch (XMPPException.XMPPErrorException e) {
                    handlerRegisterError(e);
                } catch (SmackException.NotConnectedException e) {
                    handlerRegisterError(e);
                }
            }
        }).start();

    }


    //<=========================================================================================>
    //<================================  connect callback  =====================================>
    //<=========================================================================================>

    public void addConnectCallBack(ChatConnectCallBack callBack) {
        mChatConnectManager.addChatConnectCallBack(callBack);
        connection.addConnectionListener(callBack);

    }

    public void removeConnectCallBack(ChatConnectCallBack callBack) {
        mChatConnectManager.removeChatConnectCallBack(callBack);
        connection.removeConnectionListener(callBack);
    }

    BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetUtil.isNetWorkAvaliable(context)) {
                mChatConnectManager.executeNetworkAvaliable(true);
            } else {
                mChatConnectManager.executeNetworkAvaliable(false);
            }
        }
    };

    //<=========================================================================================>
    //<================================  register callback  ====================================>
    //<=========================================================================================>
    public void registerRegisterCallBack(ChatRegisterManager.ChatRegisterCallBack callBack) {
        mChatRegisterManager.addChatRegiserCallBack(callBack);
    }

    public void unRegisterCallBack(ChatRegisterManager.ChatRegisterCallBack callBack) {
        mChatRegisterManager.removeChatConnectCallBack(callBack);
    }

    public void handleLoginError(Exception e) {

        try {
            //目前暂无密码账号错误的解决方法 重新创建一个连接
            ChatConnection connection = new ChatConnection(SmackDebug, this);
            this.connection.disconnect();
            connection.connect();
            this.connection = connection;
        } catch (SmackException e1) {

        } catch (IOException e1) {

        } catch (XMPPException e1) {

        }
        e.printStackTrace();
    }

    public void addFriendStatusListener(ChatControl.FriendStatusListener listener) {
        FriendManager.getInstance(connection, this).addPresenceListener(listener);
    }

    public void removeFriendStatusListener(ChatControl.FriendStatusListener listener) {
        FriendManager.getInstance(connection, this).removePresenceListener(listener);
    }


    public ChatConnection getConnection() {
        return connection;
    }

    public void searchFriend(SearchFriendsAsync.SearchFriendCallBack callBack, String byName) {
        mFriendManager.searchFriend(callBack, byName);
    }

    /**
     * Confirm a friendship request,add to user list,and request the friendship
     *
     * @param presence
     */
    public void replyNewFriendNotice(Presence presence) {
        try {
            mFriendManager.replyNewFriendNotice(presence);
        } catch (SmackException.NotConnectedException e) {
            Log.e(Tag, "Service is ShutDown or Connection Error!");
        }
    }

    public void startAddFriend(String Jid, String Message) {
        mFriendManager.addFriend(Jid, Message);
    }

    public Friend findFriendInfoFromJid(String jid) {
        return mFriendManager.findFriendFromList(jid);
    }

    public Friend findCurrentUserInfo() {
        return mFriendManager.getUserInfo();
    }

}