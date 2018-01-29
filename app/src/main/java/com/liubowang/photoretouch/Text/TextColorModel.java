package com.liubowang.photoretouch.Text;

import android.graphics.Color;

/**
 * Created by heshaobo on 2018/1/17.
 */

public class TextColorModel {

    public int mainColor = Color.BLACK;
    public int secondColor = Color.WHITE;
    public boolean isSelected = false;

    @Override
    public String toString() {
        return "TextColorModel:{" +
                "\nforecolor = " + mainColor +
                "\nbgColor = " + secondColor +
                "\nisSelected = " + isSelected +
                "\n}";
    }
}
