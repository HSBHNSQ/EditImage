package com.liubowang.editimage.Draw;

import android.graphics.Paint;
import android.graphics.Path;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heshaobo on 2017/7/24.
 */

public class DrawMove {


    private static DrawMove mHelper;
    private Paint mPaint;
    private float mStartX;
    private float mStartY;
    private float mEndX;
    private float mEndY;
    private DrawView.DrawStyle mDrawStyle;
    private List<Path> mDrawPathList;

    private DrawMove(){}
    public static DrawMove newDrawMove(){
        mHelper = new DrawMove();
        return mHelper;
    }

    public Paint getPaint() {    return mPaint;  }

    public DrawView.DrawStyle getDrawStyle() {  return mDrawStyle; }

    public List<Path> getDrawPathList() {  return mDrawPathList; }

    public float getStartX() { return mStartX; }

    public float getStartY() { return mStartY; }

    public float getEndX() { return mEndX; }

    public float getEndY() { return mEndY; }

    public DrawMove setPaint(Paint paint){
        this.mPaint = paint;
        if (mHelper != null)
            return mHelper;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setStartX(float startX){
        this.mStartX = startX;
        if (mHelper != null)
            return mHelper;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setStartY(float startY){
        this.mStartY = startY;
        if (mHelper != null)
            return mHelper;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setEndX(float endX){
        this.mEndX = endX;
        if (mHelper != null)
            return mHelper;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setEndY(float endY){
        this.mEndY = endY;
        if (mHelper != null)
            return mHelper;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setDrawStyle(DrawView.DrawStyle style){
        this.mDrawStyle = style;
        if (mHelper != null)
            return mHelper;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

    public DrawMove setDrawPathList(ArrayList<Path> list){
        this.mDrawPathList = list;
        if (mHelper != null)
            return mHelper;
        else throw new RuntimeException("Create new instance of DrawMove first!");
    }

}
