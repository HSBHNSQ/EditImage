package com.lafonapps.common.ad.adapter;

/**
 * Created by chenjie on 2017/8/16.
 */

public interface SplashAd {

    public static final String META_DATA_TARGET_ACTIVITY = "targetActivity";
    public static final String META_DATA_DEFAULT_IMAGE = "defaultImage";

    /* 允许等待的广告加载最大时长，超过这个时长未成功加载广告则不展示广告。单位：秒 */
    public void setRequestTimeOut(int timeOut);

    /* 加载到广告后，广告展示的时长，单位：秒 */
    public void setDisplayDuration(int duration);

    /* 指定默认图资源ID，在没有加载到广告之前会展示 */
    public void setDefaultImage(int drawableID);

    /* 加载并展示广告 */
    public void loadAndDisplay();

}
