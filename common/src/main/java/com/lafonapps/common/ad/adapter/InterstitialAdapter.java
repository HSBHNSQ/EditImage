package com.lafonapps.common.ad.adapter;

/**
 * Created by chenjie on 2017/7/5.
 */

public interface InterstitialAdapter extends AdAdapter {

    /* 显示广告 */
    public void show();

    public Object getAdapterAd();

    public Listener getListener();

    public void setListener(Listener listener);

    public static interface Listener {

        public void onAdClosed(InterstitialAdapter adapter);

        public void onAdFailedToLoad(InterstitialAdapter adapter, int i);

        public void onAdLeftApplication(InterstitialAdapter adapter);

        public void onAdOpened(InterstitialAdapter adapter);

        public void onAdLoaded(InterstitialAdapter adapter);
    }
}
