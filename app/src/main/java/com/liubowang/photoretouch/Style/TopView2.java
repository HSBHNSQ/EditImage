package com.liubowang.photoretouch.Style;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.liubowang.photoretouch.Utils.DisplayUtil;

/**
 * Created by heshaobo on 2018/1/22.
 */

public class TopView2 extends View{
    public TopView2(Context context) {
        super(context);
    }

    public TopView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TopView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int lineColor = Color.BLACK;

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int lineWidth = DisplayUtil.dpTopx(getContext(),2);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(lineColor);
        paint.setStrokeWidth(lineWidth);
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawLine(lineWidth,lineWidth,lineWidth,getHeight() - lineWidth,paint);
        canvas.drawLine(lineWidth,lineWidth,getWidth()-lineWidth,lineWidth,paint);
        canvas.drawLine(getWidth()-lineWidth,lineWidth,getWidth()-lineWidth,getHeight() - lineWidth,paint);

    }
}
