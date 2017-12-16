package com.lafonapps.common.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.lafonapps.common.AppStatusDetector;
import com.lafonapps.common.Common;
import com.lafonapps.common.NotificationCenter;
import com.lafonapps.common.ad.AdManager;
import com.lafonapps.common.ad.AdSize;
import com.lafonapps.common.ad.adapter.BannerViewAdapter;
import com.lafonapps.common.ad.adapter.InterstitialAdapter;
import com.lafonapps.common.ad.adapter.NativeAdViewAdapter;
import com.lafonapps.common.ad.adapter.banner.BannerAdapterView;
import com.lafonapps.common.ad.adapter.interstitial.InterstitialAdAdapter;
import com.lafonapps.common.ad.adapter.nativead.NativeAdAdapterView;
import com.lafonapps.common.preferences.CommonConfig;
import com.lafonapps.common.preferences.Preferences;
import com.umeng.analytics.MobclickAgent;

import java.util.Observable;
import java.util.Observer;

import static com.lafonapps.common.ad.AdManager.getSharedAdManager;

/**
 * Created by chenjie on 2017/8/14.
 */

public class BaseActivity extends AppCompatActivity implements BannerViewAdapter.Listener, NativeAdAdapterView.Listener, InterstitialAdapter.Listener {

    private static final String TAG = BaseActivity.class.getCanonicalName();
    private static int counter;
    protected String tag = getClass().getCanonicalName();
    private BannerAdapterView bannerView;
    private NativeAdAdapterView nativeAdView;
    private InterstitialAdAdapter interstitialAd;
    private Observer applicationWillEnterForegroundNotificationObserver = new Observer() {
        @Override
        public void update(Observable observable, Object o) {
            if (Common.getCurrentActivity() == BaseActivity.this
                    && shouldShowInterstitialAdWhenApplicationEnterForeground()) {
                showInterstitialAd();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(tag, "onCreate");

        getInterstitialAd(); //预加载全屏广告

        NotificationCenter.defaultCenter().addObserver
                (AppStatusDetector.APPLICATION_WILL_ENTER_FOREGROUND_NOTIFICATION,
                        applicationWillEnterForegroundNotificationObserver);

    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(tag, "onStart");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(tag, "onResume");

        //友盟统计
        MobclickAgent.onResume(this);

        if (shouldShowBannerView()) {
            ViewGroup bannerViewContainer = getBannerViewContainer();
            onAddBannerView(getBannerView(), bannerViewContainer);
        }

        if (shouldShowNativeView()) {
            ViewGroup nativeViewContainer = getNativeViewContainer();
            onAddNativeView(getNativeAdView(), nativeViewContainer);
        }

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                incrementAdCounter();
//            }
//        }, 500); //延迟执行，避免获取到的当前Activity不是最终可以看见的Activity
        incrementAdCounter();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        Log.d(tag, "onAttachedToWindow");

//        autoPresentInterstitial();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        Log.d(tag, "onDetachedFromWindow");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //友盟统计
        MobclickAgent.onPause(this);

        Log.d(tag, "onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(tag, "onRestart");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(tag, "onStop");
    }

    @Override
    protected void onDestroy() {
        Log.d(tag, "onDestroy");
        NotificationCenter.defaultCenter().removeObserver
                (AppStatusDetector.APPLICATION_WILL_ENTER_FOREGROUND_NOTIFICATION,
                        applicationWillEnterForegroundNotificationObserver);

        super.onDestroy();
    }
/* 广告 */

    /**
     * 添加横幅广告到界面。
     *
     * @param bannerView 横幅广告
     * @param container  容纳横幅广告的容器布局
     */
    protected void onAddBannerView(View bannerView, ViewGroup container) {
        if (container != null) {
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            ViewParent parentView = bannerView.getParent();
            if (container.equals(parentView)) {
                Log.d(TAG, "no need add bannerView");
            } else {
                if (parentView instanceof ViewGroup) {
                    ((ViewGroup) parentView).removeView(bannerView);
                }
                container.addView(bannerView, layoutParams);
                Log.d(TAG, "add bannerView");
            }
        }
    }

    /**
     * 添加横幅广告到界面。
     *
     * @param nativeView 横幅广告
     * @param container  容纳横幅广告的容器布局
     */
    protected void onAddNativeView(View nativeView, ViewGroup container) {
        if (container != null) {
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            ViewParent parentView = nativeView.getParent();
            if (container.equals(parentView)) {
                Log.d(TAG, "no need add nativeView");
            } else {
                if (parentView instanceof ViewGroup) {
                    ((ViewGroup) parentView).removeView(nativeView);
                }
                container.addView(nativeView, layoutParams);
                Log.d(TAG, "add nativeView");
            }
        }
    }

    /**
     * 展示全屏广告
     */
    protected void showInterstitialAd() {
        InterstitialAdAdapter interstitialAd = getInterstitialAd();
        if (interstitialAd.isReady()) {
            interstitialAd.show();
            counter = 0;
        }
    }

    /**
     * 增加即将弹出广告的计数次数，达到一定次数就会弹出广告，然后重置为0
     */
    protected void incrementAdCounter() {
        counter++;

        autoPresentInterstitial();
    }

    protected void autoPresentInterstitial() {
        int numberOfTimesToPresentInterstitial = Preferences.getSharedPreference().getNumberOfTimesToPresentInterstitial();
        Log.d(TAG, "presentedTimes = " + counter + ", numberOfTimesToPresentInterstitial = " + numberOfTimesToPresentInterstitial);
        if (counter >= numberOfTimesToPresentInterstitial && shouldAutoPresentInterstitialAd()) {
            showInterstitialAd();
        }
    }

    /**
     * 获取横幅广告
     *
     * @return
     */
    protected BannerAdapterView getBannerView() {
        if (bannerView == null) {
            bannerView = AdManager.getSharedAdManager().getBannerAdapterView(new AdSize(320, 50), this, this);
        }
        return bannerView;
    }

    /**
     * 获取小型原生
     *
     * @return
     */
    protected NativeAdAdapterView getNativeAdView() {
        if (nativeAdView == null) {
            nativeAdView = getSharedAdManager().getNativeAdAdapterViewFor80H(new AdSize(320, 80), this, this);
        }
        return nativeAdView;
    }

    /**
     * 获取全屏广告
     *
     * @return
     */
    public InterstitialAdAdapter getInterstitialAd() {
//        if (interstitialAd == null) {
        interstitialAd = getSharedAdManager().getInterstitialAdAdapter(this, this);
//        }
        return interstitialAd;
    }

    /**
     * 横幅广告的布局容器。宽度参数建议为match_parent, 高度参数建议为wrap_content
     *
     * @return 用于容纳横幅广告的布局容器
     */
    protected ViewGroup getBannerViewContainer() {
        return null;
    }

    /**
     * 原生广告的布局容器。宽度参数建议为match_parent, 高度参数建议为wrap_content
     *
     * @return 用于容纳原生广告的布局容器
     */
    protected ViewGroup getNativeViewContainer() {
        return null;
    }

    /**
     * 是否显示横幅广告。默认为true
     *
     * @return
     */
    protected boolean shouldShowBannerView() {
        return CommonConfig.sharedCommonConfig.shouldShowBannerView;
    }

    /**
     * 是否显示小型原生广告。默认为false
     *
     * @return
     */
    protected boolean shouldShowNativeView() {
        return false;
    }

    /* 是否在页面跳转次数累计到一定次数后自动弹出全屏广告。默认是true */
    protected boolean shouldAutoPresentInterstitialAd() {
        return true;
    }

    /* 是否在应用从后台进入前台时弹出全屏广告，默认是true */
    protected boolean shouldShowInterstitialAdWhenApplicationEnterForeground() {
        return true;
    }

    /* BannerViewAdapter.Listener */

    @Override
    public void onAdClosed(BannerViewAdapter adapter) {
        Log.d(tag, "onAdClosed:" + adapter);
    }

    @Override
    public void onAdFailedToLoad(BannerViewAdapter adapter, int errorCode) {
        Log.d(tag, "onAdFailedToLoad:" + adapter + "errorCode = " + errorCode);
    }

    @Override
    public void onAdLeftApplication(BannerViewAdapter adapter) {
        Log.d(tag, "onAdLeftApplication:" + adapter);
    }

    @Override
    public void onAdOpened(BannerViewAdapter adapter) {
        Log.d(tag, "onAdOpened:" + adapter);
    }

    @Override
    public void onAdLoaded(BannerViewAdapter adapter) {
        Log.d(tag, "onAdLoaded:" + adapter);
    }

    /* NativeAdAdapterView.Listener */

    @Override
    public void onAdClosed(NativeAdViewAdapter adapter) {
        Log.d(tag, "onAdClosed:" + adapter);
    }

    @Override
    public void onAdFailedToLoad(NativeAdViewAdapter adapter, int errorCode) {
        Log.d(tag, "onAdFailedToLoad:" + adapter);
    }

    @Override
    public void onAdLeftApplication(NativeAdViewAdapter adapter) {
        Log.d(tag, "onAdLeftApplication:" + adapter);
    }

    @Override
    public void onAdOpened(NativeAdViewAdapter adapter) {
        Log.d(tag, "onAdOpened:" + adapter);
    }

    @Override
    public void onAdLoaded(NativeAdViewAdapter adapter) {
        Log.d(tag, "onAdLoaded:" + adapter);
    }

    /* InterstitialAdapter.Listener */

    @Override
    public void onAdClosed(InterstitialAdapter adapter) {
        Log.d(tag, "onAdClosed:" + adapter);
    }

    @Override
    public void onAdFailedToLoad(InterstitialAdapter adapter, int i) {
        Log.d(tag, "onAdFailedToLoad:" + adapter + "errorCode = " + i);
    }

    @Override
    public void onAdLeftApplication(InterstitialAdapter adapter) {
        Log.d(tag, "onAdLeftApplication:" + adapter);
    }

    @Override
    public void onAdOpened(InterstitialAdapter adapter) {
        Log.d(tag, "onAdOpened:" + adapter);
    }

    @Override
    public void onAdLoaded(InterstitialAdapter adapter) {
        Log.d(tag, "onAdLoaded:" + adapter);
    }
}
