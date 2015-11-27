package com.lu.xmpp.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.lu.xmpp.R;
import com.lu.xmpp.activity.base.BaseActivity;
import com.lu.xmpp.adapter.SearchListAdapt;
import com.lu.xmpp.chat.ChatControl;
import com.lu.xmpp.chat.async.SearchFriendsAsync;
import com.lu.xmpp.utils.Log;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

public class SearchActivity extends BaseActivity implements View.OnClickListener, SearchFriendsAsync.SearchFriendCallBack {
    private static final String Tag = "SearchActivity";
    private RecyclerView mRecyclerView;
    private MaterialEditText materialEditText;
    private Button btnCommit;

    private SearchListAdapt adapt;

    private ChatControl control = ChatControl.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
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
        Log.e(Tag, "onError");
    }
}
