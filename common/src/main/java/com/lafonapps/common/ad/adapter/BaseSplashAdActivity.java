package com.lafonapps.common.ad.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.lafonapps.common.NotificationCenter;
import com.lafonapps.common.R;
import com.lafonapps.common.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjie on 2018/1/5.
 */

public abstract class BaseSplashAdActivity extends Activity implements SplashAd {

    private static final String TAG = BaseSplashAdActivity.class.getCanonicalName();
    protected String tag = getClass().getCanonicalName();

    public static final String ON_REQUEST_AD_PERMISSION_RESULT_NOTIFICATION = "ON_REQUEST_AD_PERMISSION_RESULT_NOTIFICATION";
    public static final int REQUEST_PERMISSION_CODE = 1001;

    /* 是否已经请求过权限 */
    protected boolean permissionRequested;
    protected int requestTimeOut = 5;
    protected int displayDuration = 5;
    protected int defaultImageID = R.drawable.default_splash;

    protected boolean dismissed;
    /**
     * 判断是否可以立刻跳转应用主页面。
     */
    private boolean shouldDismiss = false;

    @Override
    protected void onStart() {
        super.onStart();

        if (!permissionRequested) {
            permissionRequested = true;
            requestReadPhoneStatePermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");
        /**
         * 这里包含对于点击闪屏广告以后、然后返回闪屏广告页面立刻跳转应用主页面的处理。
         */
        if (shouldDismiss) {
            dismissSplashAd();
        }
        shouldDismiss = true;
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "onPause");
        /**
         * 这里包含对于点击闪屏广告以后、然后返回闪屏广告页面立刻跳转应用主页面的处理。
         */
        shouldDismiss = false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // 捕获back键，在展示广告期间按back键，不跳过广告
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 展示广告会用到的所有权限列表，包括必须的非必须的
     * @return
     */
    protected abstract String[] allPermissions();

    /**
     * 展示广告必须用到的权限列表
     * @return
     */
    protected abstract String[] requiredPermissions();

    /* 权限 */
    public void requestReadPhoneStatePermission() {
        String[] allPermissions = allPermissions();
        List<String> needRequestPermissionList = new ArrayList<>();

        for (String permission : allPermissions) {
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, permission)) {
                needRequestPermissionList.add(permission);
            }
        }

        if (0 == needRequestPermissionList.size()) {
            /**
             * 权限都已经有了，那么直接调用SDK请求广告。
             */
            Log.v(tag, "Has permission:" + allPermissions);

            NotificationCenter.defaultCenter().postNotification(ON_REQUEST_AD_PERMISSION_RESULT_NOTIFICATION, true);

            loadAndDisplay();
        } else {
            /**
             * 有权限需要申请，主动申请。
             */
            String[] temp = new String[needRequestPermissionList.size()];
            needRequestPermissionList.toArray(temp);
            ActivityCompat.requestPermissions(this, temp, REQUEST_PERMISSION_CODE);

            Log.d(tag, "Will request permissions: " + temp);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult:" + requestCode + ":" + permissions.length);
        if (requestCode == REQUEST_PERMISSION_CODE) {

            boolean hasRequiredPermission = true;

            String[] requiredPermissions = requiredPermissions();

            for (String permission : requiredPermissions) {
                if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, permission)) {
                    hasRequiredPermission = false;
                    break;
                }
            }

            // Have permission, do the thing!
            Log.v(TAG, "Has required permissions:" + hasRequiredPermission);

            NotificationCenter.defaultCenter().postNotification(ON_REQUEST_AD_PERMISSION_RESULT_NOTIFICATION, hasRequiredPermission);

             if (hasRequiredPermission) {
                loadAndDisplay();
            } else {
                dismissSplashAd();
            }
        }
    }

    protected void displaySplashImage(ImageView splashImageView) {
        //压缩图片防止崩溃发生
        Bitmap bitmap = ViewUtil.scaleBitmap(ViewUtil.readBitMap(this,getDefaultImageIDFromMetaData()),0.9F);
        splashImageView.setImageBitmap(bitmap);
    }

    @Override
    public void dismissSplashAd() {
        if (!dismissed) { // 处理多次回调弹出多个Activity的问题
            dismissed = true;
            Log.d(TAG, "dismissSplashAd");
            try {
                String targetActivityClassName = getTargetActivityClassNameFromMetaData();
                Class<Activity> targetActivityClass = (Class<Activity>) Class.forName(targetActivityClassName);
                Intent intent = new Intent(this, targetActivityClass);

                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.fadein, R.anim.fadeout);
                ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
                Log.d(TAG, "startActivity");

                finish();
//                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                Log.d(TAG, "finish");
            } catch (Exception e) {
                Log.d(TAG, "Exception:" + e);
                throw new RuntimeException(e);
            }
        } else {
            Log.w(TAG, "Already dissmissed!");
        }
    }

    /**
     * 结束开屏页面，跳转主页面。
     */
    protected void dismissSplashAdIfShould() {
        if (shouldDismiss) {
            dismissSplashAd();
        } else {
            shouldDismiss = true;
        }
    }

    @Override
    public void setRequestTimeOut(int timeOut) {
        this.requestTimeOut = timeOut;
    }

    @Override
    public void setDisplayDuration(int duration) {
        this.displayDuration = duration;
    }

    @Override
    public void setDefaultImage(int drawableID) {
        this.defaultImageID = drawableID;
    }

    protected String getTargetActivityClassNameFromMetaData() {
        try {
            ActivityInfo ai = getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
            String targetActivityClassName = ai.metaData.getString(META_DATA_TARGET_ACTIVITY);
            if (targetActivityClassName == null || targetActivityClassName.length() == 0) {
                throw new RuntimeException("meta-data named \"" + META_DATA_TARGET_ACTIVITY + "\" can not be empty!");
            }
            return targetActivityClassName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected int getDefaultImageIDFromMetaData() {
        try {
            ActivityInfo ai = getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
            int defaultImageID = ai.metaData.getInt(META_DATA_DEFAULT_IMAGE);
            if (defaultImageID <= 0) {
                throw new RuntimeException("meta-data named \"" + META_DATA_DEFAULT_IMAGE + "\" must be set!");
            } else {
                this.defaultImageID = defaultImageID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return defaultImageID;
    }

}
