package com.lafonapps.common.ad.adapter.splashad;

import android.Manifest;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.lafonapps.common.Common;
import com.lafonapps.common.R;
import com.lafonapps.common.ad.adapter.AdAdapterLayout;
import com.lafonapps.common.ad.adapter.BaseSplashAdActivity;
import com.lafonapps.common.ad.adapter.SplashAd;
import com.lafonapps.common.preferences.CommonConfig;
import com.lafonapps.common.utils.ViewUtil;
import com.oppo.mobad.api.InitParams;
import com.oppo.mobad.api.MobAdManager;
import com.oppo.mobad.api.listener.ISplashAdListener;
import com.oppo.mobad.api.params.SplashAdParams;

/**
 * Created by chenjie on 2017/8/16.
 */

public class SplashAdActivity extends BaseSplashAdActivity implements SplashAd {

    private static final String TAG = SplashAdActivity.class.getCanonicalName();
    private com.oppo.mobad.api.ad.SplashAd splashAd;
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
                Manifest.permission.WRITE_CALENDAR,
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
    public void loadAndDisplay() {
        Log.d(TAG, "loadAndDisplay");

        if (CommonConfig.sharedCommonConfig.shouldShowSplashAd) {
            try {
                InitParams initParams = new InitParams.Builder()
                        .setDebug(Common.isApkDebugable())//true打开SDK日志，当应用发布Release版本时，必须注释掉这行代码的调用，或者设为false
                        .build();
                /**
                 * 调用这行代码初始化广告SDKthis
                 */
                MobAdManager.getInstance().init(this, CommonConfig.sharedCommonConfig.appID4OPPO, initParams);
                /**
                 * SplashAd初始化参数、这里可以设置获取广告最大超时时间，
                 * 广告下面半屏的应用标题+应用描述
                 * 注意：应用标题和应用描述是必传字段，不传将抛出异常
                 */
                SplashAdParams splashAdParams = new SplashAdParams.Builder()
                        .setFetchTimeout(requestTimeOut * 1000)
                        .setTitle(Common.getAppDisplayName())
                        .setDesc("好用免费")
                        .build();
                /**
                 * 构造SplashAd对象
                 * 注意：构造函数传入的几个形参都不能为空，否则将抛出NullPointerException异常。
                 */
                splashAd = new com.oppo.mobad.api.ad.SplashAd(this, CommonConfig.sharedCommonConfig.splashAdUnitID4OPPO, new ISplashAdListener() {
                    @Override
                    public void onAdDismissed() {
                        Log.d(TAG, "onAdDismissed");

                        dismissSplashAdIfShould();
                    }

                    @Override
                    public void onAdShow() {
                        Log.d(TAG, "onAdShow");
                    }

                    @Override
                    public void onAdFailed(String s) {
                        Log.w(TAG, "onAdFailed:" + s);

                        dismissSplashAd();
                    }

                    @Override
                    public void onAdClick() {
                        Log.d(TAG, "onAdClick");
                    }
                }, splashAdParams);
            } catch (Exception e) {
                Log.w(TAG, "", e);

                dismissSplashAd();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (splashAd != null) {
            splashAd.destroyAd();
        }
        super.onDestroy();
    }
}
