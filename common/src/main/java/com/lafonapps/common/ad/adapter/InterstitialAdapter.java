package com.lafonapps.common.ad.adapter;

import android.app.Activity;

/**
 * Created by chenjie on 2017/7/5.
 */

public interface InterstitialAdapter extends AdAdapter<InterstitialAdapter.Listener> {

    /* 显示广告 */
    public void show(Activity activity);

    public Object getAdapterAd();

    public static interface Listener {

        public void onAdClosed(InterstitialAdapter adapter);

        public void onAdFailedToLoad(InterstitialAdapter adapter, int i);

        public void onAdLeftApplication(InterstitialAdapter adapter);

        public void onAdOpened(InterstitialAdapter adapter);

        public void onAdLoaded(InterstitialAdapter adapter);
    }
}
