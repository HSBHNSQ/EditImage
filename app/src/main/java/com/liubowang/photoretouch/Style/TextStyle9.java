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

public class TextStyle9 extends TextStyle{

    private View bgView;
    private AutofitTextView autofitTextView;

    public TextStyle9(Context context) {
        super(context);
        init(context);
    }

    public TextStyle9(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TextStyle9(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.text_type_9,this,true);
        bgView = findViewById(R.id.bg_view_1_style_9);
        bgView.setAlpha(0.5f);
        autofitTextView = findViewById(R.id.aftv_text_1_style_9);
        autofitTextView.setTextColor(Color.WHITE);
    }
    public void setText(String text){
        autofitTextView.setText(text);
    }

    public void setTextColor(int textColor){
        autofitTextView.setTextColor(textColor);
    }

    public void setBgColor(int bgColor){
        bgView.setBackgroundColor(bgColor);
    }

    @Override
    public void setMainText(String text) {
        setText(text);
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
