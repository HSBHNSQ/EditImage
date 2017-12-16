package com.liubowang.photoretouch.Main;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.liubowang.photoretouch.Base.EIBaseActiviry;
import com.liubowang.photoretouch.R;

public class GuidePlayActivity extends EIBaseActiviry {

    private VideoView vv_videoview;
    private MediaController mController;
    private Uri mVideoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_play);
        vv_videoview = (VideoView) findViewById(R.id.vv_videoview);
        mController = new MediaController(this);
        mVideoUri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.guide);

    }


    @Override
    protected void onResume() {
        super.onResume();
        vv_videoview.setVideoURI(mVideoUri);
        vv_videoview.setMediaController(mController);
        vv_videoview.start();
        vv_videoview.requestFocus();
        vv_videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                vv_videoview.start();
            }
        });
    }
}
