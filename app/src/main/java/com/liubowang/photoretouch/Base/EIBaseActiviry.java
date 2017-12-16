package com.liubowang.photoretouch.Base;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jaeger.library.StatusBarUtil;
import com.lafonapps.common.ad.adapter.BannerViewAdapter;
import com.lafonapps.common.base.BaseActivity;
import com.liubowang.photoretouch.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heshaobo on 2017/10/17.
 */

public class EIBaseActiviry extends BaseActivity {
    private final String TAG = getClass().getSimpleName();
    private static final int REQUEST_PERMISSION_SETTING  = 888;
    private static final int REQUEST_MULTIPLE_PERMISSION = 999;
    private static final int REQUEST_RECORD_PERMISSION = 514;
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }


    @Override
    public void setContentView(View view) {
        super.setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
    }

    protected void setStatusBar(ImageView imageView) {
        if (imageView == null){
            StatusBarUtil.setTransparent(this);
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                View decorView = getWindow().getDecorView();
                int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                decorView.setSystemUiVisibility(option);
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }else {
                imageView.setVisibility(View.GONE);
                StatusBarUtil.setTransparent(this);
            }

        }
    }
    protected void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestRecordPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                showPermissionSetting(REQUEST_PERMISSION_SETTING);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_PERMISSION);
            }
        }else {
             permissionRecordAllow();
        }
    }

    public void permissionRecordAllow(){}
    public void permissionSDCardAllow(){}


    public void requestSDCardPermission(){

        int hasWritePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int hasReadPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
//        int hasCamerPermission = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.CAMERA);
        List<String> permissionList = new ArrayList<String>();
        if(hasWritePermission != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (hasReadPermission != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
//        if (hasCamerPermission != PackageManager.PERMISSION_GRANTED){
//            permissionList.add(Manifest.permission.CAMERA);
//        }
        if (permissionList.size() > 0){
            ActivityCompat.requestPermissions(this,
                    permissionList.toArray(new String[permissionList.size()]),
                    REQUEST_MULTIPLE_PERMISSION);
        }else {
            permissionSDCardAllow();
        }
    }




        @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_MULTIPLE_PERMISSION) {

            boolean allowSelectPhoto = true;
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    allowSelectPhoto = false;
                    boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,permission);
                    if (!showRationale) {
                        showPermissionSetting(REQUEST_PERMISSION_SETTING);
                        Log.d(TAG,"ActivityCompat.shouldShowRequestPermissionRationale(this,permission)");
                        return;
                    } else if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
                        Log.d(TAG,"Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)");
                    } else if ( Manifest.permission.CAMERA.equals(permission)) {
                        Log.d(TAG,"Manifest.permission.CAMERA.equals(permission)");
                    }else if ( Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission)) {
                        Log.d(TAG,"Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission)");
                    }
                }
            }
            if (allowSelectPhoto){
                permissionSDCardAllow();
            }
        }
        else if (requestCode == REQUEST_RECORD_PERMISSION){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionRecordAllow();
            } else {
                Log.d(TAG,"录音权限拒绝");
            }
        }
    }

    private void showPermissionSetting(final int requestCode){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(getString(R.string.open_permission_))
                .setPositiveButton(getString(R.string.sure),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, requestCode);
                            }
                        })
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
        dialog.show();
    }

    @Override
    public void onAdFailedToLoad(BannerViewAdapter adapter, int errorCode) {
        super.onAdFailedToLoad(adapter, errorCode);
        ViewGroup bannerContainer = getBannerViewContainer();
        if (bannerContainer != null){
            bannerContainer.setVisibility(View.GONE);
        }
        EIApplication.getSharedApplication().bannerADLoadSuccess = false;
    }

    @Override
    public void onAdLoaded(BannerViewAdapter adapter) {
        super.onAdLoaded(adapter);
        EIApplication.getSharedApplication().bannerADLoadSuccess = true;
    }



    @Override
    protected void onResume() {
        super.onResume();
        ViewGroup bannerContainer = getBannerViewContainer();
        if (EIApplication.getSharedApplication().bannerADLoadSuccess){
            if (bannerContainer != null){
                bannerContainer.setVisibility(View.VISIBLE);
            }
        }else {
            if (bannerContainer != null){
                bannerContainer.setVisibility(View.GONE);
            }
        }
    }

    public static String getAppVersionNameAndCode(Context context) {
        String versionName = "";
        int versioncode = 0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            versioncode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName + ":" + versioncode ;
    }

    public static String getSdkVersion() {
        return android.os.Build.VERSION.RELEASE;
    }
    public static String getPhoneModel() {
        return Build.MODEL;
    }
    public static String getCountry(Context context){
        return  context.getResources().getConfiguration().locale.getCountry();
    }
}
