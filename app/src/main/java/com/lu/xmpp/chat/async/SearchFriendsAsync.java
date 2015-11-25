package com.lu.xmpp.chat.async;

import android.os.AsyncTask;

import com.lu.xmpp.chat.service.ChatService;
import com.lu.xmpp.connect.ChatConnection;
import com.lu.xmpp.contacts.ChatContacts;
import com.lu.xmpp.utils.Log;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuyu on 2015/11/25.
 */
public class SearchFriendsAsync extends AsyncTask<String, Void, List<String>> {

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
    protected List<String> doInBackground(String... params) {

        List<String> list = new ArrayList<>();

        for (String byName : params) {
            try {
                Log.e(Tag, "start friend search async ,search by name =" + byName);
                list.addAll(SearchFriend(byName));
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
    protected void onPostExecute(List<String> strings) {
        if (null != mSearchFriendCallBack) {
            if (strings.size() > 0) {
                mSearchFriendCallBack.onSearchFriend(strings);
            } else {
                mSearchFriendCallBack.onError();
            }
        } else {
            throw new NullPointerException("callback is null");
        }
        super.onPostExecute(strings);
    }

    private static final String SEARCH_URL = "search." + ChatContacts.getDomain();

    public List<String> SearchFriend(String byName) throws SmackException.NotConnectedException, XMPPException.XMPPErrorException, SmackException.NoResponseException {
        ChatConnection connection = ChatService.getInstance().getConnection();
        if (connection == null || !connection.isAuthenticated())
            throw new SmackException.NotConnectedException();
        UserSearchManager search = new UserSearchManager(connection);
        Form searchForm = search.getSearchForm(SEARCH_URL);
        Form answerForm = searchForm.createAnswerForm();
        answerForm.setAnswer("Username", true);
        answerForm.setAnswer("Email", true);
        answerForm.setAnswer("Name", true);

        answerForm.setAnswer("search", byName);
        ReportedData reportedData = search.getSearchResults(answerForm, SEARCH_URL);

        List<ReportedData.Row> rows = reportedData.getRows();
        ArrayList<String> results = new ArrayList<>();
        for (ReportedData.Row row : rows) {
            results.add(row.getValues("Username").toString());
        }
        return results;
    }

    /**
     * Execute in Asynctask.postExecute.
     */
    interface SearchFriendCallBack {

        void onSearchFriend(List<String> ids);

        void onError();

    }


}
