package com.liubowang.photoretouch.Draw;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.liubowang.photoretouch.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mac_h on 2017/11/26.
 */

public class BrushView extends FrameLayout {

    public enum BrushType {
        NORMAL,NORMAL_1,NORMAL_2,ERASER,
    }

    public interface OnBrushViewListener {
        void onStartDrawing(float x, float y);
        void onMoving(float x, float y);
        void onEndDrawing();
    }
    private static final String LOG_TAG = "DrawView";
    private int mBrushColor;
    private int mBrushWidth;
    private int mEraserWidth;
    private boolean mAntiAlias;
    private boolean mDither;
    private Paint.Style mPaintStyle;
    private Paint.Cap mLineCap;
    private BrushType mBrushType = BrushType.NORMAL;
    private int mBackgroundColor;
    public List<DrawMove> mBrushMoveHistory;
    public int mBrushMoveHistoryIndex = -1;
    private OnBrushViewListener mBrushViewListener;
    public boolean drawEnable = true;
    private boolean isDrawing = false;

    private Bitmap mResultMap;
    private Canvas mResultCanvas;

    private Bitmap mTmpMap;
    private Canvas mTmpCanvas;



    private float lastTouchX;
    private float lastTouchY;

    public BrushView(@NonNull Context context) {
        super(context);
    }

    public BrushView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttributes(context,attrs);

    }

    public BrushView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributes(context,attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BrushView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
        initAttributes(context,attrs);
    }

    public void setOnBrushViewListener(OnBrushViewListener listener){
        this.mBrushViewListener = listener;
    }

    private void init(){
        mBrushMoveHistory = new ArrayList<DrawMove>();
        setWillNotDraw(false);
        setLayerType(LAYER_TYPE_SOFTWARE,null);
    }
    private void initAttributes(Context context,AttributeSet attributeSet){
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.BrushView,0,0);
        try {
            mBrushColor       = typedArray.getColor(R.styleable.BrushView_brush_color, Color.BLACK);
            mBrushWidth       = typedArray.getInteger(R.styleable.BrushView_brush_width, 3);
            mEraserWidth      = typedArray.getInteger(R.styleable.BrushView_brush_eraser_width, 3);
            mAntiAlias       = typedArray.getBoolean(R.styleable.BrushView_brush_antialias, true);
            mDither          = typedArray.getBoolean(R.styleable.BrushView_brush_dither, true);
            mBackgroundColor = typedArray.getColor(R.styleable.BrushView_brush_background_color,
                    Color.WHITE);

            int cap = typedArray.getInteger(R.styleable.BrushView_brush_line_cap, 2);
            if (cap == 0)
                mLineCap = Paint.Cap.BUTT;
            else if (cap == 1)
                mLineCap = Paint.Cap.ROUND;
            else if (cap == 2)
                mLineCap = Paint.Cap.SQUARE;

            int paintStyle = typedArray.getInteger(R.styleable.BrushView_brush_paint_style, 2);
            if (paintStyle == 0)
                mPaintStyle = Paint.Style.FILL;
            else if (paintStyle == 1)
                mPaintStyle = Paint.Style.FILL_AND_STROKE;
            else if (paintStyle == 2)
                mPaintStyle = Paint.Style.STROKE;

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            typedArray.recycle();
        }
    }


    public void prepareBrush(){
        mResultMap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
        mResultCanvas = new Canvas(mResultMap);
        mTmpMap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
        mTmpCanvas = new Canvas(mTmpMap);
    }

    public void onTouch( MotionEvent motionEvent) {
        if (!drawEnable) return ;
        switch (motionEvent.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                touchDown(motionEvent);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(motionEvent);
                break;
            case MotionEvent.ACTION_UP:
                touchUp(motionEvent);
                break;
        }
        return ;
    }

    private void touchDown(MotionEvent motionEvent){
        if (mBrushMoveHistoryIndex >= -1 &&
                mBrushMoveHistoryIndex < mBrushMoveHistory.size() - 1) {
            mBrushMoveHistory = mBrushMoveHistory.subList(0, mBrushMoveHistoryIndex + 1);
        }

        lastTouchX = motionEvent.getX();
        lastTouchY = motionEvent.getY();

        mBrushMoveHistory.add(DrawMove.newDrawMove()
                .setPaint(getNewPaintParams())
                .setStartX(motionEvent.getX())
                .setStartY(motionEvent.getY())
                .setEndX(motionEvent.getX())
                .setEndY(motionEvent.getY()));
        mBrushMoveHistoryIndex ++;
        Path path = new Path();
        path.moveTo(lastTouchX,lastTouchY);
        path.lineTo(lastTouchX,lastTouchY);
        mBrushMoveHistory.get(mBrushMoveHistory.size() - 1)
                .setDrawPathList( new ArrayList<Path>());
        mBrushMoveHistory.get(mBrushMoveHistory.size() - 1)
                .getDrawPathList().add(path);

        isDrawing = true;
        if (mBrushViewListener != null ){
            mBrushViewListener.onStartDrawing(motionEvent.getX(),motionEvent.getY());
        }
    }
    private void touchMove(MotionEvent motionEvent){
        addLineToPath(motionEvent);
        lastTouchX = motionEvent.getX();
        lastTouchY = motionEvent.getY();
        if (mBrushViewListener != null ){
            mBrushViewListener.onMoving(motionEvent.getX(),motionEvent.getY());
        }
    }

    private void addLineToPath(MotionEvent motionEvent){
        final float x = motionEvent.getX();
        final float y = motionEvent.getY();
        final float previousX = lastTouchX;
        final float previousY = lastTouchY;
        final float dx = Math.abs(x - previousX);
        final float dy = Math.abs(y - previousY);
        // 两点之间的距离大于等于3时，生成贝塞尔绘制曲线
        mBrushMoveHistory.get(mBrushMoveHistory.size() - 1)
                .setEndX(x)
                .setEndY(y);
        DrawMove drawMove = mBrushMoveHistory.get(mBrushMoveHistory.size() - 1);
        List<Path> pathList = drawMove.getDrawPathList();
        Path path = pathList.get(pathList.size() - 1);
        if (dx >= 1 || dy >= 1) {
            // 设置贝塞尔曲线的操作点为起点和终点的一半
            float cX = (x + previousX) / 2;
            float cY = (y + previousY) / 2;
            // 二次贝塞尔，实现平滑曲线；previousX, previousY为操作点，cX, cY为终点
            path.quadTo(previousX, previousY, cX, cY);
        }
        lastTouchX = x;
        lastTouchY = y;


        if (mBrushType == BrushType.ERASER){
            mResultCanvas.drawPath(path,drawMove.getPaint());
        }else {
            drawMove.getPaint().setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            mTmpCanvas.drawPaint(drawMove.getPaint());
            drawMove.getPaint().setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
            mTmpCanvas.drawPath(path,drawMove.getPaint());
        }

        invalidate();
    }

    private void touchUp(MotionEvent motionEvent){
//        addLineToPath(motionEvent);
        if (mBrushViewListener != null ){
            mBrushViewListener.onEndDrawing();
        }
        lastTouchX = motionEvent.getX();
        lastTouchY = motionEvent.getY();
        if (mBrushType !=BrushType.ERASER && mTmpMap != null){
            Rect dstRect = new Rect(0,0,mResultCanvas.getWidth(),mResultCanvas.getHeight());
            Rect srcRect = new Rect(0,0,mTmpMap.getWidth(),mTmpMap.getHeight());
            mResultCanvas.drawBitmap(mTmpMap,0,0,null);
        }
//        for (int i = 0; i < mBrushMoveHistoryIndex + 1; i ++){
//            DrawMove drawMove = mBrushMoveHistory.get(i);
//            drawPen(drawMove,mResultCanvas);
//        }
        isDrawing = false;
    }
    public void initialOriginBrush(Bitmap bitmap){
        prepareBrush();
        if (bitmap != null){
            Rect dstRect = new Rect(0,0,mResultCanvas.getWidth(),mResultCanvas.getHeight());
            Rect srcRect = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
            mResultCanvas.drawBitmap(bitmap,srcRect,dstRect,null);
        }
        invalidate();
    }


    public Bitmap getBitmp(){
        return mResultMap;
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect dstRect = new Rect(0,0,canvas.getWidth(),canvas.getHeight());
        if (mResultMap != null){
            Rect srcRect = new Rect(0,0,mResultMap.getWidth(),mResultMap.getHeight());
            canvas.drawBitmap(mResultMap,srcRect,dstRect,null);
        }
        if (isDrawing && mBrushType != BrushType.ERASER && mTmpMap != null){
            Rect srcRect = new Rect(0,0,mTmpMap.getWidth(),mTmpMap.getHeight());
            canvas.drawBitmap(mTmpMap,srcRect,dstRect,null);
        }
    }

    private void drawRect(DrawMove drawMove,Canvas canvas){
        canvas.drawRect(drawMove.getStartX(), drawMove.getStartY(),
                drawMove.getEndX(), drawMove.getEndY(), drawMove.getPaint());
    }
    private void drawPen(DrawMove drawMove,Canvas canvas){
        if (drawMove.getDrawPathList() != null &&
                drawMove.getDrawPathList().size() > 0)
            for (Path path : drawMove.getDrawPathList())
                canvas.drawPath(path, drawMove.getPaint());
    }

    protected Paint getNewPaintParams(){
        Paint paint = new Paint();
        paint.setColor(mBrushColor);
        paint.setStyle(mPaintStyle);
        paint.setDither(mDither);
        paint.setStrokeWidth(mBrushWidth);
        paint.setAntiAlias(mAntiAlias);
        paint.setStrokeCap(mLineCap);

        if (mBrushType == BrushType.NORMAL){
            paint.setXfermode(null);
            paint.setMaskFilter(new BlurMaskFilter(0.01f, BlurMaskFilter.Blur.NORMAL));
        }
        else if (mBrushType == BrushType.NORMAL_1){
            paint.setXfermode(null);
            paint.setMaskFilter(new BlurMaskFilter(mBrushWidth * 0.1f, BlurMaskFilter.Blur.NORMAL));
        }
        else if (mBrushType == BrushType.NORMAL_2){
            paint.setXfermode(null);
            paint.setMaskFilter(new BlurMaskFilter(mBrushWidth * 0.6f, BlurMaskFilter.Blur.NORMAL));
        }else if (mBrushType == BrushType.ERASER){
            paint.setStrokeWidth(mEraserWidth);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        return paint;
    }
    protected Paint getCurrentPaintParams() {
        Paint currentPaint;
        if (mBrushMoveHistory.size() > 0 && mBrushMoveHistoryIndex >= 0) {
            currentPaint = new Paint();
            currentPaint.setColor(
                    mBrushMoveHistory.get(mBrushMoveHistoryIndex).getPaint().getColor());
            currentPaint.setStyle(
                    mBrushMoveHistory.get(mBrushMoveHistoryIndex).getPaint().getStyle());
            currentPaint.setDither(
                    mBrushMoveHistory.get(mBrushMoveHistoryIndex).getPaint().isDither());
            currentPaint.setStrokeWidth(
                    mBrushMoveHistory.get(mBrushMoveHistoryIndex).getPaint().getStrokeWidth());
            currentPaint.setAlpha(
                    mBrushMoveHistory.get(mBrushMoveHistoryIndex).getPaint().getAlpha());
            currentPaint.setAntiAlias(
                    mBrushMoveHistory.get(mBrushMoveHistoryIndex).getPaint().isAntiAlias());
            currentPaint.setStrokeCap(
                    mBrushMoveHistory.get(mBrushMoveHistoryIndex).getPaint().getStrokeCap());
            currentPaint.setXfermode(
                    mBrushMoveHistory.get(mBrushMoveHistoryIndex).getPaint().getXfermode());

        } else {
            currentPaint = getNewPaintParams();
        }
        return currentPaint;
    }
    public boolean restartBrush() {
        if (mBrushMoveHistory != null) {
            mBrushMoveHistory.clear();
            mBrushMoveHistoryIndex = -1;
            prepareBrush();
            invalidate();
            return true;
        }
        invalidate();
        return false;
    }

    public boolean undo() {
        if (mBrushMoveHistoryIndex > -1 &&
                mBrushMoveHistory.size() > 0) {
            mBrushMoveHistoryIndex--;
            invalidate();
            return true;
        }
        invalidate();
        return false;
    }

    public boolean canUndo() {
        return mBrushMoveHistoryIndex > -1 &&
                mBrushMoveHistory.size() > 0;
    }
    public boolean redo() {
        if (mBrushMoveHistoryIndex <= mBrushMoveHistory.size() - 1) {
            mBrushMoveHistoryIndex++;
            invalidate();
            return true;
        }
        invalidate();
        return false;
    }
    public boolean canRedo() {
        return mBrushMoveHistoryIndex < mBrushMoveHistory.size() - 1;
    }

    public Object createCapture(DrawView.DrawingCapture drawingCapture) {
        setDrawingCacheEnabled(false);
        setDrawingCacheEnabled(true);

        switch (drawingCapture) {
            case BITMAP:
                return getDrawingCache(true);
            case BYTES:
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                getDrawingCache(true).compress(Bitmap.CompressFormat.PNG, 100, stream);
                return stream.toByteArray();
        }
        return null;
    }


    public BrushView refreshAttributes(Paint paint) {
        mBrushColor = paint.getColor();
        mPaintStyle = paint.getStyle();
        mDither = paint.isDither();
        mBrushWidth = (int) paint.getStrokeWidth();
        mAntiAlias = paint.isAntiAlias();
        mLineCap = paint.getStrokeCap();
        return this;
    }


    public int getBrushColor() {
        return mBrushColor;
    }

    public int getBrushWidth() {
        return mBrushWidth;
    }

    public int getEraserWidth() {
        return mEraserWidth;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public Paint.Style getPaintStyle() {
        return mPaintStyle;
    }

    public Paint.Cap getLineCap() {
        return mLineCap;
    }

    public BrushType getBrushType() {
        return mBrushType;
    }

    public boolean isAntiAlias() {
        return mAntiAlias;
    }

    public boolean isDither() {
        return mDither;
    }


    public BrushView setBrushColor(int drawColor) {
        this.mBrushColor = drawColor;
        return this;
    }

    public BrushView setBrushWidth(int drawWidth) {
        this.mBrushWidth = drawWidth;
        if (this.mBrushWidth == 0){
            this.mBrushWidth = 1;
        }
        return this;
    }

    public BrushView setEraserWidth(int drawWidth) {
        this.mEraserWidth = drawWidth;
        if (this.mEraserWidth == 0){
            this.mEraserWidth = 1;
        }
        return this;
    }


    public BrushView setBackgroundDrawColor(int backgroundColor) {
        this.mBackgroundColor = backgroundColor;
        return this;
    }

    public BrushView setPaintStyle(Paint.Style paintStyle) {
        this.mPaintStyle = paintStyle;
        return this;
    }

    public BrushView setLineCap(Paint.Cap lineCap) {
        this.mLineCap = lineCap;
        return this;
    }
    public BrushView setBrushType(BrushType brushType) {
        this.mBrushType = brushType;
        return this;
    }

    public BrushView setAntiAlias(boolean antiAlias) {
        this.mAntiAlias = antiAlias;
        return this;
    }
    public BrushView setDither(boolean dither) {
        this.mDither = dither;
        return this;
    }


}
