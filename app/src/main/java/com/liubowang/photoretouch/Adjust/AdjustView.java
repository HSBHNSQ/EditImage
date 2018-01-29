package com.liubowang.photoretouch.Adjust;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by pc on 2017/6/26.
 * 算法来源：http://www.gson.org/thesis/warping-thesis.pdf
 */
public class AdjustView extends View {

    private static final String TAG = AdjustView.class.getSimpleName();
    public static final int MESH_TYPE_FACE_THIN = 1;
    public static final int MESH_TYPE_BIG_EYE = 2;
    private int mWidth, mHeight;//View 的宽高
    private int radius = 40;//作用范围半径
    private Paint circlePaint;
    private Paint directionPaint;
    private boolean showCircle;//是否显示变形圆圈
    private boolean showDirection;//是否显示变形方向
    private float startX, startY, moveX, moveY;//变形起始坐标,滑动坐标
    private int WIDTH = 200;//将图像分成多少格
    private int HEIGHT = 200;
    private int COUNT = (WIDTH + 1) * (HEIGHT + 1);//交点坐标的个数
    private float[] verts = new float[COUNT * 2];//用于保存COUNT的坐标 x0, y0, x1, y1......
    private float[] orig = new float[COUNT * 2];//用于保存原始的坐标
    private Bitmap mBitmap;
    private int meshType = MESH_TYPE_BIG_EYE;
    private List<float[]> historyList = new ArrayList<>();
    private int historyIndex = 0;
    private IOnStepChangeListener onStepChangeListener;


    public AdjustView(Context context) {
        super(context);
        init();
    }

    public AdjustView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AdjustView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(2);
        circlePaint.setColor(Color.parseColor("#FFFFFF"));
        directionPaint = new Paint();
        directionPaint.setStyle(Paint.Style.FILL);
        directionPaint.setStrokeWidth(3);
        directionPaint.setColor(Color.parseColor("#FFFFFF"));
    }


    public void setImageBitmap(Bitmap imageBitmap) {
        this.mBitmap = imageBitmap;
        initView();
        invalidate();
    }

    private void initView() {
        if (mBitmap == null) return;
        int index = 0;
        if (historyList != null){
            historyList.clear();
        }else {
            historyList = new ArrayList<>();
        }

        mBitmap = zoomBitmap(mBitmap, mWidth, mHeight);
        float bmWidth = mBitmap.getWidth();
        float bmHeight = mBitmap.getHeight();
        for (int i = 0; i < HEIGHT + 1; i++) {
            float fy = bmHeight * i / HEIGHT;
            for (int j = 0; j < WIDTH + 1; j++) {
                float fx = bmWidth * j / WIDTH;
                //X轴坐标 放在偶数位
                verts[index * 2] = fx;
                orig[index * 2] = verts[index * 2];
                //Y轴坐标 放在奇数位
                verts[index * 2 + 1] = fy;
                orig[index * 2 + 1] = verts[index * 2 + 1];
                index += 1;
            }
        }
        historyList.add(orig);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        initView();
    }

    private Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        float scale = Math.min(scaleWidth,scaleHeight);
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }


    public Bitmap getResultBmp(){
        Bitmap result = Bitmap.createBitmap(mBitmap.getWidth(),mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmapMesh(mBitmap, WIDTH, HEIGHT, verts, 0, null, 0, null);
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap == null) return;
        canvas.drawBitmapMesh(mBitmap, WIDTH, HEIGHT, verts, 0, null, 0, null);
        if (showCircle) {
            canvas.drawCircle(startX, startY, radius, circlePaint);
        }
        if (showDirection) {
//            canvas.drawLine(startX, startY, moveX, moveY, directionPaint);
            drawArrow(startX,startY,moveX,moveY,directionPaint,canvas);
        }
        if (onStepChangeListener != null){
            onStepChangeListener.onEndMesh();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //绘制变形区域
                startX = event.getX();
                startY = event.getY();
                showCircle = true;
                if (onStepChangeListener != null){
                    onStepChangeListener.onStartTouch();
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (MESH_TYPE_BIG_EYE == meshType){
                    startX = event.getX();
                    startY = event.getY();
                    showCircle = true;
                }else if (MESH_TYPE_FACE_THIN == meshType){
                    moveX = event.getX();
                    moveY = event.getY();
                    showDirection = true;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                showCircle = false;
                showDirection = false;

                if (onStepChangeListener != null){
                    onStepChangeListener.onStartMesh();
                }
                if (MESH_TYPE_FACE_THIN == meshType){
                    warp(startX, startY, event.getX(), event.getY());
                }else if (MESH_TYPE_BIG_EYE == meshType){
                    bigEye(event.getX(),event.getY());
                }
                break;
        }
        return true;
    }


    public void setMeshType(int meshType) {
        this.meshType = meshType;
    }

    private void bigEye(float centerX, float centerY) {

        float strength = -0.05f;
        int range = radius * radius;
        for (int i = 0; i < COUNT * 2; i += 2) {
            float dx = verts[i] - centerX;
            float dy = verts[i + 1] - centerY;
            float distance = dx * dx + dy * dy;
            if (distance <= range) {
                float scaleFactor = 1 - distance / range;
                scaleFactor = 1 -  strength * scaleFactor;
                float scaleX = dx * scaleFactor + centerX;
                float scaleY = dy * scaleFactor + centerY;
                verts[i] =  scaleX;
                verts[i + 1] = scaleY;
            }
        }
        addHistoryList(verts);
        invalidate();
    }

    private void addHistoryList(float[] verts){
        if (historyIndex + 1 <  historyList.size()){
            historyList = historyList.subList(0,historyIndex + 1);
        }
        float[] vertTmp = new float[COUNT * 2];
        for (int i = 0; i < vertTmp.length; i++) {
            vertTmp[i] = verts[i];
        }
        historyList.add(vertTmp);
        historyIndex += 1;
        Log.d(TAG,"addHistoryList:historyIndex:" + historyIndex);
        Log.d(TAG,"historyList.size() = " + historyList.size());
    }

    private void warp(float startX, float startY, float endX, float endY) {
        //计算拖动距离
        float ddPull = (endX - startX) * (endX - startX) + (endY - startY) * (endY - startY);
        float dPull = (float) Math.sqrt(ddPull);

        //文献中提到的算法，并不能很好的实现拖动距离 MC 越大变形效果越明显的功能，下面这行代码则是我对该算法的优化
// 优化方式1
// dPull = screenWidth - dPull >= 0.0001f ? screenWidth - dPull : 0.0001f
        //优化方式2
        float min = Math.min(mWidth,mHeight);
        float de = dPull / min  >= 0.9 ? 0.9f : dPull / min;
        dPull = (1 - de) * radius ;
        float strength = 10;
        for (int i = 0; i < COUNT * 2; i += 2) {
            //计算每个坐标点与触摸点之间的距离
            float dx = verts[i] - startX;
            float dy = verts[i + 1] - startY;
            float dd = dx * dx + dy * dy;
            float d = (float) Math.sqrt(dd);

            //文献中提到的算法同样不能实现只有圆形选区内的图像才进行变形的功能，这里需要做一个距离的判断
            if (d < radius) {
                //变形系数，扭曲度
                double e = (radius * radius - dd) * (radius * radius - dd) /
                        ((radius * radius - dd + 100/strength * dPull * dPull) * (radius * radius - dd +100/strength * dPull * dPull));
                double pullX = e * (endX - startX);
                double pullY = e * (endY - startY);
                verts[i] = (float) (verts[i] + pullX);
                verts[i + 1] = (float) (verts[i + 1] + pullY);
            }
        }
        addHistoryList(verts);
        invalidate();
    }

    /**
     * 一键恢复
     */
    public void resetView() {
        for (int i = 0; i < verts.length; i++) {
            verts[i] = orig[i];
        }
        historyList.clear();
        historyList.add(orig);
        historyIndex = 0;
        invalidate();
    }


    public void undo(){
        if (canUndo()){
            historyIndex --;
            Log.d(TAG,"undo:historyIndex:" + historyIndex);
            Log.d(TAG,"historyList.size() = " + historyList.size());
            float[] history = historyList.get(historyIndex);
            for (int i = 0; i < verts.length; i++) {
                verts[i] = history[i];
            }
            invalidate();
        }
    }

    public void redo(){
        if (canRedo()){
            historyIndex ++;
            Log.d(TAG,"redo:historyIndex:" + historyIndex);
            Log.d(TAG,"historyList.size() = " + historyList.size());
            float[] history = historyList.get(historyIndex);
            for (int i = 0; i < verts.length; i++) {
                verts[i] = history[i];
            }
            invalidate();
        }
    }

   public boolean canUndo(){
        if (historyIndex == 0 || historyList.size() == 1){
            return false;
        }
        return true;
   }
   public boolean canRedo(){
        if (historyIndex + 1 >= historyList.size()){
            return false;
        }
        return true;
   }

    /**
     * 画箭头
     *
     * @param sx
     * @param sy
     * @param ex
     * @param ey
     * @param paint
     */
    private void drawArrow(float sx, float sy,
                           float ex, float ey,
                           Paint paint,Canvas canvas) {
        int size = 5;
        int count = 20;
//        switch (width) {
//            case 0:
//                size = 5;
//                count = 20;
//                break;
//            case 5:
//                size = 8;
//                count = 30;
//                break;
//            case 10:
//                size = 11;
//                count = 40;
//                break;
//        }
        float x = ex - sx;
        float y = ey - sy;
        double d = x * x + y * y;
        double r = Math.sqrt(d);
        float zx = (float) (ex - (count * x / r));
        float zy = (float) (ey - (count * y / r));
        float xz = zx - sx;
        float yz = zy - sy;
        double zd = xz * xz + yz * yz;
        double zr = Math.sqrt(zd);
        Path triangle = new Path();
        triangle.moveTo(sx, sy);
        triangle.lineTo((float) (zx + size * yz / zr), (float) (zy - size * xz / zr));
        triangle.lineTo((float) (zx + size * 2 * yz / zr), (float) (zy - size * 2 * xz / zr));
        triangle.lineTo(ex, ey);
        triangle.lineTo((float) (zx - size * 2 * yz / zr), (float) (zy + size * 2 * xz / zr));
        triangle.lineTo((float) (zx - size * yz / zr), (float) (zy + size * xz / zr));
        triangle.close();
        canvas.drawPath(triangle, paint);
    }


    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setOnStepChangeListener(IOnStepChangeListener onStepChangeListener) {
        this.onStepChangeListener = onStepChangeListener;
    }

    public interface IOnStepChangeListener {
        void onStartTouch();
        void onStartMesh();
        void onEndMesh();
    }

}
