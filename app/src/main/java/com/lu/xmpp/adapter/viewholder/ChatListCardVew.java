package com.lu.xmpp.adapter.viewholder;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lu.xmpp.R;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by xuyu on 2015/12/2.
 */
public class ChatListCardVew extends RecyclerView.ViewHolder {

    private ImageView ivAvatar;
    private TextView tvBody;
    private TextView tvUserName;
    private TextView tvDate;

    public ChatListCardVew(View itemView) {
        super(itemView);
        ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
        tvBody = (TextView) itemView.findViewById(R.id.tv_body);
        tvUserName = (TextView) itemView.findViewById(R.id.tv_username);
        tvDate = (TextView) itemView.findViewById(R.id.tv_date);
    }

    public void setAvatar(Bitmap bitmap) {
        ivAvatar.setImageBitmap(bitmap);
    }

    public void setBody(String body) {
        tvBody.setText(body);
    }

    public void setUserName(String name) {
        tvUserName.setText(name);
    }


    private SimpleDateFormat sfg = new SimpleDateFormat("MM月dd日 HH:mm");

    public void setDate(Date date) {
        tvDate.setText(sfg.format(date));
    }

}
