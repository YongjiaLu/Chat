package com.lu.xmpp.adapter.viewholder;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lu.xmpp.R;

/**
 * Created by xuyu on 2015/11/23.
 */
public class FriendListCardView extends RecyclerView.ViewHolder {


    private TextView tvUserName;
    private ImageView ivAvatar;
    private TextView tvStatus;

    private TextView tvGroupName;

    public FriendListCardView(View itemView) {
        super(itemView);
        tvUserName = (TextView) itemView.findViewById(R.id.tv_username);
        ivAvatar = (ImageView) itemView.findViewById(R.id.image_view);
        tvStatus = (TextView) itemView.findViewById(R.id.tv_online_status);
        tvGroupName = (TextView) itemView.findViewById(R.id.tv_groupName);
    }

    private void changeToTitleMode() {
        ivAvatar.setVisibility(View.GONE);
        tvStatus.setVisibility(View.GONE);
        tvUserName.setVisibility(View.GONE);

        tvGroupName.setVisibility(View.VISIBLE);
    }

    private void changeToFriendCardMode() {
        ivAvatar.setVisibility(View.VISIBLE);
        tvStatus.setVisibility(View.VISIBLE);
        tvUserName.setVisibility(View.VISIBLE);

        tvGroupName.setVisibility(View.GONE);
    }

    public void setGroupName(String string) {
        changeToTitleMode();
        tvGroupName.setText(string);
    }

    public void setFriendCard(String name, Bitmap avatar, String status) {
        changeToFriendCardMode();
        tvUserName.setText(name);
        ivAvatar.setImageBitmap(avatar);
        tvStatus.setText(status);
    }
}
