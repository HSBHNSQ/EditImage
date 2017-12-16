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
import com.lafonapps.common.ad.adapter.SupportMutableListenerAdapter;
import com.oppo.mobad.ad.InterstitialAd;
import com.oppo.mobad.listener.IInterstitialAdListener;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjie on 2017/7/5.
 */

public class InterstitialAdAdapter implements InterstitialAdapter, SupportMutableListenerAdapter<InterstitialAdapter.Listener> {

    private static final String TAG = InterstitialAdAdapter.class.getCanonicalName();
    private static final String START_ACTIVITY_EXTRA_LISTENR = "START_ACTIVITY_EXTRA_LISTENR";
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

    @Override
    public boolean reuseable() {
        return true;
    }

    /* 构建内容 */
    public void build(AdModel adModel) {
        this.adModel = adModel;

        interstitialAd = new InterstitialAd(Common.getCurrentActivity(), adModel.getOppoAdID(), new IInterstitialAdListener() {
            @Override
            public void onAdDismissed() {
                Log.d(TAG, "onAdClosed");

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

                retryDelayForFailed += 2; //延迟时间增加2秒

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

    @Override
    public void show() {
        if (ready) {
            Activity currentActivity = Common.getCurrentActivity();
            if (currentActivity != null) {
                try {
                    Field mActivity = ReflectionUtils.findField(interstitialAd.getClass(), "mActivity", Activity.class);
                    mActivity.setAccessible(true); //为 true 则表示反射的对象在使用时取消 Java 语言访问检查
                    mActivity.set(interstitialAd, currentActivity);
                    interstitialAd.showAd(adContainer);
                }  catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "Current Activity is null, can not show interstitialAd!");
            }
//            InterstitialAdActivity.interstitialAd = interstitialAd;
//            Activity currentActivity = Common.getCurrentActivity();
//            Intent intent = new Intent(currentActivity, InterstitialAdActivity.class);
//            currentActivity.startActivity(intent);
//            interstitialAd.showAd(adContainer);
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

    @Override
    public Listener getListener() {
        throw new RuntimeException("Please call getAllListeners() method instead!");
    }

    @Override
    public void setListener(Listener listener) {
        throw new RuntimeException("Please call addListener() method instead!");
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
