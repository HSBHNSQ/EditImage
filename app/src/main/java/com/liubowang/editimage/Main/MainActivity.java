package com.liubowang.editimage.Main;

/**
 * Created by jiazhiguo on 2017/6/8.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.liubowang.editimage.Base.EIBaseActiviry;
import com.liubowang.editimage.Effect.EffectActivity;
import com.liubowang.editimage.Normal.NormalActivity;
import com.liubowang.editimage.R;
import com.liubowang.editimage.Utils.FileUtil;

public class MainActivity extends EIBaseActiviry {

    private final String TAG = getClass().getSimpleName();
    static final int REQUEST_OPEN_IMAGE = 1;
    private ImageView mIconImageView ;
    private Button mNormalButton;
    private Button mEffectButton;
    private boolean isEffects = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();


    }

    private void initUI(){
        setStatusBar(null);
        mIconImageView = (ImageView) findViewById(R.id.iv_icon);
        mNormalButton = (Button) findViewById(R.id.b_nomal);
        mEffectButton = (Button) findViewById(R.id.b_effects);
        mNormalButton.setOnClickListener(mButtonListener);
        mEffectButton.setOnClickListener(mButtonListener);
    }


    private View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.b_nomal:
                    isEffects = false;
                    requestSDCardPermission();
                    break;
                case R.id.b_effects:
                    isEffects = true;
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
                    if (isEffects){
                        startEditIntent(imagePath, EffectActivity.class);
                    }else {
                        startEditIntent(imagePath, NormalActivity.class);
                    }
                }
                break;
        }
    }
}
