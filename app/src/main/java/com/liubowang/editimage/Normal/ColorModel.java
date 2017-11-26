package com.liubowang.editimage.Normal;

import android.graphics.Color;

/**
 * Created by heshaobo on 2017/11/20.
 */

public class ColorModel {

    public boolean isSeleced  = false;
    public int color;

    private ColorModel(){super();}

    public ColorModel(String cst){
        super();
        color = Color.parseColor(cst);
    }
}
