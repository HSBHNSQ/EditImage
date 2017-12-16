package com.lafonapps.common.ad;

import com.lafonapps.common.utils.ViewUtil;

/**
 * Created by chenjie on 2017/7/5.
 */

public final class AdSize {
    private int width;
    private int height;

    public AdSize() {

    }

    public AdSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getDPWidth() {
        return ViewUtil.px2dp(width);
    }

    public int getDPHeight() {
        return ViewUtil.px2dp(height);
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName() + "@" + width + "_" + height;
    }
}
