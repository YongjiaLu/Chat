package com.lu.xmpp.database;

import android.content.Context;

import com.lu.xmpp.bean.ChatLog;
import com.lu.xmpp.dao.ChatLogDao;
import com.lu.xmpp.dao.DaoMaster;
import com.lu.xmpp.utils.Log;

import java.util.List;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by xuyu on 2015/12/1.
 */
public class ChatLogManager {

    private static final String Tag = "ChatLogManager";

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
        Log.e(Tag, "a message send from " + chatLog.getFrom() + " to " + chatLog.getTo());
        getChatLogDao().insert(chatLog);
    }

    /**
     * get chat log by jid sort by time asc
     *
     * @param jid
     * @return
     */
    public List<ChatLog> getChatLogFromJid(String jid) {

        Log.e(Tag, "list message! friendJid= " + jid + "//// userJid= " + username);

        ChatLogDao chatLogDao = getChatLogDao();

        QueryBuilder builder = chatLogDao.queryBuilder();

        builder.whereOr(ChatLogDao.Properties.From.eq(jid), ChatLogDao.Properties.To.eq(jid));

        builder.orderAsc(ChatLogDao.Properties.Time);

        List<ChatLog> list = builder.build().list();

        Log.e(Tag, jid + " log size= " + list.size());

        return list;
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

    public void showLog(ChatLog log) {

        Log.e(Tag, "-----------------------------------Show ChatLog --------------------------------------");

        Log.e(Tag, "message from :" + log.getFrom());

        Log.e(Tag, "message to :" + log.getTo());

        Log.e(Tag, "message time  :" + log.getTime().toString());

        Log.e(Tag, "message body  :" + log.getBody());

        Log.e(Tag, "-----------------------------------Show ChatLog --------------------------------------");
    }

    public List<ChatLog> getAllLog() {
        return getChatLogDao().loadAll();
    }
}
