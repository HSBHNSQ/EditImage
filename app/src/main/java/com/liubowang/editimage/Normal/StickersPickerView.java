package com.liubowang.editimage.Normal;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.liubowang.editimage.R;
import com.liubowang.editimage.Utils.DisplayUtil;
import com.liubowang.editimage.Utils.ScreenUtil;

/**
 * Created by heshaobo on 2017/11/20.
 */

public class StickersPickerView extends LinearLayout {


    private static final String TAG = StickersPickerView.class.getSimpleName();
    private static final int ANIMATION_DURATION = 300;
    private static int RECYCLE_VIEW_MAX_HEIGHT = 0;
    private ImageButton topButton ;
    private RecyclerView recyclerView;
    private boolean isOpen = false;
    private StickerPickerListener pickerListener;
    private Context context;
    public StickersPickerView(Context context) {
        super(context);
        initSubViews(context);
    }

    public StickersPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initSubViews(context);
    }

    public StickersPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSubViews(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StickersPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initSubViews(context);
    }


    private void initSubViews(Context context){
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.view_stickers_picker,this,true);
        RECYCLE_VIEW_MAX_HEIGHT = ScreenUtil.getScreenSize(context).heightPixels / 5 * 2;

        topButton = findViewById(R.id.ib_top_spv);
        recyclerView = findViewById(R.id.rv_recycle_view_spv);
        topButton.setOnClickListener(topButtonListener);


    }

    public void setupRecycleView(){
        StickersAdapter stickerAdaoter = new StickersAdapter(context.getAssets());
        stickerAdaoter.setStickersClickListener(stickerListener);
        recyclerView.setAdapter(stickerAdaoter);
        int space = DisplayUtil.dpTopx(context,8);
        int itemWidth = DisplayUtil.dpTopx(context,54);
        int totleWidth = ScreenUtil.getScreenSize(context).widthPixels - space * 3;
        int count = totleWidth / itemWidth;
        GridLayoutManager layM = new GridLayoutManager(context,count);
        layM.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layM);
    }



    public void setPickerListener(StickerPickerListener pickerListener) {
        this.pickerListener = pickerListener;
    }

    private StickersAdapter.StickersClickListener stickerListener = new StickersAdapter.StickersClickListener() {
        @Override
        public void stickerClick(Bitmap stickerBmp) {
            if (pickerListener != null){
                pickerListener.stickerPciker(stickerBmp);
            }
            if (isOpen){
                close();
            }
        }
    };

    private OnClickListener topButtonListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (isOpen){
                close();
            }else {
                open();
            }
        }
    };

    public boolean isOpen() {
        return isOpen;
    }

    public void open(){
        isOpen = true;
        animationOpenAndClose(0,RECYCLE_VIEW_MAX_HEIGHT);
    }

    public void close(){
        isOpen = false;
        animationOpenAndClose(RECYCLE_VIEW_MAX_HEIGHT,0);
    }
    private void animationOpenAndClose(int startInt,int endInt){
        ValueAnimator animator = ValueAnimator.ofInt(startInt,endInt);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)recyclerView.getLayoutParams();
                layoutParams.height = value;
                recyclerView.setLayoutParams(layoutParams);
                if (value == 0){
                    topButton.setImageResource(R.drawable.go_top);
                }
                if (value == RECYCLE_VIEW_MAX_HEIGHT){
                    topButton.setImageResource(R.drawable.go_bottom);
                }
            }
        });
        animator.start();
    }

    interface StickerPickerListener {
        void stickerPciker(Bitmap sticker);
    }

}

