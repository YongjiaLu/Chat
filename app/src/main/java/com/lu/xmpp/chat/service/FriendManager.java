package com.lu.xmpp.chat.service;

import android.content.Intent;
import android.os.Parcelable;

import com.lu.xmpp.chat.ChatControl;
import com.lu.xmpp.chat.async.SearchFriendsAsync;
import com.lu.xmpp.connect.ChatConnection;
import com.lu.xmpp.modle.Friend;
import com.lu.xmpp.utils.BitmapUtil;
import com.lu.xmpp.utils.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.roster.RosterLoadedListener;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by xuyu on 2015/11/17.
 */
class FriendManager implements RosterLoadedListener, StanzaListener {
    private static FriendManager mInstance;

    private static final long TIME_LIMIT_FOR_REFRESH = 1000 * 60 * 5;//5 minute
    private long lastUpdateTime = 0;
    private static String Tag = "FriendManager";

    private ChatService mService;
    private XMPPConnection mConnection;
    private Roster mRoster;

    private List<Friend> friends = new ArrayList<>();
    private List<ChatControl.FriendStatusListener> listeners = new ArrayList<>();

    public synchronized static FriendManager getInstance(XMPPConnection connection, ChatService service) {
        if (mInstance == null) mInstance = new FriendManager(connection, service);
        return mInstance;
    }

    private FriendManager(XMPPConnection connection, ChatService service) {
        mService = service;
        mConnection = connection;
    }

    public void init() {
        if (!validateConnection()) {
            Log.e(Tag, "connection exception!");
            return;
        }
        mRoster = Roster.getInstanceFor(mConnection);
        //mRoster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
        mRoster.addRosterLoadedListener(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e(Tag, "start get friends");
                    mRoster.reload();
                } catch (SmackException.NotLoggedInException e) {
                    handError(e);
                } catch (SmackException.NotConnectedException e) {
                    handError(e);
                }
            }
        }).start();
    }

    public void finish() {
        mRoster.removeRosterLoadedListener(this);
    }

//    /**
//     * Called when roster entries are added.
//     *
//     * @param addresses the XMPP addresses of the contacts that have been added to the roster.
//     */
//    @Override
//    public void entriesAdded(Collection<String> addresses) {
//        Update(addresses);
//    }
//
//    /**
//     * Called when a roster entries are updated.
//     *
//     * @param addresses the XMPP addresses of the contacts whose entries have been updated.
//     */
//    @Override
//    public void entriesUpdated(Collection<String> addresses) {
//        Update(addresses);
//    }
//
//    private void Update(Collection<String> addresses) {
//        if (!validateConnection()) {
//            Log.e(Tag, "Connection Exception");
//            return;
//        }
//        Roster roster = Roster.getInstanceFor(mConnection);
//
//        for (String address : addresses) {
//            RosterEntry entry = roster.getEntry(address);
//            Friend friend = parseRosterToFriend(entry, roster);
//            if (null != friend) {
//                for (Friend f : friends) {
//                    if (f.getJid().equals(friend.getJid())) {
//                        friends.remove(f);
//                        break;
//                    }
//                }
//                friends.add(friend);
//            }
//        }
//    }
//
//    /**
//     * Called when a roster entries are removed.
//     *
//     * @param addresses the XMPP addresses of the contacts that have been removed from the roster.
//     */
//    @Override
//    public void entriesDeleted(Collection<String> addresses) {
//        if (!validateConnection()) {
//            Log.e(Tag, "Connection Exception");
//            return;
//        }
//
//        for (String address : addresses) {
//            Log.e(Tag, address);
//        }
//
//        for (String jid : addresses) {
//            for (Friend friend : friends) {
//                if (jid.equals(friend.getJid())) {
//                    friends.remove(friend);
//                    break;
//                }
//            }
//        }
//    }


    /**
     * Called when the presence of a roster entry is changed. Care should be taken
     * when using the presence data delivered as part of this event. Specifically,
     * when a user account is online with multiple resources, the UI should account
     * for that. For example, say a user is online with their desktop computer and
     * mobile phone. If the user logs out of the IM client on their mobile phone, the
     * user should not be shown in the roster (contact list) as offline since they're
     * still available as another resource.<p>
     * <p/>
     * To get the current "best presence" for a user after the presence update, query the roster:
     * <pre>
     *    String user = presence.getFrom();
     *    Presence bestPresence = roster.getPresence(user);
     * </pre>
     * <p/>
     * That will return the presence value for the user with the highest priority and
     * availability.
     * <p/>
     * Note that this listener is triggered for presence (mode) changes only
     * (e.g presence of types available and unavailable. Subscription-related
     * presence packets will not cause this method to be called.
     *
     * @param presence the presence that changed.
     * @see Roster#getPresence(String)
     */
    public void presenceChanged(Presence presence) {
        /**
         * Presence.Type
         *
         *available:    表示处于在线状态
         *unavailable:  表示处于离线状态
         *subscribe:    表示发出添加好友的申请
         *subscribed:   表示通过对方申请
         *unsubscribe:  表示发出删除好友的申请
         *unsubscribed: 表示拒绝添加对方为好友
         *error: 表示presence信息报中包含了一个错误消息。
         */
        if (presence.getType() == Presence.Type.available || presence.getType() == Presence.Type.unavailable) {
            Friend friend = null;
            for (Friend target : friends) {
                if (target.getJid().equals(presence.getFrom().split("/")[0])) {
                    //target
                    target.setStatus(presence.getType().toString());
                    target.setStatusLine(presence.getStatus());
                    friend = target;
                }
            }

            if (friend != null) {
                for (ChatControl.FriendStatusListener listener : listeners) {
                    listener.onFriendsStatusChanged(friends, friend);
                }
            }
        }
        if (presence.getType() == Presence.Type.subscribe) {
            //receive a friend request
            String jid = presence.getFrom().split("/")[0] != null ? (presence.getFrom().split("/")[0]) : null;

            if (jid == null) {
                Log.e(Tag, "receive a error message !");
                return;
            }

            String username = (jid.split("@")[0]) != null ? jid.split("@")[0] : "ChatUser";

            String message = presence.getStatus() == null ? "hello! i'm " + username : presence.getStatus();

            Log.e(Tag, "receive a friend request");
            for (ChatControl.FriendStatusListener listener : listeners) {
                listener.onNewFriendAddNotice(presence, message, jid);
            }
        }
        //TODO Bug
        // be careful ,if some send Presence.Type.subscribed to here ,anyway, our user would add a error friend
        // We need a stack to save our request and confirm it where it come from ,is from where our user request
        if (presence.getType() == Presence.Type.subscribed) {
            Log.e(Tag,"request a subscribed!");
            String jid = presence.getFrom().split("/")[0] != null ? (presence.getFrom().split("/")[0]) : null;
            try {
                // don't have any group
                mRoster.createEntry(jid, null, null);
            } catch (SmackException.NotLoggedInException e) {
                e.printStackTrace();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Called when the Roster was loaded successfully.
     *
     * @param roster the Roster that was loaded successfully.
     */
    @Override
    public void onRosterLoaded(Roster roster) {
        parseRosterToFriend(roster);
        lastUpdateTime = System.currentTimeMillis();
        Intent intent = new Intent(mService, ChatService.class);
        intent.setAction(mService.ActionOnReceiverFriends);
        intent.putParcelableArrayListExtra(mService.ParamFriendList, (ArrayList<? extends Parcelable>) friends);
        mService.startService(intent);
        finish();
    }

    public List<Friend> parseRosterToFriend(Roster roster) {

        if (!validateConnection()) {
            Log.e(Tag, "Connection Exception!");
            return null;
        }

        friends.clear();

        List<String> ids = new ArrayList<>();

        Collection<RosterGroup> list = roster.getGroups();
        //Get friend from GroupEntries
        for (RosterGroup group : list) {
            Log.e(Tag, "GroupName=" + group.getName());
            Log.e(Tag, "Group" + group.getEntries());
            for (RosterEntry entry : group.getEntries()) {
                friends.add(parseRosterToFriend(entry, roster, group.getName()));
                ids.add(entry.getUser());
            }
        }
        //Get friend from default entry
        for (RosterEntry entry : roster.getEntries()) {
            if (!ids.contains(entry.getUser())) {
                friends.add(parseRosterToFriend(entry, roster));
                ids.add(entry.getUser());
            }
        }

        Collections.sort(friends, new Comparator<Friend>() {
            @Override
            public int compare(Friend lhs, Friend rhs) {
                if (lhs.getStatus().equals(Presence.Type.available.toString()) && !rhs.getStatus().equals(Presence.Type.available.toString()))
                    return 5;
                return lhs.getUsername().compareTo(rhs.getUsername()) > 0 ? 1 : -1;
            }
        });

        Log.e(Tag, "A new list had sort");

        //sortList(friends);

        return friends;
    }

    public Friend parseRosterToFriend(RosterEntry entry, Roster roster) {
        return parseRosterToFriend(entry, roster, "Friends");
    }


    public Friend parseRosterToFriend(RosterEntry entry, Roster roster, String Group) {
        Friend friend = null;
        try {
            friend = new Friend();
            VCardManager vCardManager = VCardManager.getInstanceFor(mConnection);

            VCard vCard = vCardManager.loadVCard(entry.getUser());
            Presence presence = roster.getPresence(entry.getUser());
            //From Presence
            friend.setStatus(presence.getType().toString());
            friend.setStatusLine(presence.getStatus());
            if (debug) {
                Log.e(Tag, "---------------------------------------------");
                showEntry(entry);
                showPresence(presence);
                Log.e(Tag, "---------------------------------------------");
            }
            //From VCard
            friend.setJid(entry.getUser());
            friend.setAvatar(BitmapUtil.parseByteArrayToBitmap(vCard.getAvatar(), mService));
            friend.setUsername(vCard.getNickName() != null ? vCard.getNickName() : entry.getUser().split("@")[0]);

            friend.setGroupName(Group);
        } catch (SmackException.NoResponseException e) {
            handError(e);
        } catch (XMPPException.XMPPErrorException e) {
            handError(e);
        } catch (SmackException.NotConnectedException e) {
            handError(e);
        }
        return friend;
    }


    private void handError(Exception e) {
        Log.e(Tag, e.toString());
    }

    private boolean validateConnection() {
        return mConnection.isConnected() && mConnection.isAuthenticated();
    }

    private int registerCount = 0;
    private boolean rosterPresenceFlag = false;

    public synchronized void startRosterPresenceListener() throws Exception {
        if (!rosterPresenceFlag && mRoster != null) {

            if (registerCount == 1) throw new Exception("this method had call");

            if (mConnection == null) throw new NullPointerException("Connection == null");
            if (!mConnection.isConnected())
                throw new SmackException("this connection had not connected");
            if (!mConnection.isAuthenticated())
                throw new SmackException("this connection had not login");

            mConnection.addAsyncStanzaListener(this, StanzaTypeFilter.PRESENCE);

            Log.e(Tag, "-------------------------------------------------------------------");
            Log.e(Tag, "start a presence listener ,Register Counter=" + ++registerCount);
            Log.e(Tag, "-------------------------------------------------------------------");

            rosterPresenceFlag = true;
        }
    }

    public synchronized void stopRosterPresenceListener() throws Exception {
        if (rosterPresenceFlag && mRoster != null) {
            Log.e(Tag, "stop a presence listener ,Register Counter=" + --registerCount);
            if (registerCount == 1) throw new Exception("this method had call");

            if (mConnection == null) throw new NullPointerException("Connection == null");
            if (!mConnection.isConnected())
                throw new SmackException("this connection had not connected");
            if (!mConnection.isAuthenticated())
                throw new SmackException("this connection had not login");

            mConnection.removeAsyncStanzaListener(this);

            rosterPresenceFlag = false;
        }
    }


    /**
     * show a iq of presence
     *
     * @param presence
     */
    public static boolean debug = false;

    public void showEntry(RosterEntry entry) {
        Log.e(Tag, "RosterEntry.User=" + entry.getUser());
        Log.e(Tag, "RosterEntry.States=" + entry.getStatus());
        Log.e(Tag, "RosterEntry.Type=" + entry.getType());
        Log.e(Tag, "RosterEntry.Name=" + entry.getName());
    }

    public void showPresence(Presence presence) {
        Log.e(Tag, "Presence.User=" + presence.getFrom());
        Log.e(Tag, "Presence.States=" + presence.getStatus());
        Log.e(Tag, "Presence.Type=" + presence.getType());
        Log.e(Tag, "Presence.isAvailable=" + presence.isAvailable());
        Log.e(Tag, "Presence.Mode=" + presence.getMode());
    }

    private void sortList(List<Friend> friends) {
        //Create a Sorter,online friend will be top.
        Collections.sort(friends, new Comparator<Friend>() {
            @Override
            public int compare(Friend lhs, Friend rhs) {
                if (lhs.getStatus().equals(Presence.Type.available.toString()) && !rhs.getStatus().equals(Presence.Type.available.toString()))
                    return 5;
                return lhs.getUsername().compareTo(rhs.getUsername()) > 0 ? 1 : -1;
            }
        });
    }

    /**
     * use XmppConnection.addStanzaFilter to get callback
     */

    public void addPresenceListener(ChatControl.FriendStatusListener listener) {
        if (!listeners.contains(listener)) {
            Log.e(Tag, "a Presence Listener had register");
            listeners.add(listener);
        }
    }

    public void removePresenceListener(ChatControl.FriendStatusListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
            Log.e(Tag, "a Presence Listener had remove");
        }
    }

    /**
     * Process the next stanza(/packet) sent to this stanza(/packet) listener.
     * <p>
     * A single thread is responsible for invoking all listeners, so
     * it's very important that implementations of this method not block
     * for any extended period of time.
     * </p>
     *
     * @param packet the stanza(/packet) to process.
     */
    @Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
        if (packet instanceof Presence) {
            Presence presence = (Presence) packet;
            presenceChanged(presence);
        }
    }

    public void searchFriend(SearchFriendsAsync.SearchFriendCallBack callBack, String... byName) {
        new SearchFriendsAsync(callBack).execute(byName);
    }

    public void addFriend(final String Jid, String Message) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                ChatConnection connection = ChatService.getInstance().getConnection();
                if (null == connection || !connection.isConnected() || !connection.isAuthenticated()) {
                    try {
                        throw new SmackException.NotConnectedException();
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Presence request = new Presence(Presence.Type.subscribe);
                    request.setFrom(connection.getUser());
                    request.setTo(Jid);
                    request.setMode(Presence.Mode.available);
                    connection.sendStanza(request);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void replyNewFriendNotice(Presence presence) throws SmackException.NotConnectedException {
        ChatConnection connection = ChatService.getInstance().getConnection();
        if (null == connection || !connection.isConnected() || !connection.isAuthenticated()) {
            throw new SmackException.NotConnectedException();
        }
        try {
            Roster roster = Roster.getInstanceFor(connection);
            roster.createEntry(presence.getFrom().split("/")[0], null, null);
            Presence response = new Presence(Presence.Type.subscribed);
            response.setFrom(connection.getUser());
            response.setTo(presence.getFrom());
            response.setMode(Presence.Mode.available);
            connection.sendStanza(response);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        }
    }


}
