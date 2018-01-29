package com.yalantis.ucrop.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by heshaobo on 2018/1/13.
 */

public class ToastUtil {

    private static Toast toast;
    public static void makeText(Context context,String text){
        if (toast != null){
            toast.setText(text);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        }
        else { toast  = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
