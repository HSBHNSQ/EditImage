package com.lafonapps.common.ad.adapter.splashad;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.lafonapps.common.R;
import com.lafonapps.common.ad.adapter.SplashAd;
import com.lafonapps.common.preferences.CommonConfig;
import com.lafonapps.common.preferences.Preferences;
import com.lafonapps.common.utils.ViewUtil;

/**
 * Created by chenjie on 2017/8/16.
 */

public class SplashAdActivity extends Activity implements SplashAd {

    private static final String TAG = SplashAdActivity.class.getCanonicalName();

    private int requestTimeOut = 5;
    private int displayDuration = 5;
    private int defaultImageID = R.drawable.default_splash;
    private NativeExpressAdView splashAd;
    private Button skipButton;
    private ViewGroup container;
    private boolean dismissed;
    private CountDownTimer displayTimer = new CountDownTimer(displayDuration * 1000, 1000) {
        @Override
        public void onTick(long l) {
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
        setContentView(R.layout.splash_ad);

        container = findViewById(R.id.splash_ad_container);
        ImageView splashImageView = findViewById(R.id.splash_image_view);
        //压缩图片防止崩溃发生
        Bitmap bitmap = ViewUtil.scaleBitmap(ViewUtil.readBitMap(this, getDefaultImageIDFromMetaData()), 0.9F);
        splashImageView.setImageBitmap(bitmap);

        skipButton = findViewById(R.id.skip_button);

        container.post(new Runnable() {
            @Override
            public void run() {
                loadAndDisplay();
            }
        });
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
            splashAd = new NativeExpressAdView(this);
//        splashAd.setBackgroundColor(Color.TRANSPARENT);
//        int width = container.getWidth() > 1000 ? 1000 : container.getWidth();
//        int height = container.getHeight() > 1000 ? 1000 : container.getHeight();
//        splashAd.setAdSize(new com.google.android.gms.ads.AdSize(width, height));
//        splashAd.setAdSize(new com.google.android.gms.ads.AdSize(AdSize.FULL_WIDTH, 400));
//        splashAd.setAdSize(new com.google.android.gms.ads.AdSize(320, 250));
            int dpWidth = ViewUtil.px2dp(container.getWidth());
            int dpHeight = ViewUtil.px2dp(container.getHeight());
            splashAd.setAdSize(new com.google.android.gms.ads.AdSize(dpWidth, dpHeight));
//        splashAd.setAdSize(AdSize.MEDIUM_RECTANGLE);
            splashAd.setAdUnitId(Preferences.getSharedPreference().getSplashAdUnitID4Admob());
            splashAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    Log.d(TAG, "onAdClosed");

                    dismissSplashAd();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    Log.d(TAG, "onAdFailedToLoad:" + i);
                    dismissSplashAd();
                }

                @Override
                public void onAdLeftApplication() {
                    Log.d(TAG, "onAdLeftApplication");

                }

                @Override
                public void onAdOpened() {
                    Log.d(TAG, "onAdOpened");

                }

                @Override
                public void onAdLoaded() {
                    Log.d(TAG, "onAdLoaded");

                    requestTimer.cancel();
                    displayTimer.start();

                    skipButton.setVisibility(View.VISIBLE);

                }
            });

            container.addView(splashAd, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            AdRequest.Builder requestBuilder = new AdRequest.Builder();
            for (String testDevice : Preferences.getSharedPreference().getTestDevices()) {
                requestBuilder.addTestDevice(testDevice);
            }
            requestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
            AdRequest adRequest = requestBuilder.build();

            splashAd.loadAd(adRequest);

            requestTimer.start();
        } else {
            dismissSplashAd();
        }
    }

    private void dismissSplashAd() {
        if (!dismissed) {
            dismissed = true;
            requestTimer.cancel();
            displayTimer.cancel();
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
        splashAd.destroy();
        super.onDestroy();
    }
}


/*
ERROR_CODE_INTERNAL_ERROR - Something happened internally; for instance, an invalid response was received from the ad server.
ERROR_CODE_INVALID_REQUEST - The ad request was invalid; for instance, the ad unit ID was incorrect.
ERROR_CODE_NETWORK_ERROR - The ad request was unsuccessful due to network connectivity.
ERROR_CODE_NO_FILL - The ad request was successful, but no ad was returned due to lack of ad inventory.
* */