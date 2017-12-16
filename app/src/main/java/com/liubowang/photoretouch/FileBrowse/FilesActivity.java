package com.liubowang.photoretouch.FileBrowse;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.liubowang.photoretouch.Base.EIBaseActiviry;
import com.liubowang.photoretouch.R;
import com.liubowang.photoretouch.Template.GridSpacingItemDecoration;
import com.liubowang.photoretouch.Utils.DisplayUtil;
import com.liubowang.photoretouch.Utils.ScreenUtil;

import java.lang.reflect.Field;

public class FilesActivity extends EIBaseActiviry {

    private static final String TAG = FilesActivity.class.getSimpleName();

    private RecyclerView recycleView;
    private FilesAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);
        initUI();
        setOverflowShowingAlways();
    }

    private void initUI(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.tb_files);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageView imageView = (ImageView) findViewById(R.id.iv_top_image_files);
        setStatusBar(imageView);
        recycleView = (RecyclerView) findViewById(R.id.rv_recycle_view_files);
        int screenWidth = ScreenUtil.getScreenSize(this).widthPixels;
        int itemWidth = DisplayUtil.dpTopx(this,170);
        int count = screenWidth / itemWidth;
        int space = (screenWidth - itemWidth * count) / count;
        GridLayoutManager manager = new GridLayoutManager(this,count);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        recycleView.setLayoutManager(manager);
        recycleView.addItemDecoration(new GridSpacingItemDecoration(space));
        adapter = new FilesAdapter(fileItemListener);
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

    private FilesAdapter.OnFileItemListener fileItemListener = new FilesAdapter.OnFileItemListener() {
        @Override
        public void onItemDidClick(FileInfo fileInfo) {
            Intent intent = new Intent(FilesActivity.this,ImageLookActivity.class);
            intent.putExtra("IMAGE_PATH",fileInfo.fileUrl);
            if (intent.resolveActivity(getPackageManager()) != null){
                startActivity(intent);
            }
        }

        @Override
        public void onItemMoreButtonClick(final FileInfo fileInfo) {
            String message = getString(R.string.ei_delete_image) ;
            AlertDialog dialog = new AlertDialog.Builder(FilesActivity.this)
                    .setMessage(message)
                    .setNegativeButton(getString(R.string.ei_delete), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            adapter.removeItem(fileInfo);
                        }
                    }).setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).create();
            dialog.show();
        }
    };
    private LinearLayout bannerViewContainer;
    @Override
    protected ViewGroup getBannerViewContainer() {
        if (bannerViewContainer == null){
            bannerViewContainer = (LinearLayout) findViewById(R.id.ll_banner_container_files);
        }
        return bannerViewContainer;
    }

    @Override
    protected boolean shouldShowBannerView() {
        return false;
    }
}
