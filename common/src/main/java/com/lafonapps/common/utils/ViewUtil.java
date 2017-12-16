package com.lafonapps.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;

import com.lafonapps.common.Common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by chenjie on 2017/8/17.
 */

public class ViewUtil {

    private static final String TAG = ViewUtil.class.getCanonicalName();

    /**
     * 像素单位转点单位。
     *
     * @param px 像素尺寸 pixels
     * @return 转换成dpi的尺寸
     */
    public static float px2dp(float px) {
        DisplayMetrics displayMetrics = Common.getSharedApplication().getResources().getDisplayMetrics();
        float dp = px / displayMetrics.density;
        return dp;
    }

    /**
     * 像素单位转点单位。
     *
     * @param px 像素尺寸 pixels
     * @return 转换成dpi的尺寸
     */
    public static int px2dp(int px) {
        DisplayMetrics displayMetrics = Common.getSharedApplication().getResources().getDisplayMetrics();
        float dp = px / displayMetrics.density;
        return (int) dp;
    }

    /**
     * 点单位转像素单位。
     *
     * @param dp 点尺寸 dpi
     * @return 转换成px的尺寸
     */
    public static float dp2px(float dp) {
        DisplayMetrics displayMetrics = Common.getSharedApplication().getResources().getDisplayMetrics();
        float px = dp * displayMetrics.density;
        return px;
    }

    /**
     * 点单位转像素单位。
     *
     * @param dp 点尺寸 dpi
     * @return 转换成px的尺寸
     */
    public static int dp2px(int dp) {
        DisplayMetrics displayMetrics = Common.getSharedApplication().getResources().getDisplayMetrics();
        float px = dp * displayMetrics.density;
        return (int)px;
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取当前设备的宽度，单位：px。
     *
     * @return 前设备的宽度
     */
    public static int getDeviceWidthInPx() {
        Display display = ((WindowManager) Common.getSharedApplication().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    /**
     * 获取当前设备的高度，单位：px。
     *
     * @return 前设备的高度
     */
    public static int getDeviceHeightInPx() {
        Display display = ((WindowManager) Common.getSharedApplication().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    /**
     * 获取当前设备的宽度，单位：dp。
     *
     * @return 前设备的宽度
     */
    public static int getDeviceWidthInDP() {
        return px2dp(getDeviceWidthInPx());
    }

    /**
     * 获取当前设备的高度，单位：dp。
     *
     * @return 前设备的高度
     */
    public static int getDeviceHeightInDP() {
        return px2dp(getDeviceHeightInPx());
    }

    public static void addView(ViewGroup container, View view) {

        ViewParent parentView = view.getParent();
        if (container.equals(parentView)) {
            Log.d(TAG, "no need add nativeView");
        } else {
            if (parentView instanceof ViewGroup) {
                ((ViewGroup) parentView).removeView(view);
            }
            container.addView(view);
            Log.d(TAG, "add nativeView");
        }
    }

    public static void addView(ViewGroup container, View view, int index) {
        ViewParent parentView = view.getParent();
        if (container.equals(parentView)) {
            Log.d(TAG, "no need add nativeView");
        } else {
            if (parentView instanceof ViewGroup) {
                ((ViewGroup) parentView).removeView(view);
            }
            container.addView(view, index);
            Log.d(TAG, "add nativeView");
        }
    }

    public static void addView(ViewGroup container, View view, int width, int height) {
        ViewParent parentView = view.getParent();
        if (container.equals(parentView)) {
            Log.d(TAG, "no need add nativeView");
        } else {
            if (parentView instanceof ViewGroup) {
                ((ViewGroup) parentView).removeView(view);
            }
            container.addView(view, width, height);
            Log.d(TAG, "add nativeView");
        }
    }

    public static void addView(ViewGroup container, View view, ViewGroup.LayoutParams params) {
        ViewParent parentView = view.getParent();
        if (container.equals(parentView)) {
            Log.d(TAG, "no need add nativeView");
        } else {
            if (parentView instanceof ViewGroup) {
                ((ViewGroup) parentView).removeView(view);
            }
            container.addView(view, params);
            Log.d(TAG, "add nativeView");
        }
    }

    public static void addView(ViewGroup container, View view, int index, ViewGroup.LayoutParams params) {
        ViewParent parentView = view.getParent();
        if (container.equals(parentView)) {
            Log.d(TAG, "no need add nativeView");
        } else {
            if (parentView instanceof ViewGroup) {
                ((ViewGroup) parentView).removeView(view);
            }
            container.addView(view, index, params);
            Log.d(TAG, "add nativeView");
        }
    }

    public static Bitmap readBitMap(Context context, int resId){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        Bitmap bitmap = BitmapFactory.decodeStream(is,null,opt);

        return bitmap;
    }

    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, float ratio) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }


    /**
     * Compress image by size, this will modify image width/height.
     * Used to get thumbnail
     *
     * @param image
     * @param pixelW target pixel of width
     * @param pixelH target pixel of height
     * @return
     */
    public static Bitmap ratio(Bitmap image, float pixelW, float pixelH) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        if( os.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            os.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, os);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        is = new ByteArrayInputStream(os.toByteArray());
        bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        //压缩好比例大小后再进行质量压缩
//      return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

}
