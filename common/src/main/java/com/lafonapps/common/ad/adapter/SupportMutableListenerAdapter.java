package com.lafonapps.common.ad.adapter;

/**
 * Created by chenjie on 2017/7/5.
 */

public interface SupportMutableListenerAdapter<L> {

    public void addListener(L listener);

    public void removeListener(L listener);

    public L[] getAllListeners();

}
