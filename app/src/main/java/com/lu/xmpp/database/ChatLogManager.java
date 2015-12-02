package com.lu.xmpp.database;

import android.content.Context;

import com.lu.xmpp.bean.ChatLog;
import com.lu.xmpp.dao.ChatLogDao;
import com.lu.xmpp.dao.DaoMaster;

import java.util.List;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by xuyu on 2015/12/1.
 */
public class ChatLogManager {

    private String DB_NAME = "ChatLog";

    private static ChatLogManager instance;

    private Context context;

    private String username;

    private ChatLogManager(Context context, String username) {
        this.context = context.getApplicationContext();
        this.username = username;
    }

    public static synchronized ChatLogManager getInstance(Context context, String username) {
        if (null == instance) instance = new ChatLogManager(context, username);
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
        QueryBuilder builder = chatLogDao.queryBuilder();
        builder.where(ChatLogDao.Properties.From.eq(jid), ChatLogDao.Properties.To.eq(username));
        builder.or(ChatLogDao.Properties.From.eq(username), ChatLogDao.Properties.To.eq(jid));
        builder.orderAsc(ChatLogDao.Properties.Time);
        return builder.build().list();
    }

    /**
     * delete chat log from jid
     *
     * @param jid
     */
    public void deleteLogFromJid(String jid) {
        ChatLogDao chatLogDao = getChatLogDao();
        DeleteQuery deleteQuery = chatLogDao.queryBuilder().where(ChatLogDao.Properties.From.eq(jid)).buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
    }

    /**
     * get unread message total count
     *
     * @return
     */
    public long getUnReadMessageCount() {
        ChatLogDao chatLogDao = getChatLogDao();
        return chatLogDao.queryBuilder().where(ChatLogDao.Properties.IsRead.eq(false)).count();
    }

    /**
     * get unread message count
     *
     * @param jid target jid
     * @return
     */
    public long getUnReadMessageCountFromJid(String jid) {
        return getChatLogDao().queryBuilder().where(ChatLogDao.Properties.IsRead.eq(false), ChatLogDao.Properties.From.eq(jid)).count();
    }

    /**
     * sign ChatLog read
     *
     * @param chatLog
     */
    public void signMessageRead(ChatLog chatLog) {
        chatLog.setIsRead(true);
        getChatLogDao().update(chatLog);
    }
}
