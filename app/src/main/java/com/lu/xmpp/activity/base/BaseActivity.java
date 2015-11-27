package com.lu.xmpp.activity.base;

import android.support.v7.app.AppCompatActivity;

/**
 * getHandler()
 */
public class BaseActivity extends AppCompatActivity {
    protected ChatHandler handler = new ChatHandler();

    public ChatHandler getHandler() {
        return handler;
    }
}
