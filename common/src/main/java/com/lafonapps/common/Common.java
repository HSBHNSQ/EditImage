package com.lafonapps.common;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.lafonapps.common.ad.AdManager;
import com.lafonapps.common.feedback.FeedBack;
import com.lafonapps.common.preferences.CommonConfig;
import com.lafonapps.common.preferences.Preferences;

/**
 * Created by chenjie on 2017/8/10.
 */

public class Common {


    private static final String TAG = Common.class.getCanonicalName();

    private static Application sharedApplication;
    private static AppStatusDetector appStatusDetector = new AppStatusDetector();


    public static void initialize(Application application) {
        sharedApplication = application;
        Log.d(TAG, "isApkDebugable:" + isApkDebugable());

        Preferences.getSharedPreference().getAppFirstLaunchDate(); //记录应用第一次启动的日期
        Preferences.getSharedPreference().getVersionFirstLaunchDate(); //记录或更新应用当前版本第一次启动的日期

//        LeakCanary.install(application);

        application.registerActivityLifecycleCallbacks(appStatusDetector);
        AdManager.getSharedAdManager();
        ProductFlavor.initialize(application);


        String leanCloudAppId = CommonConfig.sharedCommonConfig.leanCloudAppID;
        String leanCloudAppKey = CommonConfig.sharedCommonConfig.leanCloudAppKey;
        FeedBack.initialize(application,leanCloudAppId,leanCloudAppKey);
        FeedBack.suggestionItems = CommonConfig.sharedCommonConfig.leanCloudSuggestionItems;

        //友盟追踪
//        ApplicationInfo info = sharedApplication.getApplicationInfo();
//        String channelId = "Unknown";
//        if (info.metaData != null) {
//            channelId = info.metaData.getString("UMENG_CHANNEL_VALUE", "Unknown");
//        }
//        MobclickAgent.UMAnalyticsConfig config = new MobclickAgent.UMAnalyticsConfig(application,
//                CommonConfig.sharedCommonConfig.umengAppKey, channelId);
//        MobclickAgent.startWithConfigure(config);
//
//        if (isApkDebugable()) {
//            MobclickAgent.setDebugMode(true);
//        }
    }

    public static Application getSharedApplication() {
        return sharedApplication;
    }

    public static AppStatusDetector getAppStatusDetector() {
        return appStatusDetector;
    }

    public static boolean isApkDebugable() {
        try {
            ApplicationInfo info = sharedApplication.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getAppDisplayName() {
        ApplicationInfo applicationInfo = sharedApplication.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : sharedApplication.getString(stringId);
    }

    //获取当前版本名称
    public static String getAppVersionName() {
        String versionName = "";
        try {
            Context context = Common.getSharedApplication();
            String packageName = context.getPackageName();
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    //获取当前版本号
    public static int getAppVersionCode() {
        int versionCode = 0;
        try {
            Context context = Common.getSharedApplication();
            String packageName = context.getPackageName();
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    //可能不靠谱
//    public static Activity getCurrentActivity() {
//        Activity activity = null;
//        try {
//            Class activityThreadClass = Class.forName("android.app.ActivityThread");
//            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
//            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
//            activitiesField.setAccessible(true);
//            Map activities = (Map) activitiesField.get(activityThread);
//            for (Object activityRecord : activities.values()) {
//                Class activityRecordClass = activityRecord.getClass();
//                Field pausedField = activityRecordClass.getDeclaredField("paused");
//                pausedField.setAccessible(true);
//                if (!pausedField.getBoolean(activityRecord)) {
//                    Field activityField = activityRecordClass.getDeclaredField("activity");
//                    activityField.setAccessible(true);
//                    activity = (Activity) activityField.get(activityRecord);
//
//                }
//            }
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//        return activity;
//    }

    public static Activity getCurrentActivity() {
        return appStatusDetector.currentActivity();
    }

}
