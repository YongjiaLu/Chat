package com.lu.xmpp.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.lu.xmpp.R;
import com.lu.xmpp.activity.base.BaseActivity;
import com.lu.xmpp.chat.ConnectControl;
import com.lu.xmpp.chat.service.ChatRegisterManager;
import com.rengwuxian.materialedittext.MaterialEditText;


/**
 * 注册页面
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener,ChatRegisterManager. ChatRegisterCallBack {

    private MaterialEditText editUsername;
    private MaterialEditText editPassword;
    private MaterialEditText editPasswordRp;
    private View view;

    private Button btnCommit;

    private ConnectControl mConnectControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        view = LayoutInflater.from(this).inflate(R.layout.activity_register, null);

        setContentView(view);

        mConnectControl = ConnectControl.getInstance();

    }

    @Override
    protected void onResume() {

        initUI();

        super.onResume();
    }

    private void initUI() {
        editUsername = (MaterialEditText) findViewById(R.id.tv_username);
        editPassword = (MaterialEditText) findViewById(R.id.tv_password);
        editPasswordRp = (MaterialEditText) findViewById(R.id.tv_password_rp);

        btnCommit = (Button) findViewById(R.id.btn_commit);
        btnCommit.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_commit:
                register();
                break;
        }
    }

    /**
     * 只做简单过滤
     */

    private void register() {
        String username = editUsername.getText().toString();
        String password = editPassword.getText().toString();
        String passwordRP = editPasswordRp.getText().toString();

        if (!password.equals(passwordRP)) {
            Snackbar.make(view, "two password different!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editPassword.requestFocus();
                }
            }).show();
            return;
        }

        if (username.length() < 5) {
            Snackbar.make(view, "please type in longer username", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editUsername.requestFocus();
                }
            }).show();
        }

        mConnectControl.addRegisterCallback(this);
        mConnectControl.register(username, password, this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mConnectControl.removeRegisterCallback(this);
    }

    /**
     * 发出注册请求后的接口回调
     */
    @Override
    public void registerSuccess() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(view, "register success", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        });

    }

    @Override
    public void registerFault(final String reason) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(view, "register fault :" + reason, Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        });

    }
}
