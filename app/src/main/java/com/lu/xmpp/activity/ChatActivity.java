package com.lu.xmpp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lu.xmpp.R;
import com.lu.xmpp.activity.base.BaseActivity;
import com.lu.xmpp.adapter.ChatListAdapt;
import com.lu.xmpp.bean.ChatLog;
import com.lu.xmpp.chat.ChatControl;
import com.lu.xmpp.database.ChatLogManager;
import com.lu.xmpp.modle.Friend;

public class ChatActivity extends BaseActivity {

    public static final String PARAM_FRIEND_JID = "param_friend_jid";
    private static final String Tag = "ChatActivity";
    private String friendJid = null;

    private Friend userInfo;
    private Friend friendInfo;
    private RecyclerView mRecyclerView;

    private ChatListAdapt adapt;
    private ChatControl chatControl = ChatControl.getInstance();
    private ChatLogManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_chat);
        Intent intent = getIntent();
        friendJid = intent.getStringExtra(PARAM_FRIEND_JID);
        manager = ChatLogManager.getInstance(this, chatControl.getUserJid());

        friendInfo = chatControl.findUserFromJid(friendJid);
        userInfo = chatControl.findCurrentUserInfo();
        adapt = new ChatListAdapt(manager.getChatLogFromJid(friendJid), friendInfo.getAvatar(), userInfo.getAvatar());
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(adapt);
        //skip to last one
        mRecyclerView.scrollToPosition(adapt.getLastPosition());
    }

    @Override
    protected void onResume() {
        registerReceiver(receiver, new IntentFilter(ChatControl.Action_Receiver_Message));
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getExtras();
            ChatLog log = (ChatLog) bundle.getSerializable(ChatControl.Param_Chat_Log);
            if (log.getFrom().equals(friendJid)) {
                adapt.appendMessage(log);
                mRecyclerView.scrollToPosition(adapt.getLastPosition());
            }
        }
    };
}
