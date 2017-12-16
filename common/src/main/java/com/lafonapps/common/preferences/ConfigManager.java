package com.lafonapps.common.preferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenjie on 2017/7/4.
 */

class ConfigManager {

    private Map commonConfigMap = new HashMap();
    private Map targetConfigMap= new HashMap();
    private Map configMap = new HashMap();

    public ConfigManager() {
//        Common.getSharedApplication().getResources().getStringArray(R.array.TestDevices4Ad);
    }

    public Object configValueForKey(String key) {
        Object value = this.configMap.get(key);
        if (value == null) {
            value = this.targetConfigMap.get(key);
        }
        if (value == null) {
            value = this.commonConfigMap.get(key);
        }
        return value;
    }
}
