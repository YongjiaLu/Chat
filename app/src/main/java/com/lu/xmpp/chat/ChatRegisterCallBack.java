package com.lu.xmpp.chat;

/**
 * Created by xuyu on 2015/11/13.
 */
public interface ChatRegisterCallBack {

    void registerSuccess();

    void registerFault(String reason);

}
