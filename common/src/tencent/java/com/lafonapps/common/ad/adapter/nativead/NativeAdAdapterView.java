package com.lafonapps.common.ad.adapter.nativead;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lafonapps.common.Common;
import com.lafonapps.common.ad.AdSize;
import com.lafonapps.common.ad.adapter.AdModel;
import com.lafonapps.common.ad.adapter.NativeAdViewAdapter;
import com.lafonapps.common.ad.adapter.SupportMutableListenerAdapter;
import com.lafonapps.common.preferences.CommonConfig;
import com.qq.e.ads.nativ.NativeAD;
import com.qq.e.ads.nativ.NativeADDataRef;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjie on 2017/7/5.
 */

public class NativeAdAdapterView extends FrameLayout implements NativeAdViewAdapter, SupportMutableListenerAdapter<NativeAdViewAdapter.Listener> {
    private static final String TAG = NativeAdAdapterView.class.getCanonicalName();

    private NativeAD adView;
    private Context context;
    private String[] debugDevices;
    private boolean ready;

    private List<Listener> allListeners = new ArrayList<>();

    public NativeAdAdapterView(Context context) {
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
        return true;
    }

    @Override
    public void build(AdModel adModel, AdSize adSize) {
        NativeAD.NativeAdListener listener = new NativeAD.NativeAdListener() {
            @Override
            public void onADLoaded(List<NativeADDataRef> list) {
                Log.d(TAG, "onADLoaded");
                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdLoaded(NativeAdAdapterView.this);
                }
            }

            @Override
            public void onNoAD(int i) {
                Log.d(TAG, "onNoAD:"+i);
                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdFailedToLoad(NativeAdAdapterView.this,i);
                }
            }

            @Override
            public void onADStatusChanged(NativeADDataRef nativeADDataRef) {
                Log.d(TAG, "onADStatusChanged");
                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdLeftApplication(NativeAdAdapterView.this);
                }
            }

            @Override
            public void onADError(NativeADDataRef nativeADDataRef, int i) {
                Log.d(TAG, "onADError:"+i);
                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdFailedToLoad(NativeAdAdapterView.this,i);
                }
            }
        };

        this.adView = new NativeAD(context,
                CommonConfig.sharedCommonConfig.appID4Tencent,
                CommonConfig.sharedCommonConfig.nativeAdUnitID250H4Tencent,
                listener);

    }

    @Override
    public void loadAd() {
        adView.loadAD(1);
    }

    @Override
    public View getAdapterAdView() {
        return null;
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
