package com.yalantis.ucrop;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yalantis.ucrop.callback.BitmapCropCallback;
import com.yalantis.ucrop.callback.BitmapLoadCallback;
import com.yalantis.ucrop.model.AspectRatio;
import com.yalantis.ucrop.util.BitmapLoadUtils;
import com.yalantis.ucrop.util.DisplayUtil;
import com.yalantis.ucrop.util.FileUtils;
import com.yalantis.ucrop.util.ScreenUtil;
import com.yalantis.ucrop.util.SelectedStateListDrawable;
import com.yalantis.ucrop.view.CropImageView;
import com.yalantis.ucrop.view.GestureCropImageView;
import com.yalantis.ucrop.view.OverlayView;
import com.yalantis.ucrop.view.TransformImageView;
import com.yalantis.ucrop.view.UCropView;
import com.yalantis.ucrop.view.widget.CustomAspectRatioView;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UCropCustomActivity extends AppCompatActivity {


    public static final int DEFAULT_COMPRESS_QUALITY = 90;
    public static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;

    public static final int NONE = 0;
    public static final int SCALE = 1;
    public static final int ROTATE = 2;
    public static final int ALL = 3;

    @IntDef({NONE, SCALE, ROTATE, ALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GestureTypes {

    }

    private static final String TAG = "UCropCustomActivity";

    private static final int TABS_COUNT = 3;
    private static final int SCALE_WIDGET_SENSITIVITY_COEFFICIENT = 15000;
    private static final int ROTATE_WIDGET_SENSITIVITY_COEFFICIENT = 42;

    private String mToolbarTitle;

    // Enables dynamic coloring
    private int mToolbarColor;
    private int mStatusBarColor;
    private int mActiveWidgetColor;
    private int mToolbarWidgetColor;
    @ColorInt
    private int mRootViewBackgroundColor;
    @DrawableRes
    private int mToolbarCancelDrawable;
    @DrawableRes private int mToolbarCropDrawable;
    private int mLogoColor;

    private boolean mShowBottomControls;
    private boolean mShowLoader = true;

    private UCropView mUCropView;
    private GestureCropImageView mGestureCropImageView;
    private OverlayView mOverlayView;
    private TextView mStateAspectRatioTv;
    private TextView mStateRotateTv;
    private TextView mStateScaleTv;
    private ViewGroup mLayoutAspectRatio, mLayoutRotate, mLayoutScale;
    private List<ViewGroup> mCropAspectRatioViews = new ArrayList<>();
    private View mBlockingView;

    private Bitmap.CompressFormat mCompressFormat = DEFAULT_COMPRESS_FORMAT;
    private int mCompressQuality = DEFAULT_COMPRESS_QUALITY;
    private int[] mAllowedGestures = new int[]{SCALE, ROTATE, ALL};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.yalantis.ucrop.R.layout.ucrop_activity_custom);

        final Intent intent = getIntent();

        setupViews(intent);
        setImageData(intent);
        setInitialState();
        addBlockingView();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(com.yalantis.ucrop.R.menu.ucrop_menu_activity, menu);
        MenuItem menuItemLoader = menu.findItem(com.yalantis.ucrop.R.id.menu_loader);
        Drawable menuItemLoaderIcon = menuItemLoader.getIcon();
        if (menuItemLoaderIcon != null) {
            try {
                menuItemLoaderIcon.mutate();
                menuItemLoaderIcon.setColorFilter(mToolbarWidgetColor, PorterDuff.Mode.SRC_ATOP);
                menuItemLoader.setIcon(menuItemLoaderIcon);
            } catch (IllegalStateException e) {
                Log.i(TAG, String.format("%s - %s", e.getMessage(), getString(com.yalantis.ucrop.R.string.ucrop_mutate_exception_hint)));
            }
            ((Animatable) menuItemLoader.getIcon()).start();
        }

        MenuItem menuItemCrop = menu.findItem(com.yalantis.ucrop.R.id.menu_crop);
        Drawable menuItemCropIcon = ContextCompat.getDrawable(this, mToolbarCropDrawable);
        if (menuItemCropIcon != null) {
            menuItemCropIcon.mutate();
            menuItemCropIcon.setColorFilter(mToolbarWidgetColor, PorterDuff.Mode.SRC_ATOP);
            menuItemCrop.setIcon(menuItemCropIcon);
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(com.yalantis.ucrop.R.id.menu_crop).setVisible(!mShowLoader);
        menu.findItem(com.yalantis.ucrop.R.id.menu_loader).setVisible(mShowLoader);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == com.yalantis.ucrop.R.id.menu_crop) {
            cropAndSaveImage();
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGestureCropImageView != null) {
            mGestureCropImageView.cancelAllAnimations();
        }
    }

    /**
     * This method extracts all data from the incoming intent and setups views properly.
     */
    private void setImageData(@NonNull Intent intent) {
        Uri inputUri = intent.getParcelableExtra(UCrop.EXTRA_INPUT_URI);
        Uri outputUri = intent.getParcelableExtra(UCrop.EXTRA_OUTPUT_URI);
        processOptions(intent);

        if (inputUri != null && outputUri != null) {
            try {
                mGestureCropImageView.setImageUri(inputUri, outputUri);
            } catch (Exception e) {
                setResultError(e);
                finish();
            }
        } else {
            setResultError(new NullPointerException(getString(com.yalantis.ucrop.R.string.ucrop_error_input_data_is_absent)));
            finish();
        }
    }

    /**
     * This method extracts {@link com.yalantis.ucrop.UCrop.Options #optionsBundle} from incoming intent
     * and setups Activity, {@link OverlayView} and {@link CropImageView} properly.
     */
    @SuppressWarnings("deprecation")
    private void processOptions(@NonNull Intent intent) {
        // Bitmap compression options
        String compressionFormatName = intent.getStringExtra(UCrop.Options.EXTRA_COMPRESSION_FORMAT_NAME);
        Bitmap.CompressFormat compressFormat = null;
        if (!TextUtils.isEmpty(compressionFormatName)) {
            compressFormat = Bitmap.CompressFormat.valueOf(compressionFormatName);
        }
        mCompressFormat = (compressFormat == null) ? DEFAULT_COMPRESS_FORMAT : compressFormat;

        mCompressQuality = intent.getIntExtra(UCrop.Options.EXTRA_COMPRESSION_QUALITY, UCropActivity.DEFAULT_COMPRESS_QUALITY);

        // Gestures options
        int[] allowedGestures = intent.getIntArrayExtra(UCrop.Options.EXTRA_ALLOWED_GESTURES);
        if (allowedGestures != null && allowedGestures.length == TABS_COUNT) {
            mAllowedGestures = allowedGestures;
        }

        // Crop image view options
        mGestureCropImageView.setMaxBitmapSize(intent.getIntExtra(UCrop.Options.EXTRA_MAX_BITMAP_SIZE, CropImageView.DEFAULT_MAX_BITMAP_SIZE));
        mGestureCropImageView.setMaxScaleMultiplier(intent.getFloatExtra(UCrop.Options.EXTRA_MAX_SCALE_MULTIPLIER, CropImageView.DEFAULT_MAX_SCALE_MULTIPLIER));
        mGestureCropImageView.setImageToWrapCropBoundsAnimDuration(intent.getIntExtra(UCrop.Options.EXTRA_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION, CropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION));

        // Overlay view options
        mOverlayView.setFreestyleCropEnabled(intent.getBooleanExtra(UCrop.Options.EXTRA_FREE_STYLE_CROP, OverlayView.DEFAULT_FREESTYLE_CROP_MODE != OverlayView.FREESTYLE_CROP_MODE_DISABLE));

        mOverlayView.setDimmedColor(intent.getIntExtra(UCrop.Options.EXTRA_DIMMED_LAYER_COLOR, getResources().getColor(com.yalantis.ucrop.R.color.ucrop_color_default_dimmed)));
        mOverlayView.setCircleDimmedLayer(intent.getBooleanExtra(UCrop.Options.EXTRA_CIRCLE_DIMMED_LAYER, OverlayView.DEFAULT_CIRCLE_DIMMED_LAYER));

        mOverlayView.setShowCropFrame(intent.getBooleanExtra(UCrop.Options.EXTRA_SHOW_CROP_FRAME, OverlayView.DEFAULT_SHOW_CROP_FRAME));
        mOverlayView.setCropFrameColor(intent.getIntExtra(UCrop.Options.EXTRA_CROP_FRAME_COLOR, getResources().getColor(com.yalantis.ucrop.R.color.ucrop_color_default_crop_frame)));
        mOverlayView.setCropFrameStrokeWidth(intent.getIntExtra(UCrop.Options.EXTRA_CROP_FRAME_STROKE_WIDTH, getResources().getDimensionPixelSize(com.yalantis.ucrop.R.dimen.ucrop_default_crop_frame_stoke_width)));

        mOverlayView.setShowCropGrid(intent.getBooleanExtra(UCrop.Options.EXTRA_SHOW_CROP_GRID, OverlayView.DEFAULT_SHOW_CROP_GRID));
        mOverlayView.setCropGridRowCount(intent.getIntExtra(UCrop.Options.EXTRA_CROP_GRID_ROW_COUNT, OverlayView.DEFAULT_CROP_GRID_ROW_COUNT));
        mOverlayView.setCropGridColumnCount(intent.getIntExtra(UCrop.Options.EXTRA_CROP_GRID_COLUMN_COUNT, OverlayView.DEFAULT_CROP_GRID_COLUMN_COUNT));
        mOverlayView.setCropGridColor(intent.getIntExtra(UCrop.Options.EXTRA_CROP_GRID_COLOR, getResources().getColor(com.yalantis.ucrop.R.color.ucrop_color_default_crop_grid)));
        mOverlayView.setCropGridStrokeWidth(intent.getIntExtra(UCrop.Options.EXTRA_CROP_GRID_STROKE_WIDTH, getResources().getDimensionPixelSize(com.yalantis.ucrop.R.dimen.ucrop_default_crop_grid_stoke_width)));

        // Aspect ratio options
        float aspectRatioX = intent.getFloatExtra(UCrop.EXTRA_ASPECT_RATIO_X, 0);
        float aspectRatioY = intent.getFloatExtra(UCrop.EXTRA_ASPECT_RATIO_Y, 0);

        int aspectRationSelectedByDefault = intent.getIntExtra(UCrop.Options.EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT, 0);
        ArrayList<AspectRatio> aspectRatioList = intent.getParcelableArrayListExtra(UCrop.Options.EXTRA_ASPECT_RATIO_OPTIONS);

        if (aspectRatioX > 0 && aspectRatioY > 0) {
            if (mStateAspectRatioTv != null) {
                mStateAspectRatioTv.setVisibility(View.GONE);
            }
            mGestureCropImageView.setTargetAspectRatio(aspectRatioX / aspectRatioY);
        } else if (aspectRatioList != null && aspectRationSelectedByDefault < aspectRatioList.size()) {
            mGestureCropImageView.setTargetAspectRatio(aspectRatioList.get(aspectRationSelectedByDefault).getAspectRatioX() /
                    aspectRatioList.get(aspectRationSelectedByDefault).getAspectRatioY());
        } else {
            mGestureCropImageView.setTargetAspectRatio(CropImageView.SOURCE_IMAGE_ASPECT_RATIO);
        }

        // Result bitmap max size options
        int maxSizeX = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_X, 0);
        int maxSizeY = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_Y, 0);

        if (maxSizeX > 0 && maxSizeY > 0) {
            mGestureCropImageView.setMaxResultImageSizeX(maxSizeX);
            mGestureCropImageView.setMaxResultImageSizeY(maxSizeY);
        }
    }

    private void setupViews(@NonNull Intent intent) {
        mStatusBarColor = intent.getIntExtra(UCrop.Options.EXTRA_STATUS_BAR_COLOR, ContextCompat.getColor(this, com.yalantis.ucrop.R.color.ucrop_color_statusbar));
        mToolbarColor = intent.getIntExtra(UCrop.Options.EXTRA_TOOL_BAR_COLOR, ContextCompat.getColor(this, com.yalantis.ucrop.R.color.ucrop_color_toolbar));
        mActiveWidgetColor = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_COLOR_WIDGET_ACTIVE, ContextCompat.getColor(this, com.yalantis.ucrop.R.color.ucrop_color_widget_active));
        mToolbarWidgetColor = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_WIDGET_COLOR_TOOLBAR, ContextCompat.getColor(this, com.yalantis.ucrop.R.color.ucrop_color_toolbar_widget));
        mToolbarCancelDrawable = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_WIDGET_CANCEL_DRAWABLE, R.drawable.ucrop_ic_cancel);
        mToolbarCropDrawable = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_WIDGET_CROP_DRAWABLE, R.drawable.ucrop_ic_confirm);
        mToolbarTitle = intent.getStringExtra(UCrop.Options.EXTRA_UCROP_TITLE_TEXT_TOOLBAR);
        mToolbarTitle = mToolbarTitle != null ? mToolbarTitle : getResources().getString(com.yalantis.ucrop.R.string.ucrop_label_edit_photo);
        mLogoColor = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_LOGO_COLOR, ContextCompat.getColor(this, com.yalantis.ucrop.R.color.ucrop_color_default_logo));
        mShowBottomControls = !intent.getBooleanExtra(UCrop.Options.EXTRA_HIDE_BOTTOM_CONTROLS, false);
        mRootViewBackgroundColor = intent.getIntExtra(UCrop.Options.EXTRA_UCROP_ROOT_VIEW_BACKGROUND_COLOR, ContextCompat.getColor(this, com.yalantis.ucrop.R.color.ucrop_color_crop_background));

        setupAppBar();
        initiateRootViews();

        if (mShowBottomControls) {

            mStateAspectRatioTv = (TextView) findViewById(R.id.tv_aspect_ratio_status);
            mStateAspectRatioTv.setOnClickListener(mStateClickListener);
            mStateRotateTv = (TextView) findViewById(R.id.tv_rotation_status);
            mStateRotateTv.setOnClickListener(mStateClickListener);
            mStateScaleTv = (TextView) findViewById(R.id.tv_scale_status);
            mStateScaleTv.setOnClickListener(mStateClickListener);

            mLayoutAspectRatio = (ViewGroup) findViewById(R.id.hsv_aspect_ratio_container);
            mLayoutRotate = (ViewGroup) findViewById(R.id.ll_rotation_container);
            mLayoutScale = (ViewGroup) findViewById(R.id.ll_scale_container);

            setupAspectRatioWidget(intent);
            setupRotateWidget();
            setupScaleWidget();
//            setupStatesWrapper();
        }
    }

    /**
     * Configures and styles both status bar and toolbar.
     */
    private void setupAppBar() {
        setStatusBarColor(mStatusBarColor);

        final Toolbar toolbar = (Toolbar) findViewById(com.yalantis.ucrop.R.id.toolbar_custom);

        // Set all of the Toolbar coloring
        toolbar.setBackgroundColor(mToolbarColor);
        toolbar.setTitleTextColor(mToolbarWidgetColor);


        // Color buttons inside the Toolbar
        Drawable stateButtonDrawable = ContextCompat.getDrawable(this, mToolbarCancelDrawable).mutate();
        stateButtonDrawable.setColorFilter(mToolbarWidgetColor, PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(stateButtonDrawable);

        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void initiateRootViews() {
        mUCropView = (UCropView) findViewById(com.yalantis.ucrop.R.id.ucrop_custom);
        mGestureCropImageView = mUCropView.getCropImageView();
        mOverlayView = mUCropView.getOverlayView();

        mGestureCropImageView.setTransformImageListener(mImageListener);

        ((ImageView) findViewById(com.yalantis.ucrop.R.id.image_view_logo_custom)).setColorFilter(mLogoColor, PorterDuff.Mode.SRC_ATOP);

        findViewById(com.yalantis.ucrop.R.id.ucrop_frame_custom).setBackgroundColor(mRootViewBackgroundColor);
    }

    private TransformImageView.TransformImageListener mImageListener = new TransformImageView.TransformImageListener() {
        @Override
        public void onRotate(float currentAngle) {
            setAngleText(currentAngle);
        }

        @Override
        public void onScale(float currentScale) {
            setScaleText(currentScale);
        }

        @Override
        public void onLoadComplete() {
            mUCropView.animate().alpha(1).setDuration(300).setInterpolator(new AccelerateInterpolator());
            mBlockingView.setClickable(false);
            mShowLoader = false;
            supportInvalidateOptionsMenu();
        }

        @Override
        public void onLoadFailure(@NonNull Exception e) {
            setResultError(e);
            finish();
        }

    };

    /**
     * Use {@link #mActiveWidgetColor} for color filter
     */
    private void setupStatesWrapper() {
        ImageView stateScaleImageView = (ImageView) findViewById(com.yalantis.ucrop.R.id.image_view_state_scale);
        ImageView stateRotateImageView = (ImageView) findViewById(com.yalantis.ucrop.R.id.image_view_state_rotate);
        ImageView stateAspectRatioImageView = (ImageView) findViewById(com.yalantis.ucrop.R.id.image_view_state_aspect_ratio);

        stateScaleImageView.setImageDrawable(new SelectedStateListDrawable(stateScaleImageView.getDrawable(), mActiveWidgetColor));
        stateRotateImageView.setImageDrawable(new SelectedStateListDrawable(stateRotateImageView.getDrawable(), mActiveWidgetColor));
        stateAspectRatioImageView.setImageDrawable(new SelectedStateListDrawable(stateAspectRatioImageView.getDrawable(), mActiveWidgetColor));
    }


    /**
     * Sets status-bar color for L devices.
     *
     * @param color - status-bar color
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(color);
            }
        }
    }

    private void setupAspectRatioWidget(@NonNull final Intent intent) {

        int aspectRationSelectedByDefault = intent.getIntExtra(UCrop.Options.EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT, 0);
        ArrayList<AspectRatio> aspectRatioList = intent.getParcelableArrayListExtra(UCrop.Options.EXTRA_ASPECT_RATIO_OPTIONS);
        if (aspectRatioList == null || aspectRatioList.isEmpty()) {
            aspectRationSelectedByDefault = 0;

            aspectRatioList = new ArrayList<>();
            aspectRatioList.add(new AspectRatio(getString(R.string.ucrop_freedom).toUpperCase(),
                    1, 1));
            aspectRatioList.add(new AspectRatio(getString(R.string.ucrop_devices_size).toUpperCase(),
                    ScreenUtil.getScreenSize(UCropCustomActivity.this).widthPixels,
                    ScreenUtil.getScreenSize(UCropCustomActivity.this).heightPixels));
            aspectRatioList.add(new AspectRatio(null, 1, 1));
            aspectRatioList.add(new AspectRatio(null, 2, 3));
            aspectRatioList.add(new AspectRatio(null, 3, 4));
            aspectRatioList.add(new AspectRatio(null, 5, 7));
            aspectRatioList.add(new AspectRatio(null, 9, 16));
        }
        LinearLayout aspectRatioListContainer = (LinearLayout) findViewById(R.id.ll_aspect_ratio_container);
        CustomAspectRatioView aspectRatioView;

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(DisplayUtil.dpTopx(this,60), ViewGroup.LayoutParams.MATCH_PARENT);
//        for (AspectRatio aspectRatio : aspectRatioList) {
//            aspectRatioView = new CustomAspectRatioView(UCropCustomActivity.this);
//            aspectRatioView.setBg();
//            aspectRatioView.setLayoutParams(lp);
//            aspectRatioView.setAspectRatio(aspectRatio);
//            aspectRatioListContainer.addView(aspectRatioView);
//            mCropAspectRatioViews.add(aspectRatioView);
//        }

        for (int i = 0 ; i < aspectRatioList.size(); i ++){
            AspectRatio aspectRatio = aspectRatioList.get(i);
            aspectRatioView = new CustomAspectRatioView(UCropCustomActivity.this);
            int resId = R.drawable.ucrop_aspect_ratio_bg;
            if (i == 0){
                resId = R.drawable.ucrop_free_bg;
            }
            aspectRatioView.setBg(resId);
            aspectRatioView.setLayoutParams(lp);
            aspectRatioView.setAspectRatio(aspectRatio);
            aspectRatioListContainer.addView(aspectRatioView);
            mCropAspectRatioViews.add(aspectRatioView);
        }

        mCropAspectRatioViews.get(aspectRationSelectedByDefault).setSelected(true);

        for (ViewGroup cropAspectRatioView : mCropAspectRatioViews) {
            cropAspectRatioView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isFirstItem = mCropAspectRatioViews.get(0) == v;
                    mGestureCropImageView.setImageToWrapCropBounds();
                    if (isFirstItem){
                        mGestureCropImageView.setTargetAspectRatio(
                                ((CustomAspectRatioView) v).getAspectRatio(false));
                        mOverlayView.setFreestyleCropMode(OverlayView.FREESTYLE_CROP_MODE_ENABLE);
                    }else {
                        mGestureCropImageView.setTargetAspectRatio(
                                ((CustomAspectRatioView) v).getAspectRatio(v.isSelected()));
                        mOverlayView.setFreestyleCropMode(OverlayView.FREESTYLE_CROP_MODE_ENABLE_WITH_ASPECT_RATIO);
                    }
                    if (!v.isSelected()) {
                        for (ViewGroup cropAspectRatioView : mCropAspectRatioViews) {
                            cropAspectRatioView.setSelected(cropAspectRatioView == v);
                        }
                    }
                }
            });
        }
//        Uri inputUri = intent.getParcelableExtra(UCrop.EXTRA_INPUT_URI);
//        Bitmap thumb = getThumbImage(inputUri,200);
//        getThumbImage(inputUri, 200, new BitmapLoadCallback() {
//            @Override
//            public void onBitmapLoaded(@NonNull Bitmap bitmap, @NonNull ExifInfo exifInfo, @NonNull String imageInputPath, @Nullable String imageOutputPath) {
//
//
//            }
//
//            @Override
//            public void onFailure(@NonNull Exception bitmapWorkerException) {
//
//            }
//        });
    }


    private void rotateAspectRatio(){
        for (int i = 1; i < mCropAspectRatioViews.size(); i ++){
            CustomAspectRatioView cropAspectRatioView =
                                    (CustomAspectRatioView) mCropAspectRatioViews.get(i);
            if (cropAspectRatioView.isSelected()){
                mGestureCropImageView.setTargetAspectRatio(
                        cropAspectRatioView.getAspectRatio(cropAspectRatioView.isSelected()));
            }else {
                cropAspectRatioView.getAspectRatio(true);
            }

        }
    }

    private int rotationOldValue ;
    private void setupRotateWidget() {
        SeekBar seekBar = (SeekBar) findViewById(R.id.sb_rotation_seek);
        seekBar.setMax(360);
        seekBar.setProgress(180);
        rotationOldValue = 180;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int delta = progress - rotationOldValue;
                rotationOldValue = progress;
                mGestureCropImageView.postRotate(delta);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mGestureCropImageView.cancelAllAnimations();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mGestureCropImageView.setImageToWrapCropBounds();
            }
        });

        findViewById(R.id.tv_reset_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetRotation();
            }
        });
        findViewById(R.id.tv_rotation_1_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateByAngle(90);
            }
        });
        findViewById(R.id.tv_rotation_2_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateByAngle(-90);
            }
        });
//        findViewById(R.id.tv_filp_hor_action).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                mGestureCropImageView.doReversalHorizontal();
//            }
//        });
//        findViewById(R.id.tv_flip_ver_action).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                mGestureCropImageView.doReversalVertical();
//            }
//        });
    }

    private float currentScaleValue;
    private boolean isScaleSeekTouch = false;
    private final static int SCALE_SEEK_MAX_VALUE_200 = 200;
    private void setupScaleWidget() {
        SeekBar seekBar = (SeekBar) findViewById(R.id.sb_scale_seek);
        seekBar.setMax(SCALE_SEEK_MAX_VALUE_200);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG,"progress:" + progress );
                if (fromUser){
                    float scale = 1f;
                    if (progress > 110){
                        scale = (progress - (SCALE_SEEK_MAX_VALUE_200 / 2f))/10f;
                    }
                    if (progress <= 100){
                        scale = progress * 1f / 100;
                    }
                    Log.d(TAG,"scale:" + scale );
                    mGestureCropImageView.zoomInImage(scale);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isScaleSeekTouch = true;
                mGestureCropImageView.cancelAllAnimations();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isScaleSeekTouch = false;
                int seekValue = (int) currentScaleValue;
                if (currentScaleValue > 100){
                    seekValue =(int)(currentScaleValue / 10 + SCALE_SEEK_MAX_VALUE_200 / 2f) ;
                }
                seekBar.setProgress(seekValue);
                mGestureCropImageView.setImageToWrapCropBounds();
            }
        });
    }

    private void setAngleText(float angle) {

//        ToastUtil.makeText(this,String.format(Locale.getDefault(), "%.1f°", angle));
    }

    private void setScaleText(float scale) {
//        ToastUtil.makeText(this,String.format(Locale.getDefault(), "%d%%", (int) (scale * 100)));
        currentScaleValue = scale * 100;
        if (!isScaleSeekTouch){
            int seekValue = (int) currentScaleValue;
            if (currentScaleValue > 100){
                seekValue =(int)(currentScaleValue / 10 + SCALE_SEEK_MAX_VALUE_200 / 2f) ;
            }
            ((SeekBar) findViewById(R.id.sb_scale_seek)).setProgress(seekValue);
        }
        Log.d(TAG,String.format(Locale.getDefault(), "%d%%", (int) currentScaleValue));
    }

    private void resetRotation() {
        mGestureCropImageView.postRotate(-mGestureCropImageView.getCurrentAngle());
        mGestureCropImageView.setImageToWrapCropBounds();
    }

    private void rotateByAngle(int angle) {
        mGestureCropImageView.postRotate(angle);
        mGestureCropImageView.setImageToWrapCropBounds();
    }

    private final View.OnClickListener mStateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!v.isSelected()) {
                setWidgetState(v.getId());
            }
        }
    };

    private void setInitialState() {
        if (mShowBottomControls) {
            if (mStateAspectRatioTv.getVisibility() == View.VISIBLE) {
                setWidgetState(R.id.tv_aspect_ratio_status);
            } else {
                setWidgetState(R.id.tv_scale_status);
            }
        } else {
            setAllowedGestures(0);
        }
    }

    private int getSelectedColor(boolean selected){
        int selectedColor = getResources().getColor(R.color.ucrop_color_selected_text_color);
        int normalColor = getResources().getColor(R.color.ucrop_color_normal_text_color);
        return selected ? selectedColor : normalColor;
    }

    private void setWidgetState(@IdRes int stateViewId) {
        if (!mShowBottomControls) return;

        mStateAspectRatioTv.setSelected(stateViewId == R.id.tv_aspect_ratio_status);
        mStateRotateTv.setSelected(stateViewId == R.id.tv_rotation_status);
        mStateScaleTv.setSelected(stateViewId == R.id.tv_scale_status);

        mStateAspectRatioTv.setTextColor(getSelectedColor(mStateAspectRatioTv.isSelected()));
        mStateRotateTv.setTextColor(getSelectedColor(mStateRotateTv.isSelected()));
        mStateScaleTv.setTextColor(getSelectedColor(mStateScaleTv.isSelected()));



        mLayoutAspectRatio.setVisibility(stateViewId == R.id.tv_aspect_ratio_status ? View.VISIBLE : View.GONE);
        mLayoutRotate.setVisibility(stateViewId == R.id.tv_rotation_status ? View.VISIBLE : View.GONE);
        mLayoutScale.setVisibility(stateViewId == R.id.tv_scale_status ? View.VISIBLE : View.GONE);

        if (stateViewId == R.id.tv_scale_status) {
            setAllowedGestures(0);
        } else if (stateViewId == R.id.tv_rotation_status) {
            setAllowedGestures(1);
        } else {
            setAllowedGestures(2);
        }
    }

    private void setAllowedGestures(int tab) {
        mGestureCropImageView.setScaleEnabled(mAllowedGestures[tab] == ALL || mAllowedGestures[tab] == SCALE);
        mGestureCropImageView.setRotateEnabled(mAllowedGestures[tab] == ALL || mAllowedGestures[tab] == ROTATE);
    }

    /**
     * Adds view that covers everything below the Toolbar.
     * When it's clickable - user won't be able to click/touch anything below the Toolbar.
     * Need to block user input while loading and cropping an image.
     */
    private void addBlockingView() {
        if (mBlockingView == null) {
            mBlockingView = new View(this);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.addRule(RelativeLayout.BELOW, com.yalantis.ucrop.R.id.toolbar_custom);
            mBlockingView.setLayoutParams(lp);
            mBlockingView.setClickable(true);
        }

        ((RelativeLayout) findViewById(com.yalantis.ucrop.R.id.ucrop_photobox_custom)).addView(mBlockingView);
    }

    protected void cropAndSaveImage() {
        mBlockingView.setClickable(true);
        mShowLoader = true;
        supportInvalidateOptionsMenu();

        mGestureCropImageView.cropAndSaveImage(mCompressFormat, mCompressQuality, new BitmapCropCallback() {

            @Override
            public void onBitmapCropped(@NonNull Uri resultUri, int offsetX, int offsetY, int imageWidth, int imageHeight) {
                setResultUri(resultUri, mGestureCropImageView.getTargetAspectRatio(), offsetX, offsetY, imageWidth, imageHeight);
                finish();
            }

            @Override
            public void onCropFailure(@NonNull Throwable t) {
                setResultError(t);
                finish();
            }
        });
    }

    protected void setResultUri(Uri uri, float resultAspectRatio, int offsetX, int offsetY, int imageWidth, int imageHeight) {
        setResult(RESULT_OK, new Intent()
                .putExtra(UCrop.EXTRA_OUTPUT_URI, uri)
                .putExtra(UCrop.EXTRA_OUTPUT_CROP_ASPECT_RATIO, resultAspectRatio)
                .putExtra(UCrop.EXTRA_OUTPUT_IMAGE_WIDTH, imageWidth)
                .putExtra(UCrop.EXTRA_OUTPUT_IMAGE_HEIGHT, imageHeight)
                .putExtra(UCrop.EXTRA_OUTPUT_OFFSET_X, offsetX)
                .putExtra(UCrop.EXTRA_OUTPUT_OFFSET_Y, offsetY)
        );
    }

    protected void setResultError(Throwable throwable) {
        setResult(UCrop.RESULT_ERROR, new Intent().putExtra(UCrop.EXTRA_ERROR, throwable));
    }

    private Bitmap getThumbImage(Uri inputUri,int maxSize){
        String imagePath = FileUtils.getPath(this, inputUri);
        return BitmapLoadUtils.decodeFileMaxSize(imagePath,maxSize);
    }
    private void getThumbImage(Uri inputUri,int maxSize,BitmapLoadCallback loadCallback){
        Uri outputUri = Uri.fromFile(new File(getCacheDir(),"thumb.jpg"));
        BitmapLoadUtils.decodeBitmapInBackground(this, inputUri, outputUri, maxSize, maxSize,
                loadCallback);
    }
    public static  String getImagePath(Context context, Uri imageUri){
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(imageUri, filePathColumn,
                null, null, null);
        cursor.moveToFirst();
        int colIndex = cursor.getColumnIndex(filePathColumn[0]);
        String imagePath = cursor.getString(colIndex);
        cursor.close();
        return imagePath;
    }

}
