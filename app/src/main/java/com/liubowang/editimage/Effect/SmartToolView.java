package com.liubowang.editimage.Effect;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.liubowang.editimage.Normal.ImageTextButton;
import com.liubowang.editimage.R;
import com.liubowang.editimage.Utils.DisplayUtil;

/**
 * Created by heshaobo on 2017/11/24.
 */

public class SmartToolView extends LinearLayout {

    private ImageTextButton zhengXuan;
    private ImageTextButton fanXuan;
    private OnSmartToolListener smartToolListener;

    public SmartToolView(Context context) {
        super(context);
        initSubViews(context);
    }

    public SmartToolView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initSubViews(context);
    }

    public SmartToolView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSubViews(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SmartToolView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initSubViews(context);
    }

    private void initSubViews(Context context){
        MAX_HEIGHT = DisplayUtil.dpTopx(context,40);
        LayoutInflater.from(context).inflate(R.layout.view_smart_tool,this,true);
        zhengXuan = findViewById(R.id.itb_zheng_xuan_stv);
        fanXuan = findViewById(R.id.itb_fan_xuan_stv);
        zhengXuan.setOnClickListener(buttonCLickListener);
        fanXuan.setOnClickListener(buttonCLickListener);
    }

    public void setSmartToolListener(OnSmartToolListener smartToolListener) {
        this.smartToolListener = smartToolListener;
    }

    private OnClickListener buttonCLickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == zhengXuan){
                if (!zhengXuan.isSelected()){
                    zhengXuan.setSelected(true);
                    fanXuan.setSelected(false);
                }
                if (smartToolListener != null){
                    smartToolListener.onZhengXuan();
                }
            }
            if (view == fanXuan){
                if (!fanXuan.isSelected()){
                    fanXuan.setSelected(true);
                    zhengXuan.setSelected(false);
                }
                if (smartToolListener != null){
                    smartToolListener.onFanXuan();
                }
            }
        }
    };

    public boolean isOpen() {
        return isOpen;
    }

    public void open(){
        isOpen = true;
        animationOpenAndClose(0,MAX_HEIGHT);
    }

    public void close(){
        isOpen = false;
        animationOpenAndClose(MAX_HEIGHT,0);
    }
    private boolean isOpen = false;
    private static int MAX_HEIGHT = 40;
    private static final int ANIMATION_DURATION = 300;
    private void animationOpenAndClose(int startInt,int endInt){
        ValueAnimator animator = ValueAnimator.ofInt(startInt,endInt);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                layoutParams.height = value;
                setLayoutParams(layoutParams);
            }
        });
        animator.start();
    }



    interface OnSmartToolListener {
        void onZhengXuan();
        void onFanXuan();
    }



}
