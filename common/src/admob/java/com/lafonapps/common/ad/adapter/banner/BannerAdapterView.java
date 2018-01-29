package com.lafonapps.common.ad.adapter.banner;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.lafonapps.common.ad.AdSize;
import com.lafonapps.common.ad.adapter.AdAdapterLayout;
import com.lafonapps.common.ad.adapter.AdModel;
import com.lafonapps.common.ad.adapter.BannerViewAdapter;
import com.lafonapps.common.preferences.CommonConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjie on 2017/7/5.
 */

public class BannerAdapterView extends AdAdapterLayout implements BannerViewAdapter {

    private static final String TAG = BannerAdapterView.class.getCanonicalName();
    public static final boolean REUSEABLE = true;
    private AdView adView;
    private Context context;
    private String[] debugDevices;
    private boolean ready;

    private List<Listener> allListeners = new ArrayList<>();

    public BannerAdapterView(Context context) {
        super(context);
        this.context = context;
        this.adView = new AdView(context);

        this.setTouchListener(new TouchListener() {
            @Override
            public boolean shouldComfirmBeforeDownloadApp() {
                return CommonConfig.sharedCommonConfig.shouldComfirmBeforeDownloadAppOnBannerViewClick;
            }

            @Override
            public Rect exceptRect() {
                return new Rect();
            }
        });
    }

    @Override
    public void addView(View child, int width, int height) {
        super.addView(child, width, height);
    }

    @Override
    public void setDebugDevices(String[] debugDevices) {
        this.debugDevices = debugDevices;
    }

    @Override
    public boolean isReady() {
        return this.ready;
    }

    @Override
    public void build(AdModel adModel, AdSize adSize) {

        adView.setAdSize(com.google.android.gms.ads.AdSize.SMART_BANNER);
        adView.setAdUnitId(adModel.getAdmobAdID());
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Log.d(TAG, "onAdClosed");

                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdClosed(BannerAdapterView.this);
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                Log.d(TAG, "onAdFailedToLoad:" + i);

                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdFailedToLoad(BannerAdapterView.this, i);
                }
            }

            @Override
            public void onAdLeftApplication() {
                Log.d(TAG, "onAdLeftApplication");
                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdLeftApplication(BannerAdapterView.this);
                }
            }

            @Override
            public void onAdOpened() {
                Log.d(TAG, "onAdOpened");

                resetComfirmed();

                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdOpened(BannerAdapterView.this);
                }
            }

            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded");
                ready = true;
                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdLoaded(BannerAdapterView.this);
                }

//                new AnimationPerformer().performRandomAnimation(BannerAdapterView.this);
            }
        });

        this.addView(adView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
