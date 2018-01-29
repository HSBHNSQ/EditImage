package com.liubowang.photoretouch.Style;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.liubowang.photoretouch.Utils.DisplayUtil;

/**
 * Created by heshaobo on 2018/1/22.
 */

public class SquareView8 extends View {
    public SquareView8(Context context) {
        super(context);
    }

    public SquareView8(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareView8(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int squareColor = Color.BLACK;

    public void setSquareColor(int squareColor) {
        this.squareColor = squareColor;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int lineWidth = DisplayUtil.dpTopx(getContext(),10);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(squareColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        canvas.drawRect(lineWidth/2,lineWidth/2,getWidth() - lineWidth,getHeight() - lineWidth,paint);
    }
}
