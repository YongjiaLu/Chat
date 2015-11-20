package com.lu.xmpp.contacts;

import android.os.Debug;

import com.lu.xmpp.ChatApplication;

/**
 * 获取值,编译时注意修改Application的Debug值
 */
public class ChatContacts {

    /**
     * 获取当前DebugMode下的对应的ip
     *
     * @return
     */
    public static String getDomain() {
        //TODO 正式开发时 需要指定
        String Default = "hello";
        switch (ChatApplication.debug) {
            case Debug:
                //返回测试用的host ip
                return "192.168.1.171";
            case Release:
                return Default;

            case Other:
                return Default;
        }
        return Default;
    }

    /**
     * 获取服务名
     *
     * @return
     */
    public static String getHostName() {
        //TODO 正式开发时 需要指定
        String Default = "hello";

        switch (ChatApplication.debug) {
            case Debug:
                //返回测试用的host name
                return "luof.server.com";
            case Release:
                return Default;

            case Other:
                return Default;
        }
        return Default;

    }

    /**
     * Debug模式
     */
    public enum DebugMode {
        Debug, Release, Other
    }

    public static boolean isDebug() {
        return ChatApplication.debug == DebugMode.Debug;
    }
}
