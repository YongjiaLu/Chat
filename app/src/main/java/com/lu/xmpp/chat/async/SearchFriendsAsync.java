package com.lu.xmpp.chat.async;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.lu.xmpp.chat.service.ChatService;
import com.lu.xmpp.connect.ChatConnection;
import com.lu.xmpp.utils.BitmapUtil;
import com.lu.xmpp.utils.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuyu on 2015/11/25.
 */
public class SearchFriendsAsync extends AsyncTask<String, Void, List<SearchFriendsAsync.Entity>> {

    private static final String Tag = "SearchFriendsAsync";

    private SearchFriendCallBack mSearchFriendCallBack;

    public SearchFriendsAsync(SearchFriendCallBack callBack) {
        mSearchFriendCallBack = callBack;
    }


    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected List<SearchFriendsAsync.Entity> doInBackground(String... params) {

        List<Entity> list = new ArrayList<>();

        for (String byName : params) {
            try {
                Log.e(Tag, "start friend search async ,search by name =" + (byName.trim().equals("") ? "empty" : byName));

                List<SearchFriendsAsync.Entity> results = SearchFriend(byName);

                if (results != null && results.size() > 0) {
                    list.addAll(results);
                }
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    @Override
    protected void onPostExecute(List<SearchFriendsAsync.Entity> strings) {

        if (null != mSearchFriendCallBack) {
            if (strings != null && strings.size() > 0) {
                mSearchFriendCallBack.onSearchFriend(strings);
            } else {
                mSearchFriendCallBack.onError();
            }
        } else {
            throw new NullPointerException("callback is null");
        }
        super.onPostExecute(strings);
    }


    public List<SearchFriendsAsync.Entity> SearchFriend(String byName) throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {
        ChatConnection connection = ChatService.getInstance().getConnection();
        if (connection == null || !connection.isAuthenticated())
            throw new SmackException.NotConnectedException();

        UserSearchManager search = new UserSearchManager(connection);
        String SEARCH_URL = "search." + connection.getServiceName();
        Form searchForm = search.getSearchForm(SEARCH_URL);
        Form answerForm = searchForm.createAnswerForm();
        answerForm.setAnswer("Username", true);
        answerForm.setAnswer("Email", true);
        answerForm.setAnswer("Name", true);

        answerForm.setAnswer("search", byName);

        ReportedData reportedData = search.getSearchResults(answerForm, SEARCH_URL);

        List<ReportedData.Row> rows = reportedData.getRows();

        List<SearchFriendsAsync.Entity> entities = new ArrayList<>();

        VCardManager vm=VCardManager.getInstanceFor(connection);



        for (ReportedData.Row row : rows) {
            Entity entity = new Entity();
            entity.setUserName(fixString(row.getValues("Username").toString()));
            entity.setEmail(fixString(row.getValues("Email").toString()));
            entity.setName(fixString(row.getValues("Name").toString()));
            entity.setJid(fixString(row.getValues("JID").toString()));
            VCard  vCard=vm.loadVCard(entity.getJid());
            entity.setAvatar(BitmapUtil.parseByteArrayToBitmap(vCard.getAvatar(), ChatService.getInstance()));
            entities.add(entity);
        }
        return entities;
    }

    /**
     * Execute in Asynctask.postExecute.
     */
    public interface SearchFriendCallBack {

        void onSearchFriend(List<SearchFriendsAsync.Entity> ids);

        void onError();

    }


    public class Entity {
        private String UserName;
        private String Jid;
        private String Email;
        private String Name;
        private Bitmap Avatar;

        public Bitmap getAvatar() {
            return Avatar;
        }

        public void setAvatar(Bitmap avatar) {
            Avatar = avatar;
        }


        public String getUserName() {
            return UserName;
        }

        public void setUserName(String userName) {
            UserName = userName;
        }

        public String getJid() {
            return Jid;
        }

        public void setJid(String jid) {
            Jid = jid;
        }

        public String getEmail() {
            return Email;
        }

        public void setEmail(String email) {
            Email = email;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }
    }

    /**
     * @param string results contain "[data]"
     * @return "data"
     */
    private String fixString(String string) {
        StringBuffer sb = new StringBuffer(string);
        int start = sb.indexOf("[");
        if (start >= 0)
            sb = sb.deleteCharAt(start);
        int end = sb.indexOf("]");
        if (end >= 0)
            sb = sb.deleteCharAt(end);
        return sb.toString();
    }
}
