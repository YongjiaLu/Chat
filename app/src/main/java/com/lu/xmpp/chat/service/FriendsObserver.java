package com.lu.xmpp.chat.service;

import android.content.Intent;
import android.os.Parcelable;

import com.lu.xmpp.modle.Friend;
import com.lu.xmpp.utils.BitmapUtils;
import com.lu.xmpp.utils.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.roster.RosterLoadedListener;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by xuyu on 2015/11/17.
 */
public class FriendsObserver implements RosterListener, RosterLoadedListener {

    private static String Tag = "FriendsObserver";

    private ChatService mService;
    private XMPPConnection mConnection;
    private Roster mRoster;

    private List<Friend> friends = new ArrayList<>();

    public FriendsObserver(XMPPConnection connection, ChatService service) {
        mService = service;
        mConnection = connection;
    }

    public void init() {

        if (!validateConnection()) {
            Log.e(Tag, "connection exception!");
            return;
        }


        mRoster = Roster.getInstanceFor(mConnection);
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

    /**
     * Called when roster entries are added.
     *
     * @param addresses the XMPP addresses of the contacts that have been added to the roster.
     */
    @Override
    public void entriesAdded(Collection<String> addresses) {
        Update(addresses);
    }

    /**
     * Called when a roster entries are updated.
     *
     * @param addresses the XMPP addresses of the contacts whose entries have been updated.
     */
    @Override
    public void entriesUpdated(Collection<String> addresses) {
        Update(addresses);
    }

    private void Update(Collection<String> addresses) {
        if (!validateConnection()) {
            Log.e(Tag, "Connection Exception");
            return;
        }
        Roster roster = Roster.getInstanceFor(mConnection);

        for (String address : addresses) {
            RosterEntry entry = roster.getEntry(address);
            Friend friend = parseRosterToFriend(entry, roster);
            if (null != friend) {
                for (Friend f : friends) {
                    if (f.getJid() == friend.getJid()) {
                        friends.remove(f);
                        friends.add(friend);
                    }
                }
            }
        }
    }

    /**
     * Called when a roster entries are removed.
     *
     * @param addresses the XMPP addresses of the contacts that have been removed from the roster.
     */
    @Override
    public void entriesDeleted(Collection<String> addresses) {
        if (!validateConnection()) {
            Log.e(Tag, "Connection Exception");
            return;
        }

        for (String jid : addresses) {
            for (Friend friend : friends) {
                if (jid == friend.getJid()) {
                    friends.remove(friend);
                }
            }
        }
    }

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
    @Override
    public void presenceChanged(Presence presence) {
        if (!validateConnection()) {
            Log.e(Tag, "Connection Exception");
        }
        showPresence(presence);
    }

    /**
     * Called when the Roster was loaded successfully.
     *
     * @param roster the Roster that was loaded successfully.
     */
    @Override
    public void onRosterLoaded(Roster roster) {

        List<Friend> list = parseRosterToFriend(roster);

        Intent intent = new Intent(mService, ChatService.class);
        intent.setAction(mService.Action_On_Receiver_Friends);
        intent.putParcelableArrayListExtra(mService.Param_FriendList, (ArrayList<? extends Parcelable>) list);
        mService.startService(intent);

        finish();

    }

    public List<Friend> parseRosterToFriend(Roster roster) {

        if (!validateConnection()) {
            Log.e(Tag, "Connection Exception!");
            return null;
        }

        friends = new ArrayList<>();
        Set<RosterEntry> rosterEntries = roster.getEntries();
        for (RosterEntry entry : rosterEntries) {
            Friend friend = parseRosterToFriend(entry, roster);
            if (null != friend) {
                friends.add(friend);
            }
        }
        return friends;
    }

    public Friend parseRosterToFriend(RosterEntry entry, Roster roster) {
        Friend friend = null;
        try {
            friend = new Friend();
            VCardManager vCardManager = VCardManager.getInstanceFor(mConnection);

            VCard vCard = vCardManager.loadVCard(entry.getUser());
            Presence presence = roster.getPresence(entry.getUser());
            //From Presence
            friend.setStatus(presence.getType().toString());
            friend.setStatusLine(presence.getStatus());

            showPresence(presence);
            //From VCard
            friend.setJid(entry.getUser());
            friend.setAvatar(BitmapUtils.parseByteArrayToBitmap(vCard.getAvatar(), mService));
            friend.setUsername(vCard.getNickName());
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
        if (rosterPresenceFlag == false && mRoster != null) {

            if (registerCount == 1) throw new Exception("this method had call");

            Log.e(Tag, "-------------------------------------------------------------------");
            Log.e(Tag, "start a presence listener ,Register Counter=" + ++registerCount);
            Log.e(Tag, "-------------------------------------------------------------------");
            mRoster.addRosterListener(this);
            rosterPresenceFlag = true;
        }
    }

    public synchronized void stopRosterPresenceListener() {
        if (rosterPresenceFlag == true && mRoster != null) {
            Log.e(Tag, "stop a presence listener ,Register Counter=" + --registerCount);
            mRoster.removeRosterListener(this);
            rosterPresenceFlag = false;
        }
    }


    /**
     * show a iq of presence
     *
     * @param presence
     */
    public static boolean debug = true;

    public void showPresence(Presence presence) {
        if (debug) {
            Log.e(Tag, "---------------------------------------------");
            Log.e(Tag, "User=" + presence.getFrom());
            Log.e(Tag, "States=" + presence.getStatus());
            Log.e(Tag, "Type=" + presence.getType());
            Log.e(Tag, "isAvailable=" + presence.isAvailable());
            Log.e(Tag, "Mode=" + presence.getMode());
            Log.e(Tag, "---------------------------------------------");
        }
    }

}
