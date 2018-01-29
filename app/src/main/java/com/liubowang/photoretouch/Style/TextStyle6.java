package com.liubowang.photoretouch.Style;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.liubowang.photoretouch.R;
import com.liubowang.photoretouch.Text.AutofitTextView;

/**
 * Created by heshaobo on 2018/1/22.
 */

public class TextStyle6 extends TextStyle {


    private View bg1;
    private View bg2;
    private View bg3;
    private AutofitTextView autofitTextView;

    public TextStyle6(Context context) {
        super(context);
        init(context);
    }

    public TextStyle6(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TextStyle6(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.text_type_6,this,true);
        autofitTextView = findViewById(R.id.aftv_text_1_style_6);
        autofitTextView.setTextColor(Color.BLACK);
        bg1 = findViewById(R.id.bg_view_1_style_6);
        bg1.setAlpha(0.4f);
        bg2 = findViewById(R.id.bg_view_2_style_6);
        bg2.setAlpha(0.7f);
        bg3 = findViewById(R.id.bg_view_3_style_6);
    }

    public void setText(String text){
        autofitTextView.setText(text);
    }

    public void setTextColor(int textColor){
        autofitTextView.setTextColor(textColor);
    }

    public void setBgColor(int bgColor){
        bg1.setBackgroundColor(bgColor);
        bg2.setBackgroundColor(bgColor);
        bg3.setBackgroundColor(bgColor);
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
    }

    @Override
    public void setSecondColor(int secondColor) {
        setBgColor(secondColor);
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
