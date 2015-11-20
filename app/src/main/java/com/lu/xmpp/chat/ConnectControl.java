package com.lu.xmpp.chat;

import android.content.Context;
import android.content.Intent;

import com.lu.xmpp.chat.service.ChatService;
import com.lu.xmpp.utils.Log;

import org.jivesoftware.smack.ConnectionListener;

/**
 * 通信类，单例
 */
public class ConnectControl {

    private final static String Tag = "ConnectControl";

    private static ConnectControl mInstance = new ConnectControl();
    //用于连接
    private static ChatService service;
    //用于界面
    private Context context;

    private ConnectControl() {
    }

    public static ConnectControl getInstance() {
        service = ChatService.getInstance();
        return mInstance;
    }

    /**
     * 注册上下文对象
     *
     * @param context
     */
    public synchronized void registerContext(Context context) {
        if (null == this.context)
            this.context = context;
        else if (context == this.context) {
            Log.d(Tag, "context had regist");
        }
        if (context != this.context) {
            Log.e(Tag, "context exist,please unregist last one you set");
        }
    }

    /**
     * 注销上下文对象
     *
     * @param context
     */
    public synchronized void unRegisterContext(Context context) {
        if (context == this.context) {
            this.context = null;
        }
    }

    /**
     * 是否连接上服务器
     */
    public boolean isConnected() {
        Log.e(Tag, "service.isConnected=" + service.isConnected());
        return service.isConnected();
    }

    public void connect(Context context) {
        if (!service.isConnected()) {
            Intent intent = new Intent(context, ChatService.class);
            intent.setAction(service.Action_Connect);
            context.startService(intent);
        }
    }

    public boolean isLogin() {
        return service != null && service.isLogin();
    }


    /**
     * 服务可用或不可用时，回调
     *
     * @param callBack
     */
    public void registerConnectObserter(ChatConnectAvailableCallBack callBack) {
        service.registerConnectCallback(callBack);
    }

    public void unregisterConnectObserter(ChatConnectAvailableCallBack callBack) {
        service.unregisterConnectCallback(callBack);
    }


    /**
     * 网络可用或不可用时，回调
     *
     * @param callBack
     */
    public void registerNetworkObserver(ChatNetWorkAvailableCallBack callBack) {
        service.registerNetworkAvailableCallBack(callBack);
    }

    public void unregisterNetworkObserver(ChatNetWorkAvailableCallBack callBack) {
        service.unRegisterNetworkAvailableCallBack(callBack);
    }

    /**
     * 注册
     */
    public void addRegisterCallback(ChatRegisterCallBack callBack) {
        Log.e(Tag, "A RegisterCallback had added");
        service.registerRegisterCallBack(callBack);
    }

    public void removeRegisterCallback(ChatRegisterCallBack callBack) {

        service.unRegisterCallBack(callBack);
    }

    public void register(String username, String password, Context context) {
        Intent intent = new Intent(context, ChatService.class);
        intent.setAction(service.Action_Register);
        intent.putExtra(service.Param_UserName, username);
        intent.putExtra(service.Param_PassWord, password);
        context.startService(intent);
    }

    /**
     * login
     */
    public void login(String username, String password, Context context) {
        Intent intent = new Intent(context, ChatService.class);
        intent.setAction(service.Action_Login);
        intent.putExtra(service.Param_UserName, username);
        intent.putExtra(service.Param_PassWord, password);
        context.startService(intent);
    }

    public void registerLoginCallBack(ConnectionListener callBack) {
        service.registerLoginCallBack(callBack);
    }

    public void unRegisterLoginCallback(ConnectionListener callBack) {
        service.unRegisterLoginCallBack(callBack);
    }


}
