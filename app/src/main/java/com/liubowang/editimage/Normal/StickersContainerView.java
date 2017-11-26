package com.liubowang.editimage.Normal;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heshaobo on 2017/11/15.
 */

public class StickersContainerView extends RelativeLayout {

    private static final String TAG = StickersContainerView.class.getSimpleName();

    private StickerView currentActiveStickerView;
    private List<StickerView> stickerViewList = new ArrayList<>();

    public StickersContainerView(@NonNull Context context) {
        super(context);
    }

    public StickersContainerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StickersContainerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StickersContainerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }



    public void addSticker(Bitmap bitmap,Context context){
        StickerView stickerView = new StickerView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        addView(stickerView, params);
        stickerView.setWaterMark(bitmap);
        stickerView.setOnStickerActionListener(stickerActionListener);
        stickerViewList.add(stickerView);
        if (currentActiveStickerView != null){
            currentActiveStickerView.setShowDrawController(false);
        }
        currentActiveStickerView = stickerView;
        Log.d(TAG,"count:"+stickerViewList.size());
    }

    private StickerView.OnStickerActionListener stickerActionListener = new StickerView.OnStickerActionListener() {
        @Override
        public void onDelete(StickerView stickerView) {
            stickerViewList.remove(stickerView);
            removeView(stickerView);
            currentActiveStickerView = null;
            Log.d(TAG,"count:"+stickerViewList.size());
        }

        @Override
        public void onTouchDown(StickerView stickerView) {
            if (currentActiveStickerView != null){
                currentActiveStickerView.setShowDrawController(false);
            }
            if (!stickerView.getShowDrawController()){
                stickerView.setShowDrawController(true);
                bringChildToFront(stickerView);
            }
            currentActiveStickerView = stickerView;
        }
    };

}
