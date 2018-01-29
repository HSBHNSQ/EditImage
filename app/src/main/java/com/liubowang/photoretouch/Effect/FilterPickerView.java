package com.liubowang.photoretouch.Effect;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.liubowang.photoretouch.R;
import com.liubowang.photoretouch.Utils.DisplayUtil;

/**
 * Created by heshaobo on 2017/11/28.
 */

public class FilterPickerView extends LinearLayout {

    private RecyclerView filterRecycleView;
    private FilterAdapter filterAdapter;
    private SeekBar filterSeekBar;

    public FilterPickerView(Context context) {
        super(context);
        initSubView(context);
    }

    public FilterPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initSubView(context);
    }

    public FilterPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSubView(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FilterPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initSubView(context);
    }

    private void initSubView(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_filter_picker,this,true);
        setupReycleView(context);
        filterSeekBar = findViewById(R.id.sb_seek_filter_progress_fpv);
        filterSeekBar.setMax(100);
        filterSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
    }

    public void setSmaleSmpleBitmap(Bitmap bitmap){
        filterAdapter.setSmaleImage(bitmap);
    }

    public int getSeekProgress(){
        return filterSeekBar.getProgress();
    }

    public void setSeekBarVisiable(int visiable){
        filterSeekBar.setVisibility(visiable);
    }

    private void setupReycleView(Context context){
        VIEW_MAX_HEIGHT = DisplayUtil.dpTopx(context,120);
        filterRecycleView = findViewById(R.id.cy_recycle_view_fpv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        filterRecycleView.setLayoutManager(linearLayoutManager);
        filterAdapter = new FilterAdapter(context);
        filterRecycleView.setAdapter(filterAdapter);
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (filterChangeListener != null){
                filterChangeListener.onSeekAdjustChanged(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private FilterAdapter.OnFilterChangeListener filterChangeListener;

    public void setFilterChangeListener(FilterAdapter.OnFilterChangeListener filterChangeListener) {
        this.filterChangeListener = filterChangeListener;
        filterAdapter.setFilterChangeListener(filterChangeListener);
    }


    public boolean isOpen() {
        return isOpen;
    }
    public void open(){
        isOpen = true;
        animationOpenAndClose(0,VIEW_MAX_HEIGHT);
    }

    public void close(){
        isOpen = false;
        animationOpenAndClose(VIEW_MAX_HEIGHT,0);
    }

    private boolean isOpen = false;
    private static int VIEW_MAX_HEIGHT = 0;
    private static final int ANIMATION_DURATION = 300;
    private boolean isFirst = true;
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
        if (isFirst){
            filterAdapter.notifyDataSetChanged();
            isFirst = false;
        }
    }
}
