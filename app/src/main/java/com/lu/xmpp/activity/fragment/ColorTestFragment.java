package com.lu.xmpp.activity.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lu.xmpp.R;
import com.lu.xmpp.activity.base.BaseActivity;

/**
 * Created by xuyu on 2015/11/19.
 */
public class ColorTestFragment extends BaseFragment {

    private int color;


    public ColorTestFragment(int color, BaseActivity activity) {
        super(activity);
        this.color = color;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_roster, container, false);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.nested_scroll_view);
        layout.setBackgroundColor(color);
        return view;
    }

    @Override
    public String getTitle() {
        return String.valueOf(color);
    }
}
