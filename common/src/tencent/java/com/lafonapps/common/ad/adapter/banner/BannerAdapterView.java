package com.lafonapps.common.ad.adapter.banner;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.lafonapps.common.Common;
import com.lafonapps.common.ad.AdSize;
import com.lafonapps.common.ad.adapter.AdAdapterLayout;
import com.lafonapps.common.ad.adapter.AdModel;
import com.lafonapps.common.ad.adapter.BannerViewAdapter;
import com.lafonapps.common.preferences.CommonConfig;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.BannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjie on 2017/7/5.
 */

public class BannerAdapterView extends AdAdapterLayout implements BannerViewAdapter {

    private static final String TAG = BannerAdapterView.class.getCanonicalName();
    public static final boolean REUSEABLE = false;
    //    private AdView adView;
    private BannerView adView;

    private Context context;
    private String[] debugDevices;
    private boolean ready;

    private List<Listener> allListeners = new ArrayList<>();

    public BannerAdapterView(Context context) {
        super(context);
        this.context = context;

        this.setTouchListener(new TouchListener() {
            @Override
            public boolean shouldComfirmBeforeDownloadApp() {
                return CommonConfig.sharedCommonConfig.shouldComfirmBeforeDownloadAppOnBannerViewClick;
            }

            @Override
            public Rect exceptRect() {
                return new Rect(0, 0, 18, 18);
            }
        });

    }

    @Override
    public void setDebugDevices(String[] debugDevices) {
        this.debugDevices = debugDevices.clone();
    }

    @Override
    public boolean isReady() {
        return this.ready;
    }

    @Override
    public void build(AdModel adModel, AdSize adSize) {

        this.adView = new BannerView(Common.getCurrentActivity(),
                ADSize.BANNER,
                CommonConfig.sharedCommonConfig.appID4Tencent,
                adModel.getTencentAdID());

        adView.setRefresh(120);
        adView.setShowClose(true);
        adView.setADListener(new BannerADListener() {
            @Override
            public void onNoAD(AdError error) {
                Log.d(TAG, "onNoAD:" + error.getErrorCode());
                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdFailedToLoad(BannerAdapterView.this, error.getErrorCode());
                }
            }

            @Override
            public void onADReceiv() {
                Log.d(TAG, "onADReceiv");

                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdLoaded(BannerAdapterView.this);
                }
            }

            @Override
            public void onADExposure() {
                Log.d(TAG, "onADExposure");
                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdOpened(BannerAdapterView.this);
                }
            }

            @Override
            public void onADClosed() {
                Log.d(TAG, "onADClosed");
                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdClosed(BannerAdapterView.this);
                }
            }

            @Override
            public void onADClicked() {
                Log.d(TAG, "onADClicked");

                resetComfirmed();
            }

            @Override
            public void onADLeftApplication() {
                Log.d(TAG, "onADLeftApplication");
                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdLeftApplication(BannerAdapterView.this);
                }
            }

            @Override
            public void onADOpenOverlay() {
                Log.d(TAG, "onADOpenOverlay");
            }

            @Override
            public void onADCloseOverlay() {
                Log.d(TAG, "onADCloseOverlay");
            }
        });

        this.addView(adView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void loadAd() {
        adView.loadAD();
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