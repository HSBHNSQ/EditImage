package com.liubowang.editimage.Draw;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.liubowang.editimage.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heshaobo on 2017/7/24.
 */

public class DrawView extends FrameLayout {
    public enum DrawStyle {
        NONE,RECT,PEN,ERASER
    }
    public enum DrawingCapture {
        BITMAP,
        BYTES
    }
    public interface OnDrawViewListener {
        void onStartDrawing(float x, float y);
        void onMoving(float x, float y);
        void onEndDrawing();
    }
    private static final String LOG_TAG = "DrawView";
    private int mDrawColor;
    private int mDrawWidth;
    private int mDrawAlpha;
    private boolean mAntiAlias;
    private boolean mDither;
    private Paint.Style mPaintStyle;
    private Paint.Cap mLineCap;
    private int mBackgroundColor;
    private DrawStyle mDrawStyle;
    public List<DrawMove> mDrawMoveHistory;
    public int mDrawMoveHistoryIndex = -1;
    private OnDrawViewListener mDrawViewListener;
    public boolean drawEnable = true;


    private float lastTouchX;
    private float lastTouchY;

    public DrawView(Context context){
       super(context);
        init();
    }

    public DrawView(Context context, AttributeSet attributeSet){
       super(context,attributeSet);
        init();
        initAttributes(context,attributeSet);
    }

    public DrawView(Context context, AttributeSet attributeSet, int defStyleAttr){
       super(context,attributeSet,defStyleAttr);
        init();
        initAttributes(context,attributeSet);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DrawView(Context context, AttributeSet attributeSet, int defStyleAttr, int defStyleRes){
       super(context,attributeSet,defStyleAttr,defStyleRes);
        init();
        initAttributes(context,attributeSet);
    }

    public void setOnDrawViewListener(OnDrawViewListener listener){
        this.mDrawViewListener = listener;
    }

    private void init(){
        mDrawMoveHistory = new ArrayList<DrawMove>();
        setWillNotDraw(false);
    }
    private void initAttributes(Context context,AttributeSet attributeSet){
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attributeSet,R.styleable.DrawView,0,0);
        try {
            mDrawColor       = typedArray.getColor(R.styleable.DrawView_draw_color, Color.BLACK);
            mDrawWidth       = typedArray.getInteger(R.styleable.DrawView_draw_width, 3);
            mDrawAlpha       = typedArray.getInteger(R.styleable.DrawView_draw_alpha, 255);
            mAntiAlias       = typedArray.getBoolean(R.styleable.DrawView_draw_antialias, true);
            mDither          = typedArray.getBoolean(R.styleable.DrawView_draw_dither, true);
            mBackgroundColor = typedArray.getColor(R.styleable.DrawView_draw_background_color,
                                Color.WHITE);

            int drawStyle = typedArray.getInteger(R.styleable.DrawView_draw_stytle, 2);
            if (drawStyle == 0)
                mDrawStyle = DrawStyle.NONE;
            else if (drawStyle == 1)
                mDrawStyle = DrawStyle.RECT;
            else if (drawStyle == 2)
                mDrawStyle = DrawStyle.PEN;
            else if (drawStyle == 3)
                mDrawStyle = DrawStyle.ERASER;

            int cap = typedArray.getInteger(R.styleable.DrawView_draw_line_cap, 2);
            if (cap == 0)
                mLineCap = Paint.Cap.BUTT;
            else if (cap == 1)
                mLineCap = Paint.Cap.ROUND;
            else if (cap == 2)
                mLineCap = Paint.Cap.SQUARE;

            int paintStyle = typedArray.getInteger(R.styleable.DrawView_draw_paint_sytle, 2);
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
        if (mDrawMoveHistoryIndex >= -1 &&
                mDrawMoveHistoryIndex < mDrawMoveHistory.size() - 1) {
            mDrawMoveHistory = mDrawMoveHistory.subList(0, mDrawMoveHistoryIndex + 1);
        }

        lastTouchX = motionEvent.getX();
        lastTouchY = motionEvent.getY();

        mDrawMoveHistory.add(DrawMove.newDrawMove()
                    .setPaint(getNewPaintParams())
                    .setStartX(motionEvent.getX())
                    .setStartY(motionEvent.getY())
                    .setEndX(motionEvent.getX())
                    .setEndY(motionEvent.getY())
                    .setDrawStyle(mDrawStyle));
        mDrawMoveHistoryIndex ++;
        if (mDrawStyle == DrawStyle.PEN || mDrawStyle == DrawStyle.ERASER){
            Path path = new Path();
            path.moveTo(lastTouchX,lastTouchY);
            path.lineTo(lastTouchX,lastTouchY);
            mDrawMoveHistory.get(mDrawMoveHistory.size() - 1)
                        .setDrawPathList( new ArrayList<Path>());
            mDrawMoveHistory.get(mDrawMoveHistory.size() - 1)
                        .getDrawPathList().add(path);
        }
        if (mDrawViewListener != null ){
            mDrawViewListener.onStartDrawing(motionEvent.getX(),motionEvent.getY());
        }
    }
    private void touchMove(MotionEvent motionEvent){
        addLineToPath(motionEvent);
        lastTouchX = motionEvent.getX();
        lastTouchY = motionEvent.getY();
        if (mDrawViewListener != null ){
            mDrawViewListener.onMoving(motionEvent.getX(),motionEvent.getY());
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
        mDrawMoveHistory.get(mDrawMoveHistory.size() - 1)
                .setEndX(x)
                .setEndY(y);
        if (mDrawStyle == DrawStyle.PEN || mDrawStyle == DrawStyle.ERASER){
            DrawMove drawMove = mDrawMoveHistory.get(mDrawMoveHistory.size() - 1);
            List<Path> pathList = drawMove.getDrawPathList();
            Path path = pathList.get(pathList.size() - 1);
            if (dx >= 1 || dy >= 1) {
                // 设置贝塞尔曲线的操作点为起点和终点的一半
                float cX = (x + previousX) / 2;
                float cY = (y + previousY) / 2;
                // 二次贝塞尔，实现平滑曲线；previousX, previousY为操作点，cX, cY为终点
                path.quadTo(previousX, previousY, cX, cY);
            }
        }
        lastTouchX = x;
        lastTouchY = y;
        invalidate();
    }

    private void touchUp(MotionEvent motionEvent){
        addLineToPath(motionEvent);
        if (mDrawViewListener != null ){
            mDrawViewListener.onEndDrawing();
        }
        lastTouchX = motionEvent.getX();
        lastTouchY = motionEvent.getY();
    }
    public Bitmap getBitmp(int backgroundColor){
        Bitmap bmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectPaint.setColor(backgroundColor);
        rectPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0,0,canvas.getWidth(),canvas.getHeight(),rectPaint);
        for (int i = 0; i < mDrawMoveHistoryIndex + 1; i ++){
            DrawMove drawMove = mDrawMoveHistory.get(i);
            switch (drawMove.getDrawStyle()){
                case NONE:
                    break;
                case RECT:
                    drawRect(drawMove,canvas);
                    break;
                case PEN:
                    drawPen(drawMove,canvas);
                    break;
                case ERASER:
                    drawPen(drawMove,canvas);
                    break;
            }
        }
        return bmp;
    }
    public Bitmap getBitmp(){
        return getBitmp(Color.parseColor("#00000000"));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mDrawMoveHistoryIndex + 1; i ++){
            DrawMove drawMove = mDrawMoveHistory.get(i);
            switch (drawMove.getDrawStyle()){
                case NONE:
                    break;
                case RECT:
                    drawRect(drawMove,canvas);
                    break;
                case PEN:
                    drawPen(drawMove,canvas);
                    break;
                case ERASER:
                    drawPen(drawMove,canvas);
                    break;
            }
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
        if (mDrawStyle == DrawStyle.ERASER) {
            paint.setColor(mBackgroundColor);
        } else {
            paint.setColor(mDrawColor);
        }
        paint.setStyle(mPaintStyle);
        paint.setDither(mDither);
        paint.setStrokeWidth(mDrawWidth);
        paint.setAlpha(mDrawAlpha);
        paint.setAntiAlias(mAntiAlias);
        paint.setStrokeCap(mLineCap);
        return paint;
    }
    protected Paint getCurrentPaintParams() {
        Paint currentPaint;
        if (mDrawMoveHistory.size() > 0 && mDrawMoveHistoryIndex >= 0) {
            currentPaint = new Paint();
            currentPaint.setColor(
                    mDrawMoveHistory.get(mDrawMoveHistoryIndex).getPaint().getColor());
            currentPaint.setStyle(
                    mDrawMoveHistory.get(mDrawMoveHistoryIndex).getPaint().getStyle());
            currentPaint.setDither(
                    mDrawMoveHistory.get(mDrawMoveHistoryIndex).getPaint().isDither());
            currentPaint.setStrokeWidth(
                    mDrawMoveHistory.get(mDrawMoveHistoryIndex).getPaint().getStrokeWidth());
            currentPaint.setAlpha(
                    mDrawMoveHistory.get(mDrawMoveHistoryIndex).getPaint().getAlpha());
            currentPaint.setAntiAlias(
                    mDrawMoveHistory.get(mDrawMoveHistoryIndex).getPaint().isAntiAlias());
            currentPaint.setStrokeCap(
                    mDrawMoveHistory.get(mDrawMoveHistoryIndex).getPaint().getStrokeCap());

        } else {
            currentPaint = new Paint();
            currentPaint.setColor(mDrawColor);
            currentPaint.setStyle(mPaintStyle);
            currentPaint.setDither(mDither);
            currentPaint.setStrokeWidth(mDrawWidth);
            currentPaint.setAlpha(mDrawAlpha);
            currentPaint.setAntiAlias(mAntiAlias);
            currentPaint.setStrokeCap(mLineCap);
        }
        return currentPaint;
    }
    public boolean restartDrawing() {
        if (mDrawMoveHistory != null) {
            mDrawMoveHistory.clear();
            mDrawMoveHistoryIndex = -1;
            invalidate();
            return true;
        }
        invalidate();
        return false;
    }

    public boolean undo() {
        if (mDrawMoveHistoryIndex > -1 &&
                mDrawMoveHistory.size() > 0) {
            mDrawMoveHistoryIndex--;
            invalidate();
            return true;
        }
        invalidate();
        return false;
    }

    public boolean canUndo() {
        return mDrawMoveHistoryIndex > -1 &&
                mDrawMoveHistory.size() > 0;
    }
    public boolean redo() {
        if (mDrawMoveHistoryIndex <= mDrawMoveHistory.size() - 1) {
            mDrawMoveHistoryIndex++;
            invalidate();
            return true;
        }
        invalidate();
        return false;
    }
    public boolean canRedo() {
        return mDrawMoveHistoryIndex < mDrawMoveHistory.size() - 1;
    }

    public Object createCapture(DrawingCapture drawingCapture) {
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
    public Bitmap drawViweCreateBitmp(){
//        return BitmpUtil.getViewBitmap(this);
        //TODO:

        return null;
    }

    public DrawView refreshAttributes(Paint paint) {
        mDrawColor = paint.getColor();
        mPaintStyle = paint.getStyle();
        mDither = paint.isDither();
        mDrawWidth = (int) paint.getStrokeWidth();
        mDrawAlpha = paint.getAlpha();
        mAntiAlias = paint.isAntiAlias();
        mLineCap = paint.getStrokeCap();
        return this;
    }

    public int getDrawAlpha() {
        return mDrawAlpha;
    }

    public int getDrawColor() {
        return mDrawColor;
    }

    public int getDrawWidth() {
        return mDrawWidth;
    }

    public DrawStyle getDrawStyle() {
        return mDrawStyle;
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

    public boolean isAntiAlias() {
        return mAntiAlias;
    }

    public boolean isDither() {
        return mDither;
    }

    public DrawView setDrawAlpha(int drawAlpha) {
        this.mDrawAlpha = drawAlpha;
        return this;
    }

    public DrawView setDrawColor(int drawColor) {
        this.mDrawColor = drawColor;
        return this;
    }

    public DrawView setDrawWidth(int drawWidth) {
        this.mDrawWidth = drawWidth;
        return this;
    }

    public DrawView setDrawingTool(DrawStyle style) {
        this.mDrawStyle = style;
        return this;
    }

    public DrawView setBackgroundDrawColor(int backgroundColor) {
        this.mBackgroundColor = backgroundColor;
        return this;
    }

    public DrawView setPaintStyle(Paint.Style paintStyle) {
        this.mPaintStyle = paintStyle;
        return this;
    }

    public DrawView setLineCap(Paint.Cap lineCap) {
        this.mLineCap = lineCap;
        return this;
    }

    public DrawView setAntiAlias(boolean antiAlias) {
        this.mAntiAlias = antiAlias;
        return this;
    }
    public DrawView setDither(boolean dither) {
        this.mDither = dither;
        return this;
    }

}
