package com.lafonapps.common.ad.adapter.banner;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.lafonapps.common.ad.AdSize;
import com.lafonapps.common.ad.adapter.AdModel;
import com.lafonapps.common.ad.adapter.BannerViewAdapter;

import java.util.Set;

/**
 * Created by chenjie on 2017/7/5.
 */

public class BannerAdapterView extends FrameLayout implements BannerViewAdapter {

    private AdView adView;
    private Context context;
    private Set<String> debugDevices;
    private boolean ready;
    private boolean loadCompleted;
    private BannerViewAdapter.Listener listener;

    public BannerAdapterView(Context context) {
        super(context);
        this.context = context;
        this.adView = new AdView(context);
    }

    @Override
    public void setDebugDevices(Set<String> debugDevices) {
        this.debugDevices = debugDevices;
    }

    @Override
    public boolean isReady() {
        return this.ready;
    }

    @Override
    public boolean isLoadCompleted() {
        return this.loadCompleted;
    }

    @Override
    public void build(AdModel adModel, AdSize adSize) {

        adView.setAdSize(com.google.android.gms.ads.AdSize.SMART_BANNER);
        adView.setAdUnitId(adModel.getAdmobAdID());
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                if (listener != null) {
                    listener.onAdClosed();
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                loadCompleted = true;
                if (listener != null) {
                    listener.onAdFailedToLoad(i);
                }
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                if (listener != null) {
                    listener.onAdLeftApplication();
                }
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                if (listener != null) {
                    listener.onAdOpened();
                }
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                ready = true;
                loadCompleted = true;
                if (listener != null) {
                    listener.onAdLoaded();
                }
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
        return this;
    }

    @Override
    public Listener getListener() {
        return this.listener;
    }

    @Override
    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
