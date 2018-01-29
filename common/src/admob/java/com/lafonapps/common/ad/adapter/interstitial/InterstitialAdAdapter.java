package com.lafonapps.common.ad.adapter.interstitial;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.lafonapps.common.ad.adapter.AdModel;
import com.lafonapps.common.ad.adapter.InterstitialAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjie on 2017/7/5.
 */

public class InterstitialAdAdapter implements InterstitialAdapter {

    private static final String TAG = InterstitialAdAdapter.class.getCanonicalName();
    public static final boolean REUSEABLE = true;
    private InterstitialAd interstitialAd;
    private Context context;
    private String[] debugDevices;
    private List<Listener> allListeners = new ArrayList<>();

    private int retryDelayForFailed;

    public InterstitialAdAdapter(Context context) {
        this.context = context;
        interstitialAd = new InterstitialAd(context);
    }

    /* 是否已经请求到广告可供展示 */
    public boolean isReady() {
        return interstitialAd.isLoaded();
    }

    /* 构建内容 */
    public void build(AdModel adModel) {
        interstitialAd.setAdUnitId(adModel.getAdmobAdID());
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Log.d(TAG, "onAdClosed");

                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdClosed(InterstitialAdAdapter.this);
                }
                loadAd();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Log.d(TAG, "onAdFailedToLoad:" + i);

                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdFailedToLoad(InterstitialAdAdapter.this, i);
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
            public void onAdLeftApplication() {
                Log.d(TAG, "onAdLeftApplication");
                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdLeftApplication(InterstitialAdAdapter.this);
                }
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "onAdOpened");
                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdOpened(InterstitialAdAdapter.this);
                }
            }

            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded");

                retryDelayForFailed = 0;

                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdLoaded(InterstitialAdAdapter.this);
                }
            }
        });
    }

    /* 加载广告 */
    public void loadAd() {
        AdRequest.Builder requestBuilder = new AdRequest.Builder();
        if (this.debugDevices != null) {
            for (String testDevice : this.debugDevices) {
                requestBuilder.addTestDevice(testDevice);
            }
        }
        requestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        AdRequest adRequest = requestBuilder.build();

        this.interstitialAd.loadAd(adRequest);
    }

    @Override
    public void show(Activity activity) {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        } else {
            Log.d(TAG, "interstitialAd not ready");
        }
    }

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
}
