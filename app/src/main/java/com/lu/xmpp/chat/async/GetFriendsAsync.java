package com.lu.xmpp.chat.async;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.lu.xmpp.chat.ChatControl;
import com.lu.xmpp.chat.service.ChatService;
import com.lu.xmpp.modle.Friend;
import com.lu.xmpp.utils.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * don't use execute(Params )<br />
 * please use startTask(ChatControl.GetFriendListener)
 */
public class GetFriendsAsync {

    private static String Tag = "GetFriendsAsync";

    // 10 second time out
    private final int CountDownTime = 10000;

    private static GetFriendsAsync mInstance;

    private ChatService service = ChatService.getInstance();

    private List<ChatControl.GetFriendListener> listeners = new ArrayList<>();

    private List<Friend> friends;

    private boolean isRunning = false;

    public static GetFriendsAsync getInstance() {
        if (mInstance == null) {
            mInstance = new GetFriendsAsync();
        }
        return mInstance;
    }

    private GetFriendsAsync() {
    }

    private void startObserver() {
        if (service == null) {
            return;
        }
        isRunning = true;

        IntentFilter intentFilter = new IntentFilter(service.BroadCast_Action_On_Receiver_Friends);

        service.registerReceiver(MyFriendsReceiver, intentFilter);

        Intent intent = new Intent(service, ChatService.class);

        intent.setAction(service.ActionStartGetFriends);

        service.startService(intent);


    }

    /**
     * run in child thread, when receiver .
     */
    private void handleCallback() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (null != listeners && friends != null) {
                    for (ChatControl.GetFriendListener listener : listeners) {
                        listener.onGetFriends(friends);
                    }
                    // listeners.clear();
                }
            }
        }).start();

    }


    private BroadcastReceiver MyFriendsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            friends = intent.getParcelableArrayListExtra(service.ParamFriendList);
            handleCallback();
        }
    };

    private boolean isRunning() {
        return isRunning;
    }


    /**
     * add Friend Observer task , when friend status change will callback.
     *
     * @param listener
     */
    public void startTask(ChatControl.GetFriendListener listener) {
        if (!isRunning()) {
            Log.e(Tag, "add listener and execute");
            if (!listeners.contains(listeners))
                listeners.add(listener);
            startObserver();
        } else {
            if (!listeners.contains(listeners))
                listeners.add(listener);
            Log.e(Tag, "add listener");
        }
    }

    public void stopTask(ChatControl.GetFriendListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
        if (listeners == null || listeners.size() == 0) {
            service.unregisterReceiver(MyFriendsReceiver);
        }
    }
}
