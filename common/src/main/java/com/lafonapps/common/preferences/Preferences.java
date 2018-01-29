package com.lafonapps.common.preferences;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.lafonapps.common.Common;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.lafonapps.common.utils.DateUtil.getDayOfDistance;

/**
 * Created by chenjie on 2017/7/4.
 */

public class Preferences {
    private static final String TAG = Preferences.class.getCanonicalName();

    private final static String kIsProApp = "IsProApp";
    private final static String kProAppID = "ProAppID";
    private final static String kFreeAppID = "FreeAppID";
    private final static String kSupportMail = "SupportMail";
    private final static String kInAppPurchaseForRemoveAdIdentifier = "InAppPurchaseForRemoveAdIdentifier";
    private final static String kInAppPurchaseForUnlockFunctionIdentifier = "InAppPurchaseForUnlockFunctionIdentifier";
    private final static String kTestDevices = "TestDevices";
    private final static String kPreferedAdTypes = "PreferedAdTypes";
    private final static String kRemovedAds = "RemovedAds";
    private final static String kNumberOfTimesToPresentInterstitial = "NumberOfTimesToPresentInterstitial";
    private final static String kForceRatingEndDate = "ForceRatingEndDate";
    private final static String kRated = "Rated";
    private final static String kAppFirstLaunchDate = "AppFirstLaunchDate"; //应用第一次启动的日期
    private final static String kVersionFirstLaunchDate = "VersionFirstLaunchDate"; //当前版本第一次启动的日期

    private final static Preferences sharedPreference = new Preferences();
    private final ConfigManager configManager = new ConfigManager();
    private final SharedPreferences internalPreferences;
    private final SharedPreferences.Editor editor;
    private String[] testDevices;
    private RunState runState = RunState.Unknown;
    private boolean proAppAvailableInAppStore = false;

    private Preferences() {
        this.internalPreferences = PreferenceManager.getDefaultSharedPreferences(Common.getSharedApplication());
        this.editor = internalPreferences.edit();
    }

    public static Preferences getSharedPreference() {
        return sharedPreference;
    }

    public Object getValue(String key) {
        return getValue(key, null);
    }

    public Object getValue(String key, Object defaultValue) {
        return getValue(key, defaultValue, true);
    }

    public Object getValue(String key, Object defaultValue, boolean syncImmediately) {
        if (key == null) {
            return defaultValue;
        }
        Object value = null;
        if (defaultValue instanceof String) {
            value = internalPreferences.getString(key, (String) defaultValue);
        } else if (defaultValue instanceof Set) {
            value = internalPreferences.getStringSet(key, (Set<String>) defaultValue);
        } else if (defaultValue instanceof Integer) {
            value = internalPreferences.getInt(key, (Integer) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            value = internalPreferences.getBoolean(key, (Boolean) defaultValue);
        } else if (defaultValue instanceof Float) {
            value = internalPreferences.getFloat(key, (Float) defaultValue);
        } else if (defaultValue instanceof Long) {
            value = internalPreferences.getLong(key, (Long) defaultValue);
        } else {
            Gson gson = new Gson();
//            String jsonFromDefaultValue = gson.toJson(defaultValue);
//            String json = this.internalPreferences.getString(key, jsonFromDefaultValue);
            String json = this.internalPreferences.getString(key, null);
            value = gson.fromJson(json, defaultValue.getClass());
        }
        Log.d(TAG, "value = " + value);
        if (value == null) {
            value = this.configManager.configValueForKey(key);
            if (value != null) {
                this.putValue(key, value, syncImmediately);
            } else {
                value = defaultValue;

                if (value != null) {
                    this.putValue(key, value, syncImmediately);
                }
            }
        }
        return value;
    }

    public void putValue(String key, Object value) {
        this.putValue(key, value, true);
    }

    public void putValue(String key, Object value, boolean syncImmediately) {
        if (key == null) {
            return;
        }
        Object oldValue = null;
        if (value instanceof String) {
            oldValue = this.internalPreferences.getString(key, null);
            this.editor.putString(key, (String) value);
        } else if (value instanceof Set) {
            oldValue = this.internalPreferences.getStringSet(key, null);
            this.editor.putStringSet(key, (Set<String>) value);
        } else if (value instanceof Integer) {
            oldValue = this.internalPreferences.getInt(key, 0);
            this.editor.putInt(key, ((Integer) value).intValue());
        } else if (value instanceof Boolean) {
            oldValue = this.internalPreferences.getBoolean(key, false);
            this.editor.putBoolean(key, ((Boolean) value).booleanValue());
        } else if (value instanceof Float) {
            oldValue = this.internalPreferences.getFloat(key, 0);
            this.editor.putFloat(key, ((Float) value).floatValue());
        } else if (value instanceof Long) {
            oldValue = this.internalPreferences.getLong(key, 0);
            this.editor.putLong(key, ((Long) value).longValue());
        } else {
            Gson gson = new Gson();
            String jsonFromValue = gson.toJson(value);
            String oldValueString = this.internalPreferences.getString(key, null);

            oldValue = gson.fromJson(oldValueString, value.getClass());

            Log.d(TAG, "oldValueString = " + oldValueString);
            Log.d(TAG, "oldValue = " + oldValue);
            Log.d(TAG, "value = " + value);
            Log.d(TAG, "jsonFromValue = " + jsonFromValue);

            this.editor.putString(key, jsonFromValue);
        }

//        if (oldValue == null || oldValue.equals(value) && syncImmediately) {
//            this.synchronize();
//        }
        if (syncImmediately) {
            this.synchronize();
        }
    }

    public void remove(String key) {
        this.remove(key, true);
    }

    public void remove(String key, boolean syncImmediately) {
        if (key == null) {
            return;
        }
        this.editor.remove(key);
        if (syncImmediately) {
            this.synchronize();
        }
    }

    public void synchronize() {
        this.editor.commit();
    }

    public void dump() {
        Log.d(Preferences.class.getName(), this.internalPreferences.getAll().toString());
    }

    public String[] getTestDevices() {
//        if (testDevices == null) {
//            Set<String> stringSet = (Set<String>) getValue(kTestDevices, new HashSet<>());
//            testDevices = stringSet.toArray(new String[stringSet.size()]);
//        }
//        return testDevices;
        return CommonConfig.sharedCommonConfig.testDevices;
    }

    public void setTestDevices(String[] testDevices) {
        if (this.testDevices == null || Arrays.deepEquals(this.testDevices, testDevices) == false) {
            this.testDevices = testDevices.clone();

            Set<String> stringSet = new HashSet<>(Arrays.asList(testDevices));

            putValue(kTestDevices, stringSet);
        }
    }

    public RunState getRunState() {
        if (runState == RunState.Unknown) {
            //TODO: 判断
            runState = RunState.NewlyInstalled;
        }
        return runState;
    }

    private void doUpdate() {
        //TODO:升级版本时做更新
    }

    private void checkProAppAvailableInStore() {
        // TODO: 2017/7/4 检查付费版应用是否上架了
        proAppAvailableInAppStore = true;
    }

    public boolean isProApp() {
        return ((Boolean) this.getValue(kIsProApp, false, false)).booleanValue();
    }

    public String getCurrentAppID() {
        String appID = null;
        if (isProApp()) {
            appID = getProAppID();
        } else {
            appID = getFreeAppID();
        }
        return appID;
    }

    public String getFreeAppID() {
        return (String) getValue(kFreeAppID);
    }

    public String getProAppID() {
        return (String) getValue(kProAppID);
    }

    public String getSupportMail() {
        return (String) getValue(kSupportMail);
    }

    public String getInAppPurchaseForRemoveAdIdentifier() {
        return (String) getValue(kInAppPurchaseForRemoveAdIdentifier);
    }

    public String getInAppPurchaseForUnlockFunctionIdentifier() {
        return (String) getValue(kInAppPurchaseForUnlockFunctionIdentifier);
    }

    public String getSharedSecretForInAppPurchaseIdentifier(String inAppPurchaseIdentifier) {
        return (String) getValue(inAppPurchaseIdentifier);
    }

    public boolean isAdRemoved() {
        return ((Boolean) getValue(kRemovedAds)).booleanValue();
    }

    public void setAdRemoved(boolean removed) {
        putValue(kRemovedAds, removed);
    }

    public int getNumberOfTimesToPresentInterstitial() {
//        return ((Integer) getValue(kNumberOfTimesToPresentInterstitial, 10)).intValue();
        return CommonConfig.sharedCommonConfig.numberOfTimesToPresentInterstitial;
    }

    public String getDeviceModel() {
        //TODO: 获取设备型号
        return "Unknown";
    }

    public Date getForceRatingEndDate() {
        String dateString = (String) getValue(kForceRatingEndDate);
        //TODO: 格式化成日期
        return new Date();
    }

    public boolean isProAppAvailableInAppStore() {
        return proAppAvailableInAppStore;
    }

    public boolean isRated() {
        return (boolean) getValue(kRated, false);
    }

    public void setRated(boolean rated) {
        putValue(kRated, rated);
    }

    /**
     * 应用第一次启动的时间
     *
     * @return
     */
    public Date getAppFirstLaunchDate() {
        Date appFirstLaunchDate = (Date) getValue(kAppFirstLaunchDate, new Date(), true);
        return appFirstLaunchDate;
    }

    /**
     * 应用当前版本第一次启动的时间
     *
     * @return
     */
    public Date getVersionFirstLaunchDate() {
        String key = kVersionFirstLaunchDate + "_" + Common.getAppVersionCode();
        Date versionFirstLaunchDate = (Date) getValue(key, new Date(), true);
        return versionFirstLaunchDate;
    }

    /**
     * 本次启动距离应用第一次启动相隔多少天
     *
     * @return
     */
    public long getDayOfDistanceFromAppFirstLaunchDate() {
        Date appFirstLaunchDate = (Date) getValue(kAppFirstLaunchDate, new Date(), true);
        Date today = new Date();
        long dayOfDistance = getDayOfDistance(appFirstLaunchDate, today);

        return dayOfDistance;
    }

    /**
     * 本次启动距离应用当前版本第一次启动相隔多少天
     *
     * @return
     */
    public long getDayOfDistanceFromVersionFirstLaunchDate() {
        String key = kVersionFirstLaunchDate + "_" + Common.getAppVersionCode();
        Date versionFirstLaunchDate = (Date) getValue(key, new Date(), true);
        Date today = new Date();
        long dayOfDistance = getDayOfDistance(versionFirstLaunchDate, today);

        return dayOfDistance;
    }

}

