package com.lafonapps.common.ad.adapter.banner;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lafonapps.common.Common;
import com.lafonapps.common.ad.AdSize;
import com.lafonapps.common.ad.adapter.AdModel;
import com.lafonapps.common.ad.adapter.BannerViewAdapter;
import com.lafonapps.common.ad.adapter.SupportMutableListenerAdapter;
import com.lafonapps.common.preferences.Preferences;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.BannerADListener;
import com.qq.e.ads.banner.BannerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjie on 2017/7/5.
 */

public class BannerAdapterView extends FrameLayout implements BannerViewAdapter, SupportMutableListenerAdapter<BannerViewAdapter.Listener> {

    private static final String TAG = BannerAdapterView.class.getCanonicalName();

    //    private AdView adView;
    private BannerView adView;

    private Context context;
    private String[] debugDevices;
    private boolean ready;

    private List<Listener> allListeners = new ArrayList<>();

    public BannerAdapterView(Context context) {
        super(context);
        this.context = context;
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
        return false;
    }

    @Override
    public void build(AdModel adModel, AdSize adSize) {

        this.adView = new BannerView(Common.getCurrentActivity(),
                ADSize.BANNER,
                Preferences.getSharedPreference().getAppID4Tencent(),
                adModel.getTencentAdID());

        adView.setRefresh(30);
        adView.setShowClose(true);
        adView.setADListener(new BannerADListener() {
            @Override
            public void onNoAD(int i) {
                Log.d(TAG, "onNoAD:" + i);
                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdFailedToLoad(BannerAdapterView.this, i);
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
