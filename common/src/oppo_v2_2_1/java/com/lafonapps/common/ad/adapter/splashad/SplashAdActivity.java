package com.lafonapps.common.ad.adapter.splashad;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.lafonapps.common.Common;
import com.lafonapps.common.NotificationCenter;
import com.lafonapps.common.R;
import com.lafonapps.common.ad.adapter.SplashAd;
import com.lafonapps.common.preferences.CommonConfig;
import com.lafonapps.common.utils.ViewUtil;
import com.oppo.mobad.api.InitParams;
import com.oppo.mobad.api.MobAdManager;
import com.oppo.mobad.api.ad.InterstitialAd;
import com.oppo.mobad.api.listener.IInterstitialAdListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjie on 2017/8/16.
 */

public class SplashAdActivity extends Activity implements SplashAd {

    public static final String ON_REQUEST_AD_PERMISSION_RESULT_NOTIFICATION = "ON_REQUEST_AD_PERMISSION_RESULT_NOTIFICATION";
    public static final int REQUEST_PERMISSION_CODE = 1001;

    private static final String TAG = SplashAdActivity.class.getCanonicalName();
    /* 是否已经请求过权限 */
    private boolean permissionRequested;
    private int requestTimeOut = 5;
    private int displayDuration = 5;
    private int defaultImageID = R.drawable.default_splash;
    private InterstitialAd interstitialAd;
    private Button skipButton;
    private ViewGroup container;
    private boolean dismissed;

    private CountDownTimer displayTimer = new CountDownTimer(displayDuration * 1000, 1000) {
        @Override
        public void onTick(long l) {
            skipButton.setText(displayDuration + "");

            displayDuration--;
            Log.d(TAG, "Display countdown = " + displayDuration);
        }

        @Override
        public void onFinish() {
            Log.d(TAG, "displayTimer finish");
            dismissSplashAd();
        }
    };

    private CountDownTimer requestTimer = new CountDownTimer(requestTimeOut * 1000, 1000) {
        @Override
        public void onTick(long l) {
            requestTimeOut--;
            Log.d(TAG, "Request countdown = " + requestTimeOut);
        }

        @Override
        public void onFinish() {
            Log.d(TAG, "requestTimer finish");
            dismissSplashAd();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        container = (ViewGroup) getLayoutInflater().inflate(R.layout.splash_ad, null);
        setContentView(container);

        ImageView splashImageView = findViewById(R.id.splash_image_view);
        //压缩图片防止崩溃发生
        Bitmap bitmap = ViewUtil.scaleBitmap(ViewUtil.readBitMap(this, getDefaultImageIDFromMetaData()), 0.9F);
        splashImageView.setImageBitmap(bitmap);

        skipButton = findViewById(R.id.skip_button);

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
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // 捕获back键，在展示广告期间按back键，不跳过广告
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /* 权限 */
    public void requestReadPhoneStatePermission() {
        boolean hasReadPhoneStatePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
        boolean hasWriteExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        if (hasReadPhoneStatePermission && hasWriteExternalStoragePermission) {
            // Have permission, do the thing!
            Log.v(TAG, "Has permission:" + "READ_PHONE_STATE");

            NotificationCenter.defaultCenter().postNotification(ON_REQUEST_AD_PERMISSION_RESULT_NOTIFICATION, true);

            loadAndDisplay();
        } else {
            List<String> permissionList = new ArrayList<>();
            if (!hasReadPhoneStatePermission) {
                permissionList.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (!hasWriteExternalStoragePermission) {
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            // Ask for one permission
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), REQUEST_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult:" + requestCode + ":" + permissions.length);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            boolean permissionGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = false;
                    break;
                }
            }

            NotificationCenter.defaultCenter().postNotification(ON_REQUEST_AD_PERMISSION_RESULT_NOTIFICATION, permissionGranted);

            loadAndDisplay();

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
        Log.d(TAG, "loadAndDisplay");
        /**
         * invoke this method to init sdk.
         */
        InitParams initParams = new InitParams.Builder()
                .setDebug(Common.isApkDebugable())//pen sdk log,must comment this line when your app release.
                .build();
        MobAdManager.getInstance().init(Common.getSharedApplication(), CommonConfig.sharedCommonConfig.appID4OPPO, initParams);

        if (CommonConfig.sharedCommonConfig.shouldShowSplashAd) {
            //OPPO新版SDK不提供开屏广告，所以使用全屏广告
            interstitialAd = new InterstitialAd(this, CommonConfig.sharedCommonConfig.interstitialAdUnitID4OPPO);
            interstitialAd.setAdListener(new IInterstitialAdListener() {
                @Override
                public void onAdShow() {
                    Log.d(TAG, "onAdShow");
                    displayTimer.start();

                    skipButton.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailed(String s) {
                    Log.w(TAG, "onAdFailed:" + s);
                    dismissSplashAd();
                }

                @Override
                public void onAdReady() {
                    Log.d(TAG, "onAdReady");

                    requestTimer.cancel();

                    interstitialAd.showAd();


                }

                @Override
                public void onAdClick() {

                }

                @Override
                public void onAdClose() {
                    Log.d(TAG, "onAdClose");
                    dismissSplashAd();
                }
            });
            interstitialAd.loadAd();
            requestTimer.start();
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

    @Override
    protected void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroyAd();
        }
        super.onDestroy();
    }
}
