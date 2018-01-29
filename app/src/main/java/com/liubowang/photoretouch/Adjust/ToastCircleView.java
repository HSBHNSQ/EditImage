package com.liubowang.photoretouch.Adjust;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by heshaobo on 2018/1/16.
 */

public class ToastCircleView extends View {


    private int radius = 80;

    public ToastCircleView(Context context) {
        super(context);
        init(context);
    }

    public ToastCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initAttr(context ,attrs);
    }

    public ToastCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initAttr(context ,attrs);
    }

    private void init(Context context){

    }

    private void initAttr(Context context, AttributeSet attributeSet){

    }


    public void setRadius(int radius) {
        this.radius = radius;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectPaint.setColor(Color.parseColor("#88000000"));
        rectPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        RectF rectF = new RectF(0,0,canvas.getWidth(),canvas.getHeight());
        canvas.drawRoundRect(rectF,15,15,rectPaint);
        Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.parseColor("#FFFFFF"));
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(2);
        float centerX = canvas.getWidth() / 2;
        float centerY = canvas.getHeight() / 2;
        canvas.drawCircle(centerX,centerY,radius,circlePaint);
    }
}
