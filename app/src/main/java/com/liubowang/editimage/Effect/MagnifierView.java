package com.liubowang.editimage.Effect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.liubowang.editimage.R;

/**
 * Created by heshaobo on 2017/7/28.
 */

public class MagnifierView extends View {
    private static final String LOG_TAG = MagnifierView.class.getSimpleName();
    private static  int MAGNFIER_VIEW_SIZE = 260;
    public static final int FACTOR = 2;
    private Matrix matrix ;
    private Bitmap mBitmap;
    private  boolean canDraw = false;
    private MagnifierView(Context context){super(context);};
    public MagnifierView(Context context, ViewGroup parentView){
        super(context);
        setBackgroundColor(Color.parseColor("#00000000"));
        int width = getScreenMinLength(context);
        MAGNFIER_VIEW_SIZE = dip2px(context,width/4);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                MAGNFIER_VIEW_SIZE,MAGNFIER_VIEW_SIZE);
        parentView.addView(this);
    }

    /*
    * 开始进行放大的时候 在TouchDown中调用
    * */
    public void onBeginMoving(ViewGroup trackingView, int centerX,int centerY){
        setDrawOption(trackingView,centerX,centerY);
    }
    /*
    * 进行放大的时候 在TouchMove中调用
    * */
    public void onMoving(ViewGroup trackingView,int centerX,int centerY){
        setDrawOption(trackingView,centerX,centerY);
    }
    /*
    * 放大结束的时候 在TouchUp中调用
    * */
    public void onEndMoving(){
        cleanCanvas();
    }


    private void setDrawOption(ViewGroup maginView,int centerX,int centerY){
        Bitmap bitmap = getViewBitmap(maginView,centerX,centerY,
                MagnifierView.MAGNFIER_VIEW_SIZE/MagnifierView.FACTOR);
        if (bitmap == null) return;
        mBitmap = bitmap;
        matrix = new Matrix();
        matrix.setScale(FACTOR, FACTOR);
        canDraw = true;
        invalidate();
    }

    private void cleanCanvas(){
        canDraw = false;
        invalidate();
    }
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canDraw){
            canvas.drawBitmap(mBitmap, matrix, null);
            canDraw = false;
        }else {
            Bitmap cleanBmp = BitmapFactory.decodeResource(getResources(), R.drawable.clean_photo);
            canvas.drawBitmap(cleanBmp,0,0,null);
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int getScreenMinLength(Context ctx){
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
//        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (height / density);// 屏幕高度(dp)
        Log.d(LOG_TAG,"width:"+screenWidth + "heigth"+screenHeight );
        return screenHeight>=screenWidth ? screenWidth :screenHeight;

    }

    public  Bitmap getViewBitmap(View v,int centerX ,int centerY,int size) {

        Bitmap bitmap = getViewBitmap(v);
        if (bitmap == null){
            return  null;
        }
        int X = centerX - size/2;
        int Y = centerY - size/2;
        if (X < 0) X = 0;
        if (Y < 0) Y = 0;
        if (X + size > bitmap.getWidth()){
            X = bitmap.getWidth() - size;
        }
        if (Y + size > bitmap.getHeight()){
            Y = bitmap.getHeight() - size;
        }
        bitmap = Bitmap.createBitmap(bitmap,X,Y,size,size);
        return bitmap;
    }
    public Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            Log.e("Folder", "failed getViewBitmap(" + v + ")", new RuntimeException());
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }
}
