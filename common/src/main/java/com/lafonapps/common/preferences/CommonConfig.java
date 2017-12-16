package com.lafonapps.common.preferences;

/**
 * Created by chenjie on 2017/8/18.
 */

public class CommonConfig {

    public static final CommonConfig sharedCommonConfig = new CommonConfig();

    /** Admo告配置 */
    public String appID4Admob = "ca-app-pub-8698484584626435~1504595858";
    public String bannerAdUnitID4Admob = "ca-app-pub-8698484584626435/7634889932";
    public String nativeAdUnitID4Admob = "ca-app-pub-8698484584626435/1142645536";
    public String nativeAdUnitID132H4Admob = "ca-app-pub-8698484584626435/1284926399";
    public String nativeAdUnitID250H4Admob = "ca-app-pub-8698484584626435/9762359826";
    public String splashAdUnitID4Admob = "ca-app-pub-8698484584626435/6301405192";
    public String interstitialAdUnitID4Admob = "ca-app-pub-8698484584626435/6470159684";

    /** 小米广告配置 */
    public String appID4XiaoMi = "2882303761517605862";
    public String bannerAdUnitID4XiaoMi = "f39ec57af32135a9268b7aaffcb9f3aa";
    public String nativeAdUnitID4XiaoMi = "dbbc7737e697a11bf2249c290fee95fa";
    public String nativeAdUnitID132H4XiaoMi = "9aa660fd8bdbb6bee98461745916eb32";
    public String nativeAdUnitID250H4XiaoMi = "e8726f9c4cefc0c769877c9482e9a061";
    public String splashAdUnitID4XiaoMi = "65a1f9a1bf0aa7c2a883c2812500c5db";
    public String interstitialAdUnitID4XiaoMi = "3dc25ef175b3ffe0829648ad17057173";

    /** OPPO广告配置 */
    public String appID4OPPO = "2882303761517605862";
    public String bannerAdUnitID4OPPO = "f39ec57af32135a9268b7aaffcb9f3aa";
    public String nativeAdUnitID4OPPO = "dbbc7737e697a11bf2249c290fee95fa";
    public String nativeAdUnitID132H4OPPO = "9aa660fd8bdbb6bee98461745916eb32";
    public String nativeAdUnitID250H4OPPO = "e8726f9c4cefc0c769877c9482e9a061";
    public String splashAdUnitID4OPPO = "65a1f9a1bf0aa7c2a883c2812500c5db";
    public String interstitialAdUnitID4OPPO = "3dc25ef175b3ffe0829648ad17057173";

    /** tencent广告配置 */
    public String appID4Tencent = "1101152570";
    public String bannerAdUnitID4Tencent = "9079537218417626401";
    public String nativeAdUnitID4Tencent = "";
    public String nativeAdUnitID132H4Tencent = "";
    public String nativeAdUnitID250H4Tencent = "5000709048439488";
    public String splashAdUnitID4Tencent = "8863364436303842593";
    public String interstitialAdUnitID4Tencent = "8575134060152130849";

    /** 测试设备ID */
    public String[] testDevices = {""};

    /** 界面切换多少次后弹出全屏广告 */
    public int numberOfTimesToPresentInterstitial = 5;

    /** 是否展示广告按钮 */
    public boolean shouldShowAdButton = true;
    /** 是否显示横幅广告 */
    public boolean shouldShowBannerView = true;
    /** 是否展示开屏广告 */
    public boolean shouldShowSplashAd = true;

    /** 友盟AppKey */
    public String UmengAppKey = "";
}
