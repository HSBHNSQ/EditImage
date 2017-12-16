package com.lafonapps.common.ad.adapter.interstitial;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.lafonapps.common.Common;
import com.lafonapps.common.ad.adapter.AdModel;
import com.lafonapps.common.ad.adapter.InterstitialAdapter;
import com.lafonapps.common.ad.adapter.SupportMutableListenerAdapter;
import com.xiaomi.ad.AdListener;
import com.xiaomi.ad.adView.InterstitialAd;
import com.xiaomi.ad.common.pojo.AdError;
import com.xiaomi.ad.common.pojo.AdEvent;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjie on 2017/7/5.
 */

public class InterstitialAdAdapter implements InterstitialAdapter, SupportMutableListenerAdapter<InterstitialAdapter.Listener> {

    private static final String TAG = InterstitialAdAdapter.class.getCanonicalName();

    private InterstitialAd interstitialAd;
    private Context context;
    private AdModel adModel;
    private String[] debugDevices;
    private List<Listener> allListeners = new ArrayList<>();

    private int retryDelayForFailed;

    public InterstitialAdAdapter(Context context) {
        this.context = context;
        interstitialAd = new InterstitialAd(context, new View(context));
    }

    /* 是否已经请求到广告可供展示 */
    @Override
    public boolean isReady() {
        return interstitialAd.isReady();
    }

    /* 构建内容 */
    public void build(AdModel adModel) {
        this.adModel = adModel;
    }

    /**
     * 广告是否可以在多个界面重用
     */
    @Override
    public boolean reuseable() {
        return true;
    }

    /* 加载广告 */
    @Override
    public void loadAd() {
        interstitialAd.requestAd(adModel.getXiaomiAdID(), new AdListener() {

            @Override
            public void onAdError(AdError adError) {
                Log.d(TAG, "onAdError:" + adError);

                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdFailedToLoad(InterstitialAdAdapter.this, adError.value());
                }

                retryDelayForFailed += 2; //延迟时间增加2秒

                //延迟一段时间后重新加载
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadAd();
                    }
                }, retryDelayForFailed);
            }

            @Override
            public void onAdEvent(AdEvent adEvent) {
                Log.d(TAG, "onAdEvent:" + adEvent.name());

                Listener[] listeners = getAllListeners();
                if (adEvent.mType == AdEvent.TYPE_VIEW) { //展示广告
//                    setStatusBarVisible(false);
                } else if (adEvent.mType == AdEvent.TYPE_SKIP) { //关闭广告
//                    setStatusBarVisible(true);

                    for (Listener listener : listeners) {
                        listener.onAdClosed(InterstitialAdAdapter.this);
                    }
                    //重新加载广告
                    loadAd();
                } else if (adEvent.mType == AdEvent.TYPE_CLICK) {
                    for (Listener listener : listeners) {
                        listener.onAdOpened(InterstitialAdAdapter.this);
                    }
                } else if (adEvent.mType == AdEvent.TYPE_FINISH) {
//                    setStatusBarVisible(true);

                    for (Listener listener : listeners) {
                        listener.onAdClosed(InterstitialAdAdapter.this);
                    }

                    //重新加载广告
                    loadAd();
                } else if (adEvent.mType == AdEvent.TYPE_APP_LAUNCH_SUCCESS) {
                    for (Listener listener : listeners) {
                        listener.onAdLeftApplication(InterstitialAdAdapter.this);
                    }
                }
            }

            @Override
            public void onAdLoaded() {
                Log.d(TAG, "onAdLoaded");

                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdLoaded(InterstitialAdAdapter.this);
                }

                retryDelayForFailed = 0;
            }

            @Override
            public void onViewCreated(View view) {
                Log.d(TAG, "onViewCreated" + view);
            }
        });
    }

    @Override
    public void show() {
        if (interstitialAd.isReady()) {
            Activity currentActivity = Common.getCurrentActivity();
            if (currentActivity != null) {
                try {
                    View anchorView = currentActivity.getWindow().getDecorView();
                    Field mAnchor = interstitialAd.getClass().getDeclaredField("mAnchor");
                    mAnchor.setAccessible(true); //为 true 则表示反射的对象在使用时取消 Java 语言访问检查
                    mAnchor.set(interstitialAd, anchorView);
                    interstitialAd.show();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "Current Activity is null, can not show interstitialAd!");
            }
        } else {
            Log.d(TAG, "interstitialAd not ready");
        }
    }

    @Override
    public Object getAdapterAd() {
        return this.interstitialAd;
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

//    private void setStatusBarVisible(boolean visible) {
//        Activity currentActivity = Common.getCurrentActivity();
//        if (currentActivity != null) {
//
//            int type = visible ? TYPE_BASE_APPLICATION : TYPE_SYSTEM_ALERT;
//            final WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
//            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                    type,
//                    FLAG_WATCH_OUTSIDE_TOUCH | FLAG_LAYOUT_IN_SCREEN,
//                    PixelFormat.TRANSLUCENT);
//            params.gravity = Gravity.RIGHT | Gravity.TOP;
//        } else {
//            Log.d(TAG, "Current Activity is null, can not change statusbar visibility");
//        }
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
