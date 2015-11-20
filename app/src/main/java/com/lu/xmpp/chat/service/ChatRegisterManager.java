package com.lu.xmpp.chat.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuyu on 2015/11/20.
 */
public class ChatRegisterManager {

    private static ChatRegisterManager instance = new ChatRegisterManager();

    private ChatRegisterManager() {
    }

    public static ChatRegisterManager getInstance() {
        return instance;
    }

    private List<ChatRegisterCallBack> mChatConnectCallBacks = new ArrayList<>();

    public void addChatRegiserCallBack(ChatRegisterCallBack callBack) {
        if (!mChatConnectCallBacks.contains(callBack)) {
            mChatConnectCallBacks.add(callBack);
        }
    }

    public void removeChatConnectCallBack(ChatRegisterCallBack callBack) {
        if (mChatConnectCallBacks.contains(callBack)) {
            mChatConnectCallBacks.remove(callBack);
        }
    }

    public void executeCallbacks(boolean flag, Exception e) {
        for (ChatRegisterCallBack callBack : mChatConnectCallBacks)
            if (flag) {
                callBack.registerSuccess();
            } else {
                callBack.registerFault(e.toString());
            }
    }

    public interface ChatRegisterCallBack {
        void registerSuccess();

        void registerFault(String reason);

    }

}
