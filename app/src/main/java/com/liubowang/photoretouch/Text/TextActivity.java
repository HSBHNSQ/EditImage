package com.liubowang.photoretouch.Text;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.Image;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.liubowang.photoretouch.Base.EIBaseActiviry;
import com.liubowang.photoretouch.FileBrowse.ImageLookActivity;
import com.liubowang.photoretouch.Main.MainActivity;
import com.liubowang.photoretouch.Normal.ImageTextButton;
import com.liubowang.photoretouch.R;
import com.liubowang.photoretouch.Style.OnAutofitTextViewClickListener;
import com.liubowang.photoretouch.Style.TextStyle;
import com.liubowang.photoretouch.Utils.BitmpUtil;
import com.liubowang.photoretouch.Utils.DisplayUtil;
import com.liubowang.photoretouch.Utils.FileUtil;
import com.liubowang.photoretouch.Utils.Size;
import com.liubowang.photoretouch.Utils.ViewTransformUtil;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.callback.BitmapLoadCallback;
import com.yalantis.ucrop.model.ExifInfo;
import com.yalantis.ucrop.util.BitmapLoadUtils;

import java.io.File;

public class TextActivity extends EIBaseActiviry {

    private static final String TAG = TextActivity.class.getSimpleName();
    private String imagePath;
    private ImageView originalImageView;
    private ViewGroup containerView;
    private ConstraintLayout rootView;
    private ProgressBar progressBar;
    private Bitmap originalBmp;
    private ViewTransformUtil imageViewTransformUtil;
    private ViewTransformUtil textViewTransformUtil;
    private RecyclerView textTypeRecycleView;
    private RecyclerView colorRecycleView;
    private TextColorAdatper textColorAdatper;
    private TextTypeAdapter textTypeAdapter;
    private ImageTextButton styleImageButton;
    private ImageTextButton colorImageBUtton;
    private TextView changeColorTv;
    private SeekBar alphaSeekBar;
    private TextStyle textStyle;
    private RectF imageViewRect;
    private boolean isContainerSizeOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        initUI();
        containerView.post(new Runnable() {
            @Override
            public void run() {
                isContainerSizeOk = true;
                if (originalBmp != null){
                    setupImageView();
                }
            }
        });
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("IMAGE_PATH")){
            imagePath = intent.getStringExtra("IMAGE_PATH");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = BitmpUtil.getSuitableBitmapFromAlbum(TextActivity.this,imagePath);
                    if (bitmap == null){
                        bitmap = BitmapFactory.decodeFile(imagePath);
                    }
                    originalBmp = BitmpUtil.getProperResizedImage(bitmap,1024);
                    imagePath = FileUtil.getCurrentTimeMillisPath("png");
                    FileUtil.writeBitmap(new File(imagePath),originalBmp);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isContainerSizeOk){
                                setupImageView();
                            }
                        }
                    });

                }
            }).start();
        }
        else if (intent != null && intent.hasExtra("IMAGE_URI")){
            Uri imageUri = intent.getParcelableExtra("IMAGE_URI");
            BitmapLoadUtils.decodeBitmapInBackground(this, imageUri, imageUri, 1024, 1024,
                    new BitmapLoadCallback() {

                        @Override
                        public void onBitmapLoaded(@NonNull Bitmap bitmap, @NonNull ExifInfo exifInfo, @NonNull String imageInputPath, @Nullable String imageOutputPath) {
                            originalBmp = bitmap;
                            imagePath = imageOutputPath;
                            if (isContainerSizeOk){
                                setupImageView();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Exception bitmapWorkerException) {

                        }
                    });
        }
    }

    private void initUI(){
        originalImageView = (ImageView) findViewById(R.id.iv_original_image_text);
        rootView = (ConstraintLayout) findViewById(R.id.root_view_text);
        containerView = (ViewGroup) findViewById(R.id.cl_container_text);
        containerView.setOnTouchListener(touchListener);
        progressBar = (ProgressBar) findViewById(R.id.pb_progress_text);

        textTypeRecycleView = (RecyclerView) findViewById(R.id.rv_text_type_recycle_view_text);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        textTypeRecycleView.setLayoutManager(manager);
        textTypeAdapter = new TextTypeAdapter(this);
        textTypeAdapter.setTextTypeListener(textTypeListener);
        textTypeRecycleView.setAdapter(textTypeAdapter);

        colorRecycleView = (RecyclerView) findViewById(R.id.rv_color_recycle_view_text);
        LinearLayoutManager colorManager = new LinearLayoutManager(this);
        colorManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        colorRecycleView.setLayoutManager(colorManager);
        textColorAdatper = new TextColorAdatper();
        textColorAdatper.setTextColorListener(textColorListener);
        colorRecycleView.setAdapter(textColorAdatper);
        colorRecycleView.setVisibility(View.INVISIBLE);

        styleImageButton = (ImageTextButton) findViewById(R.id.itb_style_text);
        styleImageButton.setOnClickListener(buttonClickListener);
        colorImageBUtton = (ImageTextButton) findViewById(R.id.itb_color_text);
        colorImageBUtton.setOnClickListener(buttonClickListener);

        changeColorTv = (TextView) findViewById(R.id.tv_change_color_text);
        changeColorTv.setOnClickListener(buttonClickListener);
        changeColorTv.setVisibility(View.GONE);
        alphaSeekBar = (SeekBar) findViewById(R.id.sb_alpha_text);
        alphaSeekBar.setMax(100);
        alphaSeekBar.setProgress(100);
        alphaSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        ImageButton save = (ImageButton) findViewById(R.id.ib_save_text);
        save.setOnClickListener(buttonClickListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupImageView(){
        imageViewTransformUtil = new ViewTransformUtil(originalImageView);
        originalImageView.setImageBitmap(originalBmp);
        progressBar.setVisibility(View.INVISIBLE);
        RectF rectF = getImagetViewSize(originalBmp);
        ((FrameLayout.LayoutParams)originalImageView.getLayoutParams()).width = (int) rectF.right;
        ((FrameLayout.LayoutParams)originalImageView.getLayoutParams()).height = (int) rectF.bottom;
        ((FrameLayout.LayoutParams)originalImageView.getLayoutParams()).leftMargin = (int) rectF.left;
        ((FrameLayout.LayoutParams)originalImageView.getLayoutParams()).topMargin = (int) rectF.top;
        imageViewRect = rectF;
        originalImageView.requestLayout();
    }
    private RectF getImagetViewSize(Bitmap bitmap){
        float bmpW = bitmap.getWidth();
        float bmpH = bitmap.getHeight();
        float containerW = containerView.getWidth();
        float containerH = containerView.getHeight();
        float bmpScale = bmpW / bmpH;
        float rootScale = containerW / containerH;
        int viewHeight;
        int viewWidth;
        if (bmpScale < rootScale){//root的高度作为标准
            int dstH = 1500;
            if (containerH < dstH){
                viewHeight = (int) containerH;
                viewWidth = (int) (containerH * bmpScale);
            }else {
                viewHeight = (int) dstH;
                viewWidth = (int) (dstH * bmpScale);
            }
        }else {
            int dstW = 1000;
            if (containerW < dstW){
                viewWidth = (int) containerW;
                viewHeight = (int)(containerW / bmpScale);
            }else {
                viewWidth = (int) dstW;
                viewHeight = (int)(dstW / bmpScale);
            }
        }
        int left =(int) ((containerW - viewWidth) / 2);
        int top = (int) ((containerH - viewHeight) / 2);
//        return new Rect(left,top,left + viewWidth,top + viewHeight);
        return new RectF(left,top,viewWidth,viewHeight);
    }


    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            float alpha = progress / 100f;
            originalImageView.setAlpha(alpha);
            if (textStyle != null){
                textStyle.setAlpha(alpha);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == styleImageButton || v == colorImageBUtton){
                styleImageButton.setSelected(v == styleImageButton);
                textTypeRecycleView.setVisibility(v == styleImageButton ? View.VISIBLE:View.INVISIBLE);
                colorImageBUtton.setSelected(v == colorImageBUtton);
                colorRecycleView.setVisibility(v == colorImageBUtton ? View.VISIBLE:View.INVISIBLE);
                changeColorTv.setVisibility(v == colorImageBUtton ? View.VISIBLE:View.GONE);
            }
            else if (v == changeColorTv){
                textColorAdatper.changeColor();
            }else if (v.getId() == R.id.ib_save_text){
                saveClick();
            }
        }
    };


    private void saveClick(){
        progressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap viewBmp = Bitmap.createBitmap(containerView.getWidth(),containerView.getHeight(), Bitmap.Config.ARGB_4444);
                Canvas canvas = new Canvas(viewBmp);
                containerView.draw(canvas);
                final File file = new File(FileUtil.getCurrentTimeMillisPath(".jpg"));
                FileUtil.writeBitmap(file,viewBmp);
                viewBmp.recycle();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                        startCropActivity(file);
                    }
                });
            }
        }).start();
    }

    private void startCropActivity(@NonNull File imageFile) {
        String outPutFilePath = FileUtil.getPictureResultPathWithName(System.currentTimeMillis()+"","jpg");
        UCrop uCrop = UCrop.of(Uri.fromFile(imageFile),Uri.fromFile(new File(outPutFilePath)));
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(100);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);
        uCrop.withOptions(options);
        uCrop.startCustom(TextActivity.this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UCrop.REQUEST_CROP:
                    Uri resultUri = UCrop.getOutput(data);
                    if (resultUri != null) {
                        Intent imageLook = new Intent(TextActivity.this, ImageLookActivity.class);
                        imageLook.putExtra("IMAGE_URI",resultUri);
                        Toast.makeText(this, resultUri.getPath(), Toast.LENGTH_LONG).show();
                        if (imageLook.resolveActivity(getPackageManager()) != null){
                            startActivity(imageLook);
                        }
                    } else {
                        Toast.makeText(TextActivity.this, "失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }else {
            Toast.makeText(TextActivity.this, "失败", Toast.LENGTH_SHORT).show();
        }
    }

    private ViewTransformUtil currentTranfonmUtil;
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (v == containerView){

                if (event.getActionMasked() == MotionEvent.ACTION_DOWN){
                    boolean isTouchTextStyle = false;
                    if (textStyle != null){
                        RectF currentTextStyleRect = new RectF(textStyle.getLeft(),
                                textStyle.getTop(),
                                textStyle.getRight(),
                                textStyle.getBottom());;
                        float localX = event.getX();
                        float localY = event.getY();
                        isTouchTextStyle = currentTextStyleRect.contains(localX,localY);
                    }
                    if (isTouchTextStyle){
                        currentTranfonmUtil = textViewTransformUtil;
                        textViewTransformUtil.onTouch(event);
                    }else{
                        currentTranfonmUtil = imageViewTransformUtil;
                        imageViewTransformUtil.onTouch(event);
                    }
                }else {
                    currentTranfonmUtil.onTouch(event);
                }


                return true;
            }
            return false;
        }
    };


    private boolean hasChangeColorP = false;
    private TextColorAdatper.OnTextColorListener textColorListener = new TextColorAdatper.OnTextColorListener() {
        @Override
        public void onColorClcik(TextColorModel model) {
            Log.d(TAG,model.toString());
            if (textStyle != null){
                textStyle.setMainColor(model.mainColor);
                textStyle.setSecondColor(model.secondColor);
                if (!hasChangeColorP){
                    hasChangeColorP = true;
                    Toast.makeText(TextActivity.this, getString(R.string.ei_change_color_p), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private TextTypeAdapter.OnTextTypeListener textTypeListener = new TextTypeAdapter.OnTextTypeListener() {
        @Override
        public void onTextTypeClick(TextTypeModel model) {
           Log.d(TAG,model.toString());
           if (textStyle != null){
               containerView.removeView(textStyle);
           }
           textStyle =  TextStyleFactory.getStyleWithModel(model,TextActivity.this);
           textStyle.setOnAutofitTextViewClickListener(onAutofitTextViewClickListener);
           textViewTransformUtil = new ViewTransformUtil(textStyle);
           TextStyleFactory.addStyleToView(containerView,textStyle,imageViewRect);
        }
    };

    private OnAutofitTextViewClickListener onAutofitTextViewClickListener = new OnAutofitTextViewClickListener() {
        @Override
        public void onAutofitTextClick(final AutofitTextView autofitTextView) {
            LinearLayout linearLayout = new LinearLayout(TextActivity.this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.leftMargin = DisplayUtil.dpTopx(TextActivity.this,12);
            layoutParams.rightMargin = DisplayUtil.dpTopx(TextActivity.this,12);
            final EditText editText = new EditText(TextActivity.this);
            linearLayout.addView(editText,layoutParams);
            AlertDialog dialog = new AlertDialog.Builder(TextActivity.this)
                    .setMessage(getString(R.string.ei_plz_input_text))
                    .setView(linearLayout)
                    .setPositiveButton(getString(R.string.sure),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    autofitTextView.setText(editText.getText().toString());
                                }
                            })
                    .setNegativeButton(getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create();
            dialog.show();
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 计算指定的 View 在屏幕中的坐标。
     */
    public static RectF calcViewScreenLocation(View view) {
        if (view == null) return new RectF(0,0,0,0);
        int[] location = new int[2];
        // 获取控件在屏幕中的位置，返回的数组分别为控件左顶点的 x、y 的值
//        view.getLocationOnScreen(location);
        view.getLocationInWindow(location);
        return new RectF(location[0], location[1], location[0] + view.getWidth(),
                location[1] + view.getHeight());
    }
    private LinearLayout bannerViewContainer;

    @Override
    protected ViewGroup getBannerViewContainer() {
        if (bannerViewContainer == null) {
            bannerViewContainer = (LinearLayout) findViewById(R.id.ll_banner_container_text);
        }
        return bannerViewContainer;
    }

}
