package com.lu.xmpp.modle;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xuyu on 2015/11/17.
 */
public class Friend implements Parcelable {
    private String username;
    private String Jid;
    private Bitmap Avatar;
    private String status;
    private String statusLine;


    public Friend() {

    }

    protected Friend(Parcel in) {
        username = in.readString();
        Jid = in.readString();
        Avatar = in.readParcelable(Bitmap.class.getClassLoader());
        status = in.readString();
        statusLine = in.readString();
    }

    public static final Creator<Friend> CREATOR = new Creator<Friend>() {
        @Override
        public Friend createFromParcel(Parcel in) {
            return new Friend(in);
        }

        @Override
        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(String statusLine) {
        this.statusLine = statusLine;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getJid() {
        return Jid;
    }

    public void setJid(String jid) {
        Jid = jid;
    }

    public Bitmap getAvatar() {
        return Avatar;
    }

    public void setAvatar(Bitmap avatar) {
        Avatar = avatar;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(Jid);
        dest.writeParcelable(Avatar, flags);
        dest.writeString(status);
        dest.writeString(statusLine);
    }
}
