package com.lafonapps.common.ad.adapter.splashad;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.lafonapps.common.R;
import com.lafonapps.common.ad.adapter.SplashAd;
import com.lafonapps.common.utils.ViewUtil;

/**
 * Created by chenjie on 2017/8/16.
 */

public class SplashAdActivity extends Activity implements SplashAd {

    private static final String TAG = SplashAdActivity.class.getCanonicalName();

    private int requestTimeOut = 5;
    private int displayDuration = 5;
    private int defaultImageID = R.drawable.default_splash;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_ad);

        ImageView splashImageView = findViewById(R.id.splash_image_view);
        //压缩图片防止崩溃发生
        Bitmap bitmap = ViewUtil.scaleBitmap(ViewUtil.readBitMap(this,getDefaultImageIDFromMetaData()),0.9F);
        splashImageView.setImageBitmap(bitmap);

        dismissSplashAd();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //TODO:
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // 捕获back键，在展示广告期间按back键，不跳过广告
            return true;
        }
        return super.dispatchKeyEvent(event);
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

    @Override
    public void loadAndDisplay() {


    }

    private void dismissSplashAd() {
        try {
            String targetActivityClassName = getTargetActivityClassNameFromMetaData();
            Class<Activity> targetActivityClass = (Class<Activity>) Class.forName(targetActivityClassName);
            Intent intent = new Intent(this, targetActivityClass);
//            if (Build.VERSION.SDK_INT >= 16) {
//                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.fadeout, R.anim.fadein);
//                startActivity(intent, activityOptions.toBundle());
//            } else {
//                startActivity(intent);
//                overridePendingTransition(R.anim.fadeout, R.anim.fadein);
//            }
            startActivity(intent);
            finish();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getTargetActivityClassNameFromMetaData() {
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

    private int getDefaultImageIDFromMetaData() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}


/*
ERROR_CODE_INTERNAL_ERROR - Something happened internally; for instance, an invalid response was received from the ad server.
ERROR_CODE_INVALID_REQUEST - The ad request was invalid; for instance, the ad unit ID was incorrect.
ERROR_CODE_NETWORK_ERROR - The ad request was unsuccessful due to network connectivity.
ERROR_CODE_NO_FILL - The ad request was successful, but no ad was returned due to lack of ad inventory.
* */