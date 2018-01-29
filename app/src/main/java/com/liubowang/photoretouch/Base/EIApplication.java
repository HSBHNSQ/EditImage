package com.liubowang.photoretouch.Base;

import android.app.Application;
import android.content.Context;

import com.lafonapps.common.Common;
import com.lafonapps.common.preferences.CommonConfig;
import com.liubowang.photoretouch.BuildConfig;
import com.liubowang.photoretouch.Feedback.FeedbackOperation;
import com.liubowang.photoretouch.Feedback.JumpContactOperation;

/**
 * Created by heshaobo on 2017/11/14.
 */

public class EIApplication extends Application {

    private static Context mContext;
    public static Context getContext(){
        return mContext;
    }
    private static final String TAG = EIApplication.class.getCanonicalName();
    private static EIApplication sharedApplication;
    public static EIApplication getSharedApplication() {
        return sharedApplication;
    }
    public boolean bannerADLoadSuccess = true;
    @Override
    public void onCreate() {
        super.onCreate();
        sharedApplication = this;
        mContext = getApplicationContext();
        configAD();
        configFeedBack();
    }

    private void configFeedBack(){
        FeedbackOperation.Configuration(this,
                "irtPiFrfyaIKMDdCHrdQIYXS-gzGzoHsz",
                "awtNPPsQqWO84vohtk5FWtVP");
        JumpContactOperation.SetQQ("3334244889");
        JumpContactOperation.SetEmail("3334244889@qq.com");
    }

    private void configAD(){
/* Admob广告配置 */
        CommonConfig.sharedCommonConfig.appID4Admob = "ca-app-pub-7028363992110677~6407056179"; //广告应用ID
        CommonConfig.sharedCommonConfig.bannerAdUnitID4Admob = " ca-app-pub-7028363992110677/2460018405"; //横幅广告ID
//        CommonConfig.sharedCommonConfig.nativeAdUnitID4Admob = "ca-app-pub-8698484584626435/2229416369"; //小型原生广告ID
//        CommonConfig.sharedCommonConfig.nativeAdUnitID132H4Admob = "ca-app-pub-8698484584626435/1284926399"; //中型原生广告ID
//        CommonConfig.sharedCommonConfig.nativeAdUnitID250H4Admob = "ca-app-pub-8698484584626435/8459960407"; //大型原生广告ID
//        CommonConfig.sharedCommonConfig.splashAdUnitID4Admob = "ca-app-pub-8698484584626435/6470159684"; //用作开屏广告的原生广告ID
        CommonConfig.sharedCommonConfig.interstitialAdUnitID4Admob = "ca-app-pub-7028363992110677/7457248111"; //全屏广告ID

        /* 小米广告配置 */
        CommonConfig.sharedCommonConfig.appID4XiaoMi = "2882303761517653749"; //广告应用ID
        CommonConfig.sharedCommonConfig.bannerAdUnitID4XiaoMi = "8be557a8229ff834039aef6162990a6f"; //横幅广告ID
        CommonConfig.sharedCommonConfig.nativeAdUnitID4XiaoMi = "3d4f6f6d01da4366637ec48ed4448cf8"; //小型信息流广告ID
//        CommonConfig.sharedCommonConfig.nativeAdUnitID132H4XiaoMi = ""; //信息流组图广告ID
        CommonConfig.sharedCommonConfig.nativeAdUnitID250H4XiaoMi = "b083b1930039cad6de2f0959b4ad8981"; //大型信息流广告ID
        CommonConfig.sharedCommonConfig.splashAdUnitID4XiaoMi = "1a0d715d55550660e2f708910f4fde22"; //开屏广告ID
//        CommonConfig.sharedCommonConfig.interstitialAdUnitID4XiaoMi = "af2b07180476574b5ea8e4c0bb24da50"; //全屏广告ID

        /* OPPO广告配置 */
//        CommonConfig.sharedCommonConfig.appID4OPPO = "100";
//        CommonConfig.sharedCommonConfig.bannerAdUnitID4OPPO = "328";
//        CommonConfig.sharedCommonConfig.nativeAdUnitID4OPPO = "";
//        CommonConfig.sharedCommonConfig.nativeAdUnitID132H4OPPO = "";
//        CommonConfig.sharedCommonConfig.nativeAdUnitID250H4OPPO = "332";
//        CommonConfig.sharedCommonConfig.splashAdUnitID4OPPO = "331";
//        CommonConfig.sharedCommonConfig.interstitialAdUnitID4OPPO = "329";

        /* 腾讯广告配置 */
        CommonConfig.sharedCommonConfig.appID4Tencent = "1106553291";
        CommonConfig.sharedCommonConfig.bannerAdUnitID4Tencent = "7020128838707094";
//        CommonConfig.sharedCommonConfig.nativeAdUnitID4Tencent = "";
//        CommonConfig.sharedCommonConfig.nativeAdUnitID132H4Tencent = "";
//        CommonConfig.sharedCommonConfig.nativeAdUnitID250H4Tencent = "";
        CommonConfig.sharedCommonConfig.splashAdUnitID4Tencent = "1080720868407015";
//        CommonConfig.sharedCommonConfig.interstitialAdUnitID4Tencent = "4050321818509036";



        /* 界面切换多少次后弹出全屏广告 */
        CommonConfig.sharedCommonConfig.numberOfTimesToPresentInterstitial = 999999;

        /* 测试设备ID */
        CommonConfig.sharedCommonConfig.testDevices = new String[]{
                "2C7051C179D611A65CB34AED3255F136",
                "9E8A18C2A04EA50F41F258354D86601F",
                "7D08A034F6946BED1E0EF80F61A71124",
                "1FB61E9F3F955A3DEF1F1DCA2CD2C510",
                "226FF93D678B6499DF2DAA0AE56802F1",
                "181F363C876857CE4F79750F6A10D3AA",
                "789913A961721FF31C35187C2BAA0430"
        };

        /** 是否展示广告按钮 */
        CommonConfig.sharedCommonConfig.shouldShowAdButton = BuildConfig.showAdButton; //在app的build.gradle中进行差异化配置
        /** 是否显示横幅广告 */
        CommonConfig.sharedCommonConfig.shouldShowBannerView = BuildConfig.showBannerView; //在app的build.gradle中进行差异化配置
        /** 是否展示开屏广告 */
        CommonConfig.sharedCommonConfig.shouldShowSplashAd = BuildConfig.showSplashAd; //在app的build.gradle中进行差异化配置

        /* 友盟统计key */
//        CommonConfig.sharedCommonConfig.UmengAppKey = "59a6927b6e27a45fe200031f";

        Common.initialize(this);

    }


}
