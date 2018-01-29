package com.liubowang.photoretouch.Style;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.liubowang.photoretouch.R;

/**
 * Created by heshaobo on 2018/1/20.
 */

public class CircleView extends View {

    private int circleColor;
    public CircleView(Context context) {
        super(context);
        init(context);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initAttr(context,attrs);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initAttr(context,attrs);
    }

    private void init(Context context){
        circleColor = Color.BLACK;
    }

    private void initAttr(Context context,AttributeSet attributeSet){
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.CircleView,0,0);
        circleColor = typedArray.getColor(R.styleable.CircleView_circle_color,Color.BLACK);
        typedArray.recycle();
    }


    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int rx = getWidth() / 2;
        int ry = getHeight() / 2;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(circleColor);
        RectF rectF = new RectF(0,0,getWidth(),getHeight());
        canvas.drawRoundRect(rectF,rx,ry,paint);
    }
}
