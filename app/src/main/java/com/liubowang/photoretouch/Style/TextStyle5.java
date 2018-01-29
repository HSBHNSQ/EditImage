package com.liubowang.photoretouch.Style;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.liubowang.photoretouch.R;
import com.liubowang.photoretouch.Text.AutofitTextView;

import org.w3c.dom.Text;

/**
 * Created by heshaobo on 2018/1/22.
 */

public class TextStyle5 extends TextStyle {

    private View line1;
    private View line2;
    private View line3;
    private View line4;
    private AutofitTextView autofitTextView;

    public TextStyle5(Context context) {
        super(context);
        init(context);
    }

    public TextStyle5(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TextStyle5(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.text_type_5,this,true);
        autofitTextView = findViewById(R.id.aftv_text_1_style_5);
        autofitTextView.setTextColor(Color.BLACK);
        line1 = findViewById(R.id.line_view_1);
        line2 = findViewById(R.id.line_view_2);
        line3 = findViewById(R.id.line_view_3);
        line4 = findViewById(R.id.line_view_4);
    }

    public void setText(String text){
        autofitTextView.setText(text);
    }

    public void setTextColor(int textColor){
        autofitTextView.setTextColor(textColor);
        line1.setBackgroundColor(textColor);
        line2.setBackgroundColor(textColor);
        line3.setBackgroundColor(textColor);
        line4.setBackgroundColor(textColor);
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
