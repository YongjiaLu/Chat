package com.lu.xmpp;

import android.app.Application;
import android.content.Intent;

import com.lu.xmpp.chat.service.ChatService;
import com.lu.xmpp.contacts.ChatContacts;
import com.lu.xmpp.dao.DaoMaster;

/**
 * Created by xuyu on 2015/11/10.
 */
public class ChatApplication extends Application {

    public final static ChatContacts.DebugMode debug = ChatContacts.DebugMode.Debug;

    @Override
    public void onCreate() {
        super.onCreate();
        //start ChatService
        Intent intent = new Intent(this, ChatService.class);
        startService(intent);
    }
}
