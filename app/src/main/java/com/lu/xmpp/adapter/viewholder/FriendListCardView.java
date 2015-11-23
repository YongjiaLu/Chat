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

    public FriendListCardView(View itemView) {
        super(itemView);
        tvUserName = (TextView) itemView.findViewById(R.id.tv_username);
        ivAvatar = (ImageView) itemView.findViewById(R.id.image_view);
        tvStatus = (TextView) itemView.findViewById(R.id.tv_online_status);
    }

    private void changeToTitleMode() {
        ivAvatar.setVisibility(View.GONE);
        tvStatus.setVisibility(View.GONE);
    }

    private void changeToFriendCardMode() {
        ivAvatar.setVisibility(View.VISIBLE);
        tvStatus.setVisibility(View.VISIBLE);
    }

    public void setTitle(String string) {
        changeToTitleMode();
        tvUserName.setText(string);
    }

    public void setFriendCard(String name, Bitmap avatar, String status) {
        changeToFriendCardMode();
        tvUserName.setText(name);
        ivAvatar.setImageBitmap(avatar);
        tvStatus.setText(status);
    }


}
