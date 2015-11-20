package com.lu.xmpp.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lu.xmpp.R;
import com.lu.xmpp.modle.Friend;

import java.util.List;

/**
 * Created by xuyu on 2015/11/18.
 */
public class FriendListAdapt extends RecyclerView.Adapter<MyViewHolder> {

    private List<Friend> friends;

    public FriendListAdapt(List<Friend> friends) {
        this.friends = friends;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_roster, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }


    @Override
    public int getItemCount() {
        return friends.size();
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Friend friend = friends.get(position);
        holder.setTvUserName(friend.getUsername());
        holder.setAvator(friend.getAvatar());
    }

}

class MyViewHolder extends RecyclerView.ViewHolder {
    private TextView tvUserName;
    private ImageView ivAvator;


    public MyViewHolder(View itemView) {
        super(itemView);
        tvUserName = (TextView) itemView.findViewById(R.id.tv_username);
        ivAvator = (ImageView) itemView.findViewById(R.id.image_view);
    }

    public void setTvUserName(String name) {
        tvUserName.setText(name);
    }

    public void setAvator(Bitmap bitmap) {
        ivAvator.setImageBitmap(bitmap);
    }
}
