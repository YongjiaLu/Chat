package com.lu.xmpp.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lu.xmpp.R;

/**
 * Created by xuyu on 2015/11/26.
 */
public class SearchListCardView extends RecyclerView.ViewHolder {
    private TextView textViewUsername;
    private TextView textViewUserId;

    public SearchListCardView(View itemView) {
        super(itemView);
        textViewUsername = (TextView) itemView.findViewById(R.id.tv_username);
        textViewUserId = (TextView) itemView.findViewById(R.id.tv_user_id);
    }

    public void setUserId(String userId) {
        textViewUserId.setText(userId);
    }

    public void setUserName(String userName) {
        textViewUsername.setText(userName);
    }
}
