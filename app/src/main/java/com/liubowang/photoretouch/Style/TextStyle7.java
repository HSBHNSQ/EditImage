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

public class TextStyle7 extends TextStyle {

    private LeftView7 leftView7;
    private AutofitTextView autofitTextView;

    public TextStyle7(Context context) {
        super(context);
        init(context);
    }

    public TextStyle7(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TextStyle7(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.text_type_7, this, true);
        leftView7 = findViewById(R.id.left_view_7);
        autofitTextView = findViewById(R.id.aftv_text_1_style_7);
        autofitTextView.setTextColor(Color.WHITE);
        leftView7.setAlpha(0.5f);
    }

    public void setText(String text) {
        autofitTextView.setText(text);
    }

    public void setTextColor(int textColor) {
        autofitTextView.setTextColor(textColor);
    }

    public void setBgColor(int bgColor) {
        leftView7.setCircleColor(bgColor);
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
