package com.lu.xmpp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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


public class MainActivity extends BaseActivity {

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
        Log.e(Tag, "OnCreate");
        mChatControl = ChatControl.getInstance();
        setContentView(R.layout.activity_main);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.TRANSPARENT);
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initUI();
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_person_add:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.menu_item_group_add:
                Toast.makeText(this, "hello world", Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
