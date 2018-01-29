package com.liubowang.photoretouch.FileBrowse;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liubowang.photoretouch.Base.EIBaseActiviry;
import com.liubowang.photoretouch.R;
import com.liubowang.photoretouch.Template.GridSpacingItemDecoration;
import com.liubowang.photoretouch.Utils.DisplayUtil;
import com.liubowang.photoretouch.Utils.FileUtil;
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

    private void initUI() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.tb_files);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageView imageView = (ImageView) findViewById(R.id.iv_top_image_files);
        setStatusBar(imageView);
        recycleView = (RecyclerView) findViewById(R.id.rv_recycle_view_files);
        int screenWidth = ScreenUtil.getScreenSize(this).widthPixels;
        int itemWidth = DisplayUtil.dpTopx(this, 170);
        int count = screenWidth / itemWidth;
        int space = (screenWidth - itemWidth * count) / count;
        GridLayoutManager manager = new GridLayoutManager(this, count);
        manager.setOrientation(GridLayoutManager.VERTICAL);
        recycleView.setLayoutManager(manager);
        recycleView.addItemDecoration(new GridSpacingItemDecoration(space));
        adapter = new FilesAdapter(fileItemListener);
        recycleView.setAdapter(adapter);
        recycleView.setItemAnimator(new DefaultItemAnimator());
        TextView textView = (TextView) findViewById(R.id.tv_file_path);

        String path = getString(R.string.ei_image_path) + ":";
        path += FileUtil.getPicturesResultPath();
        textView.setText(path);
    }


    private MenuItem editItem;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_eidt:
                String title = item.getTitle().toString();
                String cancel = getString(R.string.cancel);
                String edit = getString(R.string.ei_edit);
                String delete = getString(R.string.ei_delete);
                editItem = item;
                if (title.equals(cancel)) {
                    item.setTitle(edit);
                    cancelItemClick();
                } else if (title.equals(delete)){
                    item.setTitle(edit);
                    deleteItemClick();
                }else if (title.equals(edit)){
                    item.setTitle(cancel);
                    editItemClick();
                }
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_files, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void cancelItemClick(){
        adapter.setEdit(false);
    }

    private void deleteItemClick() {
        Log.d(TAG, "doneItemClick");
        if (adapter.selectedList.size() > 0) {
            String message = getString(R.string.ei_delete_image);
            AlertDialog dialog = new AlertDialog.Builder(FilesActivity.this)
                    .setMessage(message)
                    .setNegativeButton(getString(R.string.ei_delete), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            adapter.toDelete();
                            adapter.setEdit(false);
                        }
                    }).setPositiveButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            adapter.setEdit(false);
                        }
                    }).create();
            dialog.show();
        } else {
            adapter.setEdit(false);
        }

    }

    private void editItemClick() {
        Log.d(TAG, "editItemClick");
        adapter.setEdit(true);
    }


    private FilesAdapter.OnFileItemListener fileItemListener = new FilesAdapter.OnFileItemListener() {
        @Override
        public void onItemDidClick(FileInfo fileInfo) {
            Intent intent = new Intent(FilesActivity.this, ImageLookActivity.class);
            intent.putExtra("IMAGE_PATH", fileInfo.fileUrl);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }

        @Override
        public void onItemDeleteButtonClick(final FileInfo fileInfo) {
            String message = getString(R.string.ei_delete_image);
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

        @Override
        public void onItemHasSelectedClick(FileInfo fileInfo) {
            if (editItem != null){
                if (adapter.selectedList.size() > 0){
                    editItem.setTitle(getString(R.string.ei_delete));
                }else {
                    editItem.setTitle(getString(R.string.cancel));
                }
            }

        }
    };
    private LinearLayout bannerViewContainer;

    @Override
    protected ViewGroup getBannerViewContainer() {
        if (bannerViewContainer == null) {
            bannerViewContainer = (LinearLayout) findViewById(R.id.ll_banner_container_files);
        }
        return bannerViewContainer;
    }

    @Override
    protected boolean shouldShowBannerView() {
        return false;
    }
}
