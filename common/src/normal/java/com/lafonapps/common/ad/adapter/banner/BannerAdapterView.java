package com.lafonapps.common.ad.adapter.banner;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.lafonapps.common.ad.AdSize;
import com.lafonapps.common.ad.adapter.AdAdapterLayout;
import com.lafonapps.common.ad.adapter.AdModel;
import com.lafonapps.common.ad.adapter.BannerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjie on 2017/7/5.
 */

public class BannerAdapterView extends AdAdapterLayout implements BannerViewAdapter {

    private static final String TAG = BannerAdapterView.class.getCanonicalName();
    public static final boolean REUSEABLE = true;
    private View adView;
    private Context context;
    private String[] debugDevices;
    private boolean ready;
    private Listener listener;

    private List<Listener> allListeners = new ArrayList<>();

    public BannerAdapterView(Context context) {
        super(context);
        this.context = context;
        this.ready = true;
        this.adView = new View(context);
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
        this.addView(adView, new ViewGroup.LayoutParams(0, 0));
    }

    @Override
    public void loadAd() {

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
