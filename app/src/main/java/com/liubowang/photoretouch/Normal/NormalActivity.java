package com.liubowang.photoretouch.Normal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lafonapps.common.ad.adapter.BannerViewAdapter;
import com.lafonapps.common.ad.adapter.banner.BannerAdapterView;
import com.liubowang.photoretouch.Base.EIBaseActiviry;
import com.liubowang.photoretouch.Draw.DrawView;
import com.liubowang.photoretouch.Effect.EffectActivity;
import com.liubowang.photoretouch.Effect.SaveBitmapDialog;
import com.liubowang.photoretouch.FileBrowse.ImageLookActivity;
import com.liubowang.photoretouch.Mosaic.MosaicView;
import com.liubowang.photoretouch.R;
import com.liubowang.photoretouch.Utils.BitmpUtil;
import com.liubowang.photoretouch.Utils.FileUtil;
import com.liubowang.photoretouch.Utils.ProgressHUD;
import com.liubowang.photoretouch.Utils.ViewTransformUtil;
import com.yalantis.ucrop.callback.BitmapLoadCallback;
import com.yalantis.ucrop.model.ExifInfo;
import com.yalantis.ucrop.util.BitmapLoadUtils;

import java.io.File;

public class NormalActivity extends EIBaseActiviry implements TopToolView.OnTopActionListener{

    enum EditStatus {
        MOSAIC,STICKERS,GRAFFITI
    }

    private final String TAG = getClass().getSimpleName();
    private String mImagePath;
    private TopToolView mTopToolView;
    private ImageTextButton mMosaicButton;
    private ImageTextButton mStickersButton;
    private ImageTextButton mGraffitiButton;
    private LinearLayout mBottonItemContainer;
    private ConstraintLayout mRootView;
    private ConstraintLayout mContainerView;
    private ImageView mImageView;
    private MosaicView mMosaicView;
    private DrawView mDrawView;
    private StickersContainerView mStickerContainerView;
    private EditStatus mCurrentStatus = EditStatus.MOSAIC;
    private MosaicSizeSeek mMosaicSizeSeek;
    private GraffitiSeekView mGraffitiSeekView;
    private StickersPickerView mStickersPickerView;
    private ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal);
        Intent intent = getIntent();

        initUI();
        if (intent != null && intent.hasExtra("IMAGE_PATH")){
            mImagePath = intent.getStringExtra("IMAGE_PATH");
            if (mImagePath != null){
                setupImageView();
            }
        }
        else if (intent != null && intent.hasExtra("IMAGE_URI")){
            Uri imageUri = intent.getParcelableExtra("IMAGE_URI");
            BitmapLoadUtils.decodeBitmapInBackground(this, imageUri, imageUri, 1024, 1024,
                    new BitmapLoadCallback() {

                        @Override
                        public void onBitmapLoaded(@NonNull Bitmap bitmap, @NonNull ExifInfo exifInfo, @NonNull String imageInputPath, @Nullable String imageOutputPath) {
                            mImagePath = imageOutputPath;
                            setupImageView();
                        }

                        @Override
                        public void onFailure(@NonNull Exception bitmapWorkerException) {

                        }
                    });
        }

    }

    private void setupImageView(){
        mRootView.post(new Runnable() {
            @Override
            public void run() {
                setViewSize();
            }
        });
    }

    private void initUI(){
        ImageView imageView = (ImageView) findViewById(R.id.iv_status_normal);
        setStatusBar(imageView);
        mRootView = (ConstraintLayout) findViewById(R.id.root_normal);
        mBottonItemContainer = (LinearLayout) findViewById(R.id.ll_bottom_item_container);

        mTopToolView = (TopToolView) findViewById(R.id.ttl_top_tool_normal);
        mTopToolView.setActionListener(this);
        mMosaicButton = (ImageTextButton) findViewById(R.id.itb_mosaic_normal);
        mMosaicButton.setOnClickListener(mButtonListener);
        mMosaicButton.setSelected(true);
        mStickersButton = (ImageTextButton) findViewById(R.id.itb_stickers_normal);
        mStickersButton.setOnClickListener(mButtonListener);
        mStickersButton.setSelected(false);
        mGraffitiButton = (ImageTextButton) findViewById(R.id.itb_graffiti_normal);
        mGraffitiButton.setOnClickListener(mButtonListener);
        mGraffitiButton.setSelected(false);

        mContainerView = (ConstraintLayout) findViewById(R.id.cl_container_normal);
        mContainerView.setOnTouchListener(mContainerToucherListener);
        mImageView = (ImageView) findViewById(R.id.iv_origin_image_view_normal);
        mMosaicView = (MosaicView) findViewById(R.id.mv_mosaic_view_normal);
        mMosaicView.setOnMosaicTouchListener(mMosaicTouchListener);
        mDrawView = (DrawView) findViewById(R.id.dv_draw_view_normal);
        mDrawView.setOnDrawViewListener(mDrawViewLisntenr);
        mStickerContainerView = (StickersContainerView) findViewById(R.id.sv_stickers_container_view_normal);


        mMosaicSizeSeek = (MosaicSizeSeek) findViewById(R.id.mss_mosaic_size_normal);
        mMosaicSizeSeek.setSeekValueChangedListener(mMosaicSizeListener);
        mMosaicSizeSeek.setTag(true);

        mGraffitiSeekView = (GraffitiSeekView) findViewById(R.id.gsv_graffiti_seek_normal);
        mGraffitiSeekView.setGraffitiSeekListener(mGraffitiListener);
        mGraffitiSeekView.setTag(true);
        mGraffitiSeekView.setVisibility(View.INVISIBLE);

        mStickersPickerView = (StickersPickerView) findViewById(R.id.spv_sticker_picker_normal);
        mStickersPickerView.setPickerListener(mStickerPickerListener);
        mStickersPickerView.setTag(true);
        mStickersPickerView.setupRecycleView();
        mStickersPickerView.setVisibility(View.INVISIBLE);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_progress_normal);
    }

    private void setViewSize(){
        Bitmap bitmap =  BitmapFactory.decodeFile(mImagePath);
        float bmpW = bitmap.getWidth();
        float bmpH = bitmap.getHeight();
        float rootW = mRootView.getWidth() - mTopToolView.getHeight();
        float rootH = (mBottonItemContainer.getTop() - mTopToolView.getBottom() - mTopToolView.getHeight());
        float bmpScale = bmpW / bmpH;
        float rootScale = rootW / rootH;
        int viewHeight = 0;
        int viewWidth = 0;
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
        mImageView.setImageBitmap(bitmap);
        mMosaicView.setImageBitmap(bitmap);
        mMosaicView.setMasking(true);
        mProgressBar.setVisibility(View.INVISIBLE);
        onMosaicButtonClick();

    }

//====================== all listener ======================
    private View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
                switch (view.getId()){
                    case R.id.itb_mosaic_normal:
                        onMosaicButtonClick();
                        break;
                    case R.id.itb_stickers_normal:
                        onStickersButtonClick();
                        break;
                    case R.id.itb_graffiti_normal:
                        onGraffitiButtonClick();
                        break;

                }
        }
    };

    private View.OnTouchListener mContainerToucherListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN){
                if (mMosaicSizeSeek.isOpen()){
                    mMosaicSizeSeek.close();
                }
                if (mStickersPickerView.isOpen()){
                    mStickersPickerView.close();
                }
                if (mGraffitiSeekView.isLeftOpen()){
                    mGraffitiSeekView.closeContainerlayout();
                }
            }
            switch (mCurrentStatus){
                case MOSAIC:
                    mMosaicView.onTouch(motionEvent);
                    break;
                case STICKERS:

                    break;
                case GRAFFITI:
                    mDrawView.onTouch(motionEvent);
                    break;
                default: break;
            }


            return true;
        }
    };

    private DrawView.OnDrawViewListener mDrawViewLisntenr = new DrawView.OnDrawViewListener() {
        @Override
        public void onStartDrawing(float x, float y) {

        }

        @Override
        public void onMoving(float x, float y) {

        }

        @Override
        public void onEndDrawing() {
        }
    };

    private MosaicView.OnMosaicTouchListener mMosaicTouchListener = new MosaicView.OnMosaicTouchListener() {
        @Override
        public void onTouchDown() {

        }

        @Override
        public void onTouchMove() {

        }

        @Override
        public void onTouchEnd() {

        }
    };


    private TextSeekView.OnTextSeekValueChangedListener mMosaicSizeListener = new TextSeekView.OnTextSeekValueChangedListener() {
        @Override
        public void onValueChange(TextSeekView textSeekView, int value, boolean b) {
//            if (value > 5){
//                mMosaicView.setMosaicSize(value);
//            }
        }

        @Override
        public void onStopTrackingTouch(TextSeekView textSeekView, int value) {
            int checkValue = value;
            if (checkValue < 5){
                checkValue = 5;
            }
            mMosaicView.setMosaicSize(checkValue);
        }
    };

    private GraffitiSeekView.GraffitiSeekListener mGraffitiListener = new GraffitiSeekView.GraffitiSeekListener() {
        @Override
        public void sizeChanged(int size) {
            mDrawView.setDrawWidth(size);
        }

        @Override
        public void colorChanged(int color) {
            mDrawView.setDrawColor(color);
        }
    };

    private StickersPickerView.StickerPickerListener mStickerPickerListener = new StickersPickerView.StickerPickerListener() {
        @Override
        public void stickerPciker(Bitmap sticker) {
            mStickerContainerView.addSticker(sticker,NormalActivity.this);
        }
    };

//====================== button click action ======================
    private void onMosaicButtonClick(){
        Log.d(TAG,"onMosaicButtonClick");
        if (mStickersButton.isSelected()){
            mStickersButton.setSelected(false);
            mStickersPickerView.setVisibility(View.INVISIBLE);
        }
        if (mGraffitiButton.isSelected()){
            mGraffitiButton.setSelected(false);
            mGraffitiSeekView.setVisibility(View.INVISIBLE);
        }
        if (mCurrentStatus != EditStatus.MOSAIC){
            if (mMosaicButton.isSelected()){
                mMosaicButton.setSelected(false);
                mMosaicSizeSeek.setVisibility(View.INVISIBLE);
            }else {
                mMosaicButton.setSelected(true);
                mTopToolView.setRedoEnable(true);
                mTopToolView.setUndoEnable(true);
                mMosaicSizeSeek.setVisibility(View.VISIBLE);
                mCurrentStatus = EditStatus.MOSAIC;
                boolean isFirst = (Boolean) mMosaicSizeSeek.getTag();
                if (isFirst){
                    mMosaicSizeSeek.setTag(false);
                    mMosaicSizeSeek.open();
                }
            }
        }else {
            mMosaicSizeSeek.setTag(false);
            if (mMosaicSizeSeek.isOpen()){
                mMosaicSizeSeek.close();
            }else {
                mMosaicSizeSeek.open();
            }
        }


    }
    private void onStickersButtonClick(){
        if (mMosaicButton.isSelected()){
            mMosaicButton.setSelected(false);
            mMosaicSizeSeek.setVisibility(View.INVISIBLE);
        }
        if (mGraffitiButton.isSelected()){
            mGraffitiButton.setSelected(false);
            mGraffitiSeekView.setVisibility(View.INVISIBLE);
        }
        if (mCurrentStatus != EditStatus.STICKERS){
            if (mStickersButton.isSelected()){
                mStickersButton.setSelected(false);
                mStickersPickerView.setVisibility(View.INVISIBLE);
            }else{
                mStickersButton.setSelected(true);
                mTopToolView.setRedoEnable(false);
                mTopToolView.setUndoEnable(false);
                mStickersPickerView.setVisibility(View.VISIBLE);
                mCurrentStatus = EditStatus.STICKERS;
                boolean isFirst = (Boolean) mStickersPickerView.getTag();
                if (isFirst){
                    mStickersPickerView.setTag(false);
                    mStickersPickerView.open();
                }
            }
        }else {
            mStickersPickerView.setTag(false);
            if (mStickersPickerView.isOpen()){
                mStickersPickerView.close();
            }else {
                mStickersPickerView.open();
            }
        }

    }
    private void onGraffitiButtonClick(){
        Log.d(TAG,"onGraffitiButtonClick");
        if (mMosaicButton.isSelected()){
            mMosaicButton.setSelected(false);
            mMosaicSizeSeek.setVisibility(View.INVISIBLE);
        }
        if (mStickersButton.isSelected()){
            mStickersButton.setSelected(false);
            mStickersPickerView.setVisibility(View.INVISIBLE);
        }
        if (mCurrentStatus != EditStatus.GRAFFITI){
            if (mGraffitiButton.isSelected()){
                mGraffitiButton.setSelected(false);
                mGraffitiSeekView.setVisibility(View.INVISIBLE);
            }else{
                mGraffitiButton.setSelected(true);
                mTopToolView.setRedoEnable(true);
                mTopToolView.setUndoEnable(true);
                mGraffitiSeekView.setVisibility(View.VISIBLE);
                mCurrentStatus = EditStatus.GRAFFITI;
                boolean isFirst = (Boolean) mGraffitiSeekView.getTag();
                if (isFirst){
                    mGraffitiSeekView.setTag(false);
                    mGraffitiSeekView.openContainerlayout();
                }
            }
        }else {
            mGraffitiSeekView.setTag(false);
            if (mGraffitiSeekView.isLeftOpen()){
                mGraffitiSeekView.closeContainerlayout();
            }else {
                mGraffitiSeekView.openContainerlayout();
            }
        }

    }
//=================TopToolView.OnTopActionListener=================
    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    public void onUndoClick() {
        if (mCurrentStatus == EditStatus.MOSAIC){
            if (mMosaicView.canUndo()){
                mMosaicView.undo();
            }
        }
        else if (mCurrentStatus == EditStatus.GRAFFITI){
            if (mDrawView.canUndo()) {
                mDrawView.undo();
            }
        }

    }

    @Override
    public void onRedoClick() {
        if (mCurrentStatus == EditStatus.MOSAIC){
            if (mMosaicView.canRedo()){
                mMosaicView.redo();
            }
            mTopToolView.setRedoEnable(mMosaicView.canRedo());
            mTopToolView.setRedoEnable(mMosaicView.canUndo());
        }
        else if (mCurrentStatus == EditStatus.GRAFFITI){
            if (mDrawView.canRedo()){
                mDrawView.redo();
            }
            mTopToolView.setRedoEnable(mDrawView.canRedo());
            mTopToolView.setRedoEnable(mDrawView.canUndo());
        }
    }

    @Override
    public void onResetClick() {
        mDrawView.restartDrawing();
        mMosaicView.clean();
        mStickerContainerView.clearAllStickers();
    }

    @Override
    public void onSaveClick() {
        mStickerContainerView.setCurrentStickerViewSelected(false);
        mProgressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap viewBmp = Bitmap.createBitmap(mContainerView.getWidth(),mContainerView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(viewBmp);
                mContainerView.draw(canvas);
                final Bitmap result = BitmpUtil.getProperResizedImage(viewBmp,1000);
                viewBmp.recycle();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mStickerContainerView.setCurrentStickerViewSelected(true);
                        mProgressBar.setVisibility(View.INVISIBLE);
                        final SaveBitmapDialog saveBitmapDialog = SaveBitmapDialog.newInstance();
                        saveBitmapDialog.setPreviewBitmap(result);
                        saveBitmapDialog.setAppName(getString(R.string.app_name));
                        saveBitmapDialog.setOnSaveBitmapListener(new SaveBitmapDialog.OnSaveBitmapListener() {
                            @Override
                            public void onStartSave() {
                                mProgressBar.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onSaveBitmapCompleted(final boolean success, final String bmpPath) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        saveBitmapDialog.dismiss();
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                        String message = getString(R.string.ei_save_failed);
                                        if (success){
                                            message = getString(R.string.ei_save_successful);
                                        }
                                        if (success){
                                            Toast.makeText(NormalActivity.this,message+":"+bmpPath, Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(NormalActivity.this, ImageLookActivity.class);
                                            intent.putExtra("IMAGE_PATH",bmpPath);
                                            if (intent.resolveActivity(getPackageManager()) != null){
                                                startActivity(intent);
                                            }
                                        }else {
                                            Toast.makeText(NormalActivity.this,message, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onSaveBitmapCanceled() {

                            }
                        });
                        saveBitmapDialog.show(getSupportFragmentManager(), null);


                    }
                });
            }
        }).start();

    }
//====================================================================

    private LinearLayout bannerViewContainer;
    @Override
    protected ViewGroup getBannerViewContainer() {
        if (bannerViewContainer == null){
            bannerViewContainer = (LinearLayout) findViewById(R.id.ll_banner_container_normal);
        }
        return bannerViewContainer;
    }
    @Override
    protected boolean shouldShowBannerView() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
