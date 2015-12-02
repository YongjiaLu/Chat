package com.lu.xmpp.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lu.xmpp.R;
import com.lu.xmpp.adapter.viewholder.ChatListCardVew;
import com.lu.xmpp.bean.ChatLog;
import com.lu.xmpp.chat.ChatControl;

import java.util.List;

/**
 * Created by xuyu on 2015/12/2.
 */
public class ChatListAdapt extends RecyclerView.Adapter<ChatListCardVew> {


    private Bitmap friendAvatar;
    private Bitmap userAvatar;

    public static final int TypeMessageComing = 1;
    public static final int TypeMessageGoing = 2;

    private List<ChatLog> logs;


    public ChatListAdapt(List<ChatLog> logs, Bitmap friendAvatar, Bitmap userAvatar) {
        this.logs = logs;
        this.friendAvatar = friendAvatar;
        this.userAvatar = userAvatar;
    }


    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p/>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p/>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @Override
    public ChatListCardVew onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        if (viewType == TypeMessageComing)

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_chat_item_coming, null);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_chat_item_going, null);

        ChatListCardVew cardVew = new ChatListCardVew(view);

        return cardVew;
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p/>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p/>
     * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
     * handle effcient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ChatListCardVew holder, int position) {
        ChatLog log = logs.get(position);
        holder.setAvatar(getItemViewType(position) == TypeMessageComing ? friendAvatar : userAvatar);
        holder.setBody(log.getBody());
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return logs == null ? 0 : logs.size();
    }


    @Override
    public int getItemViewType(int position) {
        return logs.get(position).getFrom().equals(ChatControl.getInstance().getUserJid()) ? TypeMessageGoing : TypeMessageComing;
    }

    public void appendMessage(ChatLog log) {
        logs.add(log);
        notifyItemInserted(getLastPosition());
    }

    public int getLastPosition() {
        return logs.size() - 1;
    }
}
