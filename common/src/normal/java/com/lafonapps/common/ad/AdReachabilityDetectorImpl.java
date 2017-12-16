package com.lafonapps.common.ad;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjie on 2017/8/21.
 */

public class AdReachabilityDetectorImpl implements AdReachabilityDetector {

    private List<ChangedCallback> callbacks = new ArrayList<>(5);

    @Override
    public void add(ChangedCallback changedCallback) {
        if (!callbacks.contains(changedCallback)) {
            callbacks.add(changedCallback);
        }
    }

    @Override
    public void remove(ChangedCallback changedCallback) {
        callbacks.remove(changedCallback);
    }

    @Override
    public void start() {
//TODO: 待实现网络监听

        ChangedCallback[] array = callbacks.toArray(new ChangedCallback[callbacks.size()]);
        for (ChangedCallback c : array) {
            c.changed(true);
        }
    }

    @Override
    public void stop() {
//TODO: 待实现
    }
}
