package com.liubowang.photoretouch.Mosaic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaoya on 2017/5/10.
 * 手动给图片打马赛克view
 */

public class MosaicView extends android.support.v7.widget.AppCompatImageView {

    private final String TAG = getClass().getSimpleName();
    private Bitmap mBitmap;
    private int BLOCK_SIZE = 50; // 马赛克的大小: BLOCK_SIZE*BLOCK_SIZE
    private int[] mSampleColors;
//    private float mLastX, mLastY;
    private int mBitmapWidth, mBitmapHeight;
    private int[] mSrcBitmapPixs; // 保留原图的像素数组
    private int[] mTempBitmapPixs; // 用于马赛克的临时像素数组
    private int mRowCount, mColumnCount;
    private final int VALID_DISTANCE = 4; // 滑动的有效距离

    private boolean isMasking = false; // 正在打码,默认为false

    private List<Mask> maskList = new ArrayList<>();
    private boolean canEdit = true; // 是否可以允许打码,默认允许
    private int mMaskHistoryIndex = -1;

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }

    public MosaicView(Context context) {
        super(context);
    }

    public MosaicView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MosaicView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }




    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        resetMosaic();
    }

    public void setMosaicSize(int size){
        BLOCK_SIZE = size;
        resetMosaic();
    }

    private int sampleBlock(int[] pxs, int startX, int startY, int blockSize, int maxX, int maxY) {
        int stopX = startX + blockSize - 1;
        int stopY = startY + blockSize - 1;
        stopX = Math.min(stopX, maxX);
        stopY = Math.min(stopY, maxY);
        int sampleColor = 0;
        int red = 0;
        int green = 0;
        int blue = 0;
        //将该块的所有点的颜色求平均值
        for (int y = startY; y <= stopY; y++) {
            int p = y * mBitmapWidth;
            for (int x = startX; x <= stopX; x++) {
                int color = pxs[p + x];
                red += Color.red(color);
                green += Color.green(color);
                blue += Color.blue(color);
            }
        }
        int sampleCount = (stopY - startY + 1) * (stopX - startX + 1);
        Log.d("sampleCount=", sampleCount + "");
        red /= sampleCount;
        green /= sampleCount;
        blue /= sampleCount;
        sampleColor = Color.rgb(red, green, blue);
        return sampleColor;
    }



    /** 屏幕坐标转bitmap坐标 */
    private float getPathX(float x) {
        float bmpW =  mBitmap.getWidth();
        float W = getWidth();
        return x * (bmpW / W);
    }

    /** 屏幕坐标转bitmap坐标 */
    private float getPathY(float y) {
        float bmpH =  mBitmap.getHeight();
        float H = getHeight();
        return y * (bmpH / H);
    }

    /**
     *
     * @param startPoint
     * @param endPoint
     * @param updateBitmap 自动加载时为false, 因为setPixels耗时较长,等计算完之后再调用setPixels一次性画出来
     */
    private void mosaic(Point startPoint, Point endPoint, boolean updateBitmap) {
        float startTouchX = startPoint.x;
        float startTouchY = startPoint.y;

        float endTouchX = endPoint.x;
        float endTouchY = endPoint.y;

        float minX = Math.min(startTouchX, endTouchX);
        float maxX = Math.max(startTouchX, endTouchX);

        int startIndexX = (int) minX / BLOCK_SIZE;
        int endIndexX = (int) maxX / BLOCK_SIZE;

        float minY = Math.min(startTouchY, endTouchY);
        float maxY = Math.max(startTouchY, endTouchY);

        int startIndexY = (int) minY / BLOCK_SIZE;
        int endIndexY = (int) maxY / BLOCK_SIZE;//确定矩形的判断范围
        if (startIndexX < 0 || startIndexY < 0 || endIndexY < 0 || endIndexY < 0) {
            return;
        }
        for (int row = startIndexY; row <= endIndexY; row++) {
            for (int colunm = startIndexX; colunm <= endIndexX; colunm++) {
                Rect rect = new Rect(colunm * BLOCK_SIZE, row * BLOCK_SIZE, (colunm + 1) * BLOCK_SIZE, (row + 1) * BLOCK_SIZE);
                Boolean intersectRect = GeometryHelper.IsLineIntersectRect(startPoint.clone(), endPoint.clone(), rect);
                if (intersectRect) {//线段与直线相交
                    int rowMax = Math.min((row + 1) * BLOCK_SIZE, mBitmapHeight);
                    int colunmMax = Math.min((colunm + 1) * BLOCK_SIZE, mBitmapWidth);
                    for (int i = row * BLOCK_SIZE; i < rowMax; i++) {
                        for (int j = colunm * BLOCK_SIZE; j < colunmMax; j++) {
                            mTempBitmapPixs[i * mBitmapWidth + j] = mSampleColors[row * mColumnCount + colunm];
                        }
                    }
                }
            }
        }
        if(updateBitmap) {
            mBitmap.setPixels(mTempBitmapPixs, 0, mBitmapWidth, 0, 0, mBitmapWidth, mBitmapHeight);
        }
    }

    public void setMasking(boolean masking) {
        isMasking = masking;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!canEdit) {
            return;
        }
        if (mBitmap == null) {
            mMaskHistoryIndex = -1;
            resetMosaic();
//            init();
        }
        if (mBitmap != null) {
            Rect srcRect = new Rect(0,0,mBitmap.getWidth(),mBitmap.getHeight());
            Rect dstRect = new Rect(0,0,getWidth(),getHeight());
            canvas.drawBitmap(mBitmap, srcRect, dstRect, null);
        }
    }

    /**
     * 清除全部打码
     */
    public void clean() {
        mBitmap = null;
        mMaskHistoryIndex = -1;
        maskList.clear();
        invalidate();
    }

    /**
     * 得到打码后的bitmap
     */
    public Bitmap getMaskedBitmap() {
        return mBitmap;
    }

    public void onTouch(MotionEvent motionEvent) {
        if(!isMasking) {
            return ;
        }

        int action = motionEvent.getAction() & MotionEvent.ACTION_MASK;

        switch (action){
            case MotionEvent.ACTION_DOWN:
                touchDown(motionEvent);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(motionEvent);
                break;
            case MotionEvent.ACTION_UP:
                touchUp(motionEvent);
                break;
            default:
                break;
        }

    }

    private void touchDown(MotionEvent motionEvent){
        if (mMaskHistoryIndex >= -1 &&
                mMaskHistoryIndex < maskList.size() - 1) {
            maskList = maskList.subList(0, mMaskHistoryIndex + 1);
        }
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        Mask mask = new Mask();
        MaskPoint maskPoint = new MaskPoint();
        maskPoint.startPoint = new Point(getPathX(Math.abs(x)),getPathY(Math.abs(y)));
        maskPoint.endPpoint = new Point(getPathX(Math.abs(x)),getPathY(Math.abs(y)));
        mask.points.add(maskPoint);
        maskList.add(mask);
        mMaskHistoryIndex ++;
        invalidate();
        if (mosaicTouchListener != null){
            mosaicTouchListener.onTouchDown();
        }
    }

    private void touchMove(MotionEvent motionEvent){
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        Point point = new Point(getPathX(x), getPathY(y));
        Mask mask = maskList.get(maskList.size() - 1);
        MaskPoint maskPoint = new MaskPoint();
        MaskPoint lastMaskPoint = mask.points.get(mask.points.size() - 1);
        maskPoint.startPoint = lastMaskPoint.endPpoint;
        maskPoint.endPpoint = point;
        mask.points.add(maskPoint);
        touchMove(maskPoint, true);
        if (mosaicTouchListener != null){
            mosaicTouchListener.onTouchMove();
        }
        invalidate();
    }
    private void touchMove(MaskPoint maskPoint, boolean updateBitmap) {
        if (Math.abs(maskPoint.endPpoint.x - maskPoint.startPoint.x) >= VALID_DISTANCE
                || Math.abs(maskPoint.endPpoint.y - maskPoint.startPoint.y) >= VALID_DISTANCE) {
            Point startPoint = maskPoint.startPoint;
            Point endPoint = maskPoint.endPpoint;
            mosaic(startPoint, endPoint, updateBitmap);
        }
    }

    private void touchUp(MotionEvent motionEvent){
        touchMove(motionEvent);
        if (mosaicTouchListener != null){
            mosaicTouchListener.onTouchEnd();
        }
    }

    public boolean undo() {
        if (mMaskHistoryIndex > -1 &&
                maskList.size() > 0) {
            mMaskHistoryIndex--;
            resetMosaic();
            return true;
        }
        return false;
    }

    public boolean canUndo(){
        return mMaskHistoryIndex > -1 &&
                maskList.size() > 0;
    }

    public boolean redo() {
        if (mMaskHistoryIndex < maskList.size() - 1) {
            mMaskHistoryIndex++;
            resetMosaic();
            return true;
        }
        return false;
    }
    public boolean canRedo() {
        return mMaskHistoryIndex < maskList.size() - 1;
    }

    private void resetMosaic(){
        Drawable drawable = super.getDrawable();
        if (drawable == null) {
            return;
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        mBitmap = bitmap;
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);

        mBitmapWidth = mBitmap.getWidth();
        mBitmapHeight = mBitmap.getHeight();
        mRowCount = (int) Math.ceil((float) mBitmapHeight / BLOCK_SIZE);
        mColumnCount = (int) Math.ceil((float) mBitmapWidth / BLOCK_SIZE);
        mSampleColors = new int[mRowCount * mColumnCount];

        int maxX = mBitmapWidth - 1;
        int maxY = mBitmapHeight - 1;
        mSrcBitmapPixs = new int[mBitmapWidth * mBitmapHeight];
        mTempBitmapPixs = new int[mBitmapWidth * mBitmapHeight];
        mBitmap.getPixels(mSrcBitmapPixs, 0, mBitmapWidth, 0, 0, mBitmapWidth, mBitmapHeight);
        mBitmap.getPixels(mTempBitmapPixs, 0, mBitmapWidth, 0, 0, mBitmapWidth, mBitmapHeight);
        for (int row = 0; row < mRowCount; row++) {
            for (int column = 0; column < mColumnCount; column++) {
                int startX = column * BLOCK_SIZE;
                int startY = row * BLOCK_SIZE;
                mSampleColors[row * mColumnCount + column] = sampleBlock(mSrcBitmapPixs, startX, startY, BLOCK_SIZE, maxX, maxY);
            }
        }
        mBitmap.setPixels(mSrcBitmapPixs, 0, mBitmapWidth, 0, 0, mBitmapWidth, mBitmapHeight);

        for (int i = 0; i < mMaskHistoryIndex + 1; i ++){
            if (i >= maskList.size()){
                break;
            }
            Mask mask = maskList.get(i);
            if (mask.points != null) {
                for (MaskPoint point: mask.points) {
                    touchMove(point, false);
                }
            }
        }

        mBitmap.setPixels(mTempBitmapPixs, 0, mBitmapWidth, 0, 0, mBitmapWidth, mBitmapHeight);
        invalidate();
    }


    public class Mask{
        List<MaskPoint> points;
        Mask() {
            this.points = new ArrayList<>();
        }
    }

    public class MaskPoint{
        Point startPoint;
        Point endPpoint;
    }

    public void setMaskList(List<Mask> maskList) {
        this.maskList = maskList;
    }

    public List<Mask> getMaskList() {
        return maskList;
    }

    public interface OnMosaicTouchListener {
        public void onTouchDown();
        public void onTouchMove();
        public void onTouchEnd();
    }

    private OnMosaicTouchListener mosaicTouchListener ;

    public void setOnMosaicTouchListener(OnMosaicTouchListener mosaicTouchListener) {
        this.mosaicTouchListener = mosaicTouchListener;
    }
}
