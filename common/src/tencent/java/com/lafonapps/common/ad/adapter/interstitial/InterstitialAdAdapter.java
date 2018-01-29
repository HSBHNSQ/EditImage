package com.lafonapps.common.ad.adapter.interstitial;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

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
    private Object interstitialAd;
    private Context context;
    private String[] debugDevices;
    private boolean ready;
    private InterstitialAdapter.Listener listener;

    private List<Listener> allListeners = new ArrayList<>();

    public InterstitialAdAdapter(Context context) {
        this.context = context;
        this.ready = true;
        interstitialAd = new Object();
    }

    /* 是否已经请求到广告可供展示 */
    @Override
    public boolean isReady() {
        return this.ready;
    }

    /* 构建内容 */
    public void build(AdModel adModel) {

    }


    /* 加载广告 */
    @Override
    public void loadAd() {

    }

    @Override
    public void show(Activity activity) {

    }

    public Object getAdapterAd() {
        return interstitialAd;
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
