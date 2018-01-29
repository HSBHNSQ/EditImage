package com.lafonapps.common;

import android.app.Application;

import com.lafonapps.common.ad.adapter.splashad.SplashAdActivity;
import com.lafonapps.common.preferences.CommonConfig;
import com.xiaomi.ad.AdSdk;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by chenjie on 2017/8/10.
 */

public class ProductFlavor {

    public static void initialize(final Application application) {
        NotificationCenter.defaultCenter().addObserver(SplashAdActivity.ON_REQUEST_AD_PERMISSION_RESULT_NOTIFICATION, new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                if (o instanceof Boolean) {
                    if (o.equals(Boolean.TRUE)) {
                        if (Common.isApkDebugable()) {
                            AdSdk.setDebugOn();
                        }
                        AdSdk.initialize(application, CommonConfig.sharedCommonConfig.appID4XiaoMi);
                    }
                }
            }
        });
    }

}
