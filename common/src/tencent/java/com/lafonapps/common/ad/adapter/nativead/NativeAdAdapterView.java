package com.lafonapps.common.ad.adapter.nativead;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lafonapps.common.ad.AdSize;
import com.lafonapps.common.ad.adapter.AdModel;
import com.lafonapps.common.ad.adapter.NativeAdViewAdapter;
import com.lafonapps.common.preferences.CommonConfig;
import com.lafonapps.common.utils.ViewUtil;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjie on 2017/7/5.
 */

public class NativeAdAdapterView extends FrameLayout implements NativeAdViewAdapter {
    private static final String TAG = NativeAdAdapterView.class.getCanonicalName();
    public static final boolean REUSEABLE = false;
    private NativeExpressAD nativeExpressAD;
    private NativeExpressADView adView;

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
        this.debugDevices = debugDevices.clone();
    }

    @Override
    public boolean isReady() {
        return this.ready;
    }

    @Override
    public void build(AdModel adModel, AdSize adSize) {
        nativeExpressAD = new NativeExpressAD(context, new ADSize(adSize.getWidth(), adSize.getHeight()), CommonConfig.sharedCommonConfig.appID4Tencent, adModel.getTencentAdID(), new NativeExpressAD.NativeExpressADListener() {
                    @Override
                    public void onNoAD(AdError error) {
                        Log.w(TAG, "onNoAD:" + error.getErrorCode() + "msg:" + error.getErrorMsg());

                        Listener[] listeners = getAllListeners();
                        for (Listener listener : listeners) {
                            listener.onAdFailedToLoad(NativeAdAdapterView.this, error.getErrorCode());
                        }
                    }

                    @Override
                    public void onADLoaded(List<NativeExpressADView> list) {
                        if (list.size() > 0) {
                            Log.d(TAG, "onADLoaded");

                            // 释放前一个NativeExpressADView的资源
                            if (adView != null) {
                                adView.destroy();
                            }

                            adView = list.get(0);
                            // 保证View被绘制的时候是可见的，否则将无法产生曝光和收益。
                            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            layoutParams.gravity = Gravity.CENTER;
                            ViewUtil.addView(NativeAdAdapterView.this, adView, layoutParams);

                            adView.render();

                            Listener[] listeners = getAllListeners();
                            for (Listener listener : listeners) {
                                listener.onAdLoaded(NativeAdAdapterView.this);
                            }

                        } else {
                            Log.d(TAG, "onADLoaded, list.size == 0");

                            Listener[] listeners = getAllListeners();
                            for (Listener listener : listeners) {
                                listener.onAdFailedToLoad(NativeAdAdapterView.this, -100);
                            }
                        }
                    }

                    @Override
                    public void onRenderFail(NativeExpressADView nativeExpressADView) {
                        Log.w(TAG, "onRenderFail");

                        Listener[] listeners = getAllListeners();
                        for (Listener listener : listeners) {
                            listener.onAdFailedToLoad(NativeAdAdapterView.this, -101);
                        }
                    }

                    @Override
                    public void onRenderSuccess(NativeExpressADView nativeExpressADView) {
                        Log.d(TAG, "onRenderSuccess");

                        ready = true;
                    }

                    @Override
                    public void onADExposure(NativeExpressADView nativeExpressADView) {
                        Log.d(TAG, "onADExposure");

                    }

                    @Override
                    public void onADClicked(NativeExpressADView nativeExpressADView) {
                        Log.d(TAG, "onADClicked");

                        Listener[] listeners = getAllListeners();
                        for (Listener listener : listeners) {
                            listener.onAdOpened(NativeAdAdapterView.this);
                        }
                    }

                    @Override
                    public void onADClosed(NativeExpressADView nativeExpressADView) {
                        Log.d(TAG, "onADClosed");

                        Listener[] listeners = getAllListeners();
                        for (Listener listener : listeners) {
                            listener.onAdClosed(NativeAdAdapterView.this);
                        }
                    }

                    @Override
                    public void onADLeftApplication(NativeExpressADView nativeExpressADView) {
                        Log.d(TAG, "onADLeftApplication");

                        Listener[] listeners = getAllListeners();
                        for (Listener listener : listeners) {
                            listener.onAdLeftApplication(NativeAdAdapterView.this);
                        }
                    }

                    @Override
                    public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {
                        Log.d(TAG, "onADOpenOverlay");

                    }
                });

    }

    @Override
    public void loadAd() {
        nativeExpressAD.loadAD(1);
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
