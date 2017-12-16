package com.lafonapps.common.ad.adapter.interstitial;

import android.content.Context;
import android.util.Log;

import com.lafonapps.common.Common;
import com.lafonapps.common.ad.adapter.AdModel;
import com.lafonapps.common.ad.adapter.InterstitialAdapter;
import com.lafonapps.common.ad.adapter.SupportMutableListenerAdapter;
import com.lafonapps.common.preferences.Preferences;
import com.qq.e.ads.interstitial.InterstitialAD;
import com.qq.e.ads.interstitial.InterstitialADListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjie on 2017/7/5.
 */

public class InterstitialAdAdapter implements InterstitialAdapter, SupportMutableListenerAdapter<InterstitialAdapter.Listener> {

    private static final String TAG = InterstitialAdAdapter.class.getCanonicalName();

    private InterstitialAD interstitialAd;
    private Context context;
    private String[] debugDevices;
    private List<Listener> allListeners = new ArrayList<>();
    private InterstitialADListener listener = null;
    private boolean ready = false;

    public InterstitialAdAdapter(Context context) {
        this.context = context;
        if (interstitialAd == null) {
            interstitialAd = new InterstitialAD(Common.getCurrentActivity(),
                    Preferences.getSharedPreference().getAppID4Tencent(),
                    Preferences.getSharedPreference().getInterstitialAdUnitID4Tencent());
        }
    }

    /* 是否已经请求到广告可供展示 */
    @Override
    public boolean isReady() {
        return ready;
    }

    /**
     * 广告是否可以在多个界面重用
     */
    @Override
    public boolean reuseable() {
        return true;
    }

    /* 构建内容 */
    public void build(AdModel adModel) {
        listener = new InterstitialADListener() {
            @Override
            public void onADReceive() {
                Log.d(TAG, "onADReceive");
                InterstitialAdAdapter.Listener[] listeners = getAllListeners();
                for (InterstitialAdAdapter.Listener listener : listeners) {
                    listener.onAdLoaded(InterstitialAdAdapter.this);
                }
                ready = true;
            }

            @Override
            public void onNoAD(int i) {
                Log.d(TAG, "onNoAD:" + i);
                InterstitialAdAdapter.Listener[] listeners = getAllListeners();
                for (InterstitialAdAdapter.Listener listener : listeners) {
                    listener.onAdFailedToLoad(InterstitialAdAdapter.this, i);
                }
            }

            @Override
            public void onADOpened() {
                Log.d(TAG, "onADOpened");
                InterstitialAdAdapter.Listener[] listeners = getAllListeners();
                for (InterstitialAdAdapter.Listener listener : listeners) {
                    listener.onAdOpened(InterstitialAdAdapter.this);
                }
            }

            @Override
            public void onADExposure() {
                Log.d(TAG, "onADExposure");
                InterstitialAdAdapter.Listener[] listeners = getAllListeners();
                for (InterstitialAdAdapter.Listener listener : listeners) {
                    listener.onAdOpened(InterstitialAdAdapter.this);
                }
            }

            @Override
            public void onADClicked() {
                Log.d(TAG, "onADClicked");
            }

            @Override
            public void onADLeftApplication() {
                Log.d(TAG, "onADLeftApplication");
                InterstitialAdAdapter.Listener[] listeners = getAllListeners();
                for (InterstitialAdAdapter.Listener listener : listeners) {
                    listener.onAdLeftApplication(InterstitialAdAdapter.this);
                }
            }

            @Override
            public void onADClosed() {
                Log.d(TAG, "onADClosed");
                InterstitialAdAdapter.Listener[] listeners = getAllListeners();
                for (InterstitialAdAdapter.Listener listener : listeners) {
                    listener.onAdClosed(InterstitialAdAdapter.this);
                }
                loadAd();
            }
        };
    }

    /* 加载广告 */
    public void loadAd() {
        if (interstitialAd != null) {
            interstitialAd = new InterstitialAD(Common.getCurrentActivity(),
                    Preferences.getSharedPreference().getAppID4Tencent(),
                    Preferences.getSharedPreference().getInterstitialAdUnitID4Tencent());
            interstitialAd.setADListener(listener);
        }
        Log.d(TAG, "loadAd");
        interstitialAd.loadAD();
    }

    public void show() {
        if (ready) {
            interstitialAd.show();
            ready = false;
        } else {
            Log.d(TAG, "Ad is not ready");
        }
    }

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
}
