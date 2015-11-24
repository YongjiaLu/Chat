package com.lu.xmpp.activity.fragment;

import android.support.v4.app.Fragment;

import com.lu.xmpp.activity.base.BaseActivity;
import com.lu.xmpp.activity.base.ChatHandler;

/**
 * Just return a Title<br/>
 * extends # android.support.v4.app.Fragment;
 */
public abstract class BaseFragment extends Fragment {
    private BaseActivity activity;

    public BaseFragment(BaseActivity activity) {
        this.activity = activity;
    }

    public ChatHandler getHandler() {
        return activity.getHandler();
    }


    public abstract String getTitle();
}
