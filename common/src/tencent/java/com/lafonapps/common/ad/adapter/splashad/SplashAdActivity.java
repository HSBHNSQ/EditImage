package com.lafonapps.common.ad.adapter.splashad;

import android.Manifest;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.lafonapps.common.R;
import com.lafonapps.common.ad.adapter.AdAdapterLayout;
import com.lafonapps.common.ad.adapter.BaseSplashAdActivity;
import com.lafonapps.common.ad.adapter.SplashAd;
import com.lafonapps.common.preferences.CommonConfig;
import com.lafonapps.common.utils.ViewUtil;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;

/**
 * Created by chenjie on 2017/8/16.
 */

public class SplashAdActivity extends BaseSplashAdActivity implements SplashAd {

    private static final String TAG = SplashAdActivity.class.getCanonicalName();

//    private Button skipButton;
    private SplashAD splashAD;
    private AdAdapterLayout container;

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
                return new Rect(ViewUtil.getDeviceWidthInDP() - 100, 0, ViewUtil.getDeviceWidthInDP(), 60);
            }
        });

        ImageView splashImageView = findViewById(R.id.splash_image_view);
        displaySplashImage(splashImageView);

//        skipButton = findViewById(R.id.skip_button);

    }

    /**
     * 展示广告会用到的所有权限列表，包括必须的非必须的
     *
     * @return
     */
    @Override
    protected String[] allPermissions() {
        return new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
        };
    }

    /**
     * 展示广告必须用到的权限列表
     *
     * @return
     */
    @Override
    protected String[] requiredPermissions() {
        return new String[]{
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };
    }

    @Override
    protected void onDestroy() {
       super.onDestroy();
    }

    @Override
    public void loadAndDisplay() {
        if (CommonConfig.sharedCommonConfig.shouldShowSplashAd) {
//            SplashAD splashAD = new SplashAD(this, container, skipButton, Preferences.getSharedPreference().getAppID4Tencent(), Preferences.getSharedPreference().getSplashAdUnitID4Tencent(), new SplashADListener() {
            splashAD = new SplashAD(this, container, CommonConfig.sharedCommonConfig.appID4Tencent, CommonConfig.sharedCommonConfig.splashAdUnitID4Tencent, new SplashADListener() {
                @Override
                public void onADPresent() {
                    Log.d(TAG, "onADPresent");

//                    skipButton.setVisibility(View.VISIBLE);
                }

                @Override
                public void onADDismissed() {
                    Log.d(TAG, "onADDismissed");

                    dismissSplashAdIfShould();
                }

                @Override
                public void onNoAD(AdError error) {
                    Log.d(TAG, "onNoAD error:" + error);

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

}