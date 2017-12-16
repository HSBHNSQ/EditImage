package com.liubowang.photoretouch.Effect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.liubowang.photoretouch.R;

/**
 * Created by heshaobo on 2017/7/28.
 */

public class MagnifierView extends View {
    private static final String LOG_TAG = MagnifierView.class.getSimpleName();
    public static final int MAGNFIER_VIEW_SIZE = 260;
    public static final int FACTOR = 2;
    private Bitmap mBitmap;
    private  boolean canDraw = false;
    private int cirlceR;
    private int cirlceColor;
    private MagnifierView(Context context){super(context);};
    public MagnifierView(Context context, ViewGroup parentView){
        super(context);
        setBackgroundColor(Color.parseColor("#00000000"));
        parentView.addView(this);
    }

    /*
    * 开始进行放大的时候 在TouchDown中调用
    * */
    public void onBeginMoving(ViewGroup trackingView, int centerX,int centerY,int cirlceR,int cirlceColor){
        setDrawOption(trackingView,centerX,centerY,cirlceR,cirlceColor);
    }
    /*
    * 进行放大的时候 在TouchMove中调用
    * */
    public void onMoving(ViewGroup trackingView,int centerX,int centerY,int cirlceR,int cirlceColor){
        setDrawOption(trackingView,centerX,centerY,cirlceR,cirlceColor);
    }
    /*
    * 放大结束的时候 在TouchUp中调用
    * */
    public void onEndMoving(){
        cleanCanvas();
    }


    private void setDrawOption(ViewGroup maginView,int centerX,int centerY,int cirlceR,int cirlceColor){
        Bitmap bitmap = getViewBitmap(maginView,centerX,centerY,
                MagnifierView.MAGNFIER_VIEW_SIZE/MagnifierView.FACTOR);
        if (bitmap == null) return;
        this.cirlceR = cirlceR;
        this.cirlceColor = cirlceColor;
        mBitmap = bitmap;
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
            Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            bgPaint.setStyle(Paint.Style.FILL);
            bgPaint.setColor(Color.parseColor("#FFFFFF"));
            canvas.drawCircle(MAGNFIER_VIEW_SIZE/2,MAGNFIER_VIEW_SIZE/2,MAGNFIER_VIEW_SIZE/2,bgPaint);

            Matrix matrix = new Matrix();
            matrix.setScale(FACTOR, FACTOR);
            BitmapShader bitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            bitmapShader.setLocalMatrix(matrix);
            Paint bmpPaint = new Paint();
            bmpPaint.setAntiAlias(true);
            bmpPaint.setShader(bitmapShader);
            canvas.drawRoundRect(new RectF(0, 0, MAGNFIER_VIEW_SIZE, MAGNFIER_VIEW_SIZE), MAGNFIER_VIEW_SIZE/2, MAGNFIER_VIEW_SIZE/2, bmpPaint);

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(cirlceColor);
            paint.setAlpha(125);
            canvas.drawCircle(MAGNFIER_VIEW_SIZE/2,MAGNFIER_VIEW_SIZE/2,cirlceR,paint);

            Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(2);
            borderPaint.setColor(Color.parseColor("#c0c0c0"));
            canvas.drawCircle(MAGNFIER_VIEW_SIZE/2,MAGNFIER_VIEW_SIZE/2,MAGNFIER_VIEW_SIZE/2,borderPaint);

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

    public Bitmap getViewBitmap(View v,int centerX ,int centerY,int size) {
        v.clearFocus();
        v.setPressed(false);
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
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
        int X = centerX - size/2;
        int Y = centerY - size/2;
        if (X < 0) X = 0;
        if (Y < 0) Y = 0;
        if (X + size > cacheBitmap.getWidth()){
            X = cacheBitmap.getWidth() - size;
        }
        if (Y + size > cacheBitmap.getHeight()){
            Y = cacheBitmap.getHeight() - size;
        }
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap,X,Y,size,size);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }
}
