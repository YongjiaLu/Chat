package com.lu.xmpp.chat.service;

import com.lu.xmpp.chat.ChatConnectCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Using for Manager Connect
 */
class ChatConnectManager {
    private static ChatConnectManager instance = new ChatConnectManager();

    private ChatConnectManager() {
    }

    public static ChatConnectManager getInstance() {
        return instance;
    }

    private List<ChatConnectCallBack> mChatConnectCallBacks = new ArrayList<>();

    public void addChatConnectCallBack(ChatConnectCallBack callBack) {
        if (!mChatConnectCallBacks.contains(callBack)) {
            mChatConnectCallBacks.add(callBack);
        }
    }

    public void removeChatConnectCallBack(ChatConnectCallBack callBack) {
        if (mChatConnectCallBacks.contains(callBack)) {
            mChatConnectCallBacks.remove(callBack);
        }
    }

    public void executeNetworkAvaliable(boolean flag) {
        for (ChatConnectCallBack callBack : mChatConnectCallBacks)
            if (flag) {
                callBack.onNetworkAble();
            } else {
                callBack.onNetworkDisable();
            }
    }

    public void onConnectError() {
        for (ChatConnectCallBack callBack : mChatConnectCallBacks)
            callBack.onNetworkAble();
    }

}
