package com.lafonapps.common.ad.adapter.splashad;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lafonapps.common.Common;
import com.lafonapps.common.NotificationCenter;
import com.lafonapps.common.R;
import com.lafonapps.common.ad.adapter.SplashAd;
import com.lafonapps.common.preferences.CommonConfig;
import com.lafonapps.common.preferences.Preferences;
import com.lafonapps.common.utils.ViewUtil;
import com.oppo.mobad.MobAdManager;
import com.oppo.mobad.listener.IInitListener;
import com.oppo.mobad.listener.ISplashAdListener;

/**
 * Created by chenjie on 2017/8/16.
 */

public class SplashAdActivity extends Activity implements SplashAd {

    public static final String ON_REQUEST_READ_PHONE_STATE_PERMISSION_RESULT_NOTIFICATION = "ON_REQUEST_READ_PHONE_STATE_PERMISSION_RESULT_NOTIFICATION";
    public static final int RC_READ_PHONE_STATE = 1001;

    private static final String TAG = SplashAdActivity.class.getCanonicalName();
    /* 是否已经请求过权限 */
    private boolean permissionRequested;
    private int requestTimeOut = 5;
    private int displayDuration = 5;
    private int defaultImageID = R.drawable.default_splash;
    private com.oppo.mobad.ad.SplashAd splashAd;
    private ViewGroup container;
    private boolean dismissed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        container = (ViewGroup) getLayoutInflater().inflate(R.layout.splash_ad, null);
        setContentView(container);

        ImageView splashImageView = findViewById(R.id.splash_image_view);
        //压缩图片防止崩溃发生
        Bitmap bitmap = ViewUtil.scaleBitmap(ViewUtil.readBitMap(this,getDefaultImageIDFromMetaData()),0.9F);
        splashImageView.setImageBitmap(bitmap);

        if (Common.isApkDebugable()) {
            MobAdManager.getInstance(Common.getSharedApplication()).enableDebugLog();
        }
        MobAdManager.getInstance(Common.getSharedApplication()).init(Preferences.getSharedPreference().getAppID4OPPO(), new IInitListener() {
            @Override
            public void onInit(boolean b) {
                Log.d(TAG, "MobAdManager onInit:" + b);

                if (b) {
                    loadAndDisplay();
                } else {
                    dismissSplashAd();
                }

                NotificationCenter.defaultCenter().postNotification(ON_REQUEST_READ_PHONE_STATE_PERMISSION_RESULT_NOTIFICATION, b);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!permissionRequested) {
            permissionRequested = true;
//            requestReadPhoneStatePermission();
//            loadAndDisplay();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // 捕获back键，在展示广告期间按back键，不跳过广告
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /* 权限 */
    public void requestReadPhoneStatePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            // Have permission, do the thing!
            Log.v(TAG, "Has permission:" + "READ_PHONE_STATE");

            NotificationCenter.defaultCenter().postNotification(ON_REQUEST_READ_PHONE_STATE_PERMISSION_RESULT_NOTIFICATION, true);

            loadAndDisplay();
        } else {
            // Ask for one permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, RC_READ_PHONE_STATE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult:" + requestCode + ":" + permissions.length);

        if (requestCode == RC_READ_PHONE_STATE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                NotificationCenter.defaultCenter().postNotification(ON_REQUEST_READ_PHONE_STATE_PERMISSION_RESULT_NOTIFICATION, true);

                loadAndDisplay();
            } else {
                NotificationCenter.defaultCenter().postNotification(ON_REQUEST_READ_PHONE_STATE_PERMISSION_RESULT_NOTIFICATION, false);

                loadAndDisplay();
            }
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

    @Override
    public void loadAndDisplay() {
        if (CommonConfig.sharedCommonConfig.shouldShowSplashAd) {
            splashAd = new com.oppo.mobad.ad.SplashAd(this, container, Preferences.getSharedPreference().getSplashAdUnitID4OPPO(), new ISplashAdListener() {
                @Override
                public void onAdDismissed() {
                    Log.d(TAG, "onAdDismissed");

                    //从最顶层视图移除
                    dismissSplashAd();
                }

                @Override
                public void onAdShow() {
                    Log.d(TAG, "onAdShow");
                }

                @Override
                public void onAdFailed(String s) {
                    Log.w(TAG, "onAdFailed:" + s);

                    //从最顶层视图移除
                    dismissSplashAd();
                }

                @Override
                public void onAdReady() {
                    Log.d(TAG, "onAdReady");
                }

                @Override
                public void onAdClick() {
                    Log.d(TAG, "onAdClick");
                }

                @Override
                public void onVerify(int i, String s) {
                    Log.d(TAG, "onVerify:" + i + " " + s);
                }
            });
        } else {
            dismissSplashAd();
        }
    }

    private void dismissSplashAd() {
        if (!dismissed) {
            dismissed = true;

            Log.d(TAG, "dismissSplashAd");
            try {
                String targetActivityClassName = getTargetActivityClassNameFromMetaData();
                Class<Activity> targetActivityClass = (Class<Activity>) Class.forName(targetActivityClassName);
                Intent intent = new Intent(this, targetActivityClass);
//            if (Build.VERSION.SDK_INT >= 16) {
//                ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.fadeout, 0);
//                ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
//            } else {
//                startActivity(intent);
//                overridePendingTransition(R.anim.fadeout, 0);
//            }
                startActivity(intent);

                Log.d(TAG, "startActivity");
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    finish();
//                }
//            }, 5);
                finish();
                Log.d(TAG, "finish");
            } catch (Exception e) {
                Log.d(TAG, "Exception:" + e);
                throw new RuntimeException(e);
            }
        }
    }


    public void skipButtonClicked(View view) {
        Log.d(TAG, "skipButtonClicked");
        dismissSplashAd();
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
}
