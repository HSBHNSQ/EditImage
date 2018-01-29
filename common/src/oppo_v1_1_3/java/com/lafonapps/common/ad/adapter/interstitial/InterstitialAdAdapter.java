package com.lafonapps.common.ad.adapter.interstitial;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.lafonapps.common.Common;
import com.lafonapps.common.R;
import com.lafonapps.common.ad.adapter.AdModel;
import com.lafonapps.common.ad.adapter.InterstitialAdapter;
import com.oppo.mobad.ad.InterstitialAd;
import com.oppo.mobad.listener.IInterstitialAdListener;
import com.qq.e.ads.interstitial.InterstitialAD;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjie on 2017/7/5.
 */

public class InterstitialAdAdapter implements InterstitialAdapter {

    private static final String TAG = InterstitialAdAdapter.class.getCanonicalName();
    public static final boolean REUSEABLE = false;
    private InterstitialAd interstitialAd;
    private Context context;
    private AdModel adModel;
    private boolean ready;
    private String[] debugDevices;
    private List<Listener> allListeners = new ArrayList<>();
    private RelativeLayout adContainer;

    private int retryDelayForFailed;

    public InterstitialAdAdapter(Context context) {
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        adContainer = (RelativeLayout) inflater.inflate(R.layout.interstitial_ad, null);

    }

    /* 是否已经请求到广告可供展示 */
    @Override
    public boolean isReady() {
        return ready;
    }

    /* 构建内容 */
    public void build(AdModel adModel) {
        this.adModel = adModel;

        interstitialAd = new InterstitialAd(Common.getCurrentActivity(), adModel.getOppoAdID(), new IInterstitialAdListener() {
            @Override
            public void onAdDismissed() {
                Log.d(TAG, "onAdClosed");

                ready = false;

                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdClosed(InterstitialAdAdapter.this);
                }

                if (InterstitialAdActivity.adListener != null) {
                    InterstitialAdActivity.adListener.onAdDismissed();
                }

                reloadAd();
            }

            @Override
            public void onAdShow() {
                Log.d(TAG, "onAdOpened");

                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdOpened(InterstitialAdAdapter.this);
                }

                if (InterstitialAdActivity.adListener != null) {
                    InterstitialAdActivity.adListener.onAdShow();
                }
            }

            @Override
            public void onAdFailed(String s) {
                Log.w(TAG, "onAdError:" + s);

                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdFailedToLoad(InterstitialAdAdapter.this, -1);
                }

                if (InterstitialAdActivity.adListener != null) {
                    InterstitialAdActivity.adListener.onAdFailed(s);
                }

                retryDelayForFailed += 2000; //延迟时间增加2秒

                //延迟一段时间后重新加载
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadAd();
                    }
                }, retryDelayForFailed);
            }

            @Override
            public void onAdReady() {
                Log.d(TAG, "onAdLoaded");

                ready = true;
                retryDelayForFailed = 0;

                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdLoaded(InterstitialAdAdapter.this);
                }

                if (InterstitialAdActivity.adListener != null) {
                    InterstitialAdActivity.adListener.onAdReady();
                }
            }

            @Override
            public void onAdClick() {
                Log.d(TAG, "onAdLeftApplication");
                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdLeftApplication(InterstitialAdAdapter.this);
                }

                if (InterstitialAdActivity.adListener != null) {
                    InterstitialAdActivity.adListener.onAdClick();
                }
            }

            @Override
            public void onVerify(int i, String s) {
                Log.d(TAG, "onVerify:" + i + " " + s);

                if (InterstitialAdActivity.adListener != null) {
                    InterstitialAdActivity.adListener.onVerify(i, s);
                }
            }
        });
    }

    /* 加载广告 */
    @Override
    public void loadAd() {
        interstitialAd.loadAd();
    }

    private void reloadAd() {
        build(adModel);
        loadAd();
    }

//    @Override
//    public void show(Activity activity) {
//        if (ready) {
//            Activity currentActivity = activity;//Common.getCurrentActivity();
//            if (currentActivity != null) {
//                try {
//                    Field mActivity = ReflectionUtils.findField(interstitialAd.getClass(), "mActivity", Activity.class);
//                    mActivity.setAccessible(true); //为 true 则表示反射的对象在使用时取消 Java 语言访问检查
//                    mActivity.set(interstitialAd, currentActivity);
//                    interstitialAd.showAd(adContainer);
//                }  catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                Log.d(TAG, "Current Activity is null, can not show interstitialAd!");
//            }
//        } else {
//            Log.d(TAG, "interstitialAd not ready");
//        }
//    }

    @Override
    public void show(Activity activity) {
        if (ready) {
            Activity currentActivity = activity;//Common.getCurrentActivity();
            if (currentActivity != null) {
                try {
                    /* 更新SDK后，需要检查以下代码是否有效 */
                    Field channelField = ReflectionUtils.findField(interstitialAd.getClass(), "channel", int.class);
                    channelField.setAccessible(true); //为 true 则表示反射的对象在使用时取消 Java 语言访问检查
                    int channel = channelField.getInt(interstitialAd);
                    if (channel == 1) { //百度广告
                        Log.d(TAG, "show baidu interstitialAd");

                        //修改弹出的Activity
                        Field mActivityField = ReflectionUtils.findField(interstitialAd.getClass(), "mActivity", Activity.class);
                        mActivityField.setAccessible(true); //为 true 则表示反射的对象在使用时取消 Java 语言访问检查
                        mActivityField.set(interstitialAd, currentActivity);
                        interstitialAd.showAd(adContainer);

                    } else if (channel == 2) { //腾讯广点通广告
                        Log.d(TAG, "show tencent guangdiantong interstitialAd");

                        //获取腾讯广点通广告实例
                        Field mGDTInterstitialADField = ReflectionUtils.findField(interstitialAd.getClass(), "mGDTInterstitialAD", InterstitialAD.class);
                        mGDTInterstitialADField.setAccessible(true); //为 true 则表示反射的对象在使用时取消 Java 语言访问检查
                        InterstitialAD mGDTInterstitialAD = (InterstitialAD)mGDTInterstitialADField.get(interstitialAd);
                        if (mGDTInterstitialAD != null) {
                            mGDTInterstitialAD.showAsPopupWindow(currentActivity);
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "Current Activity is null, can not show interstitialAd!");
            }
        } else {
            Log.d(TAG, "interstitialAd not ready");
        }
    }

    @Override
    public Object getAdapterAd() {
        return this.interstitialAd;
    }

    @Override
    public void setDebugDevices(String[] debugDevices) {
        this.debugDevices = debugDevices;
    }

    /* SupportMutableListenerAdapter */

    @Override
    public synchronized void addListener(Listener listener) {
        if (listener != null && !allListeners.contains(listener)) {
            allListeners.add(listener);
            Log.d(TAG, "addListener:" + listener);
        }
    }

    @Override
    public synchronized void removeListener(Listener listener) {
        if (allListeners.contains(listener)) {
            allListeners.remove(listener);
            Log.d(TAG, "removeListener:" + listener);
        }
    }

    @Override
    public Listener[] getAllListeners() {
        return allListeners.toArray(new Listener[allListeners.size()]);
    }

    /**
     * 用于展示全屏广告的活动
     */
    public static class InterstitialAdActivity extends Activity {

        //TODO: 不好的设计，等待改进
        static InterstitialAd interstitialAd;
        static IInterstitialAdListener adListener;

        private RelativeLayout adContainer;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.interstitial_ad);
            adContainer = findViewById(R.id.ad_container);

            adListener = new IInterstitialAdListener() {
                @Override
                public void onAdDismissed() {

                    finish();
                }

                @Override
                public void onAdShow() {

                }

                @Override
                public void onAdFailed(String s) {

                }

                @Override
                public void onAdReady() {

                }

                @Override
                public void onAdClick() {

                }

                @Override
                public void onVerify(int i, String s) {

                }
            };
        }

        @Override
        public void onAttachedToWindow() {
            super.onAttachedToWindow();

            if (interstitialAd != null) {
                interstitialAd.showAd(adContainer);
            }
        }
    }
}
