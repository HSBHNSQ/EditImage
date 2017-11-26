package com.liubowang.editimage.Effect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.liubowang.editimage.Base.EIBaseActiviry;
import com.liubowang.editimage.Draw.DrawView;
import com.liubowang.editimage.Normal.ImageTextButton;
import com.liubowang.editimage.Normal.TopToolView;
import com.liubowang.editimage.R;
import com.liubowang.editimage.Utils.BitmpUtil;
import com.liubowang.editimage.Utils.FileUtil;
import com.liubowang.editimage.Utils.GrabCutUtil;
import com.liubowang.editimage.Utils.ProgressHUD;
import com.liubowang.editimage.Utils.Size;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;

public class EffectActivity extends EIBaseActiviry {

    enum EditStatus {
        SMART_SELECT,BRUSH,ERASER
    }

    private static final String TAG = EffectActivity.class.getSimpleName();
    private static final int MAX_IMAGE_SIZE = 500;//提高处理速度
    private String mImagePath;
    private String mMaskPath;
    private Bitmap mMaskImage;
    private ConstraintLayout mRootView;
    private TopToolView mTopToolView;
    private ConstraintLayout mContainerView;
    private ImageView mOriginImageView;
    private ImageView mMaksImageView;
    private DrawView mDrawView;
    private ImageTextButton mSmartSelect;
    private ImageTextButton mBrush;
    private ImageTextButton mEraser;
    private LinearLayout mBottomContainerView;
    private EditStatus mCurrentStatus = EditStatus.SMART_SELECT;
    private MagnifierView mMagnifierView;
    private Size mOriginBmpSize = new Size(0,0);
    private SmartToolView mSmartToolView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effect);
        Intent intent = getIntent();
        if (intent.hasExtra("IMAGE_PATH")){
            mImagePath = intent.getStringExtra("IMAGE_PATH");
            Log.d(TAG,"ImagePath:"+mImagePath);
        }
        initUI();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeFile(mImagePath);
                bitmap = BitmpUtil.getProperResizedImage(bitmap,MAX_IMAGE_SIZE);
                final Bitmap finalBitmap = bitmap;
                mImagePath = FileUtil.getCurrentTimeMillisPath("jpg");
                FileUtil.writeBitmap(new File(mImagePath),bitmap);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mOriginImageView.setImageBitmap(finalBitmap);
                        mRootView.post(new Runnable() {
                            @Override
                            public void run() {
                                setViewSize(finalBitmap);
                            }
                        });
                    }
                });
            }
        }).start();

    }
    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private void initUI(){
        ImageView imageView = (ImageView) findViewById(R.id.iv_status_effect);
        setStatusBar(imageView);
        mRootView = (ConstraintLayout) findViewById(R.id.root_effect);
        mMagnifierView = new MagnifierView(this,mRootView);
        mTopToolView = (TopToolView) findViewById(R.id.ttl_top_tool_effecr);
        mTopToolView.setActionListener(mTopActionListner);
        mContainerView = (ConstraintLayout) findViewById(R.id.cl_container_effect);
        mContainerView.setOnTouchListener(mContainerToucherListener);
        mOriginImageView = (ImageView) findViewById(R.id.iv_origin_image_effect);
        mMaksImageView = (ImageView) findViewById(R.id.iv_mask_image_effect);
        mMaksImageView.setAlpha(0.5f);
        mDrawView = (DrawView) findViewById(R.id.dv_draw_effect);
        mDrawView.setOnDrawViewListener(mDrawViewListener);
        mBottomContainerView = (LinearLayout) findViewById(R.id.ll_bottom_container_effect);
        mSmartSelect = (ImageTextButton) findViewById(R.id.itb_smart_effect);
        mBrush = (ImageTextButton) findViewById(R.id.itb_brush_effect);
        mEraser = (ImageTextButton) findViewById(R.id.itb_eraser_effect);
        mSmartSelect.setOnClickListener(mButtonListener);
        mBrush.setOnClickListener(mButtonListener);
        mEraser.setOnClickListener(mButtonListener);
        mSmartSelect.setSelected(true);
        mSmartToolView = (SmartToolView) findViewById(R.id.stv_smart_tool_effect);
        mSmartToolView.setSmartToolListener(mSmartToolListener);

    }
    private void setViewSize(Bitmap bitmap){
        float bmpW = bitmap.getWidth();
        float bmpH = bitmap.getHeight();
        mOriginBmpSize = new Size(bmpW,bmpH);
        float rootW = mRootView.getWidth();
        float rootH = (mBottomContainerView.getTop() - mTopToolView.getBottom()) - mBottomContainerView.getHeight() *2;
        float bmpScale = bmpW / bmpH;
        float rootScale = rootW / rootH;
        int viewHeight;
        int viewWidth;
        if (bmpScale < rootScale){//root的高度作为标准
            viewHeight = (int) rootH;
            viewWidth = (int) (rootH * bmpScale);
        }else {
            viewWidth = (int) rootW;
            viewHeight = (int)(rootW / bmpScale);
        }
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)mContainerView.getLayoutParams();
        layoutParams.width = viewWidth;
        layoutParams.height = viewHeight;
        mContainerView.setLayoutParams(layoutParams);

    }

//================================== all listener ==================================

    private SmartToolView.OnSmartToolListener mSmartToolListener = new SmartToolView.OnSmartToolListener() {
        @Override
        public void onZhengXuan() {
            mDrawView.setDrawColor(Color.parseColor("#FFFFFF"));
        }

        @Override
        public void onFanXuan() {
            mDrawView.setDrawColor(Color.parseColor("#000000"));
        }
    };

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    //Log.i(TAG, "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    private DrawView.OnDrawViewListener mDrawViewListener = new DrawView.OnDrawViewListener() {
        @Override
        public void onStartDrawing(float x, float y) {
            float newX = mDrawView.getX()*1.0f + x;
            float newY = mDrawView.getY()*1.0f + y;
            mMagnifierView.onBeginMoving(mContainerView,(int)newX,(int)newY);
        }
        @Override
        public void onMoving(float x, float y) {
            float newX = mDrawView.getX()*1.0f + x;
            float newY = mDrawView.getY()*1.0f + y;
            mMagnifierView.onMoving(mContainerView,(int)newX,(int)newY);
        }
        @Override
        public void onEndDrawing() {
            mMagnifierView.onEndMoving();
            ProgressHUD.show(EffectActivity.this,getString(R.string.ei_processing),null);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap maskImg = creatFinalMaskImg();
                    String tmpPath = FileUtil.getCurrentTimeMillisPath("png");
                    FileUtil.writeBitmap(new File(tmpPath),maskImg);
                    mMaskPath = FileUtil.getCurrentTimeMillisPath("png");
                    GrabCutUtil.doGrabCut(mImagePath, maskImg, mMaskPath, new GrabCutUtil.OnGrabCutListener() {
                        @Override
                        public void onStartGrabCut() {

                        }

                        @Override
                        public void onFinishGrabCut() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ProgressHUD.dismiss();
                                    mMaskImage = BitmapFactory.decodeFile(mMaskPath);
                                    mMaksImageView.setImageBitmap(mMaskImage);
                                }
                            });

                        }
                    });
                }
            }).start();
        }
    };

    private Bitmap  creatFinalMaskImg(){
        Bitmap drawImg = mDrawView.getBitmp();
        Bitmap mask = Bitmap.createBitmap((int) mOriginBmpSize.width,(int) mOriginBmpSize.height,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mask);
        if (mMaskImage != null){
            Rect srcRectMask = new Rect(0,0,mMaskImage.getWidth(),mMaskImage.getHeight());
            Rect dstRect = new Rect(0,0,(int) mOriginBmpSize.width,(int) mOriginBmpSize.height);
            canvas.drawBitmap(mMaskImage,srcRectMask,dstRect,null);
            Rect srcRectDraw = new Rect(0,0,drawImg.getWidth(),drawImg.getHeight());
            canvas.drawBitmap(drawImg,srcRectDraw,dstRect,null);
        }else {
            Rect srcRect = new Rect(0,0,drawImg.getWidth(),drawImg.getHeight());
            Rect dstRect = new Rect(0,0,(int) mOriginBmpSize.width,(int) mOriginBmpSize.height);
            canvas.drawBitmap(drawImg,srcRect,dstRect,null);
        }
        return mask;
    }

    private View.OnTouchListener mContainerToucherListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mDrawView.onTouch(motionEvent);
            return true;
        }
    };


    private View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.itb_smart_effect:
                    onSmartButtonClick();
                    break;
                case R.id.itb_brush_effect:
                    onBrushButtonClick();
                    break;
                case R.id.itb_eraser_effect:
                    onEraserButtonClick();
                    break;
            }
        }
    };

    private TopToolView.OnTopActionListener mTopActionListner = new TopToolView.OnTopActionListener() {
        @Override
        public void onBackClick() {
            finish();
        }

        @Override
        public void onUndoClick() {

        }

        @Override
        public void onRedoClick() {

        }

        @Override
        public void onResetClick() {

        }

        @Override
        public void onSaveClick() {

        }
    };
//================================== all actions ==================================
    private void onSmartButtonClick(){
        if (mBrush.isSelected()){
            mBrush.setSelected(false);
        }
        if (mEraser.isSelected()){
            mEraser.setSelected(false);
        }
        if (mCurrentStatus != EditStatus.SMART_SELECT){
            if (mSmartSelect.isSelected()){
                mSmartSelect.setSelected(false);
            }else {
                mSmartSelect.setSelected(true);
                if (!mSmartToolView.isOpen()){
                    mSmartToolView.open();
                }
                mCurrentStatus = EditStatus.SMART_SELECT;
            }
        }
    }
    private void onBrushButtonClick(){

        if (mEraser.isSelected()){
            mEraser.setSelected(false);
        }
        if (mSmartSelect.isSelected()){
            mSmartSelect.setSelected(false);
        }
        if (mCurrentStatus != EditStatus.BRUSH){
            if (mBrush.isSelected()){
                mBrush.setSelected(false);
            }else {
                mBrush.setSelected(true);
                if (mSmartToolView.isOpen()){
                    mSmartToolView.close();
                }
                mCurrentStatus = EditStatus.BRUSH;
            }
        }
    }
    private void onEraserButtonClick(){
        if (mBrush.isSelected()){
            mBrush.setSelected(false);
        }

        if (mSmartSelect.isSelected()){
            mSmartSelect.setSelected(false);
        }
        if (mCurrentStatus != EditStatus.ERASER){
            if (mEraser.isSelected()){
                mEraser.setSelected(false);
            }else {
                mEraser.setSelected(true);
                if (mSmartToolView.isOpen()){
                    mSmartToolView.close();
                }
                mCurrentStatus = EditStatus.ERASER;
            }
        }
    }


}
