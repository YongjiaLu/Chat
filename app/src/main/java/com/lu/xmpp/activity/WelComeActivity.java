package com.lu.xmpp.activity;

import android.content.Intent;
import android.os.Bundle;

import com.lu.xmpp.R;
import com.lu.xmpp.activity.base.BaseActivity;
import com.lu.xmpp.chat.ConnectControl;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by xuyu on 2015/11/11.
 */
public class WelComeActivity extends BaseActivity {

    ConnectControl mConnectControl = ConnectControl.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Timer timer = new Timer();


        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent;
                if (mConnectControl.isLogin()) {
                    intent = new Intent(WelComeActivity.this, MainActivity.class);
                } else {
                    intent = new Intent(WelComeActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, 50);
    }
}
