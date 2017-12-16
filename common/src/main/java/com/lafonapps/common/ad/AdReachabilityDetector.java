package com.lafonapps.common.ad;

/**
 * Created by chenjie on 2017/8/21.
 *
 * 检测广告是否可以请求到。包括网络、权限等
 */

public interface AdReachabilityDetector {

    public void add(ChangedCallback changedCallback);
    public void remove(ChangedCallback changedCallback);

    public void start();

    public void stop();

    public static interface ChangedCallback {

        /**
         * 可达状态发生变化时被调用
         * @param reachable true为可以请求到，false为不可请求到
         */
        public void changed(boolean reachable);

    }
}
