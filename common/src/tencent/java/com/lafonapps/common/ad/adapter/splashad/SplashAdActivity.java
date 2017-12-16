package com.lafonapps.common.ad.adapter.splashad;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lafonapps.common.NotificationCenter;
import com.lafonapps.common.R;
import com.lafonapps.common.ad.adapter.SplashAd;
import com.lafonapps.common.preferences.CommonConfig;
import com.lafonapps.common.preferences.Preferences;
import com.lafonapps.common.utils.ViewUtil;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjie on 2017/8/16.
 */

public class SplashAdActivity extends Activity implements SplashAd {

    private static final String TAG = SplashAdActivity.class.getCanonicalName();
    public static final String ON_REQUEST_PERMISSION_RESULT_NOTIFICATION = "ON_REQUEST_PERMISSION_RESULT_NOTIFICATION";
    private static final int REQUESTPERMISSION = 1024;
    /* 是否已经请求过权限 */
    private boolean permissionRequested;
    private int requestTimeOut = 5;
    private int displayDuration = 5;
    private int defaultImageID = R.drawable.default_splash;
//    private Button skipButton;
    private ViewGroup container;
    private boolean dismissed;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_ad);

        container = findViewById(R.id.splash_ad_container);
        ImageView splashImageView = findViewById(R.id.splash_image_view);
        //压缩图片防止崩溃发生
        Bitmap bitmap = ViewUtil.scaleBitmap(ViewUtil.readBitMap(this, getDefaultImageIDFromMetaData()), 0.9F);
        splashImageView.setImageBitmap(bitmap);

//        skipButton = findViewById(R.id.skip_button);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!permissionRequested) {
            permissionRequested = true;
            requestReadPhoneStatePermission();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /* 权限 */
    public void requestReadPhoneStatePermission() {
        List<String> lackedPermission = new ArrayList<String>();
        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // 权限都已经有了，那么直接调用SDK
        if (lackedPermission.size() == 0) {
            NotificationCenter.defaultCenter().postNotification(ON_REQUEST_PERMISSION_RESULT_NOTIFICATION, true);

            loadAndDisplay();
        } else {
            // 请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
            String[] requestPermissions = new String[lackedPermission.size()];
            lackedPermission.toArray(requestPermissions);
            ActivityCompat.requestPermissions(this, requestPermissions, REQUESTPERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUESTPERMISSION && hasAllPermissionsGranted(grantResults)) {
            NotificationCenter.defaultCenter().postNotification(ON_REQUEST_PERMISSION_RESULT_NOTIFICATION, true);

            loadAndDisplay();
        } else {
            NotificationCenter.defaultCenter().postNotification(ON_REQUEST_PERMISSION_RESULT_NOTIFICATION, false);

            loadAndDisplay();
        }
    }

    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
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
        if (CommonConfig.sharedCommonConfig.shouldShowSplashAd) {
//            SplashAD splashAD = new SplashAD(this, container, skipButton, Preferences.getSharedPreference().getAppID4Tencent(), Preferences.getSharedPreference().getSplashAdUnitID4Tencent(), new SplashADListener() {
            SplashAD splashAD = new SplashAD(this, container, Preferences.getSharedPreference().getAppID4Tencent(), Preferences.getSharedPreference().getSplashAdUnitID4Tencent(), new SplashADListener() {
                @Override
                public void onADPresent() {
                    Log.d(TAG, "onADPresent");

//                    skipButton.setVisibility(View.VISIBLE);
                }

                @Override
                public void onADDismissed() {
                    Log.d(TAG, "onADDismissed");

                    dismissSplashAd();
                }

                @Override
                public void onNoAD(int arg0) {
                    Log.d(TAG, "onNoAD error:" + arg0);

                    dismissSplashAd();
                }


                @Override
                public void onADClicked() {
                    Log.d(TAG, "onADClicked");
                }

                @Override
                public void onADTick(long millisUntilFinished) {
                    Log.d(TAG, "onADTick" + millisUntilFinished + "ms");

//                    skipButton.setText(String.format("%ds", Math.round(millisUntilFinished / 1000f)));
                }
            });
        } else {
            dismissSplashAd();
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

    private void dismissSplashAd() {
        if (!dismissed) {
            dismissed = true;
            Log.d(TAG, "dismissSplashAd");

            try {
                String targetActivityClassName = getTargetActivityClassNameFromMetaData();
                Class<Activity> targetActivityClass = (Class<Activity>) Class.forName(targetActivityClassName);
                Intent intent = new Intent(this, targetActivityClass);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


}