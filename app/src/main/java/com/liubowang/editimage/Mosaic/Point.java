package com.liubowang.editimage.Mosaic;

/**
 * Created by zhaoya on 2017/10/24.
 */

public class Point {
    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point(){
        super();
        this.x = 0;
        this.y = 0;
    }


    public float x;
    public float y;

    public Point clone() {
        return new Point(x, y);
    }
}
