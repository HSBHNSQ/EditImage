package com.liubowang.photoretouch.Utils;

import android.graphics.Color;
import android.util.Log;

import java.util.Random;

/**
 * Created by heshaobo on 2018/1/17.
 */

public class ColorUtil {

    private static String TAG = "ColorUtil";
    public static int[] getRandomColors(int count){
        int[] colors = new int[count];
        colors[0] = Color.WHITE;
        colors[1] = Color.BLACK;
        for (int i = 2; i < count ; i ++){
            int color = getDifferentRandomColor(colors[i - 2],50);//getRandomColor();
            Log.d(TAG,"------------------------");
            colors[i] = color;
        }
        return colors;
    }


    public static double getColorDistance(int color1,int color2){
        return HSV.distanceOf(HSV.colorToHSV(color1),HSV.colorToHSV(color2));
    }


    /**
     * @param originalColor
     * @param distance (1,100)颜色差距
     * @return
     */
    public static int getDifferentRandomColor(int originalColor,int distance){
        int color = getRandomColor();
        HSV oriHSV = HSV.colorToHSV(originalColor);
        while (HSV.distanceOf(oriHSV,HSV.colorToHSV(color)) < distance){
            color = getRandomColor();
        }
        return color;
    }


    /** 获取随机颜色
     * @return color
     */
    public static int getRandomColor(){
        //红色
        String red;
        //绿色
        String green;
        //蓝色
        String blue;
        //生成随机对象
        Random random = new Random();
        //生成红色颜色代码
        red = Integer.toHexString(random.nextInt(256)).toUpperCase();
        //生成绿色颜色代码
        green = Integer.toHexString(random.nextInt(256)).toUpperCase();
        //生成蓝色颜色代码
        blue = Integer.toHexString(random.nextInt(256)).toUpperCase();
        //判断红色代码的位数
        red = red.length()==1 ? "0" + red : red ;
        //判断绿色代码的位数
        green = green.length()==1 ? "0" + green : green ;
        //判断蓝色代码的位数
        blue = blue.length()==1 ? "0" + blue : blue ;
        //生成十六进制颜色值
        String color = "#"+red+green+blue;
        return Color.parseColor(color);
    }

    /**
     * Created by heshaobo on 2018/1/17.
     */

    public static class HSV {
        public float H;
        public float S;
        public float V;

        //self-defined
        private static final double R = 100;
        private static final double angle = 30;
        private static final double h = R * Math.cos(angle / 180 * Math.PI);
        private static final double r = R * Math.sin(angle / 180 * Math.PI);

        public static HSV colorToHSV(int color){
            float[] tempHSV = new float[3];
            Color.colorToHSV(color, tempHSV);
            float h = tempHSV[0];
            float s = tempHSV[1];
            float v = tempHSV[2];
            HSV hsv = new HSV();
            hsv.H = h;
            hsv.S = s;
            hsv.V = v;
            return hsv;
        }

        public static double distanceOf(HSV hsv1, HSV hsv2) {
            double x1 = r * hsv1.V * hsv1.S * Math.cos(hsv1.H / 180 * Math.PI);
            double y1 = r * hsv1.V * hsv1.S * Math.sin(hsv1.H / 180 * Math.PI);
            double z1 = h * (1 - hsv1.V);
            double x2 = r * hsv2.V * hsv2.S * Math.cos(hsv2.H / 180 * Math.PI);
            double y2 = r * hsv2.V * hsv2.S * Math.sin(hsv2.H / 180 * Math.PI);
            double z2 = h * (1 - hsv2.V);
            double dx = x1 - x2;
            double dy = y1 - y2;
            double dz = z1 - z2;
            return Math.sqrt(dx * dx + dy * dy + dz * dz);
        }

    }
}
