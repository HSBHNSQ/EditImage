package com.liubowang.photoretouch.Style;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.liubowang.photoretouch.Utils.DisplayUtil;

/**
 * Created by heshaobo on 2018/1/22.
 */

public class LeftView7 extends View{
    public LeftView7(Context context) {
        super(context);
    }

    public LeftView7(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LeftView7(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int circleColor = Color.BLACK;

    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        RectF rectF = new RectF(-getWidth(),0,getWidth(),getHeight());
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(circleColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawArc(rectF,-90,180,true,paint);
    }
}
