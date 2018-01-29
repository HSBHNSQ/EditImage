package com.lafonapps.common.rate;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.lafonapps.common.R;
import com.lafonapps.common.base.BaseActivity;
import com.lafonapps.common.preferences.CommonConfig;
import com.lafonapps.common.preferences.Preferences;

/**
 * Created by chenjie on 2017/11/14.
 */

public class AppRateButton extends AppCompatImageButton {

    private static final String TAG = AppRateButton.class.getCanonicalName();

    /* 没有评论过之前控件的可见性。取值同Visibility一样[GONE, VISIBLE, INVISIBLE]，默认为GONE */
    private int defaultVisibilityIfNotRate = GONE;
    private AppRater appRater;
    /* 当前按钮附着的Activity对象 */
    private BaseActivity attachedActivity;
    private VisibilityChangedListener visibilityChangedListener;

    public AppRateButton(Context context) {
        super(context);
        configueration();
    }

    public AppRateButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        configueration();
    }

    public AppRateButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        configueration();
    }

    public static AppRateButton buttonForMenu(Context context, int imageResourcId) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AppRateButton adButton = (AppRateButton) inflater.inflate(R.layout.app_rate_button_for_menu, null);
        adButton.setImageResource(imageResourcId);

        return adButton;
    }

    private void configueration() {
        appRater = AppRater.defaultAppRater;
        appRater.addListener(new AppRater.Listener() {
            /**
             * 用户评论后被调用
             */
            @Override
            public void rated() {
                //评论后隐藏按钮
                setVisibility(GONE);
            }
        });
        if (Preferences.getSharedPreference().isRated()) {
            setVisibility(GONE);
        } else {
            //根据配置来决定默认是否显示评论按钮
            if (CommonConfig.sharedCommonConfig.shouldShowRateButton) {
                defaultVisibilityIfNotRate = VISIBLE;
            }
            setVisibility(defaultVisibilityIfNotRate);
        }
    }

    @Override
    public boolean performClick() {
        if (appRater.shouldRate()) {
            if (attachedActivity != null) {
                appRater.promptToRate(attachedActivity);
            } else {
                Log.w(TAG, "attachedActivity is null, do nothing");
            }
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

    public int getDefaultVisibilityIfNotRate() {
        return defaultVisibilityIfNotRate;
    }

    /**
     * 没有评论过之前是否自动隐藏。默认为GONE
     *
     * @param defaultVisibilityIfNotRate 取值同Visibility一样：[GONE, VISIBLE, INVISIBLE]
     */
    public void setDefaultVisibilityIfNotRate(int defaultVisibilityIfNotRate) {
        this.defaultVisibilityIfNotRate = defaultVisibilityIfNotRate;
    }

    public BaseActivity getAttachedActivity() {
        return attachedActivity;
    }

    /**
     * 当前按钮附着的BaseActivity对象。必须设置，因为弹出提示对话框需要依赖BaseActivity对象
     *
     * @param attachedActivity
     */
    public void setAttachedActivity(BaseActivity attachedActivity) {
        this.attachedActivity = attachedActivity;
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
