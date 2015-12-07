package com.lu.xmpp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.lu.xmpp.R;
import com.lu.xmpp.activity.base.BaseActivity;
import com.lu.xmpp.chat.ChatConnectCallBack;
import com.lu.xmpp.chat.ConnectControl;
import com.lu.xmpp.utils.Log;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.jivesoftware.smack.XMPPConnection;

public class LoginActivity extends BaseActivity implements View.OnClickListener, ChatConnectCallBack {
    private static final String Tag = "LoginActivity";

    private Button btnLogin;
    private Button btnRegister;
    private ConnectControl connectControl = ConnectControl.getInstance();


    private MaterialEditText editUserName;
    private MaterialEditText editPassword;

    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(this).inflate(R.layout.activity_login, null);
        connectControl.isConnected();
        setContentView(view);
        initUI();
    }

    /**
     * 初始化界面
     */
    private void initUI() {

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);

        btnRegister = (Button) findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this);


        editUserName = (MaterialEditText) findViewById(R.id.tv_username);
        editPassword = (MaterialEditText) findViewById(R.id.tv_password);


    }

    @Override
    protected void onResume() {
        super.onResume();
        initStatus();

    }

    private void initStatus() {
        connectControl.addConnectCallBack(this);

        if (connectControl.isConnected()) {
            setBtnEnable(true);
        } else {
            connectControl.connect(this);
            setBtnEnable(false);
        }
    }

    private void setBtnEnable(boolean visible) {
        btnLogin.setEnabled(visible);
        btnRegister.setEnabled(visible);
    }


    @Override
    protected void onPause() {
        if (connectControl.isConnected())
            connectControl.removeConnectCallback(this);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_register:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }


    private void login() {
        String username = editUserName.getText().toString();
        String password = editPassword.getText().toString();
        connectControl.login(username, password, this);
    }


    private Snackbar snackbar;
    //<==================================================================================>
    //<============================== ConnectionListener ================================>
    //<==================================================================================>


    /**
     * Notification that the connection has been successfully connected to the remote endpoint (e.g. the XMPP server).
     * <p>
     * Note that the connection is likely not yet authenticated and therefore only limited operations like registering
     * an account may be possible.
     * </p>
     *
     * @param connection the XMPPConnection which successfully connected to its endpoint.
     */
    @Override
    public void connected(XMPPConnection connection) {
        Log.e(Tag, "hello world");
        handler.post(new Runnable() {
            @Override
            public void run() {
                setBtnEnable(true);
            }
        });
    }

    /**
     * Notification that the connection has been authenticated.
     *
     * @param connection the XMPPConnection which successfully authenticated.
     * @param resumed    true if a previous XMPP session's stream was resumed.
     */
    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        });
    }

    /**
     * Notification that the connection was closed normally.
     */
    @Override
    public void connectionClosed() {
        Log.e(Tag, "connectionClosed");
    }

    /**
     * Notification that the connection was closed due to an exception. When
     * abruptly disconnected it is possible for the connection to try reconnecting
     * to the server.
     *
     * @param e the exception.
     */
    @Override
    public void connectionClosedOnError(Exception e) {
        e.printStackTrace();
        handler.post(new Runnable() {
            @Override
            public void run() {
                snackbar = Snackbar.make(view, "Connect fault,please check your network", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (snackbar != null && snackbar.isShown()) {
                            snackbar.dismiss();
                        }
                    }
                });
                snackbar.show();
                setBtnEnable(false);
            }
        });
    }

    /**
     * The connection has reconnected successfully to the server. Connections will
     * reconnect to the server when the previous socket connection was abruptly closed.
     */
    @Override
    public void reconnectionSuccessful() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                setBtnEnable(true);
            }
        });
    }

    @Override
    public void reconnectingIn(int seconds) {

    }

    @Override
    public void reconnectionFailed(Exception e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                snackbar = Snackbar.make(view, "Connect fault,please check your network", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (snackbar != null && snackbar.isShown()) {
                            snackbar.dismiss();
                        }
                    }
                });
                snackbar.show();
                setBtnEnable(false);
            }
        });
    }

    @Override
    public void onNetworkAble() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                setBtnEnable(true);
            }
        });
    }

    @Override
    public void onNetworkDisable() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                snackbar = Snackbar.make(view, "Connect fault,please check your network", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (snackbar != null && snackbar.isShown()) {
                            snackbar.dismiss();
                        }
                    }
                });
                snackbar.show();
                setBtnEnable(false);
            }
        });
    }
}
