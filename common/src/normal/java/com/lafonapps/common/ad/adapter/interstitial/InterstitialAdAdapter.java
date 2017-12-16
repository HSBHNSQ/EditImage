package com.lafonapps.common.ad.adapter.interstitial;

import android.content.Context;
import android.util.Log;

import com.lafonapps.common.ad.adapter.AdModel;
import com.lafonapps.common.ad.adapter.InterstitialAdapter;
import com.lafonapps.common.ad.adapter.SupportMutableListenerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjie on 2017/7/5.
 */

public class InterstitialAdAdapter implements InterstitialAdapter, SupportMutableListenerAdapter<InterstitialAdapter.Listener> {

    private static final String TAG = InterstitialAdAdapter.class.getCanonicalName();

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

    /* 构建内容 */
    public void build(AdModel adModel) {

    }

    /* 加载广告 */
    public void loadAd() {

    }

    public void show() {

    }

    public Object getAdapterAd() {
        return interstitialAd;
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
