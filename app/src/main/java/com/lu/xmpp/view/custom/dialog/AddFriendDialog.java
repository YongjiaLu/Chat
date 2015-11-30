package com.lu.xmpp.view.custom.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lu.xmpp.R;
import com.lu.xmpp.chat.async.SearchFriendsAsync;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Dialog ,use in add friend
 */
public class AddFriendDialog extends Dialog implements View.OnClickListener {
    private final static String Tag = "AddFriendDialog";

    private MaterialEditText mEditText;
    private Button btnCancel;
    private Button btnConfirm;

    private TextView tvUsername;
    private TextView tvUserId;
    private ImageView ivAvatar;

    private OnButtonClick listener;

    private SearchFriendsAsync.Entity entity;


    /**
     * Creates a dialog window that uses the default dialog theme.
     * <p/>
     * The supplied {@code context} is used to obtain the window manager and
     * base theme used to present the dialog.
     *
     * @param context the context in which the dialog should run
     * @see android.R.styleable#Theme_dialogTheme
     */
    public AddFriendDialog(Context context, SearchFriendsAsync.Entity entity) {
        super(context);
        this.entity = entity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_friend);

        mEditText = (MaterialEditText) findViewById(R.id.et_message);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
        btnConfirm = (Button) findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(this);

        tvUsername = (TextView) findViewById(R.id.tv_username);
        tvUserId = (TextView) findViewById(R.id.tv_user_id);
        tvUsername.setText(entity.getUserName());
        tvUserId.setText(entity.getJid());
        ivAvatar = (ImageView) findViewById(R.id.iv_avatar);
        ivAvatar.setImageBitmap(entity.getAvatar());
    }

     /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (null == listener) return;

        switch (v.getId()) {
            case R.id.btn_cancel:
                listener.onCancel();
                dismiss();
                break;
            case R.id.btn_confirm:
                listener.onConfirm(entity);
                dismiss();
                break;
        }
    }

    public interface OnButtonClick {
        void onConfirm(SearchFriendsAsync.Entity entity);

        void onCancel();
    }

    public void setOnButtonClick(OnButtonClick listener) {
        this.listener = listener;
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
    }
}
