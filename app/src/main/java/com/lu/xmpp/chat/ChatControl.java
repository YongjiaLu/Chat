package com.lu.xmpp.chat;

import android.content.Context;

import com.lu.xmpp.async.GetFriendsAsync;
import com.lu.xmpp.chat.service.ChatService;
import com.lu.xmpp.modle.Friend;
import com.lu.xmpp.utils.Log;

import java.util.List;

/**
 * Created by xuyu on 2015/11/17.
 */
public class ChatControl {

    private final static String Tag = "ChatControl";

    private static ChatControl mInstance = new ChatControl();

    private static ChatService service= ChatService.getInstance();

    private ChatControl() {

    }

    public static ChatControl getInstance() {
        service = ChatService.getInstance();
        return mInstance;
    }

    /**
     * 当获取好友任务处理完成时，将触发接口回调
     *
     * @param listener
     */
    public void getFriends(GetFriendListener listener) {
        Log.e(Tag,"getFriends(GetFriendListener listener)");
        GetFriendsAsync async = GetFriendsAsync.getInstance();
        async.startTask(listener);
    }

    /**
     * it will be run in child thread!<br />
     * please use mhandler.post(Runanle runable) back to main thread!
     */
    public interface GetFriendListener {
        void onGetFriends(List<Friend> friends);
    }
}
