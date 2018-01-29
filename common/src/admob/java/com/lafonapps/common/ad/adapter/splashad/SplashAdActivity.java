package com.lafonapps.common.ad.adapter.splashad;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.lafonapps.common.R;
import com.lafonapps.common.ad.adapter.AdAdapterLayout;
import com.lafonapps.common.ad.adapter.BaseSplashAdActivity;
import com.lafonapps.common.ad.adapter.SplashAd;
import com.lafonapps.common.preferences.CommonConfig;
import com.lafonapps.common.preferences.Preferences;
import com.lafonapps.common.utils.ViewUtil;

/**
 * Created by chenjie on 2017/8/16.
 */

public class SplashAdActivity extends BaseSplashAdActivity implements SplashAd {

    private static final String TAG = SplashAdActivity.class.getCanonicalName();

    private NativeExpressAdView splashAd;
    private Button skipButton;
    private AdAdapterLayout container;
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

        container.setTouchListener(new AdAdapterLayout.TouchListener() {
            @Override
            public boolean shouldComfirmBeforeDownloadApp() {
                return CommonConfig.sharedCommonConfig.shouldComfirmBeforeDownloadAppOnSplashAdClick;
            }

            @Override
            public Rect exceptRect() {
                //开屏广告的"跳过按钮"位置
                return new Rect();
            }
        });

        ImageView splashImageView = findViewById(R.id.splash_image_view);
        displaySplashImage(splashImageView);

        skipButton = findViewById(R.id.skip_button);

        container.post(new Runnable() {
            @Override
            public void run() {
                loadAndDisplay();
            }
        });
    }

    /**
     * 展示广告会用到的所有权限列表，包括必须的非必须的
     *
     * @return
     */
    @Override
    protected String[] allPermissions() {
        return new String[0];
    }

    /**
     * 展示广告必须用到的权限列表
     *
     * @return
     */
    @Override
    protected String[] requiredPermissions() {
        return new String[0];
    }

    @Override
    public void loadAndDisplay() {
        if (CommonConfig.sharedCommonConfig.shouldShowSplashAd) {
            splashAd = new NativeExpressAdView(this);
            int dpWidth = ViewUtil.px2dp(container.getWidth());
            int dpHeight = ViewUtil.px2dp(container.getHeight());
            splashAd.setAdSize(new com.google.android.gms.ads.AdSize(dpWidth, dpHeight));
            splashAd.setAdUnitId(CommonConfig.sharedCommonConfig.splashAdUnitID4Admob);
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

    public void skipButtonClicked(View view) {
        Log.d(TAG, "skipButtonClicked");
        dismissSplashAd();
    }

    @Override
    protected void onDestroy() {
        if (splashAd != null) {
            splashAd.destroy();
        }
        super.onDestroy();
    }
}


/*
ERROR_CODE_INTERNAL_ERROR - Something happened internally; for instance, an invalid response was received from the ad server.
ERROR_CODE_INVALID_REQUEST - The ad request was invalid; for instance, the ad unit ID was incorrect.
ERROR_CODE_NETWORK_ERROR - The ad request was unsuccessful due to network connectivity.
ERROR_CODE_NO_FILL - The ad request was successful, but no ad was returned due to lack of ad inventory.
* */