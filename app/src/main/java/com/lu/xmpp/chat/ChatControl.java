package com.lu.xmpp.chat;

import com.lu.xmpp.chat.async.GetFriendsAsync;
import com.lu.xmpp.chat.async.SearchFriendsAsync;
import com.lu.xmpp.chat.service.ChatService;
import com.lu.xmpp.modle.Friend;
import com.lu.xmpp.utils.Log;

import org.jivesoftware.smack.packet.Presence;

import java.util.List;

/**
 * Created by xuyu on 2015/11/17.
 */
public class ChatControl {

    private final static String Tag = "ChatControl";

    private static ChatControl mInstance = new ChatControl();

    private static ChatService service = ChatService.getInstance();


    private ChatControl() {

    }

    public static ChatControl getInstance() {
        service = ChatService.getInstance();
        return mInstance;
    }

    public void addFriendStatusListener(FriendStatusListener listener) {
        if (null != service)
            service.addFriendStatusListener(listener);
    }

    public void removeFriendStatusListener(FriendStatusListener listener) {
        if (null != service)
            service.removeFriendStatusListener(listener);
    }

    /**
     * 当获取好友任务处理完成时，将触发接口回调
     *
     * @param listener
     */
    public void startFriendObserver(GetFriendListener listener) {
        Log.e(Tag, "Start Friend Observer");
        GetFriendsAsync async = GetFriendsAsync.getInstance();
        async.startTask(listener);
    }

    public void stopFriendObserver(GetFriendListener listener) {
        GetFriendsAsync async = GetFriendsAsync.getInstance();
        async.stopTask(listener);
    }

    public interface GetFriendListener {
        /**
         * it will be run in child thread!<br />
         * please use mhandler.post(Runanle runable) back to main thread!
         */
        void onGetFriends(List<Friend> friends);
    }

    /**
     * Friend Listener
     */
    public interface FriendStatusListener {
        /**
         * On Friend Status Change when a friend available/unavailable.
         *
         * @param friends friend collection
         * @param friend  which one changed
         */
        void onFriendsStatusChanged(List<Friend> friends, Friend friend);

        /**
         * A new friend want to add , there will be child thread
         *
         * @param presence presence body
         * @param message  message
         * @param jid      which one call you
         */
        void onNewFriendAddNotice(Presence presence, String message, String jid);

    }

    public void replyNewFriendNotice(Presence presence) {
        service.replyNewFriendNotice(presence);
    }

    public void searchFriends(SearchFriendsAsync.SearchFriendCallBack callBack, String byName) {
        service.searchFriend(callBack, byName);
    }

    public void startAddFriend(String Jid, String Message) {
        service.startAddFriend(Jid, Message);
    }
}
