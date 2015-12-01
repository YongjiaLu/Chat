package com.lu.xmpp.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.lu.xmpp.bean.ChatLog;

import com.lu.xmpp.dao.ChatLogDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig chatLogDaoConfig;

    private final ChatLogDao chatLogDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        chatLogDaoConfig = daoConfigMap.get(ChatLogDao.class).clone();
        chatLogDaoConfig.initIdentityScope(type);

        chatLogDao = new ChatLogDao(chatLogDaoConfig, this);

        registerDao(ChatLog.class, chatLogDao);
    }
    
    public void clear() {
        chatLogDaoConfig.getIdentityScope().clear();
    }

    public ChatLogDao getChatLogDao() {
        return chatLogDao;
    }

}
