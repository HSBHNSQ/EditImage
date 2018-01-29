package com.lafonapps.common;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;
import com.lafonapps.common.preferences.CommonConfig;

/**
 * Created by chenjie on 2017/8/10.
 */

public class ProductFlavor {

    public static void initialize(Application application) {
        MobileAds.initialize(application, CommonConfig.sharedCommonConfig.appID4Admob);
    }

}
