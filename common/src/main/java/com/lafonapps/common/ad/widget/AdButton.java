package com.lafonapps.common.ad.widget;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.lafonapps.common.R;
import com.lafonapps.common.ad.adapter.InterstitialAdapter;
import com.lafonapps.common.ad.adapter.interstitial.InterstitialAdAdapter;
import com.lafonapps.common.base.BaseActivity;
import com.lafonapps.common.preferences.CommonConfig;

/**
 * Created by chenjie on 2017/11/14.
 */

public class AdButton extends AppCompatImageButton implements InterstitialAdapter.Listener {

    private static final String TAG = AdButton.class.getCanonicalName();

    /* 没有请求到广告之前是否自动隐藏。取值同Visibility一样[GONE, VISIBLE, INVISIBLE]，默认为GONE */
    private int defaultVisibilityIfAdNotReady = GONE;
    /* 当前按钮附着的Activity对象 */
    private BaseActivity attachedActivity;
    private InterstitialAdAdapter interstitialAd;
    private VisibilityChangedListener visibilityChangedListener;

    public AdButton(Context context) {
        super(context);
        configueration();
    }

    public AdButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        configueration();
    }

    public AdButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        configueration();
    }

    public static AdButton buttonForMenu(Context context, int imageResourcId) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AdButton adButton = (AdButton) inflater.inflate(R.layout.ad_button_for_menu, null);
        adButton.setImageResource(imageResourcId);

        return adButton;
    }

    private void configueration() {
        setVisibility(defaultVisibilityIfAdNotReady);
    }

    @Override
    public boolean performClick() {
        if (this.interstitialAd != null && this.interstitialAd.isReady()) {
            this.interstitialAd.show(attachedActivity);
            setVisibility(defaultVisibilityIfAdNotReady);
        }
        return super.performClick();
    }

    @Override
    public void setVisibility(final int visibility) {
        super.setVisibility(visibility);

        Techniques techniques = Techniques.ZoomOut;
        if (visibility == VISIBLE) {

            techniques = Techniques.BounceIn;
        }

        YoYo.with(techniques)
                .duration(500)
                .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                .interpolate(new AccelerateDecelerateInterpolator())
                .withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (visibilityChangedListener != null) {
                            visibilityChangedListener.onVisibilityChange(visibility);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .playOn(this);


    }

    public VisibilityChangedListener getVisibilityChangedListener() {
        return visibilityChangedListener;
    }

    public void setVisibilityChangedListener(VisibilityChangedListener visibilityChangedListener) {
        this.visibilityChangedListener = visibilityChangedListener;
    }

    public int getDefaultVisibilityIfAdNotReady() {
        return defaultVisibilityIfAdNotReady;
    }

    /**
     * 没有请求到广告之前是否自动隐藏。默认为GONE
     *
     * @param defaultVisibilityIfAdNotReady 取值同Visibility一样：[GONE, VISIBLE, INVISIBLE]
     */
    public void setDefaultVisibilityIfAdNotReady(int defaultVisibilityIfAdNotReady) {
        this.defaultVisibilityIfAdNotReady = defaultVisibilityIfAdNotReady;
    }

    public BaseActivity getAttachedActivity() {
        return attachedActivity;
    }

    /**
     * 当前按钮附着的BaseActivity对象。必须设置，因为当前按钮的显示状态依赖BaseActivity对象中的全屏广告InterstitialAdAdapter
     *
     * @param attachedActivity
     */
    public void setAttachedActivity(BaseActivity attachedActivity) {
        //移除上一个activity对应全屏广告的监听
        if (interstitialAd != null) {
            interstitialAd.removeListener(this);
        }

        this.attachedActivity = attachedActivity;

        if (attachedActivity != null) {
            InterstitialAdAdapter interstitialAd = attachedActivity.getInterstitialAd();
            this.interstitialAd = interstitialAd;
            if (interstitialAd != null && CommonConfig.sharedCommonConfig.shouldShowAdButton) {
                //添加监听
                interstitialAd.addListener(this);
                if (interstitialAd.isReady()) {
                    setVisibility(VISIBLE);
                }
            } else {
                setVisibility(defaultVisibilityIfAdNotReady);
            }

        } else {
            setVisibility(defaultVisibilityIfAdNotReady);
        }
    }

    /* InterstitialAdapter.Listener */

    @Override
    public void onAdClosed(InterstitialAdapter adapter) {

    }

    @Override
    public void onAdFailedToLoad(InterstitialAdapter adapter, int i) {

    }

    @Override
    public void onAdLeftApplication(InterstitialAdapter adapter) {

    }

    @Override
    public void onAdOpened(InterstitialAdapter adapter) {

        setVisibility(defaultVisibilityIfAdNotReady);
    }

    @Override
    public void onAdLoaded(InterstitialAdapter adapter) {
        setVisibility(VISIBLE);
    }

    public static interface VisibilityChangedListener {

        /**
         * 按钮的显示状态发生变化后回调。
         *
         * @param visibility 取值同Visibility一样：[GONE, VISIBLE, INVISIBLE]
         */
        public void onVisibilityChange(int visibility);
    }
}
