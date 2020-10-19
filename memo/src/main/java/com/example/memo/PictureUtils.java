package com.example.memo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class PictureUtils {
    public static Bitmap getScaleBitmap(String path, Activity activity){
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);
        return getScaleBitmap(path,point.x,point.y);
    }
    public static Bitmap getScaleBitmap(String path, int destWidth, int destHeight) {
        // 设置选项
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 预加载,获取大小
        BitmapFactory.decodeFile(path, options);
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;
        // 计算采样率
        options.inSampleSize = getSampleSize(srcWidth,srcHeight,destWidth,destHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path,options);
    }

    private static int getSampleSize(int srcWidth, int srcHeight, int destWidth, int destHeight) {
        int sampleSize = 1;
        if (srcHeight > srcWidth && srcHeight > destHeight) {
            sampleSize = srcHeight / destHeight;
        } else if (srcWidth > srcHeight && srcWidth > destWidth) {
            sampleSize = srcWidth / destWidth;
        }
        if (sampleSize <= 0) {
            sampleSize = 1;
        }
        return sampleSize;
    }

}
