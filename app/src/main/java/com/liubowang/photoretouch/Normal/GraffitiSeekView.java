package com.liubowang.photoretouch.Normal;

import android.animation.ValueAnimator;
import android.content.Context;
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

import com.liubowang.photoretouch.R;
import com.liubowang.photoretouch.Utils.DisplayUtil;
import com.liubowang.photoretouch.Utils.ScreenUtil;

/**
 * Created by heshaobo on 2017/11/17.
 */

public class GraffitiSeekView extends LinearLayout {

    private static final String TAG = GraffitiSeekView.class.getSimpleName();
    private static final int ANIMATION_DURATION = 300;
    private static int MAX_RECYCLER_HEIGHT = 0;
    private static int MAX_CONTAINER_WIDTH = 0;
    private static final int LINE_COUNT = 4;
    private static final int ITEM_WIDTH = 40;

    private ImageButton leftOpenButton;
    private ImageButton topOpenButton;
    private TextSeekView textSeekView;
    private RecyclerView recyclerView;
    private LinearLayout containerLayout;
    private GraffitiSeekListener graffitiSeekListener;

    private boolean isTopOpen = false;
    private boolean isLeftOpen = false;
    private boolean isFirst = true;
    private boolean isChangeRecycleHeight = false;


    public GraffitiSeekView(Context context) {
        super(context);
        initSubViews(context);
    }

    public GraffitiSeekView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initSubViews(context);
    }

    public GraffitiSeekView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSubViews(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GraffitiSeekView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initSubViews(context);
    }

    private void initSubViews(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_graffiti_seek,this,true);
        int screenHeight = ScreenUtil.getScreenSize(context).heightPixels;
        int sceenWidth = ScreenUtil.getScreenSize(context).widthPixels;
        MAX_RECYCLER_HEIGHT = screenHeight / 5 * 3;
        int minWidth = DisplayUtil.dpTopx(context,ITEM_WIDTH) * LINE_COUNT;
        int midWidth = DisplayUtil.dpTopx(context,ITEM_WIDTH) * (LINE_COUNT+2);
        MAX_CONTAINER_WIDTH = Math.max(Math.min(sceenWidth,midWidth),minWidth);

        leftOpenButton = findViewById(R.id.ib_open_gsv);
        topOpenButton = findViewById(R.id.ib_top_gsv);
        textSeekView = findViewById(R.id.tsv_size_seek_gsv);
        textSeekView.setOnTextSeekValueChangedListener(sizeListener);
        recyclerView = findViewById(R.id.rl_recycle_view_gsv);
        containerLayout = findViewById(R.id.ll_container_gsv);
        leftOpenButton.setOnClickListener(buttonListener);
        topOpenButton.setOnClickListener(buttonListener);
        ColorAdapter colorAdapter = new ColorAdapter();
        recyclerView.setAdapter(colorAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(context,LINE_COUNT);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        colorAdapter.setItemClickListener(colorListener);
    }

    public void setGraffitiSeekListener(GraffitiSeekListener graffitiSeekListener) {
        this.graffitiSeekListener = graffitiSeekListener;
    }

    private TextSeekView.OnTextSeekValueChangedListener sizeListener = new TextSeekView.OnTextSeekValueChangedListener() {
        @Override
        public void onValueChange(TextSeekView textSeekView, int value, boolean b) {
            if (graffitiSeekListener != null){
                graffitiSeekListener.sizeChanged(value);
            }
        }

        @Override
        public void onStopTrackingTouch(TextSeekView textSeekView, int value) {

        }
    };

    private ColorAdapter.ColorItemClickListener colorListener = new ColorAdapter.ColorItemClickListener() {
        @Override
        public void itemClick(int color, boolean selected) {
            if (selected){
                if (graffitiSeekListener != null){
                    graffitiSeekListener.colorChanged(color);
                }
            }
        }
    };

    private OnClickListener buttonListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.ib_open_gsv){
                if (isLeftOpen){
                    closeContainerlayout();
                }else {
                    openContainerlayout();
                }
            }
            else if (id == R.id.ib_top_gsv){
                if (isTopOpen){
                    closeRecycleView();
                }else {
                    openRecycleView();
                }
            }
        }
    };

    public void openContainerlayout(){
        isLeftOpen = true;
        isChangeRecycleHeight = false;
        animationOpenAndClose(0,MAX_CONTAINER_WIDTH);
    }
    public void closeContainerlayout(){
        isLeftOpen = false;
        isChangeRecycleHeight = false;
        animationOpenAndClose(MAX_CONTAINER_WIDTH,0);
    }

    public boolean isLeftOpen() {
        return isLeftOpen;
    }

    private void openRecycleView(){
        isTopOpen = true;
        isChangeRecycleHeight = true;
        animationOpenAndClose(0,MAX_RECYCLER_HEIGHT);
    }

    private void closeRecycleView(){
        isTopOpen = false;
        isChangeRecycleHeight = true;
        animationOpenAndClose(MAX_RECYCLER_HEIGHT,0);
    }


    private void animationOpenAndClose(int startInt,int endInt){
        ValueAnimator animator = ValueAnimator.ofInt(startInt,endInt);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                if (isChangeRecycleHeight){
                    changeRecycleViewHeith(value);
                }else {
                    changeContainerWidth(value);
                }
            }
        });
        animator.start();
    }

    private void changeRecycleViewHeith(int height){
        LinearLayout.LayoutParams recycleLp = (LinearLayout.LayoutParams) recyclerView.getLayoutParams() ;
        recycleLp.height = height;
        recyclerView.setLayoutParams(recycleLp);
        if (height == MAX_RECYCLER_HEIGHT){
            topOpenButton.setImageResource(R.drawable.go_bottom);
        }
        if (height == 0){
            topOpenButton.setImageResource(R.drawable.go_top);
        }
    }
    private void changeContainerWidth(int width){
        LinearLayout.LayoutParams containerLp = (LinearLayout.LayoutParams) containerLayout.getLayoutParams() ;
        containerLp.width = width;
        containerLayout.setLayoutParams(containerLp);
        if (width == MAX_CONTAINER_WIDTH){
            leftOpenButton.setImageResource(R.drawable.go_right);
            if (isFirst){
                openRecycleView();
                isFirst = false;
            }
        }
        if (width == 0){
            leftOpenButton.setImageResource(R.drawable.go_left);
        }
    }


    interface GraffitiSeekListener {
        void sizeChanged(int size);
        void colorChanged(int color);
    }


}
