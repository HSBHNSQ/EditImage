package com.liubowang.photoretouch.Normal;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.liubowang.photoretouch.R;

/**
 * Created by heshaobo on 2017/11/17.
 */

public class ColorItemView extends View {

    private static final String TAG  = ColorItemView.class.getSimpleName();
    private static final int ANIMATION_DURATION = 300;
    private static int RADIUS = 0;
    private static int MIN_LENGTH = 0;
    private static int LINE_WIDTH = 2;
    private ColorModel model;
    private int itemColor = Color.parseColor("#000000");
    private int currentRadiusValue = -1;
    private int currentCornerValue = -1;
    private boolean isChangingCornerValue;
    private boolean isAnimating = false;

    public ColorItemView(Context context) {
        super(context);
        init(context);
    }

    public ColorItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ColorItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ColorItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context){
        setBackgroundColor(getResources().getColor(R.color.colorClean));
    }

    private void setItemColor(int itemColor) {
        this.itemColor = itemColor;
        currentRadiusValue = -1;
        currentCornerValue = -1;
        if (cornerAnimator != null && cornerAnimator.isRunning()){
            cornerAnimator.cancel();
        }
        if (radiusAnimator != null && radiusAnimator.isRunning()){
            radiusAnimator.cancel();
        }
        invalidate();
    }

    private int getItemColor() {
        return itemColor;
    }

    public void setModel(ColorModel model) {
        this.model = model;
        setItemColor(model.color);
        if (model.isSeleced){
            startChangeRadius();
        }
    }

    public ColorModel getModel() {
        return model;
    }
    private void setSelectedItem(boolean selectedItem) {

        if (selectedItem){
            startChangeRadius();
        }else {
            startChangeCorner();
        }
    }

    private ValueAnimator cornerAnimator;
    private void startChangeCorner(){
        isChangingCornerValue = true;
        int startValue = 0;
        int endValue = 0;
        if (model.isSeleced){
            startValue = MIN_LENGTH / 2 ;
        }else {
            endValue = MIN_LENGTH / 2;
        }
        ValueAnimator animatior = ValueAnimator.ofInt(startValue,endValue);
        cornerAnimator = animatior;
        animatior.setDuration(ANIMATION_DURATION);
        animatior.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float v) {
                return v;
            }
        });
        animatior.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentCornerValue = (int) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        animatior.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!model.isSeleced){
                    startChangeRadius();
                }else {
                    isAnimating = false;
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                currentRadiusValue = -1;
                currentCornerValue = -1;
                invalidate();
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatior.start();
    }

    private ValueAnimator radiusAnimator ;
    private void startChangeRadius(){
        isChangingCornerValue = false;
        int startValue ;
        int endValue ;
        if (model.isSeleced){
            startValue = RADIUS;
            endValue = MIN_LENGTH / 2 - LINE_WIDTH * 2;
        }else {
            startValue = MIN_LENGTH / 2 - LINE_WIDTH * 2;
            endValue = RADIUS;
        }
        ValueAnimator animatior = ValueAnimator.ofInt(startValue,endValue);
        radiusAnimator = animatior;
        animatior.setDuration(ANIMATION_DURATION);
        animatior.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float v) {
                return v;
            }
        });
        animatior.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentRadiusValue = (int) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        animatior.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (model.isSeleced){
                    startChangeCorner();
                }else {
                    isAnimating = false;
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                currentRadiusValue = -1;
                currentCornerValue = -1;
                invalidate();
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatior.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        MIN_LENGTH = Math.min(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(MIN_LENGTH, MIN_LENGTH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RADIUS = MIN_LENGTH / 2 / 5 * 3;
        if (isChangingCornerValue && currentCornerValue >= 0){
            Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            rectPaint.setAntiAlias(true);
            rectPaint.setColor(itemColor);
            rectPaint.setStyle(Paint.Style.STROKE);
            rectPaint.setStrokeWidth(LINE_WIDTH);
            int yS =  LINE_WIDTH ;
            int xS =  LINE_WIDTH ;
            RectF rect = new RectF(xS,yS,MIN_LENGTH - xS,MIN_LENGTH- yS);
            canvas.drawRoundRect(rect,currentCornerValue,currentCornerValue,rectPaint);
        }
        else if (!isChangingCornerValue && currentRadiusValue >= 0){
            Paint circlePant = new Paint(Paint.ANTI_ALIAS_FLAG);
            circlePant.setColor(itemColor);
            circlePant.setStyle(Paint.Style.STROKE);
            circlePant.setStrokeWidth(LINE_WIDTH);
            circlePant.setAntiAlias(true);
            canvas.drawCircle(MIN_LENGTH/2,MIN_LENGTH/2,currentRadiusValue,circlePant);
        }
        Paint centerPant = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPant.setColor(itemColor);
        centerPant.setAntiAlias(true);
        centerPant.setStrokeWidth(1);
        canvas.drawCircle(MIN_LENGTH/2,MIN_LENGTH/2,RADIUS,centerPant);
    }
}
