package com.lu.xmpp.adapter.viewholder;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lu.xmpp.R;

/**
 * Created by xuyu on 2015/12/2.
 */
public class ChatListCardVew extends RecyclerView.ViewHolder {

    private ImageView ivAvatar;
    private TextView tvBody;

    public ChatListCardVew(View itemView) {
        super(itemView);
        ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
        tvBody = (TextView) itemView.findViewById(R.id.tv_body);
    }

    public void setAvatar(Bitmap bitmap) {
        ivAvatar.setImageBitmap(bitmap);
    }

    public void setBody(String body) {
        tvBody.setText(body);
    }

}
