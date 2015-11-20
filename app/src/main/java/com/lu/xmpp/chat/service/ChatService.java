package com.lu.xmpp.chat.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;

import com.lu.xmpp.chat.ChatConnectAvailableCallBack;
import com.lu.xmpp.chat.ChatNetWorkAvailableCallBack;
import com.lu.xmpp.chat.ChatRegisterCallBack;
import com.lu.xmpp.connect.ChatConnection;
import com.lu.xmpp.contacts.ChatContacts;
import com.lu.xmpp.utils.Log;
import com.lu.xmpp.utils.NetUtil;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 由Chat类调用，其他地方一律通过Chat类来通信获取数据
 * <br/>
 * 后期需要管理消息堆栈，先预留
 * <br/>
 * 启动优先级最高
 */
public class ChatService extends Service {

    private static final boolean SmackDebug = false;

    //Action
    //<===========================================================>
    public final String Action_Connect = "chat.service.connect";
    public final String Action_Login = "chat.service.login";
    public final String Action_Register = "chat.service.register";
    public final String Action_Start_Get_Friends = "chat.service.start_get_friends";
    public final String Action_On_Receiver_Friends = "chat.service.on_receiver_Friends";
    //<===========================================================>

    //Param
    //<===========================================================>
    public final String Param_FriendList = "friends";
    public final String Param_UserName = "username";
    public final String Param_PassWord = "password";
    //<===========================================================>

    //BroadCastReceiver Action
    //<===========================================================>
    public final String BroadCast_Action_On_Receiver_Friends = "chat.broadcast.receiver_friends";
    //<===========================================================>
    private final static String Tag = "ChatService";

    private static ChatService mInstance;
    //连接实例，一般一个客户端只有一个
    private ChatConnection connection;
    //Error Stack
    private List<Exception> errorStack = new ArrayList<>();
    //CallBack
    private CallbackManager mCallbackManager = new CallbackManager();
    //Friends Observer
    private FriendsObserver mFriendsObserver;

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
                case Action_Connect: {
                    //创建连接
                    connect();
                }
                break;
                case Action_Login: {
                    //登陆
                    String username = intent.getStringExtra(Param_UserName);
                    String password = intent.getStringExtra(Param_PassWord);
                    login(username, password);
                }
                break;
                case Action_Register: {
                    //注册
                    String username = intent.getStringExtra(Param_UserName);
                    String password = intent.getStringExtra(Param_PassWord);
                    register(username, password);
                }
                break;
                case Action_Start_Get_Friends: {
                    //开始一个获取Roster的任务
                    mFriendsObserver = new FriendsObserver(connection, this);
                    mFriendsObserver.init();
                }
                break;

                case Action_On_Receiver_Friends: {
                    //收到FriendsObserver处理好的好友列表，通过广播转发
                    Intent broadcast = new Intent();
                    broadcast.setAction(BroadCast_Action_On_Receiver_Friends);
                    broadcast.putExtra(Param_FriendList, intent.getParcelableArrayListExtra(Param_FriendList));
                    sendBroadcast(broadcast);
                    if (mFriendsObserver == null) {
                        mFriendsObserver = new FriendsObserver(connection, this);
                    }
                    try {
                        mFriendsObserver.startRosterPresenceListener();
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

        if (null != mFriendsObserver) {
            mFriendsObserver.stopRosterPresenceListener();
            mFriendsObserver.finish();
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
                        if (connection.isConnected() && connection.isSecureConnection()) {
                            Log.e(Tag, "connect success");
                            mCallbackManager.executeConnectCallback(true, null);
                        }
                    } else {
                        Log.e(Tag, "connect fault");
                        handleConnectError(new Exception("No Connect"));
                    }
                    Log.e(Tag, "connect complete");
                } catch (XMPPException e) {
                    handleConnectError(e);
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
//                        mCallbackManager.executeLoginCallBack(true, null);
                        //when login successful ,FriendsObserver start
                        mFriendsObserver = new FriendsObserver(connection, mInstance);
                        mFriendsObserver.init();
                    } else {
                        handleConnectError(new Exception("username or password error"));
                    }
                } catch (XMPPException e) {
//                    handleLoginError(e);
                } catch (SmackException e) {
//                    handleLoginError(e);
                } catch (IOException e) {
//                    handleLoginError(e);
                }
            }
        }).start();
    }

    public boolean isLogin() {
        return connection.isAuthenticated();
    }


    private void handleConnectError(Exception e) {
        e.printStackTrace();
        errorStack.add(e);
        mCallbackManager.executeConnectCallback(false, e);
    }

    private void handlerRegisterError(Exception e) {
        e.printStackTrace();
        errorStack.add(e);
        mCallbackManager.executeRegisterCallBack(false, e.toString());
    }


    /**
     * 是否与服务器成功建立连接
     *
     * @return
     */
    public boolean isConnected() {
        return connection.isConnected();
    }

    /**
     * 显示异常堆栈 Debug模式下有效
     */
    public void getErrorStack() {
        for (Exception e : errorStack) {
            e.printStackTrace();
        }
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
                        mCallbackManager.executeRegisterCallBack(true, null);
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

    public void registerConnectCallback(ChatConnectAvailableCallBack callback) {
        mCallbackManager.addConnectAvailableCallBack(callback);
    }

    public void unregisterConnectCallback(ChatConnectAvailableCallBack callback) {
        mCallbackManager.removeConnectAvailableCallBack(callback);
    }


    //<=========================================================================================>
    //<================================= net work callback =====================================>
    //<=========================================================================================>

    /**
     * 网络状态发生改变
     */
    BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetUtil.isNetWorkAvaliable(context)) {
                mCallbackManager.executeNetworkCallback(true);
            } else {
                mCallbackManager.executeNetworkCallback(false);
            }
        }
    };

    public void registerNetworkAvailableCallBack(ChatNetWorkAvailableCallBack callBack) {
        mCallbackManager.addNetworkAvailableCallBack(callBack);
    }

    public void unRegisterNetworkAvailableCallBack(ChatNetWorkAvailableCallBack callBack) {
        mCallbackManager.removeNetworkAvailableCallBack(callBack);
    }

    //<=========================================================================================>
    //<================================  register callback  ====================================>
    //<=========================================================================================>
    public void registerRegisterCallBack(ChatRegisterCallBack callBack) {
        mCallbackManager.addRegisterCallback(callBack);
    }

    public void unRegisterCallBack(ChatRegisterCallBack callBack) {
        mCallbackManager.removeRegisterCallback(callBack);
    }

    //<=========================================================================================>
    //<==================================  login callback  =====================================>
    //<=========================================================================================>

    public void registerLoginCallBack(ConnectionListener callBack) {
        connection.addConnectionListener(callBack);
    }

    public void unRegisterLoginCallBack(ConnectionListener callBack) {
        connection.removeConnectionListener(callBack);
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
        errorStack.add(e);
       // mCallbackManager.executeLoginCallBack(false, e.toString());
    }

}
