package com.lafonapps.common.ad.adapter.nativead;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lafonapps.common.ad.AdSize;
import com.lafonapps.common.ad.adapter.AdModel;
import com.lafonapps.common.ad.adapter.NativeAdViewAdapter;
import com.lafonapps.common.ad.adapter.SupportMutableListenerAdapter;
import com.lafonapps.common.utils.ViewUtil;
import com.xiaomi.ad.AdListener;
import com.xiaomi.ad.NativeAdInfoIndex;
import com.xiaomi.ad.NativeAdListener;
import com.xiaomi.ad.adView.StandardNewsFeedAd;
import com.xiaomi.ad.common.pojo.AdError;
import com.xiaomi.ad.common.pojo.AdEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjie on 2017/7/5.
 */

public class NativeAdAdapterView extends FrameLayout implements NativeAdViewAdapter, SupportMutableListenerAdapter<NativeAdViewAdapter.Listener> {
    private static final String TAG = NativeAdAdapterView.class.getCanonicalName();

    private StandardNewsFeedAd standardNewsFeedAd;
    private View adView;
    private Context context;
    private AdModel adModel;
    private String[] debugDevices;
    private boolean ready;

    private List<Listener> allListeners = new ArrayList<>();

    public NativeAdAdapterView(Context context) {
        super(context);
        this.context = context;
        this.standardNewsFeedAd = new StandardNewsFeedAd(context);
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
        this.adModel = adModel;

//        this.addView(adView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void loadAd() {
        standardNewsFeedAd.requestAd(adModel.getXiaomiAdID(), 1, new NativeAdListener() {
            @Override
            public void onNativeInfoFail(AdError adError) {
                Log.d(TAG, "onNativeInfoFail:" + adError);

                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdFailedToLoad(NativeAdAdapterView.this, adError.value());
                }
            }

            @Override
            public void onNativeInfoSuccess(List<NativeAdInfoIndex> list) {
                NativeAdInfoIndex response = list.get(0);
                int width = getWidth() > 0 ? getWidth() : ViewUtil.getDeviceWidthInPx();
                standardNewsFeedAd.buildViewAsync(response, width, new AdListener() {
                    @Override
                    public void onAdError(AdError adError) {
                        Log.d(TAG, "onAdError:" + adError);

                        Listener[] listeners = getAllListeners();
                        for (Listener listener : listeners) {
                            listener.onAdFailedToLoad(NativeAdAdapterView.this, adError.value());
                        }

                        removeAllViews();
                    }

                    @Override
                    public void onAdEvent(AdEvent adEvent) {
                        Log.d(TAG, "onAdEvent:" + adEvent.name());

                        Listener[] listeners = getAllListeners();
                        if (adEvent.mType == AdEvent.TYPE_SKIP) {
                            for (Listener listener : listeners) {
                                listener.onAdClosed(NativeAdAdapterView.this);
                            }
                        } else if (adEvent.mType == AdEvent.TYPE_CLICK) {
                            for (Listener listener : listeners) {
                                listener.onAdOpened(NativeAdAdapterView.this);
                            }
                        } else if (adEvent.mType == AdEvent.TYPE_FINISH) {
                            for (Listener listener : listeners) {
                                listener.onAdClosed(NativeAdAdapterView.this);
                            }
                        } else if (adEvent.mType == AdEvent.TYPE_APP_LAUNCH_SUCCESS) {
                            for (Listener listener : listeners) {
                                listener.onAdLeftApplication(NativeAdAdapterView.this);
                            }
                        }
                    }

                    @Override
                    public void onAdLoaded() {
                        Log.d(TAG, "onAdLoaded");
                        ready = true;
                        Listener[] listeners = getAllListeners();
                        for (Listener listener : listeners) {
                            listener.onAdLoaded(NativeAdAdapterView.this);
                        }
                    }

                    @Override
                    public void onViewCreated(View view) {
                        Log.e(TAG, "onViewCreated:" + view);

                        adView = view;

                        removeAllViews();
                        addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    }
                });
            }
        });
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
