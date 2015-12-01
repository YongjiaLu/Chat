package com.lu.xmpp.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.lu.xmpp.R;
import com.lu.xmpp.activity.base.BaseActivity;
import com.lu.xmpp.adapter.SearchListAdapt;
import com.lu.xmpp.chat.ChatControl;
import com.lu.xmpp.chat.async.SearchFriendsAsync;
import com.lu.xmpp.modle.Friend;
import com.lu.xmpp.utils.Log;
import com.lu.xmpp.view.custom.dialog.AddFriendDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.jivesoftware.smack.packet.Presence;

import java.util.List;

public class SearchActivity extends BaseActivity implements View.OnClickListener, SearchFriendsAsync.SearchFriendCallBack, SearchListAdapt.OnItemClickListener, AddFriendDialog.OnButtonClick, ChatControl.FriendStatusListener {
    private static final String Tag = "SearchActivity";
    private RecyclerView mRecyclerView;
    private MaterialEditText materialEditText;
    private Button btnCommit;

    private SearchListAdapt adapt;

    private View view;

    private ChatControl control = ChatControl.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = LayoutInflater.from(this).inflate(R.layout.activity_search, null);
        setContentView(view);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        materialEditText = (MaterialEditText) findViewById(R.id.et_by_name);
        btnCommit = (Button) findViewById(R.id.btn_commit);
        btnCommit.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        adapt = new SearchListAdapt();
        adapt.setOnItemClickListener(this);
        mRecyclerView.setAdapter(adapt);
        //mRecyclerView.addItemDecoration(new SearchListAdapt.DividerGridItemDecoration(this));
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_commit:
                String byName = materialEditText.getText().toString().trim();
                control.searchFriends(this, byName);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                Toast.makeText(SearchActivity.this, byName, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onSearchFriend(List<SearchFriendsAsync.Entity> entities) {
        for (SearchFriendsAsync.Entity entity : entities) {
            Log.e(Tag, "---------------------------------------------------");
            Log.e(Tag, entity.getEmail());
            Log.e(Tag, entity.getJid());
            Log.e(Tag, entity.getName());
            Log.e(Tag, entity.getUserName());
            Log.e(Tag, "---------------------------------------------------");
        }
        adapt.setEntities(entities);
    }

    @Override
    public void onError() {
        Toast.makeText(SearchActivity.this, "Hey baby,take easy,change some key word and take more try~", Toast.LENGTH_SHORT).show();
        adapt.setEntities(null);
    }

    @Override
    public void onItemClick(int Position, SearchFriendsAsync.Entity entity) {
        AddFriendDialog dialog = new AddFriendDialog(this, entity);
        dialog.setOnButtonClick(this);
        dialog.show();
    }

    @Override
    public void onConfirm(SearchFriendsAsync.Entity entity) {
        String message = materialEditText.getText().toString();
        control.startAddFriend(entity.getJid(), message);

    }

    @Override
    public void onCancel() {
        Log.d(Tag, "user cancel");
    }

    /**
     * On Friend Status Change when a friend available/unavailable.
     *
     * @param friends friend collection
     * @param friend  which one changed
     */
    @Override
    public void onFriendsStatusChanged(List<Friend> friends, Friend friend) {

    }


    /**
     * @param presence presence body
     * @param message  message
     * @param jid      which one call you
     */
    @Override
    public void onNewFriendAddNotice(final Presence presence, final String message, final String jid) {
        getHandler().post(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(view, jid + "want to join with you!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChatControl.getInstance().replyNewFriendNotice(presence);
                        ChatControl.getInstance().startFriendObserver(new ChatControl.GetFriendListener() {
                            @Override
                            public void onGetFriends(final List<Friend> friends) {
                                getHandler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //showFriendList(friends);
                                    }
                                });
                            }
                        });
                    }
                }).show();
            }
        });
    }

    /**
     * A friend delete our account
     *
     * @param friends friend collection
     * @param friend  which one delete you from his friend list
     */
    @Override
    public void onFriendDeleteNotice(List<Friend> friends, Friend friend) {

    }
}
