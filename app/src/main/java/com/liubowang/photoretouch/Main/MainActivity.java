package com.liubowang.photoretouch.Main;

/**
 * Created by jiazhiguo on 2017/6/8.
 */

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lafonapps.common.Common;
import com.lafonapps.common.ad.adapter.BannerViewAdapter;
import com.liubowang.photoretouch.Adjust.SmallAdjustActivity;
import com.liubowang.photoretouch.Base.EIBaseActiviry;
import com.liubowang.photoretouch.Effect.EffectActivity;
import com.liubowang.photoretouch.Feedback.FeedBackActicity;
import com.liubowang.photoretouch.FileBrowse.FilesActivity;
import com.liubowang.photoretouch.FileBrowse.ImageLookActivity;
import com.liubowang.photoretouch.Normal.ImageTextButton;
import com.liubowang.photoretouch.Normal.NormalActivity;
import com.liubowang.photoretouch.R;
import com.liubowang.photoretouch.Template.EffectTemplateActivity;
import com.liubowang.photoretouch.Text.TextActivity;
import com.liubowang.photoretouch.Utils.FileUtil;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends EIBaseActiviry {

    private final String TAG = getClass().getSimpleName();
    private static final int REQUEST_OPEN_IMAGE = 1;
    private static final int STATUS_NORMAL_PICKER_IMAGE = 554;
    private static final int STATUS_CROP_PICKER_IMAGE = 400;
    private static final int STATUS_ADJUST_PICKER_IMAGE = 335;
    private static final int STATUS_TEXT_PICKER_IMAGE = 334;
    private static final int STATUS_EFFECT_PICKET_IMAGE = 687;
    private ImageTextButton mNormalButton;
    private ImageTextButton mEffectButton;
    private ImageTextButton mMyFilesButton;
    private ImageTextButton mCropButton;
    private ImageTextButton mSmallAdjust;
    private ImageTextButton mTextButton;
    private ImageTextButton mCommonButton;
    private int mPickerStatus;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        FileUtil.removeTmpFiles();
    }

    private void initUI(){
        setStatusBar(null);
        mNormalButton = (ImageTextButton) findViewById(R.id.itv_normal_mian);
        mEffectButton = (ImageTextButton) findViewById(R.id.itv_effect_main);
        mMyFilesButton = (ImageTextButton) findViewById(R.id.itv_my_photots_main);
        mCropButton = (ImageTextButton) findViewById(R.id.itv_crop_main);
        mSmallAdjust = (ImageTextButton) findViewById(R.id.itv_small_adjust_main);
        mTextButton = (ImageTextButton) findViewById(R.id.itv_text_main);
        mCommonButton = (ImageTextButton) findViewById(R.id.itv_common_main);
        mNormalButton.setOnClickListener(mButtonListener);
        mEffectButton.setOnClickListener(mButtonListener);
        mMyFilesButton.setOnClickListener(mButtonListener);
        mCropButton.setOnClickListener(mButtonListener);
        mSmallAdjust.setOnClickListener(mButtonListener);
        mTextButton.setOnClickListener(mButtonListener);
        mCommonButton.setOnClickListener(mButtonListener);
    }


    private View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.itv_normal_mian:
                    mPickerStatus = STATUS_NORMAL_PICKER_IMAGE;
                    requestSDCardPermission();
                    break;
                case R.id.itv_effect_main:
                    mPickerStatus = STATUS_EFFECT_PICKET_IMAGE;
                    requestSDCardPermission();
                    break;
                case R.id.itv_my_photots_main:
                    Intent filesIntent = new Intent(MainActivity.this,FilesActivity.class);
                    if (filesIntent.resolveActivity(getPackageManager()) != null){
                        startActivity(filesIntent);
                    }else {
                        Log.d(TAG,"Intent start failed");
                    }
                    break;
                case R.id.itv_common_main:
                    Intent set = new Intent(MainActivity.this,FeedBackActicity.class);
                    if (set.resolveActivity(getPackageManager()) != null){
                        startActivity(set);
                    }else {
                        Log.d(TAG,"Intent start failed");
                    }
                    break;
                case R.id.itv_crop_main:
                    mPickerStatus = STATUS_CROP_PICKER_IMAGE;
                    requestSDCardPermission();
                    break;
                case R.id.itv_small_adjust_main:
                    mPickerStatus = STATUS_ADJUST_PICKER_IMAGE;
                    requestSDCardPermission();
                    break;
                case R.id.itv_text_main:
                    mPickerStatus = STATUS_TEXT_PICKER_IMAGE;
                    requestSDCardPermission();
                    break;

            }
        }
    };

    @Override
    public void permissionSDCardAllow() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_OPEN_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_OPEN_IMAGE:
                    Uri imgUri = data.getData();
                    if (imgUri == null) return;
                    startCropActivity(imgUri);
                    break;
                case UCrop.REQUEST_CROP:
                    handleCropResult(data);
                    break;
            }
        }else {
            Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void startImageLookActivity(Uri imageUri){
        if (imageUri != null) {
            Intent imageLook = new Intent(MainActivity.this, ImageLookActivity.class);
            imageLook.putExtra("IMAGE_URI",imageUri);
            if (imageLook.resolveActivity(getPackageManager()) != null){
                startActivity(imageLook);
            }
        } else {
            Toast.makeText(MainActivity.this, "失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void startEffectActivity(Uri imageUri){
        Intent intent = new Intent(MainActivity.this, EffectActivity.class);
        intent.putExtra("IMAGE_URI",imageUri);
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
    }
    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (STATUS_NORMAL_PICKER_IMAGE == mPickerStatus) {
            startNormalActicity(resultUri);
        } else if (STATUS_CROP_PICKER_IMAGE == mPickerStatus) {
            Toast.makeText(this, resultUri.getPath(), Toast.LENGTH_LONG).show();
            startImageLookActivity(resultUri);
        }else if (STATUS_ADJUST_PICKER_IMAGE == mPickerStatus){
            startSmallAdjustActivity(resultUri);
        }else if (STATUS_TEXT_PICKER_IMAGE == mPickerStatus){
            startTextActivity(resultUri);
        }else if (STATUS_EFFECT_PICKET_IMAGE == mPickerStatus){
            startEffectActivity(resultUri);
        }
    }
    private void startCropActivity(@NonNull Uri uri) {
         String outPutFilePath = FileUtil.getTmpPath() + System.currentTimeMillis()+""+".jpg";
        if (STATUS_CROP_PICKER_IMAGE == mPickerStatus){
            outPutFilePath = FileUtil.getPictureResultPathWithName(System.currentTimeMillis()+"","jpg");
        }
        UCrop uCrop = UCrop.of(uri,Uri.fromFile(new File(outPutFilePath)));
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(100);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);
        uCrop.withOptions(options);
        uCrop.startCustom(MainActivity.this);
    }
    private void startNormalActicity(Uri imageUri){
        Intent intent = new Intent(MainActivity.this,NormalActivity.class);
        intent.putExtra("IMAGE_URI",imageUri);
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }else {
            Log.d(TAG,"Intent start failed");
        }
    }

    private void startSmallAdjustActivity(Uri imageUri){
        Intent intent = new Intent(MainActivity.this,SmallAdjustActivity.class);
        intent.putExtra("IMAGE_URI",imageUri);
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }else {
            Log.d(TAG,"Intent start failed");
        }
    }

    private void startTextActivity(Uri imageUri){
        Intent intent = new Intent(MainActivity.this,TextActivity.class);
        intent.putExtra("IMAGE_URI",imageUri);
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }else {
            Log.d(TAG,"Intent start failed");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }
    private LinearLayout bannerViewContainer;
    @Override
    protected ViewGroup getBannerViewContainer() {
        if (bannerViewContainer == null){
            bannerViewContainer = (LinearLayout) findViewById(R.id.ll_banner_container_main);
        }
        return bannerViewContainer;
    }

    //-------------以下评分的方法---------------------------------
    private boolean mIsExit;
    private boolean isPingfen;
    /**
     * 弹出评分窗口,请求评分    类方法
     */
    public static void scoreView(final Context context){
        new AlertDialog.Builder(context).setTitle("☺☺☺☺☺")//设置对话框标题

                .setMessage(context.getString(com.lafonapps.common.R.string.score_app))//设置显示的内容

                .setPositiveButton(context.getString(com.lafonapps.common.R.string.hard_top_finish),new DialogInterface.OnClickListener() {//添加确定按钮



                    @Override

                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                        // TODO Auto-generated method stub
                        if (hasAnyMarketInstalled(context)){
                            Uri uri = Uri.parse("market://details?id="+context.getPackageName());
                            Intent intentpf = new Intent(Intent.ACTION_VIEW,uri);
                            intentpf.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intentpf);
                        }else {
                            Toast.makeText(context, context.getString(com.lafonapps.common.R.string.toast), Toast.LENGTH_SHORT).show();
                        }

                    }

                }).setNegativeButton(context.getString(com.lafonapps.common.R.string.hard_top_cancel),new DialogInterface.OnClickListener() {//添加返回按钮



            @Override

            public void onClick(DialogInterface dialog, int which) {//响应事件

                // TODO Auto-generated method stub



            }

        }).show();//在按键响应事件中显示此对话框
    }

    /**
     * 弹出评分窗口,请求评分    对象方法
     */
    public void scoreView(){

        new AlertDialog.Builder(MainActivity.this).setTitle("☺☺☺☺☺")//设置对话框标题

                .setMessage(this.getString(com.lafonapps.common.R.string.score_app))//设置显示的内容

                .setPositiveButton(this.getString(com.lafonapps.common.R.string.hard_top_finish),new DialogInterface.OnClickListener() {//添加确定按钮



                    @Override

                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                        // TODO Auto-generated method stub
                        if (hasAnyMarketInstalled(MainActivity.this)){
                            Uri uri = Uri.parse("market://details?id="+getPackageName());
                            Intent intentpf = new Intent(Intent.ACTION_VIEW,uri);
                            intentpf.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intentpf);
                        }else {
                            Toast.makeText(MainActivity.this, getString(com.lafonapps.common.R.string.toast), Toast.LENGTH_SHORT).show();
                        }

                    }

                }).setNegativeButton(this.getString(com.lafonapps.common.R.string.hard_top_cancel),new DialogInterface.OnClickListener() {//添加返回按钮



            @Override

            public void onClick(DialogInterface dialog, int which) {//响应事件

                // TODO Auto-generated method stub


            }

        }).show();//在按键响应事件中显示此对话框
    }

    public static boolean hasAnyMarketInstalled(Context context){

        Intent intent = new Intent();
        intent.setData(Uri.parse("market://details?id=android.browser"));
        List list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return 0 != list.size();
    }


    /**
     * 双击返回键退出
     */
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if (Common.isApkDebugable()){
//            mIsExit = true;
//        }
//
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (mIsExit) {
//                this.finish();
//
//            } else {
//                if (!isPingfen){
//                    scoreView();
//                    isPingfen = true;
//                }else {
//                    Toast.makeText(this, this.getString(com.lafonapps.common.R.string.exit_app), Toast.LENGTH_SHORT).show();
//                    mIsExit = true;
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            mIsExit = false;
//                        }
//                    }, 2000);
//                }
//
//            }
//            return true;
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileUtil.removeTmpFiles();
    }
    @Override
    protected boolean shouldShowBannerView() {
        return false;
    }
}
