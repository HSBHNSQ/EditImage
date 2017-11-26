package com.liubowang.editimage.Base;

import android.app.Application;
import android.util.Log;

/**
 * Created by heshaobo on 2017/11/14.
 */

public class EIApplication extends Application {

    private final String TAG = getClass().getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate");
    }
}
