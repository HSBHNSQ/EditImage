package com.liubowang.photoretouch.Effect;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.liubowang.photoretouch.Normal.ImageTextButton;
import com.liubowang.photoretouch.R;

/**
 * Created by heshaobo on 2017/11/24.
 */

public class SBEToolView extends LinearLayout {

    private static final int DEFAULT_MAX_VALUE = 100;

    private LinearLayout smartContainer;
    private LinearLayout brushContainer;
    private LinearLayout eraserContainer;
    private ImageTextButton zhengXuan;
    private ImageTextButton fanXuan;
    private ImageTextButton normal;
    private ImageTextButton normal1;
    private ImageTextButton normal2;
    private SeekBar brushSizeSeek;
    private SeekBar eraserSizeSeek;
    private OnSBEToolListener sbeToolListener;
    private LinearLayout currentOpenContainer;
    private LinearLayout lastOpenContainer;
    private ImageButton add;
    private ImageButton reduce;
    private int minValue;
    private int maxValue;
    private int MAX_WIDTH;
    public SBEToolView(Context context) {
        super(context);
        initSubViews(context);
    }

    public SBEToolView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initSubViews(context);
    }

    public SBEToolView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSubViews(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SBEToolView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initSubViews(context);
    }

    private void initSubViews(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_sbe_tool,this,true);

        smartContainer =  findViewById(R.id.ll_smart_container_sbetv);
        zhengXuan = findViewById(R.id.itb_zheng_xuan_sbetv);
        fanXuan = findViewById(R.id.itb_fan_xuan_sbetv);
        zhengXuan.setOnClickListener(buttonCLickListener);
        fanXuan.setOnClickListener(buttonCLickListener);

        brushContainer = findViewById(R.id.ll_brush_container_sbetv);
        normal = findViewById(R.id.itb_normal_brush_sbetv);
        normal1 = findViewById(R.id.itb_normal_1_brush_sbetv);
        normal2 = findViewById(R.id.itb_normal_2_brush_sbetv);
        normal.setOnClickListener(buttonCLickListener);
        normal1.setOnClickListener(buttonCLickListener);
        normal2.setOnClickListener(buttonCLickListener);
        brushSizeSeek = findViewById(R.id.sb_seek_brush_sbetv);
        brushSizeSeek.setOnSeekBarChangeListener(onSeekBarChangeListener);
        brushSizeSeek.setMax(DEFAULT_MAX_VALUE);

        add = findViewById(R.id.iv_jia_sbetv);
        add.setOnClickListener(buttonCLickListener);
        reduce = findViewById(R.id.iv_jian_sbetv);
        reduce.setOnClickListener(buttonCLickListener);


        eraserContainer =  findViewById(R.id.ll_eraser_container_sbetv);
        eraserSizeSeek = findViewById(R.id.sb_seek_eraser_sbetv);
        eraserSizeSeek.setOnSeekBarChangeListener(onSeekBarChangeListener);
        eraserSizeSeek.setMax(DEFAULT_MAX_VALUE);

        currentOpenContainer = smartContainer;
        lastOpenContainer = smartContainer;
    }

    public void setSeekRange(int min,int max){
        this.minValue = min;
        this.maxValue = max;
    }

    public void setSBEToolListener(OnSBEToolListener listener) {
        this.sbeToolListener = listener;
    }

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (seekBar.getId() == R.id.sb_seek_brush_sbetv){
                if (sbeToolListener != null){
                    sbeToolListener.onBrushSizeChange(getSeekValue(i));
                }
            }
            else if (seekBar.getId() == R.id.sb_seek_eraser_sbetv){
                if (sbeToolListener != null){
                    sbeToolListener.onEraserSizeChange(getSeekValue(i));
                }
            }
        }

        private int getSeekValue(int i){
            float scale = (i * 1.0f ) / DEFAULT_MAX_VALUE;
            int distance = maxValue - minValue;
            float value = minValue + distance * scale;
            return (int) value;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private OnClickListener buttonCLickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view == zhengXuan){
                if (!zhengXuan.isSelected()){
                    zhengXuan.setSelected(true);
                    fanXuan.setSelected(false);
                }
                if (sbeToolListener != null){
                    sbeToolListener.onZhengXuan();
                }
            }
            if (view == fanXuan){
                if (!fanXuan.isSelected()){
                    fanXuan.setSelected(true);
                    zhengXuan.setSelected(false);
                }
                if (sbeToolListener != null){
                    sbeToolListener.onFanXuan();
                }
            }

            if (view == normal){
                if (normal1.isSelected()){
                    normal1.setSelected(false);
                }
                if (normal2.isSelected()){
                    normal2.setSelected(false);
                }
                if (!normal.isSelected()){
                    normal.setSelected(true);
                }
                if (sbeToolListener != null){
                    sbeToolListener.onBrushNormal();
                }
            }
            if (view == normal1){
                if (normal.isSelected()){
                    normal.setSelected(false);
                }
                if (normal2.isSelected()){
                    normal2.setSelected(false);
                }
                if (!normal1.isSelected()){
                    normal1.setSelected(true);
                }
                if (sbeToolListener != null){
                    sbeToolListener.onBrushNormal1();
                }
            }
            if (view == normal2){
                if (normal.isSelected()){
                    normal.setSelected(false);
                }
                if (normal1.isSelected()){
                    normal1.setSelected(false);
                }
                if (!normal2.isSelected()){
                    normal2.setSelected(true);
                }
                if (sbeToolListener != null){
                    sbeToolListener.onBrushNormal2();
                }
            }
            if (view == add){
                brushSizeSeek.setProgress( brushSizeSeek.getProgress() + 5);
            }
            if (view == reduce){
                brushSizeSeek.setProgress(brushSizeSeek.getProgress() - 5);
            }

        }
    };


    public void openSmart(){
        if (lastOpenContainer == smartContainer){
            return;
        }
        MAX_WIDTH = getWidth();
        currentOpenContainer = smartContainer;
        animationOpenAndClose(0,MAX_WIDTH);
    }

    public void openBrush(){
        if (lastOpenContainer == brushContainer){
            return;
        }
        MAX_WIDTH = getWidth();
        currentOpenContainer = brushContainer;
        animationOpenAndClose(0,MAX_WIDTH);
    }
    public void openEraser(){
        if (lastOpenContainer == eraserContainer){
            return;
        }
        MAX_WIDTH = getWidth();
        currentOpenContainer = eraserContainer;
        animationOpenAndClose(0,MAX_WIDTH);
    }

    public void close(){

//        animationOpenAndClose(MAX_HEIGHT,0);
    }
    private static final int ANIMATION_DURATION = 300;
    private void animationOpenAndClose(int startInt,int endInt){
        ValueAnimator animator = ValueAnimator.ofInt(startInt,endInt);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams currentlayoutParams = currentOpenContainer.getLayoutParams();
                ViewGroup.LayoutParams lastLayoutParams = lastOpenContainer.getLayoutParams();
                currentlayoutParams.width = value;
                lastLayoutParams.width = MAX_WIDTH - value;
                currentOpenContainer.setLayoutParams(currentlayoutParams);
                lastOpenContainer.setLayoutParams(lastLayoutParams);
                if (value == MAX_WIDTH){
                    lastOpenContainer = currentOpenContainer;
                }
            }
        });
        animator.start();
    }



    interface OnSBEToolListener {
        void onZhengXuan();
        void onFanXuan();
        void onBrushNormal();
        void onBrushNormal1();
        void onBrushNormal2();
        void onBrushSizeChange(int value);
        void onEraserSizeChange(int value);
    }



}
