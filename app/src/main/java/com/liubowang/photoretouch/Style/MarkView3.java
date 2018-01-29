package com.liubowang.photoretouch.Style;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by heshaobo on 2018/1/22.
 */

public class MarkView3 extends View {
    public MarkView3(Context context) {
        super(context);
    }

    public MarkView3(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MarkView3(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int markColor = Color.GREEN;

    public void setMarkColor(int markColor) {
        this.markColor = markColor;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path markPath = new Path();
        markPath.moveTo(0,0);
        markPath.lineTo(0,getHeight());
        markPath.lineTo(getWidth()/2,getHeight()/4 * 3);
        markPath.lineTo(getWidth(),getHeight());
        markPath.lineTo(getWidth(),0);
        markPath.lineTo(0,0);
        markPath.close();


        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(markColor);

        canvas.drawPath(markPath,paint);
    }
}
