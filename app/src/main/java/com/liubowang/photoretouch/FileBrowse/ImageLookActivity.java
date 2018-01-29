package com.liubowang.photoretouch.FileBrowse;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.liubowang.photoretouch.Base.EIBaseActiviry;
import com.liubowang.photoretouch.Normal.PinchImageView;
import com.liubowang.photoretouch.R;
import com.liubowang.photoretouch.Utils.FileProvider7;
import com.yalantis.ucrop.callback.BitmapLoadCallback;
import com.yalantis.ucrop.model.ExifInfo;
import com.yalantis.ucrop.util.BitmapLoadUtils;

import java.io.File;
import java.lang.reflect.Field;

public class ImageLookActivity extends EIBaseActiviry {

    private PinchImageView pinchImageView;
    private String imagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_look);
        initUI();
        setOverflowShowingAlways();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("IMAGE_PATH")){
            imagePath = intent.getStringExtra("IMAGE_PATH");
            if (imagePath != null){
                pinchImageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
            }
        }
        else if (intent != null && intent.hasExtra("IMAGE_URI")){
            Uri imageUri = intent.getParcelableExtra("IMAGE_URI");
             BitmapLoadUtils.decodeBitmapInBackground(this, imageUri, imageUri, 1024, 1024,
                new BitmapLoadCallback() {

                    @Override
                    public void onBitmapLoaded(@NonNull Bitmap bitmap, @NonNull ExifInfo exifInfo, @NonNull String imageInputPath, @Nullable String imageOutputPath) {
                        imagePath = imageOutputPath;
                        pinchImageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onFailure(@NonNull Exception bitmapWorkerException) {

                    }
                });
        }


    }

    private void initUI(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.tb_look);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageView imageView = (ImageView) findViewById(R.id.iv_top_image_look                                                                                                                                                                                                 );
        setStatusBar(imageView);
        setTitle(null);
        pinchImageView = (PinchImageView) findViewById(R.id.piv_pinch_image_view_look);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_look,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_share_look:
                shareItemClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void shareItemClick(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        Uri uri = FileProvider7.getUriForFile(this,new File(imagePath));
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivity(Intent.createChooser(intent, getString(R.string.ei_share)));
        }
    }
    private LinearLayout bannerViewContainer;
    @Override
    protected ViewGroup getBannerViewContainer() {
        if (bannerViewContainer == null){
            bannerViewContainer = (LinearLayout) findViewById(R.id.ll_banner_container_look);
        }
        return bannerViewContainer;
    }
    @Override
    protected boolean shouldShowBannerView() {
        return false;
    }
}
