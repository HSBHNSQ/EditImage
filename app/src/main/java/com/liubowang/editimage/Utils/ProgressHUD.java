package com.liubowang.editimage.Utils;

import android.app.Activity;
import android.graphics.Point;
import android.os.Handler;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.liubowang.editimage.R;

/**
 * Created by heshaobo on 2017/10/27.
 */

public class ProgressHUD {

    private static KProgressHUD hud = null;

    public static void show(Activity activity,
                            String label,
                            String detailLabel){
        if (hud != null){
            hud.dismiss();
        }
        Point point = getSize(activity);
        hud = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(label)
                .setDetailsLabel(detailLabel)
                .setBackgroundColor(activity.getResources().getColor(R.color.color_3B5038))
                .setCancellable(false)
                .setDimAmount(0.5f);
        hud.setSize(point.x,point.y);
        hud.show();
    }

    public static void showProgressUpdate(Activity activity,
                            String label,
                            String detailLabel){
        if (hud != null){
            hud.dismiss();
        }
        Point point = getSize(activity);
        hud = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                .setLabel(label)
                .setDetailsLabel(detailLabel)
                .setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary))
                .setDimAmount(0.5f)
                .setCancellable(false)
                .setMaxProgress(100);
        hud.setSize(point.x,point.y);
        hud.setProgress(0);
        hud.show();
    }
    public static void dismiss(int afterDelay){
        if (hud == null)  return;
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hud.dismiss();
            }
        },afterDelay);
    }
    public static void dismiss(){
        dismiss(0);
    }

    public static void setProgressUpdate(int progress) {
        if (hud == null)  return;
        hud.setProgress(progress);
    }

    private static Point getSize(Activity activity){
        ScreenUtil.Screen screen = ScreenUtil.getScreenSize(activity);
        Point point = new Point((int) (screen.widthDp / 2),(int)(screen.heightDp / 4) );
        return point;
    }


}
