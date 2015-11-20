package com.lu.xmpp.utils;

import com.lu.xmpp.ChatApplication;
import com.lu.xmpp.contacts.ChatContacts;

/**
 * 处于Debug模式下Log可见
 */
public class Log {

    private static boolean debug = ChatApplication.debug == ChatContacts.DebugMode.Debug;

    public static void d(String Tag, String message, Throwable t) {
        if (debug)
            android.util.Log.d(Tag, message, t);
    }

    public static void d(String Tag, String message) {
        if (debug) android.util.Log.d(Tag, message);
    }

    public static void e(String Tag, String message, Throwable t) {
        if (debug)
            android.util.Log.e(Tag, message, t);
    }

    public static void e(String Tag, String message) {
        if (debug) android.util.Log.e(Tag, message);
    }

    public static void v(String Tag, String message, Throwable t) {
        if (debug) android.util.Log.v(Tag, message, t);
    }

    public static void v(String Tag, String message) {
        if (debug) android.util.Log.v(Tag, message);
    }

    public static void i(String Tag, String message, Throwable t) {
        if (debug) android.util.Log.i(Tag, message, t);
    }

    public static void i(String Tag, String message) {
        if (debug) android.util.Log.i(Tag, message);
    }

    public static void w(String Tag, String message, Throwable t) {
        if (debug) android.util.Log.w(Tag, message, t);
    }

    public static void w(String Tag, String message) {
        if (debug) android.util.Log.w(Tag, message);
    }
}
