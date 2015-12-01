package com.lu.xmpp.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.lu.xmpp.R;
import com.lu.xmpp.activity.base.BaseActivity;

public class ChatActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
    }
}
