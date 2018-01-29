package com.lafonapps.common.ad.adapter.interstitial;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.lafonapps.common.ad.adapter.AdModel;
import com.lafonapps.common.ad.adapter.InterstitialAdapter;
import com.oppo.mobad.api.ad.InterstitialAd;
import com.oppo.mobad.api.listener.IInterstitialAdListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjie on 2017/7/5.
 */

public class InterstitialAdAdapter implements InterstitialAdapter {

    private static final String TAG = InterstitialAdAdapter.class.getCanonicalName();
    public static final boolean REUSEABLE = false;
    private InterstitialAd interstitialAd;
//    private Context context;
    private Activity activity;
    private AdModel adModel;
    private boolean ready;
    private String[] debugDevices;
    private List<Listener> allListeners = new ArrayList<>();

    private int retryDelayForFailed;

//    public InterstitialAdAdapter(Context context) {
//        this.context = context;
//
//    }

    public InterstitialAdAdapter(Activity activity) {
        this.activity = activity;

    }

    /* 是否已经请求到广告可供展示 */
    @Override
    public boolean isReady() {
        return ready;
    }

    /* 构建内容 */
    public void build(AdModel adModel) {
        this.adModel = adModel;
        if (interstitialAd != null) {
            interstitialAd.destroyAd();
        }
        interstitialAd = new InterstitialAd(activity, adModel.getOppoAdID());
        interstitialAd.setAdListener(new IInterstitialAdListener() {
            @Override
            public void onAdShow() {
                Log.d(TAG, "onAdOpened");

                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdOpened(InterstitialAdAdapter.this);
                }
            }

            @Override
            public void onAdFailed(String s) {
                Log.w(TAG, "onAdError:" + s);

                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdFailedToLoad(InterstitialAdAdapter.this, -1);
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
            }

            @Override
            public void onAdClick() {
                Log.d(TAG, "onAdLeftApplication");
                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdLeftApplication(InterstitialAdAdapter.this);
                }
            }

            @Override
            public void onAdClose() {
                Log.d(TAG, "onAdClosed");

                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdClosed(InterstitialAdAdapter.this);
                }

                reloadAd();
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
    public void show(Activity activity) {
        if (ready) {
            interstitialAd.showAd();
            ready = false;
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

}
