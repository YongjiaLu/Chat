package com.lu.xmpp.bean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

import java.io.Serializable;

/**
 * Entity mapped to table "CHAT_LOG".
 */
public class ChatLog implements Serializable{

    /** Not-null value. */
    private String from;
    /** Not-null value. */
    private String to;
    private java.util.Date time;
    private Boolean isRead;
    private String body;

    public ChatLog() {
    }

    public ChatLog(String from, String to, java.util.Date time, Boolean isRead, String body) {
        this.from = from;
        this.to = to;
        this.time = time;
        this.isRead = isRead;
        this.body = body;
    }

    /** Not-null value. */
    public String getFrom() {
        return from;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setFrom(String from) {
        this.from = from;
    }

    /** Not-null value. */
    public String getTo() {
        return to;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setTo(String to) {
        this.to = to;
    }

    public java.util.Date getTime() {
        return time;
    }

    public void setTime(java.util.Date time) {
        this.time = time;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}