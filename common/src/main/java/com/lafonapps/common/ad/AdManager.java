package com.lafonapps.common.ad;

import android.content.Context;
import android.util.Log;

import com.lafonapps.common.Common;
import com.lafonapps.common.ad.adapter.AdAdapter;
import com.lafonapps.common.ad.adapter.AdModel;
import com.lafonapps.common.ad.adapter.BannerViewAdapter;
import com.lafonapps.common.ad.adapter.InterstitialAdapter;
import com.lafonapps.common.ad.adapter.NativeAdViewAdapter;
import com.lafonapps.common.ad.adapter.RewardBasedVideoAdAdapter;
import com.lafonapps.common.ad.adapter.banner.BannerAdapterView;
import com.lafonapps.common.ad.adapter.interstitial.InterstitialAdAdapter;
import com.lafonapps.common.ad.adapter.nativead.NativeAdAdapterView;
import com.lafonapps.common.ad.adapter.reward.AnimationRewardVideoAdAdapter;
import com.lafonapps.common.preferences.Preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenjie on 2017/7/5.
 */

public class AdManager {
    private static final String TAG = AdManager.class.getCanonicalName();

    private static final AdManager sharedAdManager = new AdManager();

    private Map<String, BannerAdapterView> bannerAdapterViewPool = new HashMap<>();
    private Map<String, NativeAdAdapterView> nativeAdAdapterViewPool = new HashMap<>();
    private InterstitialAdAdapter interstitialAdAdapter;

    private List<AdAdapter> waitingForReachedAdAdapters = new ArrayList<>(5);
    private AdReachabilityDetector detector = new AdReachabilityDetectorImpl();

    private boolean adReachedOnce; //是否满足请求广告的条件，只要有一次满足就可以

    private Object bannerAdapterViewBuilderLock = new Object();
    private Object nativeAdAdapterViewBuilderLock = new Object();
    private Object interstitialAdapterBuilderLock = new Object();

    private AdManager() {

        detector.add(new AdReachabilityDetector.ChangedCallback() {
            @Override
            public void changed(boolean reachable) {

                adReachedOnce = true;

                AdAdapter[] array = waitingForReachedAdAdapters.toArray(new AdAdapter[waitingForReachedAdAdapters.size()]);
                for (AdAdapter a : array) {
                    a.loadAd();
                }

                waitingForReachedAdAdapters.clear();
            }
        });
        detector.start();
    }

    public static AdManager getSharedAdManager() {
        return sharedAdManager;
    }

    public BannerAdapterView getBannerAdapterView(AdSize adSize, Context context, BannerViewAdapter.Listener listener) {
        String key = adSize.toString();
        if (context == null) {
            context = Common.getSharedApplication();
        }
        BannerAdapterView adapterView = bannerAdapterViewPool.get(key);
        if (adapterView != null) {

        } else {
            synchronized (bannerAdapterViewBuilderLock) {
                AdModel adModel = new AdModel();
                adModel.setAdmobAdID(Preferences.getSharedPreference().getBannerAdUnitID4Admob());
                adModel.setFacebookAdID(Preferences.getSharedPreference().getBannerAdUnitID4Facebook());
                adModel.setXiaomiAdID(Preferences.getSharedPreference().getBannerAdUnitID4XiaoMi());
                adModel.setOppoAdID(Preferences.getSharedPreference().getBannerAdUnitID4OPPO());
                adModel.setTencentAdID(Preferences.getSharedPreference().getBannerAdUnitID4Tencent());

                adapterView = new BannerAdapterView(context);
                adapterView.setDebugDevices(Preferences.getSharedPreference().getTestDevices());
                adapterView.build(adModel, adSize);

                if (adapterView.reuseable()) {
                    bannerAdapterViewPool.put(key, adapterView);
                }
            }
            if (adReachedOnce) {
                adapterView.loadAd();
            } else {
                if (!waitingForReachedAdAdapters.contains(adapterView)) {
                    waitingForReachedAdAdapters.add(adapterView);
                }
            }
        }

        adapterView.addListener(listener);
        return adapterView;
    }

    /* 小尺寸的原生广告, 高度80~1200 */
    public NativeAdAdapterView getNativeAdAdapterView(AdSize adSize, Context context, NativeAdViewAdapter.Listener listener) {
        return getNativeAdAdapterViewFor80H(adSize, context, listener);
    }

    /* 小尺寸的原生广告, 高度80~1200 */
    public NativeAdAdapterView getNativeAdAdapterViewFor80H(AdSize adSize, Context context, NativeAdViewAdapter.Listener listener) {
        String flag = "80~1200";
        AdModel adModel = new AdModel();
        adModel.setAdmobAdID(Preferences.getSharedPreference().getNativeAdUnitID4Admob());
        adModel.setFacebookAdID(Preferences.getSharedPreference().getNativeAdUnitID4Facebook());
        adModel.setXiaomiAdID(Preferences.getSharedPreference().getNativeAdUnitID4XiaoMi());
        adModel.setOppoAdID(Preferences.getSharedPreference().getNativeAdUnitID4OPPO());
        adModel.setTencentAdID(Preferences.getSharedPreference().getNativeAdUnitID4Tencent());

        return getNativeAdAdapterViewForCustomerSize(adSize, flag, adModel, context, listener);
    }

    /* 中等尺寸的原生广告, 高度132~1200 */
    public NativeAdAdapterView getNativeAdAdapterViewFor132H(AdSize adSize, Context context, NativeAdViewAdapter.Listener listener) {
        String flag = "132~1200";
        AdModel adModel = new AdModel();
        adModel.setAdmobAdID(Preferences.getSharedPreference().getNativeAdUnitID132H4Admob());
        adModel.setFacebookAdID(Preferences.getSharedPreference().getNativeAdUnitID132H4Facebook());
        adModel.setXiaomiAdID(Preferences.getSharedPreference().getNativeAdUnitID132H4XiaoMi());
        adModel.setOppoAdID(Preferences.getSharedPreference().getNativeAdUnitID132H4OPPO());
        adModel.setTencentAdID(Preferences.getSharedPreference().getNativeAdUnitID132H4Tencent());

        return getNativeAdAdapterViewForCustomerSize(adSize, flag, adModel, context, listener);
    }

    /* 大尺寸的原生广告, 高度250~1200 */
    public NativeAdAdapterView getNativeAdAdapterViewFor250H(AdSize adSize, Context context, NativeAdViewAdapter.Listener listener) {
        String flag = "250~1200";
        AdModel adModel = new AdModel();
        adModel.setAdmobAdID(Preferences.getSharedPreference().getNativeAdUnitID250H4Admob());
        adModel.setFacebookAdID(Preferences.getSharedPreference().getNativeAdUnitID250H4Facebook());
        adModel.setXiaomiAdID(Preferences.getSharedPreference().getNativeAdUnitID250H4XiaoMi());
        adModel.setOppoAdID(Preferences.getSharedPreference().getNativeAdUnitID250H4OPPO());
        adModel.setTencentAdID(Preferences.getSharedPreference().getNativeAdUnitID250H4Tencent());

        return getNativeAdAdapterViewForCustomerSize(adSize, flag, adModel, context, listener);
    }

    /* 自定义尺寸的原生广告，可以指定广告单元ID, 当一个应用中用到几个同等规格的原生广告时会用到此方法 高度80~1200 */
    public NativeAdAdapterView getNativeAdAdapterViewForCustomerSize(AdSize adSize, String flag, AdModel adModel, Context context, NativeAdViewAdapter.Listener listener) {
        String key = adSize.toString();
        if (context == null) {
            context = Common.getSharedApplication();
        }
        NativeAdAdapterView adapterView = nativeAdAdapterViewPool.get(key);
        if (adapterView != null) {

        } else {
            synchronized (nativeAdAdapterViewBuilderLock) {
                adapterView = new NativeAdAdapterView(context);
                adapterView.setDebugDevices(Preferences.getSharedPreference().getTestDevices());
                adapterView.build(adModel, adSize);

                if (adapterView.reuseable()) {
                    nativeAdAdapterViewPool.put(key, adapterView);
                }
            }
            if (adReachedOnce) {
                adapterView.loadAd();
            } else {
                if (!waitingForReachedAdAdapters.contains(adapterView)) {
                    waitingForReachedAdAdapters.add(adapterView);
                }
            }
        }
        adapterView.addListener(listener);
        return adapterView;
    }

    /* 全屏广告 */
    public InterstitialAdAdapter getInterstitialAdAdapter(Context context, InterstitialAdapter.Listener listener) {
        if (context == null) {
            context = Common.getSharedApplication();
        }

        InterstitialAdAdapter adAdapter = interstitialAdAdapter;
        if (adAdapter == null) {
            synchronized (interstitialAdapterBuilderLock) {
                AdModel adModel = new AdModel();
                adModel.setAdmobAdID(Preferences.getSharedPreference().getInterstitialAdUnitID4Admob());
                adModel.setFacebookAdID(Preferences.getSharedPreference().getInterstitialAdUnitID4Facebook());
                adModel.setXiaomiAdID(Preferences.getSharedPreference().getInterstitialAdUnitID4XiaoMi());
                adModel.setOppoAdID(Preferences.getSharedPreference().getInterstitialAdUnitID4OPPO());
                adModel.setTencentAdID(Preferences.getSharedPreference().getInterstitialAdUnitID4Tencent());

                adAdapter = new InterstitialAdAdapter(context);
                adAdapter.setDebugDevices(Preferences.getSharedPreference().getTestDevices());
                adAdapter.build(adModel);

                if (adAdapter.reuseable()) {
                    interstitialAdAdapter = adAdapter;
                }

            }
            if (adReachedOnce) {
                adAdapter.loadAd();
            } else {
                if (!waitingForReachedAdAdapters.contains(adAdapter)) {
                    waitingForReachedAdAdapters.add(adAdapter);
                }
            }
        }
        adAdapter.addListener(listener);
        return adAdapter;
    }

    /* 激励视频广告 */
    public AnimationRewardVideoAdAdapter getRewardBasedVideoAdAdapter(RewardBasedVideoAdAdapter.Listener listener) {
//TODO:待实现
        return null;
    }
}
