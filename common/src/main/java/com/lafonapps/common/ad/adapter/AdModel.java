package com.lafonapps.common.ad.adapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjie on 2017/7/5.
 */

public class AdModel {

    private String admobAdID;
    private String facebookAdID;
    private String xiaomiAdID;
    private String oppoAdID;
    private String tencentAdID;
    /* 广告优先顺序。越靠前优先级越高 */
    private List<AdPlatform> preferedAdPlatforms;

    public AdModel() {
        this.preferedAdPlatforms = new ArrayList<>(5);
        this.preferedAdPlatforms.add(AdPlatform.ADMOB);
        this.preferedAdPlatforms.add(AdPlatform.FACEBOOK);
    }

    public String getAdmobAdID() {
        return admobAdID;
    }

    public void setAdmobAdID(String admobAdID) {
        this.admobAdID = admobAdID;
    }

    public String getFacebookAdID() {
        return facebookAdID;
    }

    public void setFacebookAdID(String facebookAdID) {
        this.facebookAdID = facebookAdID;
    }

    public String getXiaomiAdID() {
        return xiaomiAdID;
    }

    public void setXiaomiAdID(String xiaomiAdID) {
        this.xiaomiAdID = xiaomiAdID;
    }

    public String getOppoAdID() {
        return oppoAdID;
    }

    public void setOppoAdID(String oppoAdID) {
        this.oppoAdID = oppoAdID;
    }

    public String getTencentAdID() {
        return tencentAdID;
    }

    public void setTencentAdID(String tencentAdID) {
        this.tencentAdID = tencentAdID;
    }

    public List<AdPlatform> getPreferedAdPlatforms() {
        return preferedAdPlatforms;
    }

    public void setPreferedAdPlatform(List<AdPlatform> preferedAdPlatforms) {
        if (preferedAdPlatforms == null || preferedAdPlatforms.size() < 1) {
            List<AdPlatform> platform = new ArrayList<>(5);
            platform.add(AdPlatform.ADMOB);
            platform.add(AdPlatform.FACEBOOK);
            this.preferedAdPlatforms = platform;
        }
    }

    public static enum AdPlatform {
        ADMOB("Admob"),
        FACEBOOK("Facebook");

        /* 广告平台名称 */
        private String platformName;

        private AdPlatform(String platformName) {
            this.platformName = platformName;
        }
    }

}
