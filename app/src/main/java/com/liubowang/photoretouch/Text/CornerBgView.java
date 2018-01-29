package com.liubowang.photoretouch.Text;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by heshaobo on 2018/1/20.
 */

public class CornerBgView extends View {


    public CornerBgView(Context context) {
        super(context);
    }

    public CornerBgView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CornerBgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private int bgColor = Color.WHITE;

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(bgColor);
        paint.setStyle(Paint.Style.FILL);
        RectF rect = new RectF(0,0,canvas.getWidth(),canvas.getHeight());
        canvas.drawRoundRect(rect,10,10,paint);
    }
}
