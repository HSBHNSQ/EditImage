package com.yalantis.ucrop.view.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.yalantis.ucrop.R;
import com.yalantis.ucrop.model.AspectRatio;
import com.yalantis.ucrop.view.CropImageView;

import java.util.Locale;

/**
 * Created by heshaobo on 2018/1/11.
 */

public class CustomAspectRatioView extends ConstraintLayout {

    private TextView textView;
    private ImageView imageView;
    private AspectRatio aspectRatio;
    private float mAspectRatio;
    private float mOldAspectRatio = 1;
    private String mAspectRatioTitle;
    private float mAspectRatioX, mAspectRatioY;
    ConstraintLayout.LayoutParams imageViewLayoutParams;

    public CustomAspectRatioView(Context context) {
        super(context);
        init(context);
    }
    public CustomAspectRatioView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        configAttrs(context,attrs);
    }

    public CustomAspectRatioView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        configAttrs(context,attrs);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.ucrop_custom_aspect_ratio,this,true);
        textView = (TextView) findViewById(R.id.ucrop_textview_ar);
        imageView = (ImageView) findViewById(R.id.ucrop_imageview_ar);
        imageViewLayoutParams = (ConstraintLayout.LayoutParams) imageView.getLayoutParams();
        textView.setTextColor(context.getResources().getColor(R.color.ucrop_color_normal_text_color));
        post(new Runnable() {
            @Override
            public void run() {
                scaleImageViewSize();
            }
        });
    }


    private void configAttrs(Context context,AttributeSet attributeSet){

    }

    public void setBg(int resId) {
        imageView.setBackgroundResource(resId);
    }

    public void setAspectRatio(AspectRatio aspectRatio) {
        this.aspectRatio = aspectRatio;
        mAspectRatioTitle = aspectRatio.getAspectRatioTitle();
        mAspectRatioX = aspectRatio.getAspectRatioX();
        mAspectRatioY = aspectRatio.getAspectRatioY();

        if (mAspectRatioX == CropImageView.SOURCE_IMAGE_ASPECT_RATIO || mAspectRatioY == CropImageView.SOURCE_IMAGE_ASPECT_RATIO) {
            mAspectRatio = CropImageView.SOURCE_IMAGE_ASPECT_RATIO;
        } else {
            mAspectRatio = mAspectRatioX / mAspectRatioY;
        }
        setTitle();
        scaleImageViewSize();
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (selected){
            textView.setTextColor(getContext().getResources().getColor(R.color.ucrop_color_selected_text_color));
        }else {
            textView.setTextColor(getContext().getResources().getColor(R.color.ucrop_color_normal_text_color));
        }
    }

    public AspectRatio getAspectRatio() {
        return aspectRatio;
    }

    public float getAspectRatio(boolean toggleRatio) {
        if (toggleRatio) {
            toggleAspectRatio();
            setTitle();
        }
        scaleImageViewSize();
        return mAspectRatio;
    }

    private void scaleImageViewSize(){
        if (mAspectRatio > 0){
            ValueAnimator animator = ValueAnimator.ofFloat(mOldAspectRatio,mAspectRatio);
            animator.setDuration(300);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (Float) animation.getAnimatedValue();
                    imageViewLayoutParams.dimensionRatio = String.valueOf(value);
                    imageView.requestLayout();
                    if (value == mAspectRatio){
                        mOldAspectRatio = mAspectRatio;
                    }
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mOldAspectRatio = mAspectRatio;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mOldAspectRatio = mAspectRatio;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.start();

        }
    }

    private void toggleAspectRatio() {
        if (mAspectRatio != CropImageView.SOURCE_IMAGE_ASPECT_RATIO) {
            float tempRatioW = mAspectRatioX;
            mAspectRatioX = mAspectRatioY;
            mAspectRatioY = tempRatioW;

            mAspectRatio = mAspectRatioX / mAspectRatioY;
        }
    }

    private void setTitle() {
        if (!TextUtils.isEmpty(mAspectRatioTitle)) {
            textView.setText(mAspectRatioTitle);
        } else {
            textView.setText(String.format(Locale.US, "%d:%d", (int) mAspectRatioX, (int) mAspectRatioY));
        }
    }
}
