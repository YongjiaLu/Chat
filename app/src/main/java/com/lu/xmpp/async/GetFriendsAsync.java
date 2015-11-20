package com.lu.xmpp.async;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;

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
public class GetFriendsAsync extends AsyncTask<Void, Void, List<Friend>> {

    private static String Tag = "GetFriendsAsync";

    // 10 seccond time out
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

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected List<Friend> doInBackground(Void... params) {
        if (service == null) {
            return null;
        }
        Log.e(Tag, "start doInBackground ");
        isRunning = true;

        IntentFilter intentFilter = new IntentFilter(service.BroadCast_Action_On_Receiver_Friends);

        service.registerReceiver(MyFriendsReceiver, intentFilter);

        Intent intent = new Intent(service, ChatService.class);

        intent.setAction(service.Action_Start_Get_Friends);

        service.startService(intent);

        int time = 0;

        while (isRunning && time < CountDownTime) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
            time += 1000;
        }

        service.unregisterReceiver(MyFriendsReceiver);

        if (null != listeners && friends != null) {
            for (ChatControl.GetFriendListener listener : listeners) {
                listener.onGetFriends(friends);
            }
            listeners.clear();
        }

        mInstance = null;

        return null;
    }


    private BroadcastReceiver MyFriendsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            friends = intent.getParcelableArrayListExtra(service.Param_FriendList);
            isRunning = false;
        }
    };


    private boolean isRunning() {
        return isRunning;
    }

    public void startTask(ChatControl.GetFriendListener listener) {
        if (!isRunning()) {
            Log.e(Tag, "add listener and execute");
            listeners.add(listener);
            execute();
        } else {
            listeners.add(listener);
            Log.e(Tag, "add listener");
        }
    }

}
