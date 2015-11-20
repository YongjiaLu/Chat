package com.lu.xmpp.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lu.xmpp.activity.fragment.BaseFragment;

import java.util.List;

/**
 * Created by xuyu on 2015/11/17.
 */
public class MainActivityFragmentAdapt extends FragmentPagerAdapter {

    private List<BaseFragment> fragments;

    public MainActivityFragmentAdapt(FragmentManager fm, List<BaseFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragments.get(position).getTitle();
    }
}
