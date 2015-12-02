package com.lu.xmpp.utils;

/**
 * Created by xuyu on 2015/11/30.
 */
public class StringUtil {

    public static String getUserIdFromStanza(String string) {

        if (string.contains("/")) {
            return string.split("/")[0];
        }
        return string;
    }

}
