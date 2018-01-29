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

public class TextStyle8 extends TextStyle {

    private SquareView8 squareView8;
    private AutofitTextView autofitTextView1;
    private AutofitTextView autofitTextView2;

    public TextStyle8(Context context) {
        super(context);
        init(context);
    }

    public TextStyle8(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TextStyle8(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.text_type_8,this,true);
        squareView8 = findViewById(R.id.square_view_8);
        squareView8.setSquareColor(Color.parseColor("#E5D4C3"));
        autofitTextView1 = findViewById(R.id.aftv_text_1_style_8);
        autofitTextView2 = findViewById(R.id.aftv_text_2_style_8);

    }

    public void setText1(String text){
        autofitTextView1.setText(text);
    }

    public void setText2(String text){
        autofitTextView2.setText(text);
    }
    public void setTextColor(int textColor){
        autofitTextView1.setTextColor(textColor);
        autofitTextView2.setTextColor(textColor);

    }


    @Override
    public void setMainText(String text) {
        setText1(text);
    }

    @Override
    public void setSecondText(String text) {
        setText2(text);
    }

    @Override
    public void setMainColor(int mainColor) {
        setTextColor(mainColor);
    }

    @Override
    public void setSecondColor(int secondColor) {
        squareView8.setSquareColor(secondColor);
    }

    private OnAutofitTextViewClickListener onAutofitTextViewClickListener ;

    public void setOnAutofitTextViewClickListener(OnAutofitTextViewClickListener onAutofitTextViewClickListener) {
        this.onAutofitTextViewClickListener = onAutofitTextViewClickListener;
        autofitTextView1.setOnClickListener(onClickListener);
        autofitTextView2.setOnClickListener(onClickListener);
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
