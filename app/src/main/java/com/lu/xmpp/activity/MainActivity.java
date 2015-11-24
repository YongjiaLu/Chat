package com.lu.xmpp.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.lu.xmpp.R;
import com.lu.xmpp.activity.base.BaseActivity;
import com.lu.xmpp.activity.base.BaseFragment;
import com.lu.xmpp.activity.fragment.RosterFragment;
import com.lu.xmpp.adapter.MainActivityFragmentAdapt;
import com.lu.xmpp.chat.ChatControl;
import com.lu.xmpp.modle.Friend;
import com.lu.xmpp.utils.Log;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity implements ChatControl.GetFriendListener {

    private static String Tag = "MainActivity";


    private ChatControl mChatControl = ChatControl.getInstance();

    private List<BaseFragment> fragments = new ArrayList<>();
    private MainActivityFragmentAdapt adapt;
    private RosterFragment rosterFragment;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    private TabLayout tabLayout;
    private ViewPager viewpager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mChatControl = ChatControl.getInstance();
        setContentView(R.layout.activity_main);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.TRANSPARENT);
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initUI();
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } catch (NullPointerException e) {
            Log.e(Tag, "Hello World ~");
        }
    }

    private void initUI() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewpager = (ViewPager) findViewById(R.id.view_pager);

        rosterFragment = new RosterFragment(this);
        fragments.add(rosterFragment);

        adapt = new MainActivityFragmentAdapt(getSupportFragmentManager(), fragments);
        viewpager.setAdapter(adapt);
        tabLayout.setTabsFromPagerAdapter(adapt);
        tabLayout.setupWithViewPager(viewpager);
        mChatControl.getFriends(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onGetFriends(final List<Friend> data) {

        Log.e(Tag, String.valueOf(data.size()));

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (null != rosterFragment)
                    rosterFragment.showFriendList(data);
            }
        });
    }
}
