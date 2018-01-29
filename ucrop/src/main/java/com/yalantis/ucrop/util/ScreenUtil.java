package com.yalantis.ucrop.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by heshaobo on 2017/11/16.
 */

public class ScreenUtil {
    public static class Screen {
        public int widthPixels;
        public int heightPixels;
        public int densityDpi;
        public float widthDp;
        public float heightDp;
        public float density;
        public Screen(){}

    }
    private static Screen mScreen;
    private static int mLedBitmapCount = 0;
    public static Screen getScreenSize(Context ctx){
        if (mScreen != null) return mScreen;
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        mScreen = new Screen();
        mScreen.widthPixels = dm.widthPixels;
        mScreen.heightPixels = dm.heightPixels;
        mScreen.densityDpi = dm.densityDpi;
        mScreen.widthDp = (dm.widthPixels / dm.density);
        mScreen.heightDp =(dm.heightPixels / dm.density);
        mScreen.density = dm.density;
        return mScreen;
    }
}
