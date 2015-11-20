package com.lu.xmpp.chat;

import org.jivesoftware.smack.ConnectionListener;

/**
 * extends ConnectionListener
 */
public interface ChatConnectCallBack extends ConnectionListener {

    /**
     * when network disable to able
     */
    void onNetworkAble();

    /**
     * when network able to disable
     */
    void onNetworkDisable();

}
