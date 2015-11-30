package com.lu.xmpp.adapter.viewholder;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lu.xmpp.R;

/**
 * Created by xuyu on 2015/11/26.
 */
public class SearchListCardView extends RecyclerView.ViewHolder {
    private TextView textViewUsername;
    private TextView textViewUserId;
    private ImageView ivAvatar;

    private View itemView;

    private int position = -1;

    public SearchListCardView(View itemView) {
        super(itemView);
        this.itemView = itemView;
        textViewUsername = (TextView) itemView.findViewById(R.id.tv_username);
        textViewUserId = (TextView) itemView.findViewById(R.id.tv_user_id);
        ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
    }

    public void setUserId(String userId) {
        textViewUserId.setText(userId);
    }

    public void setUserName(String userName) {
        textViewUsername.setText(userName);
    }

    public void setAvater(Bitmap bitmap) {
        ivAvatar.setImageBitmap(bitmap);
    }

    public final void setPosition(int position) {
        this.position = position;
    }

    public final int readPosition() {
        return position;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        if (null != listener)
            itemView.setOnClickListener(listener);
    }
}
