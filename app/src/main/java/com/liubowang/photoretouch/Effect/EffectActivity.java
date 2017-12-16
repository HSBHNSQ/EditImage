package com.liubowang.photoretouch.Effect;

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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lafonapps.common.ad.adapter.BannerViewAdapter;
import com.liubowang.photoretouch.Base.EIBaseActiviry;
import com.liubowang.photoretouch.Draw.BrushView;
import com.liubowang.photoretouch.Draw.DrawView;
import com.liubowang.photoretouch.FileBrowse.ImageLookActivity;
import com.liubowang.photoretouch.Main.GuidePlayActivity;
import com.liubowang.photoretouch.Normal.ImageTextButton;
import com.liubowang.photoretouch.Normal.TopToolView;
import com.liubowang.photoretouch.R;
import com.liubowang.photoretouch.Template.Template;
import com.liubowang.photoretouch.Template.TemplateModel;
import com.liubowang.photoretouch.Utils.BitmpUtil;
import com.liubowang.photoretouch.Utils.FileUtil;
import com.liubowang.photoretouch.Utils.GrabCutUtil;
import com.liubowang.photoretouch.Utils.ProgressHUD;
import com.liubowang.photoretouch.Utils.Size;
import com.martin.ads.omoshiroilib.camera.IWorkerCallback;
import com.martin.ads.omoshiroilib.filter.base.AbsFilter;
import com.martin.ads.omoshiroilib.glessential.GLRootView;
import com.martin.ads.omoshiroilib.imgeditor.gl.GLWrapper;
import com.martin.ads.omoshiroilib.util.BitmapUtils;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;

import static android.R.attr.path;

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
    private ImageView mResultImageView;
    private DrawView mDrawView;
    private BrushView mBrushView;
    private ImageTextButton mSmartSelect;
    private ImageTextButton mBrush;
    private ImageTextButton mEraser;
    private LinearLayout mBottomContainerView;
    private EditStatus mCurrentStatus = EditStatus.SMART_SELECT;
    private MagnifierView mMagnifierView;
    private Size mOriginBmpSize = new Size(0,0);
    private SBEToolView mSBEToolView;
    private BrushView.BrushType mCurrentBrushType;
    private GLRootView mGLRootView;
    private GLWrapper mGLWrapper;
    private FilterPickerView mFilterPickerView;
    private TextView mCutout;
    private TextView mPreview;
    private TextView mEffect;
    private TemplateModel templateModel;
    private ProgressBar mProgressBar;
    private FilterModel currentFilterModel;
    private UndoRedoManager mUndoRedoManager = new UndoRedoManager();
    private LinearLayout mEidtTypeContainer;
    private ImageView mGuide;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effect);
        Intent intent = getIntent();
        if (intent.hasExtra("IMAGE_PATH")){
            mImagePath = intent.getStringExtra("IMAGE_PATH");
            Log.d(TAG,"ImagePath:"+mImagePath);
        }
        if (intent.hasExtra("TEMPLATE_MODEL")){
            templateModel = intent.getParcelableExtra("TEMPLATE_MODEL");
            Log.d(TAG,templateModel.name);
        }
        initUI();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmpUtil.getSuitableBitmapFromAlbum(EffectActivity.this,mImagePath);
                if (bitmap == null){
                    bitmap = BitmapFactory.decodeFile(mImagePath);
                }
                bitmap = BitmpUtil.getProperResizedImage(bitmap,MAX_IMAGE_SIZE);
                final Bitmap finalBitmap = bitmap;
                mImagePath = FileUtil.getCurrentTimeMillisPath("png");
                FileUtil.writeBitmap(new File(mImagePath),bitmap);
                Log.d(TAG,"ImagePath:"+mImagePath);
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
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
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
        mResultImageView = (ImageView) findViewById(R.id.iv_result_image_effect);
        mDrawView = (DrawView) findViewById(R.id.dv_draw_effect);
        mDrawView.setOnDrawViewListener(mDrawViewListener);
        mBrushView = (BrushView) findViewById(R.id.bv_brush_effect);
        mBrushView.setOnBrushViewListener(mBrushViewListener);
        mBrushView.setAlpha(0.5f);
        mBottomContainerView = (LinearLayout) findViewById(R.id.ll_bottom_container_effect);
        mSmartSelect = (ImageTextButton) findViewById(R.id.itb_smart_effect);
        mBrush = (ImageTextButton) findViewById(R.id.itb_brush_effect);
        mEraser = (ImageTextButton) findViewById(R.id.itb_eraser_effect);
        mSmartSelect.setOnClickListener(mButtonListener);
        mBrush.setOnClickListener(mButtonListener);
        mEraser.setOnClickListener(mButtonListener);
        mSmartSelect.setSelected(true);
        mSBEToolView = (SBEToolView) findViewById(R.id.sbetv_sbe_tool_effect);
        mSBEToolView.setSBEToolListener(mSBEToolListener);
        mSBEToolView.setSeekRange(5,100);
        mGLRootView= (GLRootView) findViewById(R.id.glrv_glroot_view_effect);
        mGLRootView.setVisibility(View.INVISIBLE);
        mGLWrapper= GLWrapper.newInstance()
                .setGlImageView(mGLRootView)
                .setContext(this)
                .init();
        mFilterPickerView = (FilterPickerView) findViewById(R.id.fpv_filter_picker_effect);
        mFilterPickerView.setFilterChangeListener(mFilterChangeListener);
        mCutout = (TextView) findViewById(R.id.tv_cutout_effect);
        mCutout.setSelected(true);
        mCutout.setBackgroundResource(R.drawable.edit_type_left_bg);
        mCutout.setOnClickListener(mButtonListener);
        mPreview = (TextView) findViewById(R.id.tv_preview_effect);
        mPreview.setSelected(false);
        mPreview.setBackgroundColor(getResources().getColor(R.color.colorClean));
        mPreview.setOnClickListener(mButtonListener);
        mEffect = (TextView) findViewById(R.id.tv_effect_effect);
        mEffect.setSelected(false);
        mEffect.setBackgroundColor(getResources().getColor(R.color.colorClean));
        mEffect.setOnClickListener(mButtonListener);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_progress_effect);
        mEidtTypeContainer = (LinearLayout) findViewById(R.id.ll_edit_type_container_effect);
        mGuide = (ImageView) findViewById(R.id.iv_guide_effect);
        mGuide.setOnClickListener(mButtonListener);
    }

    private void setUpGLRootViewImage() {
        final Bitmap bitmap= BitmapUtils.loadBitmapFromFile(mImagePath);
        mGLRootView.setAspectRatio(bitmap.getWidth(),bitmap.getHeight());
        bitmap.recycle();
        mGLWrapper.setFilePath(mImagePath);

        if (templateModel != null && Template.toTemplate(templateModel.template) != Template.NONE){
            currentFilterModel = FilterModel.create(templateModel);
            mGLWrapper.switchFilter((AbsFilter) FilterFactory.creatFilter(currentFilterModel,
                    EffectActivity.this));
        }else {
            templateModel = null;
        }

    }

    private void setViewSize(Bitmap bitmap){
        float bmpW = bitmap.getWidth();
        float bmpH = bitmap.getHeight();
        mOriginBmpSize = new Size(bmpW,bmpH);
        float rootW = mRootView.getWidth() - MagnifierView.MAGNFIER_VIEW_SIZE / 2;
        float rootH = mRootView.getHeight() - mEidtTypeContainer.getBottom() * 2 - 5;
        //(mBottomContainerView.getTop() - mTopToolView.getBottom()) - mBottomContainerView.getHeight() *2;
        float bmpScale = bmpW / bmpH;
        float rootScale = rootW / rootH;
        int viewHeight;
        int viewWidth;
        if (bmpScale < rootScale){//root的高度作为标准
            int dstH = 1500;
            if (rootH < dstH){
                viewHeight = (int) rootH;
                viewWidth = (int) (rootH * bmpScale);
            }else {
                viewHeight = (int) dstH;
                viewWidth = (int) (dstH * bmpScale);
            }
        }else {
            int dstW = 1000;
            if (rootW < dstW){
                viewWidth = (int) rootW;
                viewHeight = (int)(rootW / bmpScale);
            }else {
                viewWidth = (int) dstW;
                viewHeight = (int)(dstW / bmpScale);
            }
        }
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)mContainerView.getLayoutParams();
        layoutParams.width = viewWidth;
        layoutParams.height = viewHeight;
        mContainerView.setLayoutParams(layoutParams);
        mBrushView.post(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.INVISIBLE);
                mBrushView.prepareBrush();
            }
        });

        setUpGLRootViewImage();
    }

//================================== all listener ==================================

    private FilterAdapter.OnFilterChangeListener mFilterChangeListener = new FilterAdapter.OnFilterChangeListener() {
        @Override
        public void onFilterChanged(FilterModel filterModel) {
            Log.d(TAG,filterModel.title);
            if (filterModel.rendererType == FilterModel.RendererType.ABSFILTER){
                currentFilterModel = filterModel;
                mGLWrapper.switchFilter((AbsFilter) FilterFactory.creatFilter(filterModel,EffectActivity.this));
            }
        }
    };

    private SBEToolView.OnSBEToolListener mSBEToolListener = new SBEToolView.OnSBEToolListener() {
        @Override
        public void onZhengXuan() {
            mDrawView.setDrawColor(Color.parseColor("#FFFFFF"));
        }

        @Override
        public void onFanXuan() {
            mDrawView.setDrawColor(Color.parseColor("#000000"));
        }

        @Override
        public void onBrushNormal() {
            mBrushView.setBrushType(BrushView.BrushType.NORMAL);
            mCurrentBrushType = BrushView.BrushType.NORMAL;
        }

        @Override
        public void onBrushNormal1() {
            mBrushView.setBrushType(BrushView.BrushType.NORMAL_1);
            mCurrentBrushType = BrushView.BrushType.NORMAL_1;
        }

        @Override
        public void onBrushNormal2() {
            mBrushView.setBrushType(BrushView.BrushType.NORMAL_2);
            mCurrentBrushType = BrushView.BrushType.NORMAL_2;
        }

        @Override
        public void onBrushSizeChange(int value) {
            mBrushView.setBrushWidth(value);
        }

        @Override
        public void onEraserSizeChange(int value) {
            mBrushView.setEraserWidth(value);
        }
    };

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                } break;
                default:
                {
                    Log.i(TAG, "OpenCV loaded failled");
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    private BrushView.OnBrushViewListener mBrushViewListener = new BrushView.OnBrushViewListener() {
        @Override
        public void onStartDrawing(float x, float y) {
            float newX = mContainerView.getX()*1.0f + x;
            float newY = mContainerView.getY()*1.0f + y;
            if (mCurrentStatus == EditStatus.BRUSH){
                mMagnifierView.onBeginMoving(mRootView,(int)newX,(int)newY,mBrushView.getBrushWidth(),Color.parseColor("#00FF00"));
            }else if (mCurrentStatus == EditStatus.ERASER){
                mMagnifierView.onBeginMoving(mRootView,(int)newX,(int)newY,mBrushView.getEraserWidth(),Color.parseColor("#000000"));
            }
        }
        @Override
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              public void onMoving(float x, float y) {
            float newX = mContainerView.getX()*1.0f + x;
            float newY = mContainerView.getY()*1.0f + y;
            if (mCurrentStatus == EditStatus.BRUSH){
                mMagnifierView.onMoving(mRootView,(int)newX,(int)newY,mBrushView.getBrushWidth(),Color.parseColor("#00FF00"));
            }else if (mCurrentStatus == EditStatus.ERASER){
                mMagnifierView.onMoving(mRootView,(int)newX,(int)newY,mBrushView.getEraserWidth(),Color.parseColor("#000000"));
            }

        }
        @Override
        public void onEndDrawing() {
            mMagnifierView.onEndMoving();
            UndoRedoInfo info = new UndoRedoInfo(mMaskPath,false);
            mUndoRedoManager.addAction(info);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String maskPathString = FileUtil.getCurrentTimeMillisPath("png");
                    Bitmap maskImage = mBrushView.getBitmp();
                    boolean success = FileUtil.writeBitmap(new File(maskPathString),maskImage);
                    if (success){
                        mMaskPath = maskPathString;
                    }
                    mUndoRedoManager.getLastInfo().reDoMaskPath = mMaskPath;
                }
            }).start();
        }
    };

    private DrawView.OnDrawViewListener mDrawViewListener = new DrawView.OnDrawViewListener() {
        @Override
        public void onStartDrawing(float x, float y) {
            float newX = mDrawView.getX()*1.0f + x;
            float newY = mDrawView.getY()*1.0f + y;
            mMagnifierView.onBeginMoving(mContainerView,(int)newX,(int)newY,0,Color.parseColor("#00000000"));
        }
        @Override
        public void onMoving(float x, float y) {
            float newX = mDrawView.getX()*1.0f + x;
            float newY = mDrawView.getY()*1.0f + y;
            mMagnifierView.onMoving(mContainerView,(int)newX,(int)newY,0,Color.parseColor("#00000000"));
        }
        @Override
        public void onEndDrawing() {
            mMagnifierView.onEndMoving();
            UndoRedoInfo info = new UndoRedoInfo(mMaskPath,true);
            mUndoRedoManager.addAction(info);
            ProgressHUD.show(EffectActivity.this,getString(R.string.ei_processing),null);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap maskImg = createFinalMaskImg();
                    String tmpPath = FileUtil.getCurrentTimeMillisPath("png");
                    FileUtil.writeBitmap(new File(tmpPath),maskImg);
                    mMaskPath = FileUtil.getCurrentTimeMillisPath("png");
                    mUndoRedoManager.getLastInfo().reDoMaskPath = mMaskPath;
                    GrabCutUtil.doGrabCut(mImagePath, maskImg, mMaskPath, new GrabCutUtil.OnGrabCutListener() {
                        @Override
                        public void onStartGrabCut() {

                        }

                        @Override
                        public void onFinishGrabCut() {
                            mMaskImage = BitmapFactory.decodeFile(mMaskPath);
//                            String tmpPath = FileUtil.getCurrentTimeMillisPath("png");
//                            GrabCutUtil.bianYuanQuJuChi(mMaskPath,tmpPath);
//                            mMaskImage = BitmapFactory.decodeFile(tmpPath);
//                            mMaskImage = GrabCutUtil.bianYuanXuHua(tmpPath);
//                            tmpPath = FileUtil.getCurrentTimeMillisPath("png");
//                            FileUtil.writeBitmap(new File(tmpPath),mMaskImage);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ProgressHUD.dismiss();
                                    if (mMaskImage != null){
                                        mBrushView.initialOriginBrush(mMaskImage);
                                    }
                                }
                            });
                        }
                    });
                }
            }).start();
        }
    };


    private Bitmap getResultBmp(Bitmap mask){

        Bitmap oriImage = BitmapFactory.decodeFile(mImagePath);
        if (mask.getHeight() != oriImage.getHeight() || mask.getWidth() != oriImage.getWidth()){
            mask = Bitmap.createScaledBitmap(mask,oriImage.getWidth(),oriImage.getHeight(),true);
        }
        Bitmap result = BitmpUtil.creatMaskBitmp(oriImage,mask);
        return result;
    }

    private Bitmap  createFinalMaskImg(){
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
            if (!mCutout.isSelected()) return false;
            switch (mCurrentStatus){
                case SMART_SELECT:
                    if (mDrawView.getVisibility() == View.INVISIBLE){
                        mDrawView.setVisibility(View.VISIBLE);
                    }
                    mDrawView.onTouch(motionEvent);
                    break;
                case BRUSH:
                    if (mBrushView.getBrushType() != mCurrentBrushType){
                        mBrushView.setBrushType(mCurrentBrushType);
                    }
                    mBrushView.onTouch(motionEvent);
                    break;
                case ERASER:
                    if (mBrushView.getBrushType() != BrushView.BrushType.ERASER){
                        mBrushView.setBrushType(BrushView.BrushType.ERASER);
                    }
                    mBrushView.onTouch(motionEvent);
            }

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
                case R.id.tv_cutout_effect:
                    onCutoutButtonClick();
                    break;
                case R.id.tv_preview_effect:
                    onPreviewButtonClick();
                    break;
                case R.id.tv_effect_effect:
                    onEffectButtonClick();
                    break;
                case R.id.iv_guide_effect:
                    onGuideButtonClick();
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
            Log.d(TAG,"onUndoClick");
            if (mUndoRedoManager.canUndo()){
                UndoRedoInfo info = mUndoRedoManager.undo();
                if (info != null){
                    if (info.canEditDraw){
                        mDrawView.undo();
                    }
                    Log.d(TAG,info.toString());
                    mMaskImage  = BitmapFactory.decodeFile(info.unDoMaskPath);
                    mMaskPath = info.unDoMaskPath;
                    mBrushView.initialOriginBrush(mMaskImage);
                }
            }
        }

        @Override
        public void onRedoClick() {
            Log.d(TAG,"onUndoClick");
            if (mUndoRedoManager.canRedo()){
                UndoRedoInfo info = mUndoRedoManager.redo();
                if (info != null){
                    if (info.canEditDraw){
                        mDrawView.redo();
                    }
                    Log.d(TAG,info.toString());
                    mMaskImage  = BitmapFactory.decodeFile(info.reDoMaskPath);
                    mMaskPath = info.reDoMaskPath;
                    mBrushView.initialOriginBrush(mMaskImage);
                }

            }
        }

        @Override
        public void onResetClick() {
            mUndoRedoManager.clean();
            mDrawView.restartDrawing();
            mBrushView.restartBrush();
        }

        @Override
        public void onSaveClick() {
            mProgressBar.setVisibility(View.VISIBLE);
            final Bitmap bitmap = BitmapFactory.decodeFile(mImagePath);
            final String outPutFilterBmpPath = FileUtil.getCurrentTimeMillisPath("png");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    BitmapUtils.saveBitmapWithFilterApplied(EffectActivity.this,
                            FilterFactory.getAbsFilterType(currentFilterModel), bitmap, outPutFilterBmpPath, new IWorkerCallback() {
                                @Override
                                public void onPostExecute(Exception exception) {
                                    if (exception == null){
                                        Bitmap filterBmp = BitmapFactory.decodeFile(outPutFilterBmpPath);
                                        Bitmap cutoutBmp = getCutoutResult();
                                        final Bitmap result = Bitmap.createBitmap(filterBmp.getWidth(),filterBmp.getHeight(), Bitmap.Config.ARGB_8888);
                                        Canvas canvas = new Canvas(result);
                                        canvas.drawBitmap(filterBmp,0,0,null);
                                        Rect srcRect = new Rect(0,0,cutoutBmp.getWidth(),cutoutBmp.getHeight());
                                        Rect dstRect = new Rect(0,0,result.getWidth(),result.getHeight());
                                        canvas.drawBitmap(cutoutBmp,srcRect,dstRect,null);
//                                        final String path = FileUtil.getPictureResultPathWithName(System.currentTimeMillis()+"","png");
//                                        final boolean success = FileUtil.writeBitmap(new File(path),result);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mProgressBar.setVisibility(View.INVISIBLE);
                                                SaveBitmapDialog saveBitmapDialog = SaveBitmapDialog.newInstance();
                                                saveBitmapDialog.setPreviewBitmap(result);
                                                saveBitmapDialog.setAppName(getString(R.string.app_name));
                                                saveBitmapDialog.setOnSaveBitmapListener(new SaveBitmapDialog.OnSaveBitmapListener() {

                                                    @Override
                                                    public void onSaveBitmapCompleted(boolean success,String bmpPath) {
                                                        String message = getString(R.string.ei_save_failed);
                                                        if (success){
                                                            message = getString(R.string.ei_save_successful);
                                                        }
                                                        if (success){
                                                            Toast.makeText(EffectActivity.this,message+":"+bmpPath, Toast.LENGTH_LONG).show();
                                                            Intent intent = new Intent(EffectActivity.this, ImageLookActivity.class);
                                                            intent.putExtra("IMAGE_PATH",bmpPath);
                                                            if (intent.resolveActivity(getPackageManager()) != null){
                                                                startActivity(intent);
                                                            }
                                                        }else {
                                                            Toast.makeText(EffectActivity.this, message, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                    @Override
                                                    public void onSaveBitmapCanceled() {

                                                    }
                                                });
                                                saveBitmapDialog.show(getSupportFragmentManager(), null);
                                            }
                                        });
                                    }else {
                                        Log.d(TAG,"保存失败");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mProgressBar.setVisibility(View.INVISIBLE);
                                                String message = getString(R.string.ei_save_failed);
                                                Toast.makeText(EffectActivity.this,message,Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                }
                            });

                }
            }).start();
        }
    };
//================================== all actions ==================================

    private void onGuideButtonClick(){
        Intent guide = new Intent(EffectActivity.this, GuidePlayActivity.class);
        if (guide.resolveActivity(getPackageManager()) != null){
            startActivity(guide);
        }
    }

    private void onCutoutButtonClick(){
        if (mPreview.isSelected()){
            mPreview.setSelected(false);
            mPreview.setBackgroundColor(getResources().getColor(R.color.colorClean));
        }
        if (mEffect.isSelected()){
            mEffect.setSelected(false);
            mEffect.setBackgroundColor(getResources().getColor(R.color.colorClean));
        }
        if (!mCutout.isSelected()){
            mCutout.setSelected(true);
            mCutout.setBackgroundResource(R.drawable.edit_type_left_bg);
            if (mCurrentStatus == EditStatus.SMART_SELECT){
                if (mDrawView.getVisibility() == View.INVISIBLE){
                    mDrawView.setVisibility(View.VISIBLE);
                }
            }
            if (mBrushView.getVisibility() == View.INVISIBLE){
                mBrushView.setVisibility(View.VISIBLE);
            }
            if (mGLRootView.getVisibility() == View.VISIBLE){
                mGLRootView.setVisibility(View.INVISIBLE);
            }
            if (mFilterPickerView.isOpen()){
                mFilterPickerView.close();
            }
        }
    }

    private void onPreviewButtonClick(){
        if (mCutout.isSelected()){
            mCutout.setSelected(false);
            mCutout.setBackgroundColor(getResources().getColor(R.color.colorClean));
        }
        if (mEffect.isSelected()){
            mEffect.setSelected(false);
            mEffect.setBackgroundColor(getResources().getColor(R.color.colorClean));
        }
        if (!mPreview.isSelected()){
            mPreview.setSelected(true);
            mPreview.setBackgroundResource(R.drawable.edit_type_center_bg);
            Bitmap result = getCutoutResult();
            mResultImageView.setImageBitmap(result);
            if (mDrawView.getVisibility() == View.VISIBLE){
                mDrawView.setVisibility(View.INVISIBLE);
            }
            if (mBrushView.getVisibility() == View.VISIBLE){
                mBrushView.setVisibility(View.INVISIBLE);
            }
            if (mGLRootView.getVisibility() == View.INVISIBLE){
                mGLRootView.setVisibility(View.VISIBLE);
            }
            if (mFilterPickerView.isOpen()){
                mFilterPickerView.close();
            }
        }
    }

    private void onEffectButtonClick(){

        if (mCutout.isSelected()){
            mCutout.setSelected(false);
            mCutout.setBackgroundColor(getResources().getColor(R.color.colorClean));
        }
        if (mPreview.isSelected()){
            mPreview.setSelected(false);
            mPreview.setBackgroundColor(getResources().getColor(R.color.colorClean));
        }
        if (!mEffect.isSelected()){
            mEffect.setSelected(true);
            mEffect.setBackgroundResource(R.drawable.edit_type_right_bg);
            Bitmap result = getCutoutResult();
            mResultImageView.setImageBitmap(result);
            if (mGLRootView.getVisibility() == View.INVISIBLE){
                mGLRootView.setVisibility(View.VISIBLE);
            }
            if (mDrawView.getVisibility() == View.VISIBLE){
                mDrawView.setVisibility(View.INVISIBLE);
            }
            if (mBrushView.getVisibility() == View.VISIBLE){
                mBrushView.setVisibility(View.INVISIBLE);
            }
            if (!mFilterPickerView.isOpen()){
                mFilterPickerView.open();
            }
        }
    }

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
                mDrawView.setVisibility(View.VISIBLE);
                mSBEToolView.openSmart();
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
                mDrawView.setVisibility(View.INVISIBLE);
                mSBEToolView.openBrush();
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
                mDrawView.setVisibility(View.INVISIBLE);
                mSBEToolView.openEraser();
                mCurrentStatus = EditStatus.ERASER;
            }
        }
    }

    private Bitmap getCutoutResult(){
        Bitmap mask = mBrushView.getBitmp();
        Bitmap result = getResultBmp(mask);
        return result;
    }

    private LinearLayout bannerViewContainer;
    @Override
    protected ViewGroup getBannerViewContainer() {
        if (bannerViewContainer == null){
            bannerViewContainer = (LinearLayout) findViewById(R.id.id_banner_container_effect);
        }
       return bannerViewContainer;
    }

    @Override
    public void onAdLoaded(BannerViewAdapter adapter) {
        super.onAdLoaded(adapter);
    }

    @Override
    protected boolean shouldShowBannerView() {
        return true;
    }
}
