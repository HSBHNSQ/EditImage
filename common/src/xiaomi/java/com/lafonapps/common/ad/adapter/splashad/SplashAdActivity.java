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
import com.lafonapps.common.preferences.CommonConfig;
import com.lafonapps.common.utils.ViewUtil;
import com.xiaomi.ad.SplashAdListener;

/**
 * Created by chenjie on 2017/8/16.
 */

public class SplashAdActivity extends BaseSplashAdActivity {

    public static final String ON_REQUEST_AD_PERMISSION_RESULT_NOTIFICATION = "ON_REQUEST_AD_PERMISSION_RESULT_NOTIFICATION";
    public static final int REQUEST_PERMISSION_CODE = 1001;

    private static final String TAG = SplashAdActivity.class.getCanonicalName();

    private AdAdapterLayout container;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        container = (AdAdapterLayout) getLayoutInflater().inflate(R.layout.splash_ad, null);
        setContentView(container);

        container.setTouchListener(new AdAdapterLayout.TouchListener() {
            @Override
            public boolean shouldComfirmBeforeDownloadApp() {
                return CommonConfig.sharedCommonConfig.shouldComfirmBeforeDownloadAppOnSplashAdClick;
            }

            @Override
            public Rect exceptRect() {
                //开屏广告的"跳过按钮"位置
                return new Rect(ViewUtil.getDeviceWidthInDP() - 100, 0, ViewUtil.getDeviceWidthInDP(), 50);
            }
        });

        ImageView splashImageView = findViewById(R.id.splash_image_view);
        displaySplashImage(splashImageView);
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
        };
    }

    @Override
    public void loadAndDisplay() {
//        if (Common.isApkDebugable()) {
//            AdSdk.setDebugOn();
//        }
//        AdSdk.initialize(Common.getSharedApplication(), Preferences.getSharedPreference().getAppID4XiaoMi());
        if (CommonConfig.sharedCommonConfig.shouldShowSplashAd) {
            com.xiaomi.ad.adView.SplashAd splashAd = new com.xiaomi.ad.adView.SplashAd(this, container,
                    getDefaultImageIDFromMetaData(), new SplashAdListener() {

                @Override
                public void onAdPresent() {
                    Log.d(TAG, "onAdPresent");
                }

                @Override
                public void onAdClick() {
                    Log.d(TAG, "onAdClick");
                }

                @Override
                public void onAdDismissed() {
                    Log.d(TAG, "onAdDismissed");

                    //从最顶层视图移除
//                    dismissSplashAd();
                    dismissSplashAdIfShould();
                }

                @Override
                public void onAdFailed(String s) {
                    Log.d(TAG, "onAdFailed, message: " + s);

                    //从最顶层视图移除
                    dismissSplashAd();

                }
            });
            splashAd.requestAd(CommonConfig.sharedCommonConfig.splashAdUnitID4XiaoMi);
        } else {
            dismissSplashAd();
        }
    }

    public void skipButtonClicked(View view) {
        Log.d(TAG, "skipButtonClicked");
        dismissSplashAd();
    }



}
