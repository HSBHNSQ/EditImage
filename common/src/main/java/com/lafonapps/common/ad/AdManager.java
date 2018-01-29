package com.lafonapps.common.ad;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

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
import com.lafonapps.common.preferences.CommonConfig;
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
    private Map<String, InterstitialAdAdapter> interstitialAdAdapterPool = new HashMap<>();

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

    public BannerAdapterView getBannerAdapterView(AdSize adSize, Activity activity, BannerViewAdapter.Listener listener) {
        String key4Size = adSize.toString();
        String key4SizeAndActivity = key4Size + "_" + activity.hashCode();
        String key = key4SizeAndActivity;
        if (BannerViewAdapter.REUSEABLE) {
            key = key4Size;
        }
        BannerAdapterView adapterView = bannerAdapterViewPool.get(key);
        if (adapterView != null) {
            adapterView.addListener(listener);
        } else {
            synchronized (bannerAdapterViewBuilderLock) {
                AdModel adModel = new AdModel();
                adModel.setAdmobAdID(CommonConfig.sharedCommonConfig.bannerAdUnitID4Admob);
                adModel.setXiaomiAdID(CommonConfig.sharedCommonConfig.bannerAdUnitID4XiaoMi);
                adModel.setOppoAdID(CommonConfig.sharedCommonConfig.bannerAdUnitID4OPPO);
                adModel.setTencentAdID(CommonConfig.sharedCommonConfig.bannerAdUnitID4Tencent);

                adapterView = new BannerAdapterView(activity);
                adapterView.addListener(listener);
                adapterView.setDebugDevices(Preferences.getSharedPreference().getTestDevices());
                adapterView.build(adModel, adSize);

                bannerAdapterViewPool.put(key, adapterView);
            }
            if (CommonConfig.sharedCommonConfig.shouldShowBannerView) {
                if (adReachedOnce) {
                    adapterView.loadAd();
                } else {
                    if (!waitingForReachedAdAdapters.contains(adapterView)) {
                        waitingForReachedAdAdapters.add(adapterView);
                    }
                }
            } else {
                Log.d(TAG, "showBannerView == false, will not load Banner Ad");
            }
        }

        return adapterView;
    }

    /* 小尺寸的原生广告, 高度80~1200 */
    public NativeAdAdapterView getNativeAdAdapterView(AdSize adSize, Activity activity, NativeAdViewAdapter.Listener listener) {
        return getNativeAdAdapterViewFor80H(adSize, activity, listener);
    }

    /* 小尺寸的原生广告, 高度80~1200 */
    public NativeAdAdapterView getNativeAdAdapterViewFor80H(AdSize adSize, Activity activity, NativeAdViewAdapter.Listener listener) {
        String flag = "80~1200";
        AdModel adModel = new AdModel();
        adModel.setAdmobAdID(CommonConfig.sharedCommonConfig.nativeAdUnitID4Admob);
        adModel.setXiaomiAdID(CommonConfig.sharedCommonConfig.nativeAdUnitID4XiaoMi);
        adModel.setOppoAdID(CommonConfig.sharedCommonConfig.nativeAdUnitID4OPPO);
        adModel.setTencentAdID(CommonConfig.sharedCommonConfig.nativeAdUnitID4Tencent);

        return getNativeAdAdapterViewForCustomerSize(adSize, flag, adModel, activity, listener);
    }

    /* 中等尺寸的原生广告, 高度132~1200 */
    public NativeAdAdapterView getNativeAdAdapterViewFor132H(AdSize adSize, Activity activity, NativeAdViewAdapter.Listener listener) {
        String flag = "132~1200";
        AdModel adModel = new AdModel();
        adModel.setAdmobAdID(CommonConfig.sharedCommonConfig.nativeAdUnitID132H4Admob);
        adModel.setXiaomiAdID(CommonConfig.sharedCommonConfig.nativeAdUnitID132H4XiaoMi);
        adModel.setOppoAdID(CommonConfig.sharedCommonConfig.nativeAdUnitID132H4OPPO);
        adModel.setTencentAdID(CommonConfig.sharedCommonConfig.nativeAdUnitID132H4Tencent);

        return getNativeAdAdapterViewForCustomerSize(adSize, flag, adModel, activity, listener);
    }

    /* 大尺寸的原生广告, 高度250~1200 */
    public NativeAdAdapterView getNativeAdAdapterViewFor250H(AdSize adSize, Activity activity, NativeAdViewAdapter.Listener listener) {
        String flag = "250~1200";
        AdModel adModel = new AdModel();
        adModel.setAdmobAdID(CommonConfig.sharedCommonConfig.nativeAdUnitID250H4Admob);
        adModel.setXiaomiAdID(CommonConfig.sharedCommonConfig.nativeAdUnitID250H4XiaoMi);
        adModel.setOppoAdID(CommonConfig.sharedCommonConfig.nativeAdUnitID250H4OPPO);
        adModel.setTencentAdID(CommonConfig.sharedCommonConfig.nativeAdUnitID250H4Tencent);

        return getNativeAdAdapterViewForCustomerSize(adSize, flag, adModel, activity, listener);
    }

    /* 自定义尺寸的原生广告，可以指定广告单元ID, 当一个应用中用到几个同等规格的原生广告时会用到此方法 高度80~1200 */
    public NativeAdAdapterView getNativeAdAdapterViewForCustomerSize(AdSize adSize, String flag, AdModel adModel, Activity activity, NativeAdViewAdapter.Listener listener) {
        String key4Size = adSize.toString();
        String key4SizeAndActivity = key4Size + "_" + activity.hashCode();
        String key = key4SizeAndActivity;
        if (NativeAdAdapterView.REUSEABLE) {
            key = key4Size;
        }
        NativeAdAdapterView adapterView = nativeAdAdapterViewPool.get(key);
        if (adapterView != null) {
            adapterView.addListener(listener);
        } else {
            synchronized (nativeAdAdapterViewBuilderLock) {
                adapterView = new NativeAdAdapterView(activity);
                adapterView.addListener(listener);
                adapterView.setDebugDevices(Preferences.getSharedPreference().getTestDevices());
                adapterView.build(adModel, adSize);

                nativeAdAdapterViewPool.put(key, adapterView);

            }
            if (adReachedOnce) {
                adapterView.loadAd();
            } else {
                if (!waitingForReachedAdAdapters.contains(adapterView)) {
                    waitingForReachedAdAdapters.add(adapterView);
                }
            }
        }
        return adapterView;
    }

    /* 全屏广告 */
    public InterstitialAdAdapter getInterstitialAdAdapter(@NonNull Activity activity, InterstitialAdapter.Listener listener) {
        String key4Global = "Global";
        String key4Activity = "" + activity.hashCode();
        String key = key4Activity;
        if (InterstitialAdAdapter.REUSEABLE) {
            key = key4Global;
        }
        InterstitialAdAdapter adAdapter = interstitialAdAdapterPool.get(key);
        if (adAdapter != null) {
            adAdapter.addListener(listener);
        } else {
            synchronized (interstitialAdapterBuilderLock) {
                AdModel adModel = new AdModel();
                adModel.setAdmobAdID(CommonConfig.sharedCommonConfig.interstitialAdUnitID4Admob);
                adModel.setXiaomiAdID(CommonConfig.sharedCommonConfig.interstitialAdUnitID4XiaoMi);
                adModel.setOppoAdID(CommonConfig.sharedCommonConfig.interstitialAdUnitID4OPPO);
                adModel.setTencentAdID(CommonConfig.sharedCommonConfig.interstitialAdUnitID4Tencent);

                adAdapter = new InterstitialAdAdapter(activity);
                adAdapter.addListener(listener);
                adAdapter.setDebugDevices(Preferences.getSharedPreference().getTestDevices());
                adAdapter.build(adModel);

                interstitialAdAdapterPool.put(key, adAdapter);
            }
            if (CommonConfig.sharedCommonConfig.shouldShowInterstitialAd) {
                if (adReachedOnce) {
                    adAdapter.loadAd();
                } else {
                    if (!waitingForReachedAdAdapters.contains(adAdapter)) {
                        waitingForReachedAdAdapters.add(adAdapter);
                    }
                }
            } else {
                Log.d(TAG, "showInterstitialAd == false, will not load Interstitial Ad");
            }
        }
        return adAdapter;
    }

    /* 激励视频广告 */
    public AnimationRewardVideoAdAdapter getRewardBasedVideoAdAdapter(RewardBasedVideoAdAdapter.Listener listener) {
//TODO:待实现
        return null;
    }
}
