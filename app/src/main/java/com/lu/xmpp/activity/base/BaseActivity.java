package com.lu.xmpp.activity.base;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by xuyu on 2015/11/11.
 */
public class BaseActivity extends AppCompatActivity {
    protected ChatHandler handler = new ChatHandler();

    public ChatHandler getHandler() {
        return handler;
    }
}
