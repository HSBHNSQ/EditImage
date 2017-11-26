package com.liubowang.editimage.Main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Shader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.liubowang.editimage.Draw.BrushView;
import com.liubowang.editimage.Draw.DrawView;
import com.liubowang.editimage.R;

public class TestActivity extends AppCompatActivity {


    private BrushView brushView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Button brush = (Button) findViewById(R.id.brush_test);
        brushView = (BrushView) findViewById(R.id.brush_view_test);
        imageView = (ImageView) findViewById(R.id.image_view_test);
        brushView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                brushView.onTouch(motionEvent);
                return true;
            }
        });
        brush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                brushView.setBrushType(BrushView.BrushType.NORMAL);
            }
        });

        Button eraser = (Button) findViewById(R.id.eraser_test);
        eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                brushView.setBrushType(BrushView.BrushType.ERASER);

            }
        });
    }
}
