package com.lafonapps.common.ad.adapter.banner;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lafonapps.common.ad.AdSize;
import com.lafonapps.common.ad.adapter.AdModel;
import com.lafonapps.common.ad.adapter.BannerViewAdapter;
import com.lafonapps.common.ad.adapter.SupportMutableListenerAdapter;
import com.lafonapps.common.utils.ViewUtil;
import com.xiaomi.ad.adView.BannerAd;
import com.xiaomi.ad.common.pojo.AdEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjie on 2017/7/5.
 */

public class BannerAdapterView extends FrameLayout implements BannerViewAdapter, SupportMutableListenerAdapter<BannerViewAdapter.Listener> {

    private static final String TAG = BannerAdapterView.class.getCanonicalName();

    private BannerAd adView;
    private Context context;
    private AdModel adModel;
    private String[] debugDevices;
    private boolean ready;
    private List<Listener> allListeners = new ArrayList<>();
    private boolean shouldLoad;
    private boolean inWindow;

    public BannerAdapterView(Context context) {
        super(context);
        this.context = context;
        this.adView = new BannerAd(context, this, new BannerAd.BannerListener() {
            @Override
            public void onAdEvent(AdEvent adEvent) {
                Log.d(TAG, "onAdEvent:" + adEvent.name());

                Listener[] listeners = getAllListeners();

                if (adEvent.mType == AdEvent.TYPE_LOAD) {
                    ready = true;
                    for (Listener listener : listeners) {
                        listener.onAdLoaded(BannerAdapterView.this);
                    }
                } else if (adEvent.mType == AdEvent.TYPE_SKIP) {
                    for (Listener listener : listeners) {
                        listener.onAdClosed(BannerAdapterView.this);
                    }
                } else if (adEvent.mType == AdEvent.TYPE_LOAD_FAIL) {
                    for (Listener listener : listeners) {
                        listener.onAdFailedToLoad(BannerAdapterView.this, adEvent.mType);
                    }
                } else if (adEvent.mType == AdEvent.TYPE_CLICK) {
                    for (Listener listener : listeners) {
                        listener.onAdOpened(BannerAdapterView.this);
                    }
                } else if (adEvent.mType == AdEvent.TYPE_FINISH) {
                    for (Listener listener : listeners) {
                        listener.onAdClosed(BannerAdapterView.this);
                    }
                } else if (adEvent.mType == AdEvent.TYPE_APP_LAUNCH_SUCCESS) {
                    for (Listener listener : listeners) {
                        listener.onAdLeftApplication(BannerAdapterView.this);
                    }
                }
            }
        });
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
        this.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //避免误点概率，增加上下间距
        this.setPadding(0, ViewUtil.dp2px(4), 0, ViewUtil.dp2px(4));
    }

    @Override
    public void loadAd() {
        try {
            this.adView.show(adModel.getXiaomiAdID());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (inWindow) {
//            this.adView.show(adModel.getXiaomiAdID());
//            shouldLoad = false;
//        } else {
//            shouldLoad = true;
//        }
    }

    @Override
    public View getAdapterAdView() {
        return this;
    }

    @Override
    public Listener getListener() {
        throw new RuntimeException("Please call getAllListeners() method instead!");
    }

    @Override
    public void setListener(Listener listener) {
        throw new RuntimeException("Please call addListener() method instead!");
    }

//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        inWindow = true;
//        if (shouldLoad) {
//            this.adView.show(adModel.getXiaomiAdID());
//        }
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        inWindow = false;
//    }

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
