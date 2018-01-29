package com.liubowang.photoretouch.Utils;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heshaobo on 2018/1/16.
 */

public class ViewTransformUtil {

    private final static String TAG = ViewTransformUtil.class.getSimpleName();
    private ScaleGestureDetector mScaleDetector;
    private RotationGestureDetector mRotateDetector;
    private GestureDetector mGestureDetector;
    private WeakReference<View> weakView;
    private float orignY = 0;
    private float orignX = 0;
    private float curScale = 1;
    private float preScale = 1;
    private float angle = 0;
    private float positionX = 0;
    private float positionY = 0;
    private ViewTransformUtil(){
        super();
    }

    private static List<WeakReference<View>> weakReferenceList = new ArrayList<>();

    public ViewTransformUtil(View view){
        this();
        attechView(view);
    }


    public void attechView(final View view){
        if (view == null) return;
        weakView = new WeakReference<View>(view);
        weakReferenceList.add(weakView);
        view.setTag(this);
        setupGestureListeners();
    }

    @Override
    protected void finalize() throws Throwable {
        if (weakView != null){
            weakReferenceList.remove(weakView);
        }
        super.finalize();
    }

    public void onTouch(MotionEvent event) {
        if (mGestureDetector != null){
            mGestureDetector.onTouchEvent(event);
        }
        if (mScaleDetector != null){
            mScaleDetector.onTouchEvent(event);
        }
        if (mRotateDetector != null){
            mRotateDetector.onTouchEvent(event);
        }
    }
    private void setupGestureListeners() {
        View view = weakView.get();
        if (view != null){
            mGestureDetector = new GestureDetector(view.getContext(), new GestureListener(), null, true);
            mScaleDetector = new ScaleGestureDetector(view.getContext(), new ScaleListener());
            mRotateDetector = new RotationGestureDetector(new RotateListener());
        }
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            View view = weakView.get();
            if (view != null){
                curScale = detector.getScaleFactor() * preScale;
                if(curScale>5||curScale<0.1){
                    preScale=curScale;
                    return true;
                }
                preScale=curScale;
                view.setScaleX(curScale);
                view.setScaleY(curScale);
            }
            return true;
        }
    }

    public void resetData(){
        orignX = 0;
        orignY = 0;
        angle = 0;
        curScale = 1;
        preScale = 1;
        positionX = orignX;
        positionY = orignY;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            for (WeakReference<View> w: weakReferenceList) {
                View view = w.get();
                if (view != null){
                    view.setScaleX(1);
                    view.setScaleY(1);
                    view.setRotation(0);
                    view.setTranslationY(0);
                    view.setTranslationX(0);
                   Object object = view.getTag();
                   if (object != null){
                       ((ViewTransformUtil) object).resetData();
                   }
                }
            }
//            View view = weakView.get();
//            if (view != null){
//                view.setScaleX(1);
//                view.setScaleY(1);
//                view.setRotation(0);
//                view.setTranslationY(0);
//                view.setTranslationX(0);
//                orignX = 0;
//                orignY = 0;
//                angle = 0;
//                curScale = 1;
//                preScale = 1;
//                positionX = orignX;
//                positionY = orignY;
//            }
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            View view = weakView.get();
            if (view != null){
                Log.d(TAG,"distanceX:" + distanceX);
                Log.d(TAG,"distanceY:" + distanceY);
                Log.d(TAG,"view.getX():" + view.getX());
                Log.d(TAG,"view.getY():" + view.getY());
                positionX = positionX - distanceX;
                positionY = positionY - distanceY;
                view.setTranslationX(positionX);
                view.setTranslationY(positionY);

            }
            return true;
        }

    }

    private  class RotateListener extends RotationGestureDetector.SimpleOnRotationGestureListener {
        @Override
        public boolean onRotation(RotationGestureDetector rotationDetector) {
            angle += rotationDetector.getAngle();
            View view = weakView.get();
            if (view != null) {
                view.setRotation(angle);
            }
            return true;
        }

    }



}
