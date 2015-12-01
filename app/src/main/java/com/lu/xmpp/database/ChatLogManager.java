package com.lu.xmpp.database;

import android.content.Context;

import com.lu.xmpp.bean.ChatLog;
import com.lu.xmpp.dao.ChatLogDao;
import com.lu.xmpp.dao.DaoMaster;

import java.util.List;

import de.greenrobot.dao.query.DeleteQuery;

/**
 * Created by xuyu on 2015/12/1.
 */
public class ChatLogManager {

    private String DB_NAME = "ChatLog";

    private static ChatLogManager instance;

    private Context context;

    private ChatLogManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized ChatLogManager getInstance(Context context) {
        if (null == instance) instance = new ChatLogManager(context);
        return instance;
    }

    /**
     * get a ChatLogDao
     *
     * @return
     */
    private ChatLogDao getChatLogDao() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
        DaoMaster master = new DaoMaster(helper.getWritableDatabase());
        return master.newSession().getChatLogDao();
    }


    /**
     * @param chatLog
     */
    public void addLog(ChatLog chatLog) {
        getChatLogDao().insert(chatLog);
    }

    /**
     * get chat log by jid sort by time asc
     *
     * @param jid
     * @return
     */
    public List<ChatLog> getChatLogFromJid(String jid) {
        ChatLogDao chatLogDao = getChatLogDao();
        return chatLogDao.queryBuilder().where(ChatLogDao.Properties.From.eq(jid)).orderAsc(ChatLogDao.Properties.Time).build().list();
    }

    public void deleteLogFromJid(String jid) {
        ChatLogDao chatLogDao = getChatLogDao();
        DeleteQuery deleteQuery = chatLogDao.queryBuilder().where(ChatLogDao.Properties.From.eq(jid)).buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
    }

    public long getUnReadMessageCount() {
        ChatLogDao chatLogDao = getChatLogDao();
        return chatLogDao.queryBuilder().where(ChatLogDao.Properties.IsRead.eq(false)).count();
    }

    public long getUnReadMessageCountFromJid(String jid) {
        return getChatLogDao().queryBuilder().where(ChatLogDao.Properties.IsRead.eq(false), ChatLogDao.Properties.From.eq(jid)).count();
    }

    public void signMessageRead(ChatLog chatLog) {
        chatLog.setIsRead(true);
        getChatLogDao().update(chatLog);
    }
}
