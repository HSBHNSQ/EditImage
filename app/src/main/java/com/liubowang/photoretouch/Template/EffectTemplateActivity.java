package com.liubowang.photoretouch.Template;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.liubowang.photoretouch.Base.EIBaseActiviry;
import com.liubowang.photoretouch.Effect.EffectActivity;
import com.liubowang.photoretouch.R;
import com.liubowang.photoretouch.Utils.DisplayUtil;
import com.liubowang.photoretouch.Utils.FileUtil;
import com.liubowang.photoretouch.Utils.ScreenUtil;

import java.lang.reflect.Field;

public class EffectTemplateActivity extends EIBaseActiviry {

    private static final String TAG = EffectTemplateActivity.class.getSimpleName();
    private RecyclerView recycleView;
    private TemplateAdatper adapter;
    private TemplateModel currentModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_effect_template);
        initUI();
        setOverflowShowingAlways();
    }

    private void initUI(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.tb_template);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageView imageView = (ImageView) findViewById(R.id.iv_top_image_template);
        setStatusBar(imageView);
        recycleView = (RecyclerView) findViewById(R.id.rv_recycle_view_template);
        int screenWidth = ScreenUtil.getScreenSize(this).widthPixels;
        int itemWidth = DisplayUtil.dpTopx(this,170);
        int count = screenWidth / itemWidth;
        int space = (screenWidth - itemWidth * count) / count;
        GridLayoutManager manager = new GridLayoutManager(this,count);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        recycleView.setLayoutManager(manager);
        recycleView.addItemDecoration(new GridSpacingItemDecoration(space));
        adapter = new TemplateAdatper(getAssets());
        adapter.setTemplateChangeListener(onTemplateListener);
        recycleView.setAdapter(adapter);
        recycleView.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
//            case android.R.id.home:
//                finish();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private TemplateAdatper.OnTemplateChangeListener onTemplateListener = new TemplateAdatper.OnTemplateChangeListener() {
        @Override
        public void onTemplateChanged(TemplateModel model) {
            Log.d(TAG,model.name);
            currentModel = model;
            requestSDCardPermission();

        }
    };

    private static final int REQUEST_OPEN_IMAGE = 949;
    @Override
    public void permissionSDCardAllow() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_OPEN_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_OPEN_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri imgUri = data.getData();
                    String imagePath = FileUtil.getImagePath(EffectTemplateActivity.this,imgUri);
                    Intent intent = new Intent(EffectTemplateActivity.this, EffectActivity.class);
                    intent.putExtra("TEMPLATE_MODEL",currentModel);
                    intent.putExtra("IMAGE_PATH",imagePath);
                    if (intent.resolveActivity(getPackageManager()) != null){
                        startActivity(intent);
                    }
                }
                break;
        }
    }


    private LinearLayout bannerViewContainer;
    @Override
    protected ViewGroup getBannerViewContainer() {
        if (bannerViewContainer == null){
            bannerViewContainer = (LinearLayout) findViewById(R.id.ll_banner_container_template);
        }
        return bannerViewContainer;
    }
    @Override
    protected boolean shouldShowBannerView() {
        return false;
    }
}
