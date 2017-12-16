package com.lafonapps.common.ad.adapter;

/**
 * Created by chenjie on 2017/8/21.
 */

public interface AdAdapter {

    /**
     * 调试用的设备ID数组
     * @param debugDevices
     */
    public void setDebugDevices(String[] debugDevices);

    /**
     * 是否已经请求到广告可供展示
     * @return
     */
    public boolean isReady();

    /**
     * 加载广告
     */
    public void loadAd();

    /**
     * 广告是否可以在多个界面重用
     */
    public boolean reuseable();

}
