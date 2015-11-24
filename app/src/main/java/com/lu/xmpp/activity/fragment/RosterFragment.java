package com.lu.xmpp.activity.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lu.xmpp.R;
import com.lu.xmpp.activity.base.BaseActivity;
import com.lu.xmpp.adapter.FriendListAdapt;
import com.lu.xmpp.chat.ChatControl;
import com.lu.xmpp.modle.Friend;
import com.lu.xmpp.utils.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuyu on 2015/11/17.
 */
public class RosterFragment extends BaseFragment implements ChatControl.FriendStatusListener {

    private static String Tag = "RosterFragment";

    private RecyclerView mRecyclerView;

    private List<Friend> friends = new ArrayList<>();

    public RosterFragment(BaseActivity activity) {
        super(activity);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_roster, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(container.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.e(Tag, "onResume");
        super.onResume();
        if (friends != null && friends.size() != 0) {
            showFriendList(friends);
        }
        ChatControl.getInstance().addFriendStatusListener(this);
    }

    @Override
    public void onPause() {
        Log.e(Tag, "onPause");
        ChatControl.getInstance().removeFriendStatusListener(this);
        super.onPause();
    }

    /**
     * just change the UI,this method will run in main thread
     *
     * @param friends what you want to show
     */
    public void showFriendList(List<Friend> friends) {
        if (mRecyclerView == null) return;
        FriendListAdapt adapt = new FriendListAdapt(friends);
        mRecyclerView.setAdapter(adapt);
    }

    @Override
    public String getTitle() {
        return "Friend";
    }

    /**
     * On Friend Status Change when a friend available/unavailable.
     *
     * @param friends friend collection
     * @param friend  which one changed
     */
    @Override
    public void onFriendsStatusChanged(List<Friend> friends, Friend friend) {
        this.friends = friends;
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                showFriendList(RosterFragment.this.friends);
            }
        });
    }

    /**
     * A new friend want to add , there will be child thread
     *
     * @param friends friend collection
     * @param friend  which one ask for notice
     */
    @Override
    public void onNewFriendAddNotice(List<Friend> friends, Friend friend) {

    }

    /**
     * A friend delete our account
     *
     * @param friends friend collection
     * @param friend  which one delete you from his friend list
     */
    @Override
    public void onFriendDeleteNotice(List<Friend> friends, Friend friend) {

    }
}
