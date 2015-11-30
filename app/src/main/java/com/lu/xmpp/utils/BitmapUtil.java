package com.lu.xmpp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.lu.xmpp.R;

import java.io.ByteArrayInputStream;

/**
 * Created by xuyu on 2015/11/17.
 */
public class BitmapUtil {

    public static Bitmap parseByteArrayToBitmap(byte[] data, Context context) {
        Bitmap bitmap;
        if (null != data) {
            try {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                return bitmap;
            } catch (Exception e) {
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
            }
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        }

        return bitmap;
    }
}
