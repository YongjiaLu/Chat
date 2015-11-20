package com.lu.xmpp.chat;

/**
 * Created by xuyu on 2015/11/11.
 */
public interface ChatConnectAvailableCallBack {

    void success();
    void fault(Exception e);
}
