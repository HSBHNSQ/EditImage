package com.liubowang.photoretouch.Style;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.liubowang.photoretouch.R;
import com.liubowang.photoretouch.Text.AutofitTextView;

/**
 * Created by heshaobo on 2018/1/22.
 */

public class TextStyle2 extends TextStyle{

    private TopView2 topView;
    private BottomView2 bottomView;
    private AutofitTextView autofitTextView;

    public TextStyle2(Context context) {
        super(context);
        init(context);
    }

    public TextStyle2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TextStyle2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.text_type_2,this,true);
        topView = findViewById(R.id.top_view_2);
        bottomView = findViewById(R.id.bottom_view_2);
        autofitTextView = findViewById(R.id.aftv_text_1_style_2);
        bottomView.setLineColor(Color.WHITE);
        topView.setLineColor(Color.WHITE);
        autofitTextView.setTextColor(Color.WHITE);
    }

    private void setText(String text){
        autofitTextView.setText(text);
    }

    private void setTextColor(int color){
        autofitTextView.setTextColor(color);
    }

    private void setBgLineColor(int bgLineColor){
        bottomView.setLineColor(bgLineColor);
        topView.setLineColor(bgLineColor);
    }


    @Override
    public void setMainText(String text) {
        setText(text);
    }

    @Override
    public void setSecondText(String text) {

    }

    @Override
    public void setMainColor(int mainColor) {
        setTextColor(mainColor);
        setBgLineColor(mainColor);
    }

    @Override
    public void setSecondColor(int secondColor) {

    }
    private OnAutofitTextViewClickListener onAutofitTextViewClickListener ;

    public void setOnAutofitTextViewClickListener(OnAutofitTextViewClickListener onAutofitTextViewClickListener) {
        this.onAutofitTextViewClickListener = onAutofitTextViewClickListener;
        autofitTextView.setOnClickListener(onClickListener);
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (onAutofitTextViewClickListener != null){
                onAutofitTextViewClickListener.onAutofitTextClick((AutofitTextView) v);
            }
        }
    };
}
