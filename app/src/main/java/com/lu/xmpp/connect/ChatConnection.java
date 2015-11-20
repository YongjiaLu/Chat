package com.lu.xmpp.connect;

import android.content.Context;
import android.os.Build;

import com.lu.xmpp.contacts.ChatContacts;
import com.lu.xmpp.utils.Log;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.File;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import de.duenndns.ssl.MemorizingTrustManager;

/**
 * Created by Jack on 2015/11/5.
 */
public class ChatConnection extends XMPPTCPConnection {

    private final static String Tag = "Connection";

    private final static int port = 5222;


    public ChatConnection(boolean debug, Context context) {
        super(getConfig(debug, context));
    }

    private static XMPPTCPConnectionConfiguration getConfig(boolean debug, Context context) {

        Log.d(Tag, "init ChatConnection");

        XMPPTCPConnectionConfiguration.Builder configuration = XMPPTCPConnectionConfiguration.builder();

        configuration.setHost(ChatContacts.getDomain());//设置主机的ip或者域名

        configuration.setPort(port);//端口

        configuration.setDebuggerEnabled(debug);//Debug 模式

        configuration.setServiceName(ChatContacts.getHostName());

        configuration.setSecurityMode(ConnectionConfiguration.SecurityMode.required);//使用SSL连接

        configuration.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return hostname.equals(ChatContacts.getDomain()) || hostname.equals(ChatContacts.getHostName());
            }
        });//设置信任的主机

        SASLAuthentication.unregisterSASLMechanism("EXTERNAL");

        SASLAuthentication.unregisterSASLMechanism("KERBEROS_V4");//Android不支持这种校验方式

        SASLAuthentication.unregisterSASLMechanism("GSSAPI"); //同上

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            //Android4.0以上指定CA Type 用于服务器SSL连接验证
            configuration.setKeystoreType("AndroidCAStore");
            configuration.setKeystorePath(null);
        } else {
            //Android4.0以下指定CA Type 用于服务器SSL连接验证
            configuration.setKeystoreType("BKS");
            String path = System.getProperty("javax.net.ssl.trustStore");
            if (path == null)
                path = System.getProperty("java.home") + File.separator + "etc" + File.separator + "security" + File.separator + "cacerts.bks";
            configuration.setKeystorePath(path);
        }

        try {
            //设置一个SSLContext
            SSLContext sc = SSLContext.getInstance("TLS");
            //不懂
            SecureRandom mSecureRandom = new java.security.SecureRandom();
            /**
             * MemorizingTrustManager源自一个开源项目，能让用户手动确认SSL证书验证具体查询MemorizingActivity
             */
            sc.init(null, new TrustManager[]{new MemorizingTrustManager(context)}, mSecureRandom);
            //向configuration填充SSLContext
            configuration.setCustomSSLContext(sc);

        } catch (java.security.GeneralSecurityException e) {
            Log.d(Tag, "ChatConnection init error:" + e.toString());
        }
        configuration.setSendPresence(true);

        Log.d(Tag, "ChatConnection init finish");
        return configuration.build();
    }
}
