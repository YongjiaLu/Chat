package com.lu.xmpp.chat.service;

import com.lu.xmpp.chat.ChatConnectAvailableCallBack;
import com.lu.xmpp.chat.ChatNetWorkAvailableCallBack;
import com.lu.xmpp.chat.ChatRegisterCallBack;
import com.lu.xmpp.utils.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuyu on 2015/11/12.
 */
class CallbackManager {
    private static final String Tag = "CallbackManager";
//<=========================================================================================>
//<================================  connect callback  =====================================>
//<=========================================================================================>

    private List<ChatConnectAvailableCallBack> connectCallbacks = new ArrayList<>();

    protected void addConnectAvailableCallBack(ChatConnectAvailableCallBack callBack) {
        if (!connectCallbacks.contains(callBack)) {
            Log.e(Tag, "a Connect callback had sign in stack");
            connectCallbacks.add(callBack);
        }
    }

    protected void removeConnectAvailableCallBack(ChatConnectAvailableCallBack callBack) {
        if (connectCallbacks.contains(callBack)) {
            Log.e(Tag, "a Connect callback had remove from stack");
            connectCallbacks.remove(callBack);
        }
    }

    protected void executeConnectCallback(boolean flag, Exception e) {

        for (ChatConnectAvailableCallBack callBack : connectCallbacks) {
            if (flag) {
                callBack.success();
            } else {
                callBack.fault(e);
            }
        }
    }


//<=========================================================================================>
//<================================= net work callback =====================================>
//<=========================================================================================>

    private List<ChatNetWorkAvailableCallBack> networkCallbacks = new ArrayList<>();

    protected void addNetworkAvailableCallBack(ChatNetWorkAvailableCallBack callBack) {
        if (!networkCallbacks.contains(callBack)) {
            Log.e(Tag, "a Network callback had sign in stack");
            networkCallbacks.add(callBack);
        }
    }

    protected void removeNetworkAvailableCallBack(ChatNetWorkAvailableCallBack callBack) {
        if (networkCallbacks.contains(callBack)) {
            Log.e(Tag, "a Network callback had remove from stack");
            networkCallbacks.remove(callBack);
        }
    }

    protected void executeNetworkCallback(boolean flag) {

        for (ChatNetWorkAvailableCallBack callBack : networkCallbacks) {
            if (flag) {
                callBack.onNetworkAble();
            } else {
                callBack.onNetworkDisable();
            }
        }
    }
//    <=========================================================================================>
//    <================================= net work callback =====================================>
//    <=========================================================================================>

    private List<ChatRegisterCallBack> registerCallBacks = new ArrayList<>();

    protected void addRegisterCallback(ChatRegisterCallBack callBack) {
        if (!registerCallBacks.contains(callBack))
            registerCallBacks.add(callBack);
    }

    protected void removeRegisterCallback(ChatRegisterCallBack callBack) {
        if (registerCallBacks.contains(callBack))
            registerCallBacks.remove(callBack);

    }

    protected void executeRegisterCallBack(boolean flag, String reason) {
        for (ChatRegisterCallBack callBack : registerCallBacks) {
            if ((flag)) {
                callBack.registerSuccess();
            } else {
                callBack.registerFault(reason);
            }
        }
    }

    //<=========================================================================================>
    //<=================================  login call back  =====================================>
    //<=========================================================================================>

//    private List<ChatLoginCallBack> loginCallbacks = new ArrayList<>();
//
//    protected void addLoginCallbacks(ChatLoginCallBack callBack) {
//        if (!loginCallbacks.contains(callBack)) {
//            loginCallbacks.add(callBack);
//        }
//    }
//
//    protected void removeLoginCallBack(ChatLoginCallBack callBack) {
//        if (loginCallbacks.contains(callBack)) {
//            loginCallbacks.remove(callBack);
//        }
//    }
//
//    protected void executeLoginCallBack(boolean flag, String reason) {
//        for (ChatLoginCallBack callBack : loginCallbacks) {
//            if ((flag)) {
//                callBack.onLoginSuccess();
//            } else {
//                callBack.onLoginFault(reason);
//            }
//        }
//    }

}
