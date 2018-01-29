package com.liubowang.photoretouch.Adjust;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.liubowang.photoretouch.Base.EIBaseActiviry;
import com.liubowang.photoretouch.Effect.SaveBitmapDialog;
import com.liubowang.photoretouch.FileBrowse.ImageLookActivity;
import com.liubowang.photoretouch.Normal.ImageTextButton;
import com.liubowang.photoretouch.Normal.NormalActivity;
import com.liubowang.photoretouch.Normal.TopToolView;
import com.liubowang.photoretouch.R;
import com.liubowang.photoretouch.Utils.BitmpUtil;
import com.liubowang.photoretouch.Utils.DisplayUtil;
import com.liubowang.photoretouch.Utils.FileUtil;
import com.liubowang.photoretouch.Utils.ScreenUtil;
import com.liubowang.photoretouch.Utils.Size;
import com.yalantis.ucrop.callback.BitmapLoadCallback;
import com.yalantis.ucrop.model.ExifInfo;
import com.yalantis.ucrop.util.BitmapLoadUtils;

import java.io.File;

public class SmallAdjustActivity extends EIBaseActiviry {

    private static String TAG = "SmallAdjustActivity";
    private static final int STATUS_FACE_THIN = 667;
    private static final int STATUS_BIG_EYE = 417;
    private static final int SEEK_MAX_VALUE = 120;
    private String mImagePath;
    private ConstraintLayout rootView;
    private ConstraintLayout containerView;
    private AdjustView adjustView;
    private TopToolView topToolView;
    private ImageTextButton faceThinButon;
    private ImageTextButton bigEyeButton;
    private ProgressBar progressBar;
    private SeekBar circleRadiusSeekBar;
    private ToastCircleView circleView;
    private ActionTextView compareTv;
    private ImageView originIV;
    private int currentStatus = STATUS_BIG_EYE;
    private boolean bigEyeHasP = false;
    private boolean faceThinHasP = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_small_adjust);
        initUI();
        Intent intent = getIntent();
        if (intent.hasExtra("IMAGE_PATH")){
            mImagePath = intent.getStringExtra("IMAGE_PATH");
            setupImageView();
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmpUtil.getSuitableBitmapFromAlbum(SmallAdjustActivity.this,mImagePath);
                if (bitmap == null){
                    bitmap = BitmapFactory.decodeFile(mImagePath);
                }
                bitmap = BitmpUtil.getProperResizedImage(bitmap,1000);
                mImagePath = FileUtil.getCurrentTimeMillisPath("png");
                FileUtil.writeBitmap(new File(mImagePath),bitmap);
                final Bitmap finalBitmap = bitmap;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rootView.post(new Runnable() {
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

    private void initUI(){
        rootView = (ConstraintLayout) findViewById(R.id.root_adjust);
        containerView = (ConstraintLayout) findViewById(R.id.cl_container_adjust);
        topToolView = (TopToolView) findViewById(R.id.ttl_top_tool_adjust);
        topToolView.setActionListener(topActionListener);
        adjustView = (AdjustView) findViewById(R.id.av_adjust_view);
        faceThinButon = (ImageTextButton) findViewById(R.id.itb_face_thin_adjust);
        bigEyeButton = (ImageTextButton) findViewById(R.id.itb_big_eye_adjust);
        faceThinButon.setOnClickListener(buttonListener);
        bigEyeButton.setOnClickListener(buttonListener);
        progressBar = (ProgressBar) findViewById(R.id.pb_progress_adjust);
        circleRadiusSeekBar = (SeekBar) findViewById(R.id.sb_circle_adjust);
        circleRadiusSeekBar.setMax(SEEK_MAX_VALUE);
        circleRadiusSeekBar.setProgress(SEEK_MAX_VALUE/2);
        circleRadiusSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        adjustView.setRadius(SEEK_MAX_VALUE/2);
        circleView = (ToastCircleView) findViewById(R.id.tcv_circle_adjust);
        circleView.setRadius(SEEK_MAX_VALUE/2);
        circleView.setVisibility(View.INVISIBLE);
        compareTv = (ActionTextView) findViewById(R.id.atv_comparable_adjust);
        compareTv.setTextViewActionListener(textViewActionListener);
        originIV = (ImageView) findViewById(R.id.iv_ori_image_view_adjust);
    }

    private void setViewSize(Bitmap bitmap){
        float bmpW = bitmap.getWidth();
        float bmpH = bitmap.getHeight();
        float rootW = rootView.getWidth() - DisplayUtil.dpTopx(this,8) * 2;
        float rootH = rootView.getHeight() - topToolView.getBottom() - DisplayUtil.dpTopx(this,120);
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
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)containerView.getLayoutParams();
        layoutParams.width = viewWidth;
        layoutParams.height = viewHeight;
        containerView.setLayoutParams(layoutParams);
        adjustView.setImageBitmap(bitmap);
        originIV.setImageBitmap(bitmap);
        progressBar.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!bigEyeHasP){
            bigEyeHasP = true;
            Toast.makeText(this, getString(R.string.ei_big_eye_p), Toast.LENGTH_SHORT).show();
        }
    }


    //---------------all listener --------------

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            circleView.setRadius(progress);
            adjustView.setRadius(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            circleView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            circleView.setVisibility(View.INVISIBLE);
        }
    };


    private View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v == faceThinButon){
                currentStatus = STATUS_FACE_THIN;
                adjustView.setMeshType(AdjustView.MESH_TYPE_FACE_THIN);
                if (!faceThinHasP){
                    faceThinHasP = true;
                    Toast.makeText(SmallAdjustActivity.this, getString(R.string.ei_face_thin_p), Toast.LENGTH_SHORT).show();
                }
            }
            else if (v == bigEyeButton){
                currentStatus = STATUS_BIG_EYE;
                adjustView.setMeshType(AdjustView.MESH_TYPE_BIG_EYE);
                if (!bigEyeHasP){
                    bigEyeHasP = true;
                    Toast.makeText(SmallAdjustActivity.this, getString(R.string.ei_big_eye_p), Toast.LENGTH_SHORT).show();
                }
            }
            if (v == faceThinButon || v == bigEyeButton){
                faceThinButon.setSelected(v == faceThinButon);
                bigEyeButton.setSelected(v == bigEyeButton);
            }
        }
    };

    private ActionTextView.OnTextViewActionListener textViewActionListener = new ActionTextView.OnTextViewActionListener() {
        @Override
        public void onTouchDown() {
            adjustView.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onTouchUp() {
            adjustView.setVisibility(View.VISIBLE);
        }
    };

    private TopToolView.OnTopActionListener topActionListener = new TopToolView.OnTopActionListener() {
        @Override
        public void onBackClick() {
            finish();
        }

        @Override
        public void onUndoClick() {
            adjustView.undo();
        }

        @Override
        public void onRedoClick() {
            adjustView.redo();
        }

        @Override
        public void onResetClick() {
            adjustView.resetView();
        }

        @Override
        public void onSaveClick() {
            progressBar.setVisibility(View.VISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Bitmap result = adjustView.getResultBmp();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.INVISIBLE);
                            final SaveBitmapDialog saveBitmapDialog = SaveBitmapDialog.newInstance();
                            saveBitmapDialog.setPreviewBitmap(result);
                            saveBitmapDialog.setAppName(getString(R.string.app_name));
                            saveBitmapDialog.setOnSaveBitmapListener(new SaveBitmapDialog.OnSaveBitmapListener() {
                                @Override
                                public void onStartSave() {
                                    progressBar.setVisibility(View.VISIBLE);
                                }
                                @Override
                                public void onSaveBitmapCompleted(final boolean success, final String bmpPath) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            saveBitmapDialog.dismiss();
                                            progressBar.setVisibility(View.INVISIBLE);
                                            String message = getString(R.string.ei_save_failed);
                                            if (success){
                                                message = getString(R.string.ei_save_successful);
                                            }
                                            if (success){
                                                Toast.makeText(SmallAdjustActivity.this,message+":"+bmpPath, Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(SmallAdjustActivity.this, ImageLookActivity.class);
                                                intent.putExtra("IMAGE_PATH",bmpPath);
                                                if (intent.resolveActivity(getPackageManager()) != null){
                                                    startActivity(intent);
                                                }
                                            }else {
                                                Toast.makeText(SmallAdjustActivity.this,message, Toast.LENGTH_SHORT).show();
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
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private LinearLayout bannerViewContainer;

    @Override
    protected ViewGroup getBannerViewContainer() {
        if (bannerViewContainer == null) {
            bannerViewContainer = (LinearLayout) findViewById(R.id.ll_banner_container_adjust);
        }
        return bannerViewContainer;
    }
}
