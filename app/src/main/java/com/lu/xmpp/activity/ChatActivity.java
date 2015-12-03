package com.lu.xmpp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.lu.xmpp.R;
import com.lu.xmpp.activity.base.BaseActivity;
import com.lu.xmpp.adapter.ChatListAdapt;
import com.lu.xmpp.bean.ChatLog;
import com.lu.xmpp.chat.ChatControl;
import com.lu.xmpp.database.ChatLogManager;
import com.lu.xmpp.modle.Friend;
import com.lu.xmpp.utils.Log;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Date;

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


    private FloatingActionButton mActionButton;
    private MaterialEditText etMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_chat);
        Intent intent = getIntent();
        friendJid = intent.getStringExtra(PARAM_FRIEND_JID);
        friendInfo = chatControl.findUserFromJid(friendJid);
        userInfo = chatControl.findCurrentUserInfo();
        Log.e(Tag, userInfo.getJid());
        manager = ChatLogManager.getInstance(this, chatControl.getUserJid());
        adapt = new ChatListAdapt(manager.getChatLogFromJid(friendJid), friendInfo, userInfo);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(adapt);
        //skip to last one
        mRecyclerView.scrollToPosition(adapt.getLastPosition());
        //about message

        mActionButton = (FloatingActionButton) findViewById(R.id.btn_send);
        etMessage = (MaterialEditText) findViewById(R.id.et_message);

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && !s.toString().trim().equals("")) {
                    mActionButton.setEnabled(true);
                } else {
                    mActionButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        Log.e(Tag, "**********************************************************************************");

        for (ChatLog log : manager.getAllLog()) {
            manager.showLog(log);
        }

        Log.e(Tag, "**********************************************************************************");
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

    public void onClick(View v) {
        ChatLog chatLog = new ChatLog();
        chatLog.setBody(etMessage.getText().toString());
        chatLog.setFrom(userInfo.getJid());
        chatLog.setIsRead(true);
        chatLog.setTime(new Date(System.currentTimeMillis()));
        chatLog.setTo(friendJid);
        ChatControl.getInstance().sendMessageToFriend(chatLog);
        adapt.appendMessage(chatLog);
        mRecyclerView.scrollToPosition(adapt.getLastPosition());
        etMessage.setText("");
    }
}
