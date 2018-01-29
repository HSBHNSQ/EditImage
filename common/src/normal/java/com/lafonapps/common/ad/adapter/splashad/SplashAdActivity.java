package com.lafonapps.common.ad.adapter.splashad;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.lafonapps.common.R;
import com.lafonapps.common.ad.adapter.BaseSplashAdActivity;
import com.lafonapps.common.ad.adapter.SplashAd;

/**
 * Created by chenjie on 2017/8/16.
 */

public class SplashAdActivity extends BaseSplashAdActivity implements SplashAd {

    private static final String TAG = SplashAdActivity.class.getCanonicalName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_ad);

        ImageView splashImageView = findViewById(R.id.splash_image_view);
        displaySplashImage(splashImageView);

        dismissSplashAd();
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


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}