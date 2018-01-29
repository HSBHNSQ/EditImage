package com.lafonapps.common.rate;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.lafonapps.common.Common;
import com.lafonapps.common.R;
import com.lafonapps.common.preferences.CommonConfig;
import com.lafonapps.common.preferences.Preferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjie on 2017/9/28.
 * <p>
 * 与评论相关的类
 */

public class AppRater {

    public static final AppRater defaultAppRater = new AppRater();
    private static final String TAG = AppRater.class.getCanonicalName();

    private boolean canExitByBackEvent; //按back键是否可以退出应用
    private boolean alertToRating; //正在提示评论

    private List<Listener> allListeners = new ArrayList<>();

    /**
     * 判断当前设备是否安装了应用商店
     *
     * @return
     */
    public boolean hasMarketInstalled() {
        Intent intent = new Intent();
        intent.setData(Uri.parse("market://details?id=android.browser"));
        List list = Common.getSharedApplication().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        return list.size() > 0;
    }

    /**
     * 是否可以评论。
     *
     * @return 当前设备没有安装应用商店，或者已经评论过就会返回false，其他情况返回true
     */
    public boolean shouldRate() {
        boolean shouldRate = Preferences.getSharedPreference().isRated() == false && hasMarketInstalled();
        return shouldRate;
    }

    /**
     * 打开评论界面
     *
     * @param activity
     */
    public void goRating(Activity activity) {
        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent goToMarketIntent = new Intent(Intent.ACTION_VIEW, uri);
        goToMarketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            activity.startActivity(goToMarketIntent);
            Preferences.getSharedPreference().setRated(true);

            Listener[] listeners = getAllListeners();
            for (Listener listener : listeners) {
                listener.rated();
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, activity.getString(R.string.no_market_install_message), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 弹出提示评论框，引导用户评论
     *
     * @param activity
     */
    public void promptToRate(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle("☺☺☺☺☺")//设置对话框标题
                .setMessage(activity.getString(R.string.rate_message, activity.getString(R.string.app_name)))//设置显示的内容
                .setPositiveButton("👍" + activity.getString(R.string.rate_good_app), new DialogInterface.OnClickListener() {//添加确定按钮

                    @Override
                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                        Log.d(TAG, "onPositiveButtonClick");
                        goRating(activity);
                    }

                });
        if (!TextUtils.isEmpty(CommonConfig.sharedCommonConfig.feedbackEmailAddress)) {
            builder.
                    setNegativeButton("👎" + activity.getString(R.string.rate_bad_app), new DialogInterface.OnClickListener() {//添加返回按钮

                        @Override
                        public void onClick(DialogInterface dialog, int which) {//响应事件
                            Log.d(TAG, "onNegativeButtonClick");
                            FeedbackSender.sendEmail(CommonConfig.sharedCommonConfig.feedbackEmailAddress, activity);

                            Preferences.getSharedPreference().setRated(true);
                        }

                    });
        } else {
            builder.setNegativeButton(activity.getString(R.string.rate_cancel_button_title), new DialogInterface.OnClickListener() {//添加返回按钮

                @Override
                public void onClick(DialogInterface dialog, int which) {//响应事件
                    Log.d(TAG, "onNegativeButtonClick");

                }

            });
        }
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d(TAG, "onCancel");

            }
        })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        Log.d(TAG, "onDismiss");

                    }
                });
        builder.show();//在按键响应事件中显示此对话框
    }

//    public void promptToRate(final Activity activity) {
//        new AlertDialog.Builder(activity)
//                .setTitle("☺☺☺☺☺")//设置对话框标题
//                .setMessage(activity.getString(R.string.rate_message, activity.getString(R.string.app_name)))//设置显示的内容
//                .setPositiveButton(activity.getString(R.string.rate_rate_button_title), new DialogInterface.OnClickListener() {//添加确定按钮
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
//                        Log.d(TAG, "onPositiveButtonClick");
//                        goRating(activity);
//                    }
//
//                })
//                .setNegativeButton(activity.getString(R.string.rate_cancel_button_title), new DialogInterface.OnClickListener() {//添加返回按钮
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {//响应事件
//                        Log.d(TAG, "onNegativeButtonClick");
//
//                    }
//
//                })
//                .setOnCancelListener(new DialogInterface.OnCancelListener() {
//                    @Override
//                    public void onCancel(DialogInterface dialogInterface) {
//                        Log.d(TAG, "onCancel");
//
//                    }
//                })
//                .setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialogInterface) {
//                        Log.d(TAG, "onDismiss");
//
//                    }
//                })
//                .show();//在按键响应事件中显示此对话框
//    }

    private boolean promptToRateForBackKey(final Activity activity) {
        if (!alertToRating) {
            Log.d(TAG, "AlertDialog");

            alertToRating = true;
            new AlertDialog.Builder(activity)
                    .setTitle("☺☺☺☺☺")//设置对话框标题
                    .setMessage(activity.getString(R.string.rate_message, activity.getString(R.string.app_name)))//设置显示的内容
                    .setPositiveButton(activity.getString(R.string.rate_rate_button_title), new DialogInterface.OnClickListener() {//添加确定按钮

                        @Override
                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                            Log.d(TAG, "onPositiveButtonClick");
                            goRating(activity);
                        }

                    })
                    .setNegativeButton(activity.getString(R.string.rate_cancel_button_title), new DialogInterface.OnClickListener() {//添加返回按钮

                        @Override
                        public void onClick(DialogInterface dialog, int which) {//响应事件
                            Log.d(TAG, "onNegativeButtonClick");

                            Toast.makeText(activity, activity.getString(R.string.again_to_exit_app), Toast.LENGTH_SHORT).show();

                            canExitByBackEvent = true;

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    canExitByBackEvent = false;
                                }
                            }, 2000);

                        }

                    })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            Log.d(TAG, "onCancel");

                            Toast.makeText(activity, activity.getString(R.string.again_to_exit_app), Toast.LENGTH_SHORT).show();

                            canExitByBackEvent = true;

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    canExitByBackEvent = false;
                                }
                            }, 2000);
                        }
                    })
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            Log.d(TAG, "onDismiss");

                            alertToRating = false;
                        }
                    })
                    .show();//在按键响应事件中显示此对话框
            return true;
        } else {
            Log.w(TAG, "is prompting");
            return false;
        }
    }

    //    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public boolean handBackEventToPromtRate(final Activity activity) {
        // 捕获back键，当只有最后一个Activity的时候，如果没有评论过，则提示评论
        boolean isLastActivity = Common.getAppStatusDetector().getActivities().length == 1;
        if (isLastActivity) {
            if (shouldRate() && !canExitByBackEvent && promptToRateForBackKey(activity)) {
                return true;
            } else {
                if (!alertToRating && !canExitByBackEvent && !shouldRate()) {
                    Log.d(TAG, "Type back again to exit");

                    Toast.makeText(activity, activity.getString(R.string.again_to_exit_app), Toast.LENGTH_SHORT).show();

                    canExitByBackEvent = true;

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            canExitByBackEvent = false;
                        }
                    }, 2000);

                    return true;
                }

            }
        }
        return false;
    }

    public synchronized void addListener(Listener listener) {
        if (listener != null && !allListeners.contains(listener)) {
            allListeners.add(listener);
            Log.d(TAG, "addListener:" + listener);
        }
    }

    public synchronized void removeListener(Listener listener) {
        if (allListeners.contains(listener)) {
            allListeners.remove(listener);
            Log.d(TAG, "removeListener:" + listener);
        }
    }

    public Listener[] getAllListeners() {
        return allListeners.toArray(new Listener[allListeners.size()]);
    }

    public static interface Listener {

        /**
         * 用户评论后被调用
         */
        void rated();

    }

}