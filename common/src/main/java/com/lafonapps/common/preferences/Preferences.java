package com.lafonapps.common.preferences;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.lafonapps.common.Common;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by chenjie on 2017/7/4.
 */

public class Preferences {


    private final static String kIsProApp = "IsProApp";
    private final static String kProAppID = "ProAppID";
    private final static String kFreeAppID = "FreeAppID";
    private final static String kSupportMail = "SupportMail";
    private final static String kInAppPurchaseForRemoveAdIdentifier = "InAppPurchaseForRemoveAdIdentifier";
    private final static String kInAppPurchaseForUnlockFunctionIdentifier = "InAppPurchaseForUnlockFunctionIdentifier";
    private final static String kAppID4Admob = "AppID4Admob";
    private final static String kBannerAdUnitID4Admob = "BannerAdUnitID4Admob";
    private final static String kNativeAdUnitID4Admob = "NativeAdUnitID4Admob";
    private final static String kNativeAdUnitID132H4Admob = "NativeAdUnitID132H4Admob";
    private final static String kNativeAdUnitID250H4Admob = "NativeAdUnitID250H4Admob";
    private final static String kInterstitialAdUnitID4Admob = "InterstitialAdUnitID4Admob";
    private final static String kRewardBasedVideoAdUnitID4Admob = "RewardBasedVideoAdUnitID4Admob";
    private final static String kSplashAdUnitID4Admob = "SplashAdUnitID4Admob";
    private final static String kBannerAdUnitID4Facebook = "BannerAdUnitID4Facebook";
    private final static String kNativeAdUnitID4Facebook = "NativeAdUnitID4Facebook";
    private final static String kNativeAdUnitID132H4Facebook = "NativeAdUnitID132H4Facebook";
    private final static String kNativeAdUnitID250H4Facebook = "NativeAdUnitID250H4Facebook";
    private final static String kInterstitialAdUnitID4Facebook = "InterstitialAdUnitID4Facebook";
    private final static String kRewardBasedVideoAdUnitID4Facebook = "RewardBasedVideoAdUnitID4Facebook";
    private final static String kSplashAdUnitID4Facebook = "SplashAdUnitID4Facebook";
    private final static String kFacebookNativeAdViewLayoutName80H = "FacebookNativeAdViewLayoutName80H";
    private final static String kFacebookNativeAdViewLayoutName132H = "FacebookNativeAdViewLayoutName132H";
    private final static String kFacebookNativeAdViewLayoutName250H = "FacebookNativeAdViewLayoutName250H";
    private final static String kAppID4XiaoMi = "AppID4XiaoMi";
    private final static String kBannerAdUnitID4XiaoMi = "BannerAdUnitID4XiaoMi";
    private final static String kNativeAdUnitID4XiaoMi = "NativeAdUnitID4XiaoMi";
    private final static String kNativeAdUnitID132H4XiaoMi = "NativeAdUnitID132H4XiaoMi";
    private final static String kNativeAdUnitID250H4XiaoMi = "NativeAdUnitID250H4XiaoMi";
    private final static String kInterstitialAdUnitID4XiaoMi = "InterstitialAdUnitID4XiaoMi";
    private final static String kSplashAdUnitID4XiaoMi = "SplashAdUnitID4XiaoMi";
    private final static String kTestDevices = "TestDevices";
    private final static String kPreferedAdTypes = "PreferedAdTypes";
    private final static String kRemovedAds = "RemovedAds";
    private final static String kNumberOfTimesToPresentInterstitial = "NumberOfTimesToPresentInterstitial";
    private final static String kForceRatingEndDate = "ForceRatingEndDate";


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
            return null;
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
        }
        if (value == null) {
            value = this.configManager.configValueForKey(key);
            if (value != null) {
                this.putValue(key, value, syncImmediately);
            }
        }
        if (value == null) {
            value = defaultValue;
        }
        if (value == null && defaultValue != null) {
            this.putValue(key, value, syncImmediately);
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
            oldValue = this.internalPreferences.getString(key, (String) value);
            this.editor.putString(key, (String) value);
        } else if (value instanceof Set) {
            oldValue = this.internalPreferences.getStringSet(key, (Set<String>) value);
            this.editor.putStringSet(key, (Set<String>) value);
        } else if (value instanceof Integer) {
            oldValue = this.internalPreferences.getInt(key, (Integer) value);
            this.editor.putInt(key, ((Integer) value).intValue());
        } else if (value instanceof Boolean) {
            oldValue = this.internalPreferences.getBoolean(key, (Boolean) value);
            this.editor.putBoolean(key, ((Boolean) value).booleanValue());
        } else if (value instanceof Float) {
            oldValue = this.internalPreferences.getFloat(key, (Float) value);
            this.editor.putFloat(key, ((Float) value).floatValue());
        } else if (value instanceof Long) {
            oldValue = this.internalPreferences.getLong(key, (Long) value);
            this.editor.putLong(key, ((Long) value).longValue());
        }

        if (oldValue == null || oldValue.equals(value) && syncImmediately) {
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
        if (this.testDevices == null || !this.testDevices.equals(testDevices)) {
            this.testDevices = testDevices;

            Set<String> stringSet = new HashSet<>(Arrays.asList(testDevices));

            putValue(kTestDevices, stringSet);
        }
    }

    public RunState getRunState() {
        if (runState.equals(RunState.Unknown)) {
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

    public String getAppID4Admob() {
//        if (Common.isApkDebugable()) {
//            return "ca-app-pub-8698484584626435~1504595858";
//        } else {
//            return (String) getValue(kAppID4Admob);
//        }
        return CommonConfig.sharedCommonConfig.appID4Admob;
    }

    public String getBannerAdUnitID4Admob() {
//        if (Common.isApkDebugable()) {
//            return "ca-app-pub-8698484584626435/7634889932";
//        } else {
//            return (String) getValue(kBannerAdUnitID4Admob);
//        }
        return CommonConfig.sharedCommonConfig.bannerAdUnitID4Admob;
    }

    public String getNativeAdUnitID4Admob() {
//        if (Common.isApkDebugable()) {
//            return "ca-app-pub-8698484584626435/1142645536";
//        } else {
//            return (String) getValue(kNativeAdUnitID4Admob);
//        }
        return CommonConfig.sharedCommonConfig.nativeAdUnitID4Admob;
    }

    public String getNativeAdUnitID132H4Admob() {
//        if (Common.isApkDebugable()) {
//            return "ca-app-pub-8698484584626435/1284926399";
//        } else {
//            return (String) getValue(kNativeAdUnitID132H4Admob);
//        }
        return CommonConfig.sharedCommonConfig.nativeAdUnitID132H4Admob;
    }

    public String getNativeAdUnitID250H4Admob() {
//        if (Common.isApkDebugable()) {
//            return "ca-app-pub-8698484584626435/9762359826";
//        } else {
//            return (String) getValue(kNativeAdUnitID250H4Admob);
//        }
        return CommonConfig.sharedCommonConfig.nativeAdUnitID250H4Admob;
    }

    public String getSplashAdUnitID4Admob() {
//        if (Common.isApkDebugable()) {
//            return "ca-app-pub-8698484584626435/6470159684";
//        } else {
//            return (String) getValue(kSplashAdUnitID4Admob);
//        }
        return CommonConfig.sharedCommonConfig.splashAdUnitID4Admob;
    }

    public String getInterstitialAdUnitID4Admob() {
//        if (Common.isApkDebugable()) {
//            return "ca-app-pub-8698484584626435/6470159684";
//        } else {
//            return (String) getValue(kInterstitialAdUnitID4Admob);
//        }
        return CommonConfig.sharedCommonConfig.interstitialAdUnitID4Admob;
    }

    public String getRewardBasedVideoAdUnitID4Admob() {
        if (Common.isApkDebugable()) {
            return "ca-app-pub-8698484584626435/8812062629";
        } else {
            return (String) getValue(kRewardBasedVideoAdUnitID4Admob);
        }
    }

    public String getBannerAdUnitID4Facebook() {
        if (Common.isApkDebugable()) {
            return "";
        } else {
            return (String) getValue(kBannerAdUnitID4Facebook);
        }
    }

    public String getNativeAdUnitID4Facebook() {
        if (Common.isApkDebugable()) {
            return "";
        } else {
            return (String) getValue(kNativeAdUnitID4Facebook);
        }
    }

    public String getNativeAdUnitID132H4Facebook() {
        if (Common.isApkDebugable()) {
            return "";
        } else {
            return (String) getValue(kNativeAdUnitID132H4Facebook);
        }
    }

    public String getNativeAdUnitID250H4Facebook() {
        if (Common.isApkDebugable()) {
            return "";
        } else {
            return (String) getValue(kNativeAdUnitID250H4Facebook);
        }
    }

    public String getSplashAdUnitID4Facebook() {
        if (Common.isApkDebugable()) {
            return "";
        } else {
            return (String) getValue(kSplashAdUnitID4Facebook);
        }
    }

    public String getInterstitialAdUnitID4Facebook() {
        if (Common.isApkDebugable()) {
            return "";
        } else {
            return (String) getValue(kInterstitialAdUnitID4Facebook);
        }
    }

    public String getRewardBasedVideoAdUnitID4Facebook() {
        if (Common.isApkDebugable()) {
            return "";
        } else {
            return (String) getValue(kRewardBasedVideoAdUnitID4Facebook);
        }
    }

    public String getFacebookNativeAdViewLayoutName80H() {
        return (String) getValue(kFacebookNativeAdViewLayoutName80H);
    }

    public String getFacebookNativeAdViewLayoutName132H() {
        return (String) getValue(kFacebookNativeAdViewLayoutName132H);
    }

    public String getFacebookNativeAdViewLayoutName250H() {
        return (String) getValue(kFacebookNativeAdViewLayoutName250H);
    }

    public String getAppID4XiaoMi() {
//        if (Common.isApkDebugable()) {
//            return "2882303761517605862";
//        } else {
//            return (String) getValue(kAppID4XiaoMi);
//        }
        return CommonConfig.sharedCommonConfig.appID4XiaoMi;
    }

    public String getBannerAdUnitID4XiaoMi() {
//        if (Common.isApkDebugable()) {
//            return "f39ec57af32135a9268b7aaffcb9f3aa";
//        } else {
//            return (String) getValue(kBannerAdUnitID4XiaoMi);
//        }
        return CommonConfig.sharedCommonConfig.bannerAdUnitID4XiaoMi;
    }

    public String getNativeAdUnitID4XiaoMi() {
//        if (Common.isApkDebugable()) {
//            return "dbbc7737e697a11bf2249c290fee95fa";
//        } else {
//            return (String) getValue(kNativeAdUnitID4XiaoMi);
//        }
        return CommonConfig.sharedCommonConfig.nativeAdUnitID4XiaoMi;
    }

    public String getNativeAdUnitID132H4XiaoMi() {
//        if (Common.isApkDebugable()) {
//            return "9aa660fd8bdbb6bee98461745916eb32";
//        } else {
//            return (String) getValue(kNativeAdUnitID132H4XiaoMi);
//        }
        return CommonConfig.sharedCommonConfig.nativeAdUnitID132H4XiaoMi;
    }

    public String getNativeAdUnitID250H4XiaoMi() {
//        if (Common.isApkDebugable()) {
//            return "e8726f9c4cefc0c769877c9482e9a061";
//        } else {
//            return (String) getValue(kNativeAdUnitID250H4XiaoMi);
//        }
        return CommonConfig.sharedCommonConfig.nativeAdUnitID250H4XiaoMi;
    }

    public String getSplashAdUnitID4XiaoMi() {
//        if (Common.isApkDebugable()) {
//            return "65a1f9a1bf0aa7c2a883c2812500c5db";
//        } else {
//            return (String) getValue(kSplashAdUnitID4XiaoMi);
//        }
        return CommonConfig.sharedCommonConfig.splashAdUnitID4XiaoMi;
    }

    public String getInterstitialAdUnitID4XiaoMi() {
//        if (Common.isApkDebugable()) {
//            return "3dc25ef175b3ffe0829648ad17057173";
//        } else {
//            return (String) getValue(kInterstitialAdUnitID4XiaoMi);
//        }
        return CommonConfig.sharedCommonConfig.interstitialAdUnitID4XiaoMi;
    }

    public String getAppID4OPPO() {
//        if (Common.isApkDebugable()) {
//            return "2882303761517605862";
//        } else {
//            return (String) getValue(kAppID4OPPO);
//        }
        return CommonConfig.sharedCommonConfig.appID4OPPO;
    }

    public String getBannerAdUnitID4OPPO() {
//        if (Common.isApkDebugable()) {
//            return "f39ec57af32135a9268b7aaffcb9f3aa";
//        } else {
//            return (String) getValue(kBannerAdUnitID4OPPO);
//        }
        return CommonConfig.sharedCommonConfig.bannerAdUnitID4OPPO;
    }

    public String getNativeAdUnitID4OPPO() {
//        if (Common.isApkDebugable()) {
//            return "dbbc7737e697a11bf2249c290fee95fa";
//        } else {
//            return (String) getValue(kNativeAdUnitID4OPPO);
//        }
        return CommonConfig.sharedCommonConfig.nativeAdUnitID4OPPO;
    }

    public String getNativeAdUnitID132H4OPPO() {
//        if (Common.isApkDebugable()) {
//            return "9aa660fd8bdbb6bee98461745916eb32";
//        } else {
//            return (String) getValue(kNativeAdUnitID132H4OPPO);
//        }
        return CommonConfig.sharedCommonConfig.nativeAdUnitID132H4OPPO;
    }

    public String getNativeAdUnitID250H4OPPO() {
//        if (Common.isApkDebugable()) {
//            return "e8726f9c4cefc0c769877c9482e9a061";
//        } else {
//            return (String) getValue(kNativeAdUnitID250H4OPPO);
//        }
        return CommonConfig.sharedCommonConfig.nativeAdUnitID250H4OPPO;
    }

    public String getSplashAdUnitID4OPPO() {
//        if (Common.isApkDebugable()) {
//            return "65a1f9a1bf0aa7c2a883c2812500c5db";
//        } else {
//            return (String) getValue(kSplashAdUnitID4OPPO);
//        }
        return CommonConfig.sharedCommonConfig.splashAdUnitID4OPPO;
    }

    public String getInterstitialAdUnitID4OPPO() {
//        if (Common.isApkDebugable()) {
//            return "3dc25ef175b3ffe0829648ad17057173";
//        } else {
//            return (String) getValue(kInterstitialAdUnitID4OPPO);
//        }
        return CommonConfig.sharedCommonConfig.interstitialAdUnitID4OPPO;
    }

    public String getAppID4Tencent() {
//        if (Common.isApkDebugable()) {
//            return "2882303761517605862";
//        } else {
//            return (String) getValue(kAppID4OPPO);
//        }
        return CommonConfig.sharedCommonConfig.appID4Tencent;
    }

    public String getBannerAdUnitID4Tencent() {
//        if (Common.isApkDebugable()) {
//            return "f39ec57af32135a9268b7aaffcb9f3aa";
//        } else {
//            return (String) getValue(kBannerAdUnitID4OPPO);
//        }
        return CommonConfig.sharedCommonConfig.bannerAdUnitID4Tencent;
    }

    public String getNativeAdUnitID4Tencent() {
//        if (Common.isApkDebugable()) {
//            return "dbbc7737e697a11bf2249c290fee95fa";
//        } else {
//            return (String) getValue(kNativeAdUnitID4OPPO);
//        }
        return CommonConfig.sharedCommonConfig.nativeAdUnitID4Tencent;
    }

    public String getNativeAdUnitID132H4Tencent() {
//        if (Common.isApkDebugable()) {
//            return "9aa660fd8bdbb6bee98461745916eb32";
//        } else {
//            return (String) getValue(kNativeAdUnitID132H4OPPO);
//        }
        return CommonConfig.sharedCommonConfig.nativeAdUnitID132H4Tencent;
    }

    public String getNativeAdUnitID250H4Tencent() {
//        if (Common.isApkDebugable()) {
//            return "e8726f9c4cefc0c769877c9482e9a061";
//        } else {
//            return (String) getValue(kNativeAdUnitID250H4OPPO);
//        }
        return CommonConfig.sharedCommonConfig.nativeAdUnitID250H4Tencent;
    }

    public String getSplashAdUnitID4Tencent() {
//        if (Common.isApkDebugable()) {
//            return "65a1f9a1bf0aa7c2a883c2812500c5db";
//        } else {
//            return (String) getValue(kSplashAdUnitID4OPPO);
//        }
        return CommonConfig.sharedCommonConfig.splashAdUnitID4Tencent;
    }

    public String getInterstitialAdUnitID4Tencent() {
//        if (Common.isApkDebugable()) {
//            return "3dc25ef175b3ffe0829648ad17057173";
//        } else {
//            return (String) getValue(kInterstitialAdUnitID4OPPO);
//        }
        return CommonConfig.sharedCommonConfig.interstitialAdUnitID4Tencent;
    }

    public List<String> preferedAdTypes() {
        //TODO: 类型强制会出问题
        return (List<String>) getValue(kPreferedAdTypes);
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


}

