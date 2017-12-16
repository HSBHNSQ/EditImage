package com.lafonapps.common.ad.adapter;

import android.view.View;

import com.lafonapps.common.ad.AdSize;

/**
 * Created by chenjie on 2017/7/5.
 */

public interface NativeAdViewAdapter extends AdAdapter {
    /* 构建内容 */
    public void build(AdModel adModel, AdSize adSize);

    public View getAdapterAdView();

    public Listener getListener();

    public void setListener(Listener listener);

    public static interface Listener {
        public void onAdClosed(NativeAdViewAdapter adapter);

        public void onAdFailedToLoad(NativeAdViewAdapter adapter, int errorCode);

        public void onAdLeftApplication(NativeAdViewAdapter adapter);

        public void onAdOpened(NativeAdViewAdapter adapter);

        public void onAdLoaded(NativeAdViewAdapter adapter);
    }
}
