package com.liubowang.editimage.Normal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.ConstraintLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.liubowang.editimage.Base.EIBaseActiviry;
import com.liubowang.editimage.Draw.DrawView;
import com.liubowang.editimage.Mosaic.MosaicView;
import com.liubowang.editimage.R;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal);
        Intent intent = getIntent();
        if (intent.hasExtra("IMAGE_PATH")){
            mImagePath = intent.getStringExtra("IMAGE_PATH");
            Log.d(TAG,"ImagePath:"+mImagePath);
        }
        initUI();
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
        mDrawView = (DrawView) findViewById(R.id.dv_draw_view_normal);
        mStickerContainerView = (StickersContainerView) findViewById(R.id.sv_stickers_container_view_normal);
        mRootView.post(new Runnable() {
            @Override
            public void run() {
                setViewSize();
            }
        });

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

    }

    private void setViewSize(){
        Bitmap bitmap = BitmapFactory.decodeFile(mImagePath);
        float bmpW = bitmap.getWidth();
        float bmpH = bitmap.getHeight();
        float rootW = mRootView.getWidth();
        float rootH = (mBottonItemContainer.getTop() - mTopToolView.getBottom());
        float bmpScale = bmpW / bmpH;
        float rootScale = rootW / rootH;
        int viewHeight = 0;
        int viewWidth = 0;
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
        mImageView.setImageBitmap(bitmap);
        mMosaicView.setImageBitmap(bitmap);
        mMosaicView.setMasking(true);
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
            if (mDrawView.canUndo()){
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
        }
        else if (mCurrentStatus == EditStatus.GRAFFITI){
            if (mDrawView.canRedo()){
                mDrawView.redo();
            }
        }
    }

    @Override
    public void onResetClick() {

    }

    @Override
    public void onSaveClick() {

    }
//====================================================================
}
