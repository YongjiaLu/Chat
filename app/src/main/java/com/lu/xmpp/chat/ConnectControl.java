package com.lu.xmpp.chat;

import android.content.Context;
import android.content.Intent;

import com.lu.xmpp.chat.service.ChatRegisterManager;
import com.lu.xmpp.chat.service.ChatService;
import com.lu.xmpp.utils.Log;

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
     * 是否连接上服务器
     */
    public boolean isConnected() {
        Log.e(Tag, "service.isConnected=" + service.isConnected());
        return service.isConnected();
    }

    public void connect(Context context) {
        if (!service.isConnected()) {
            Intent intent = new Intent(context, ChatService.class);
            intent.setAction(service.ActionConnect);
            context.startService(intent);
        }
    }

    public boolean isLogin() {
        return service != null && service.isLogin();
    }


    public void addConnectCallBack(ChatConnectCallBack callBack) {
        service.addConnectCallBack(callBack);
    }

    public void removeConnectCallback(ChatConnectCallBack callBack) {
        service.removeConnectCallBack(callBack);
    }


    /**
     * 注册
     */
    public void addRegisterCallback(ChatRegisterManager.ChatRegisterCallBack callBack) {
        Log.e(Tag, "A RegisterCallback had added");
        service.registerRegisterCallBack(callBack);
    }

    public void removeRegisterCallback(ChatRegisterManager.ChatRegisterCallBack callBack) {

        service.unRegisterCallBack(callBack);
    }

    public void register(String username, String password, Context context) {
        Intent intent = new Intent(context, ChatService.class);
        intent.setAction(service.ActionRegister);
        intent.putExtra(service.ParamUserName, username);
        intent.putExtra(service.ParamPassword, password);
        context.startService(intent);
    }

    /**
     * login
     */
    public void login(String username, String password, Context context) {
        Intent intent = new Intent(context, ChatService.class);
        intent.setAction(service.ActionLogin);
        intent.putExtra(service.ParamUserName, username);
        intent.putExtra(service.ParamPassword, password);
        context.startService(intent);
    }
}
