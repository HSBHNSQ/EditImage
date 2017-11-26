package com.liubowang.editimage.Normal;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.liubowang.editimage.R;
import com.liubowang.editimage.Utils.ScreenUtil;

/**
 * Created by heshaobo on 2017/11/16.
 */

public class MosaicSizeSeek extends LinearLayout {

    private static final String TAG = MosaicSizeSeek.class.getSimpleName();
    private TextSeekView textSeekView;
    private ImageButton openButton;
    private TextSeekView.OnTextSeekValueChangedListener seekValueChangedListener;
    private static final int ANIMATION_DURATION = 300;
    private static int SEEK_VIEW_MAX_WIDTH = 0;
    private boolean isOpen = false;
    public MosaicSizeSeek(Context context) {
        super(context);
        initSubviews(context);
    }

    public MosaicSizeSeek(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initSubviews(context);
    }

    public MosaicSizeSeek(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSubviews(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MosaicSizeSeek(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initSubviews(context);
    }


    private void initSubviews(Context context){

        int screenWidth = ScreenUtil.getScreenSize(context).widthPixels;
        SEEK_VIEW_MAX_WIDTH = screenWidth / 4 * 3;
        LayoutInflater.from(context).inflate(R.layout.view_mosaic_size_seek,this,true);
        textSeekView = findViewById(R.id.tsv_size_seek_mss);
        openButton = findViewById(R.id.ib_open_mss);
        openButton.setOnClickListener(openButtonListener);
    }

    private OnClickListener openButtonListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isOpen){
                close();
            }else {
                open();
            }
        }
    };



    public void setSeekValueChangedListener(TextSeekView.OnTextSeekValueChangedListener seekValueChangedListener) {
        this.seekValueChangedListener = seekValueChangedListener;
        textSeekView.setOnTextSeekValueChangedListener(seekValueChangedListener);
    }

    public boolean isOpen() {
        return isOpen;
    }
    public void open(){
        isOpen = true;
        animationOpenAndClose(0,SEEK_VIEW_MAX_WIDTH);
    }

    public void close(){
        isOpen = false;
        animationOpenAndClose(SEEK_VIEW_MAX_WIDTH,0);
    }

    private void animationOpenAndClose(int startInt,int endInt){
        ValueAnimator animator = ValueAnimator.ofInt(startInt,endInt);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int width = (int) valueAnimator.getAnimatedValue();
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)textSeekView.getLayoutParams();
                layoutParams.width = width;
                textSeekView.setLayoutParams(layoutParams);
                if (width == 0){
                    openButton.setImageResource(R.drawable.go_right);
                }
                if (width == SEEK_VIEW_MAX_WIDTH){
                    openButton.setImageResource(R.drawable.go_left);
                }
            }
        });
        animator.start();
    }

}
