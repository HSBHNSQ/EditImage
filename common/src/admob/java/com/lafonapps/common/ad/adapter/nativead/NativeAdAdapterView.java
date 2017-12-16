package com.lafonapps.common.ad.adapter.nativead;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.lafonapps.common.ad.AdSize;
import com.lafonapps.common.ad.adapter.AdModel;
import com.lafonapps.common.ad.adapter.NativeAdViewAdapter;
import com.lafonapps.common.ad.adapter.SupportMutableListenerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjie on 2017/7/5.
 */

public class NativeAdAdapterView extends FrameLayout implements NativeAdViewAdapter, SupportMutableListenerAdapter<NativeAdViewAdapter.Listener> {
    private static final String TAG = NativeAdAdapterView.class.getCanonicalName();

    private NativeExpressAdView adView;
    private Context context;
    private String[] debugDevices;
    private boolean ready;

    private List<Listener> allListeners = new ArrayList<>();

    public NativeAdAdapterView(Context context) {
        super(context);
        this.context = context;
        this.adView = new NativeExpressAdView(context);
    }

    @Override
    public void setDebugDevices(String[] debugDevices) {
        this.debugDevices = debugDevices;
    }

    @Override
    public boolean isReady() {
        return this.ready;
    }

    /**
     * 广告是否可以在多个界面重用
     */
    @Override
    public boolean reuseable() {
        return true;
    }

    @Override
    public void build(AdModel adModel, AdSize adSize) {

        adView.setAdSize(new com.google.android.gms.ads.AdSize(adSize.getWidth(), adSize.getHeight()));
//        adView.setAdSize(com.google.android.gms.ads.AdSize.MEDIUM_RECTANGLE);
        adView.setAdUnitId(adModel.getAdmobAdID());
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Log.d(TAG, "onAdClosed");

                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdClosed(NativeAdAdapterView.this);
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Log.d(TAG, "onAdFailedToLoad:" + i);

                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdFailedToLoad(NativeAdAdapterView.this, i);
                }
            }

            @Override
            public void onAdLeftApplication() {
                Log.d(TAG, "onAdLeftApplication");
                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdLeftApplication(NativeAdAdapterView.this);
                }
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "onAdOpened");
                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdOpened(NativeAdAdapterView.this);
                }
            }

            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded");
                ready = true;
                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdLoaded(NativeAdAdapterView.this);
                }
            }
        });

        this.addView(adView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void loadAd() {
        AdRequest.Builder requestBuilder = new AdRequest.Builder();
        if (this.debugDevices != null) {
            for (String testDevice : this.debugDevices) {
                requestBuilder.addTestDevice(testDevice);
            }
        }
        requestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        AdRequest adRequest = requestBuilder.build();

        this.adView.loadAd(adRequest);
    }

    @Override
    public View getAdapterAdView() {
        return adView;
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
