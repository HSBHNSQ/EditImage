package com.lafonapps.common.ad;

import com.lafonapps.common.NotificationCenter;
import com.lafonapps.common.ad.adapter.splashad.SplashAdActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by chenjie on 2017/8/21.
 */

public class AdReachabilityDetectorImpl implements AdReachabilityDetector {

    private static final String TAG = AdReachabilityDetectorImpl.class.getCanonicalName();
    private List<ChangedCallback> callbacks = new ArrayList<>(5);

    private boolean hasPermissionForReadPhoneState = false;
    private boolean isNetworkAvaliable = false;
    private Observer permissionObserver = new Observer() {
        @Override
        public void update(Observable observable, Object o) {
            if (o instanceof Boolean) {
                hasPermissionForReadPhoneState = (Boolean) o;

                ChangedCallback[] callbackArray = callbacks.toArray(new ChangedCallback[callbacks.size()]);
                for (ChangedCallback c : callbackArray) {
                    c.changed(hasPermissionForReadPhoneState);
                }
            }
        }
    };

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
        //TODO: 待实现网络监听和权限监听
        NotificationCenter.defaultCenter().addObserver(SplashAdActivity.ON_REQUEST_READ_PHONE_STATE_PERMISSION_RESULT_NOTIFICATION, permissionObserver);


        NotificationCenter.defaultCenter().addObserver("aaa", new Observer() {
            @Override
            public void update(Observable observable, Object o) {

            }
        });
    }

    @Override
    public void stop() {
        //TODO: 待实现
        NotificationCenter.defaultCenter().removeObserver(SplashAdActivity.ON_REQUEST_READ_PHONE_STATE_PERMISSION_RESULT_NOTIFICATION, permissionObserver);
    }

}
