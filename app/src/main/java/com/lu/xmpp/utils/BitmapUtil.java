package com.lu.xmpp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

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

    /**
     * 直接操作像素点的argb值，来改变图像的效果(灰色照片)
     *
     * @param bitmap
     * @return
     */

    public static Bitmap handleImagePixelsGrayPhoto(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap faceIconGreyBitmap = Bitmap
                .createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(faceIconGreyBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
                colorMatrix);
        paint.setColorFilter(colorMatrixFilter);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return faceIconGreyBitmap;
    }

}
