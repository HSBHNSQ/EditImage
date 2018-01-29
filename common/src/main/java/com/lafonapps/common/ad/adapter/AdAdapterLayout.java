package com.lafonapps.common.ad.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.lafonapps.common.Common;
import com.lafonapps.common.utils.ViewUtil;

/**
 * Created by chenjie on 2017/12/27.
 */
public class AdAdapterLayout extends FrameLayout {

    private static final String TAG = AdAdapterLayout.class.getCanonicalName();
    /**
     * Max allowed duration for a "click", in milliseconds.
     */
    private static final int MAX_CLICK_DURATION = 1000;

    /**
     * Max allowed distance to move during a "click", in DP.
     */
    private static final int MAX_CLICK_DISTANCE = 15;

    private long pressStartTime;
    private float pressedX;
    private float pressedY;

    /** 是否已提示过 */
    private boolean comfirmed = false;
    private TouchListener touchListener;

    public AdAdapterLayout(@NonNull Context context) {
        super(context);
    }

    public AdAdapterLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AdAdapterLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AdAdapterLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        Log.d(TAG, "onInterceptTouchEvent:" + event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                pressStartTime = System.currentTimeMillis();
                pressedX = event.getX();
                pressedY = event.getY();
                break;
            }
            case MotionEvent.ACTION_UP: {
                long pressDuration = System.currentTimeMillis() - pressStartTime;
                float distance = ViewUtil.distanceInDp(pressedX, pressedY, event.getX(), event.getY());

                Log.d(TAG, "press duration = " + pressDuration + ", distance = " + distance);
                Log.d(TAG, "MAX_CLICK_DURATION = " + MAX_CLICK_DURATION + ", MAX_CLICK_DISTANCE = " + MAX_CLICK_DISTANCE);
                Log.d(TAG, "Click point = (" + ViewUtil.px2dp(event.getX()) + ", " + ViewUtil.px2dp(event.getY()) + ")");

                boolean isClick = pressDuration < MAX_CLICK_DURATION && distance < MAX_CLICK_DISTANCE;

                Rect exceptRect = new Rect();
                if (touchListener != null) {
                    exceptRect = touchListener.exceptRect();
                }
                int clickedX = ViewUtil.px2dp((int)event.getX());
                int clickedY = ViewUtil.px2dp((int)event.getY());
                boolean inExceptRect = exceptRect.contains(clickedX, clickedY);

                if (isClick && !inExceptRect) {
                    // Click event has occurred
                    Log.d(TAG, "Detect click");
                    boolean shouldComfirm = false;
                    if (touchListener != null) {
                        shouldComfirm = touchListener.shouldComfirmBeforeDownloadApp();
                    }
                    if (shouldComfirm && !isComfirmed()) {
                        comfirmed = true;
                        Toast.makeText(Common.getSharedApplication(), "再次点击下载应用或打开网址", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                break;
            }
        }
        return super.onInterceptTouchEvent(event);
    }

    /**
     * 判断是否提示过用户
     * @return true为已提示过，false为未提示过
     */
    protected boolean isComfirmed() {
        return comfirmed;
    }

    /**
     * 重设为没有提示过用户的状态
     */
    protected void resetComfirmed() {
        this.comfirmed = false;
    }

    public TouchListener getTouchListener() {
        return touchListener;
    }

    public void setTouchListener(TouchListener touchListener) {
        this.touchListener = touchListener;
    }

    public static interface TouchListener {

        /**
         * 点击横幅广告下载应用或打开网址之前是否提示用户。
         * @return true为先提醒用户，再次点击会下载应用或打开网址。false为不提示用户，直接下载应用或打开网址。
         */
        boolean shouldComfirmBeforeDownloadApp();

        /**
         * 除外的区域。当点击到此区域内的时候，将不会提示用户。
         * @return 基于当前Layout的坐标系的矩形区域。以dp为单位
         */
        Rect exceptRect();

    }
}
