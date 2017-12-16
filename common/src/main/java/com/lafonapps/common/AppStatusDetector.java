package com.lafonapps.common;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by chenjie on 2017/8/21.
 */

public class AppStatusDetector implements Application.ActivityLifecycleCallbacks {
    /* 应用程序刚完成启动的通知名称常量 */
    public static final String APPLICATION_DID_FINISH_LAUNCHING_NOTIFICATION = "ApplicationDidFinishLaunchingNotification";
    /* 应用程序即将从后台进入前台的通知名称常量 */
    public static final String APPLICATION_WILL_ENTER_FOREGROUND_NOTIFICATION = "ApplicationWillEnterForegroundNotification";
    /* 应用程序已经从前台进入后台的通知名称常量 */
    public static final String APPLICATION_DID_ENTER_BACKGROUND_NOTIFICATION = "ApplicationDidEnterBackgroundNotification";
    /* 应用程序即将退出的通知名称常量 */
    public static final String APPLICATION_WILL_TERMINATE_LAUNCHING_NOTIFICATION = "ApplicationWillTerminateNotification";

    private static final String TAG = AppStatusDetector.class.getCanonicalName();
    private static boolean appDidFinishLaunching;
    /**
     * 维护Activity 的list
     */
    private List<Activity> activitys = new LinkedList<Activity>();
    private int activityCreateCount = 0;
    private int activityStartedCount = 0;

    /**
     * get current Activity 获取当前Activity（栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activitys.get(activitys.size() - 1);
        return activity;
    }

    public boolean appInForeground() {
        return activityStartedCount >= 1;
    }

    /* Application.ActivityLifecycleCallbacks */

    public boolean appInBackground() {
        return activityStartedCount <= 0;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        //添加到栈中
        pushActivity(activity);

        activityCreateCount++;
        if (activityCreateCount == 1) {
            //发送应用完成启动的通知
            Log.d(TAG, "postNotification:" + APPLICATION_DID_FINISH_LAUNCHING_NOTIFICATION);
            NotificationCenter.defaultCenter().postNotification(APPLICATION_DID_FINISH_LAUNCHING_NOTIFICATION);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

        activityStartedCount++;
        Log.d(TAG, "onActivityStarted:" + activityStartedCount);
        if (appDidFinishLaunching && activityStartedCount == 1) {
            //发送应用从后台回到前台的通知
            Log.d(TAG, "postNotification:" + APPLICATION_WILL_ENTER_FOREGROUND_NOTIFICATION);
            NotificationCenter.defaultCenter().postNotification(APPLICATION_WILL_ENTER_FOREGROUND_NOTIFICATION);
        }
        appDidFinishLaunching = true;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        //先移除，再添加，保证在最后一个
        activitys.remove(activity);
        activitys.add(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        activityStartedCount--;
        Log.d(TAG, "onActivityStopped:" + activityStartedCount);
        if (appInBackground()) {
            //发送应用从前台退出到后台的通知
            Log.d(TAG, "postNotification:" + APPLICATION_DID_ENTER_BACKGROUND_NOTIFICATION);
            NotificationCenter.defaultCenter().postNotification(APPLICATION_DID_ENTER_BACKGROUND_NOTIFICATION);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        activityCreateCount--;
        if (activityCreateCount == 0) {
            //发送应用退出的通知
            Log.d(TAG, "postNotification:" + APPLICATION_WILL_TERMINATE_LAUNCHING_NOTIFICATION);
            NotificationCenter.defaultCenter().postNotification(APPLICATION_WILL_TERMINATE_LAUNCHING_NOTIFICATION);
        }
        //移除
        popActivity(activity);
    }

    /**
     * @param activity 作用说明 ：添加一个activity到管理里
     */
    public void pushActivity(Activity activity) {
        activitys.add(activity);
        Log.d(TAG, "activityList:size:" + activitys.size());
    }

    /**
     * @param activity 作用说明 ：删除一个activity在管理里
     */
    public void popActivity(Activity activity) {
        activitys.remove(activity);
        Log.d(TAG, "activityList:size:" + activitys.size());
    }
}
