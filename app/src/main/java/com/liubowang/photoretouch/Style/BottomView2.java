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

public class BottomView2 extends View {
    public BottomView2(Context context) {
        super(context);
    }

    public BottomView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomView2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        int w = getWidth();
        int h = getHeight();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(lineColor);
        paint.setStrokeWidth(lineWidth);
        paint.setStyle(Paint.Style.STROKE);

        int dy = h / 10;
        canvas.drawLine(lineWidth,lineWidth ,lineWidth,dy,paint);
        canvas.drawLine(w -lineWidth,lineWidth,w - lineWidth,dy,paint);

        RectF rectF = new RectF(lineWidth,dy - h /2,w - lineWidth ,dy + h / 2);
        canvas.drawArc(rectF,0,180,false,paint);
    }
}
