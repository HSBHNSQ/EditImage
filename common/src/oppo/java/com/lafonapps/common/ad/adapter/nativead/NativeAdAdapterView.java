package com.lafonapps.common.ad.adapter.nativead;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.androidquery.AQuery;
import com.baidu.mobad.feeds.NativeErrorCode;
import com.baidu.mobad.feeds.NativeResponse;
import com.baidu.mobad.feeds.RequestParameters;
import com.lafonapps.common.Common;
import com.lafonapps.common.R;
import com.lafonapps.common.ad.AdSize;
import com.lafonapps.common.ad.adapter.AdModel;
import com.lafonapps.common.ad.adapter.NativeAdViewAdapter;
import com.lafonapps.common.ad.adapter.SupportMutableListenerAdapter;
import com.lafonapps.common.utils.ViewUtil;
import com.oppo.mobad.ad.feeds.NativeAd;
import com.oppo.mobad.listener.INativeAdListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjie on 2017/7/5.
 */

public class NativeAdAdapterView extends FrameLayout implements NativeAdViewAdapter, SupportMutableListenerAdapter<NativeAdViewAdapter.Listener> {
    private static final String TAG = NativeAdAdapterView.class.getCanonicalName();

    private NativeAd nativeAd;
    private ViewGroup adView;
    private Context context;
    private AdModel adModel;
    private AdSize adSize;
    private String[] debugDevices;
    private boolean ready;

    private List<Listener> allListeners = new ArrayList<>();

    public NativeAdAdapterView(Context context) {
        super(context);
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        adView = (ViewGroup) inflater.inflate(R.layout.native_ad, null);
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
    public boolean reuseable() {
        return false;
    }

    @Override
    public void build(AdModel adModel, AdSize adSize) {
        this.adModel = adModel;
        this.adSize = adSize;

        this.nativeAd = new NativeAd(Common.getCurrentActivity(), adModel.getOppoAdID(), new INativeAdListener() {

            @Override
            public void onNativeFail(NativeErrorCode arg0) {
                Log.w(TAG, "onNativeFail reason:" + arg0.toString());

                Listener[] listeners = getAllListeners();
                for (Listener listener : listeners) {
                    listener.onAdFailedToLoad(NativeAdAdapterView.this, arg0.ordinal());
                }
            }

            @Override
            public void onNativeLoad(List list) {
                Log.d(TAG, "onNativeLoad:" + list);
                /**
                 * 一个广告只允许展现一次，多次展现、点击只会计入一次。
                 */
                if (list != null && list.size() > 0) {
                    NativeResponse nrAd = (NativeResponse) list.get(0);

                    adView.invalidate();
                    AQuery aq = new AQuery(adView);
                    aq.id(R.id.native_icon_image).image(nrAd.getIconUrl(), false, true);
                    aq.id(R.id.native_main_image).image(nrAd.getImageUrl(), false, true);
                    aq.id(R.id.native_text).text(nrAd.getDesc());
                    aq.id(R.id.native_title).text(nrAd.getTitle());
                    aq.id(R.id.native_brand_name).text(nrAd.getBrandName());
                    aq.id(R.id.native_adlogo).image(nrAd.getAdLogoUrl(), false, true);
                    aq.id(R.id.native_oppologo).image(nrAd.getBaiduLogoUrl(), false, true);
                    String text = nrAd.isDownloadApp() ? "下载" : "查看";
                    aq.id(R.id.native_cta).text(text);
                    aq.clickable(true);
                    aq.clicked(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.w(TAG, "onClick");

                            Listener[] listeners = getAllListeners();
                            for (Listener listener : listeners) {
                                listener.onAdOpened(NativeAdAdapterView.this);
                            }

                            reloadAd();
                        }
                    });
                    nrAd.recordImpression(adView);

                    ViewUtil.addView(NativeAdAdapterView.this, adView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    Log.d(TAG, "onAdLoaded");
                    ready = true;
                    Listener[] listeners = getAllListeners();
                    for (Listener listener : listeners) {
                        listener.onAdLoaded(NativeAdAdapterView.this);
                    }
                } else {
                    Log.w(TAG, "onNativeLoadFailed: list is empty");

                    Listener[] listeners = getAllListeners();
                    for (Listener listener : listeners) {
                        listener.onAdFailedToLoad(NativeAdAdapterView.this, -1);
                    }
                }
            }

        });
    }

    @Override
    public void loadAd() {
        RequestParameters requestParameters =
                new RequestParameters.Builder()
                        .downloadAppConfirmPolicy(
                                RequestParameters.DOWNLOAD_APP_CONFIRM_ONLY_MOBILE)
                        .setWidth(adSize.getWidth())
                        .setHeight(adSize.getHeight())
                        .build();

        nativeAd.makeRequest(requestParameters);
    }

    private void reloadAd() {
        build(adModel, adSize);
        loadAd();
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
