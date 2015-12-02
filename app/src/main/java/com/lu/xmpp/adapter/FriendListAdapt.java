package com.lu.xmpp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lu.xmpp.R;
import com.lu.xmpp.adapter.viewholder.FriendListCardView;
import com.lu.xmpp.modle.Friend;

import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xuyu on 2015/11/18.
 */
public class FriendListAdapt extends RecyclerView.Adapter<FriendListCardView> {

    public static final int TypeGroupName = 1;
    public static final int TypeFriend = 2;

    private OnItemClickListener listener;
    private List friends;

    public FriendListAdapt(List<Friend> friends) {
        this.friends = handleData(friends);
    }

    private List<Friend> handleData(List<Friend> friends) {
        Map<String, List<Friend>> map = new HashMap<>();
        for (Friend friend : friends) {
            if (!map.containsKey(friend.getGroupName()))
                map.put(friend.getGroupName(), new ArrayList<Friend>());
            map.get(friend.getGroupName()).add(friend);
        }
        Set<Map.Entry<String, List<Friend>>> entrySet = map.entrySet();
        List list = new ArrayList();
        for (Map.Entry entry : entrySet) {
            list.add(entry.getKey().toString());
            List<Friend> group = (List<Friend>) entry.getValue();
            for (Friend friend : group) {
                list.add(friend);
            }
        }
        return list;
    }

    @Override
    public FriendListCardView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_roster, parent, false);
        FriendListCardView viewHolder = new FriendListCardView(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    @Override
    public int getItemViewType(int position) {
        String string = friends.get(position).getClass().getSimpleName();
        return string.equals(Friend.class.getSimpleName()) ? TypeFriend : TypeGroupName;
    }

    @Override
    public void onBindViewHolder(FriendListCardView holder, final int position) {
        int type = getItemViewType(position);
        if (type == TypeFriend) {
            Friend friend = (Friend) friends.get(position);
            holder.setFriendCard(friend.getUsername(), friend.getAvatar(), friend.getStatus().equals(Presence.Type.available.toString()) ? "online" : "offline");
            if (null != listener)
                holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(position, (Friend) friends.get(position));
                    }
                });
        } else {
            String string = friends.get(position).toString();
            holder.setGroupName(string);
            if (null != listener) {
                holder.removeOnclickListener();
            }
        }

    }

    public interface OnItemClickListener {
        void onItemClick(int Position, Friend friend);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}

