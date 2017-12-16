package com.liubowang.photoretouch.Main;

/**
 * Created by jiazhiguo on 2017/6/8.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lafonapps.common.Common;
import com.lafonapps.common.ad.adapter.BannerViewAdapter;
import com.liubowang.photoretouch.Base.EIBaseActiviry;
import com.liubowang.photoretouch.FileBrowse.FilesActivity;
import com.liubowang.photoretouch.Normal.ImageTextButton;
import com.liubowang.photoretouch.Normal.NormalActivity;
import com.liubowang.photoretouch.R;
import com.liubowang.photoretouch.Template.EffectTemplateActivity;
import com.liubowang.photoretouch.Utils.FileUtil;

import java.util.List;

public class MainActivity extends EIBaseActiviry {

    private final String TAG = getClass().getSimpleName();
    static final int REQUEST_OPEN_IMAGE = 1;
    private ImageTextButton mNormalButton;
    private ImageTextButton mEffectButton;
    private ImageTextButton mMyFilesButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    private void initUI(){
        setStatusBar(null);
        mNormalButton = (ImageTextButton) findViewById(R.id.itv_normal_mian);
        mEffectButton = (ImageTextButton) findViewById(R.id.itv_effect_main);
        mMyFilesButton = (ImageTextButton) findViewById(R.id.itv_my_photots_main);
        mNormalButton.setOnClickListener(mButtonListener);
        mEffectButton.setOnClickListener(mButtonListener);
        mMyFilesButton.setOnClickListener(mButtonListener);
    }


    private View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.itv_normal_mian:
                    requestSDCardPermission();
                    break;
                case R.id.itv_effect_main:
                    Intent intent = new Intent(MainActivity.this,EffectTemplateActivity.class);
                    if (intent.resolveActivity(getPackageManager()) != null){
                        startActivity(intent);
                    }else {
                        Log.d(TAG,"Intent start failed");
                    }
                    break;
                case R.id.itv_my_photots_main:
                    Intent filesIntent = new Intent(MainActivity.this,FilesActivity.class);
                    if (filesIntent.resolveActivity(getPackageManager()) != null){
                        startActivity(filesIntent);
                    }else {
                        Log.d(TAG,"Intent start failed");
                    }
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

    private void startEditIntent(String imagePath,Class startClass){
        Intent intent = new Intent(MainActivity.this,startClass);
        intent.putExtra("IMAGE_PATH",imagePath);
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }else {
            Log.d(TAG,"Intent start failed");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_OPEN_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri imgUri = data.getData();
                    String imagePath = FileUtil.getImagePath(MainActivity.this,imgUri);
                    startEditIntent(imagePath, NormalActivity.class);
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FileUtil.removeTmpFiles();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (Common.isApkDebugable()){
            mIsExit = true;
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsExit) {
                this.finish();

            } else {
                if (!isPingfen){
                    scoreView();
                    isPingfen = true;
                }else {
                    Toast.makeText(this, this.getString(com.lafonapps.common.R.string.exit_app), Toast.LENGTH_SHORT).show();
                    mIsExit = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mIsExit = false;
                        }
                    }, 2000);
                }


            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected boolean shouldShowBannerView() {
        return false;
    }
}
