package com.liubowang.photoretouch.Style;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.liubowang.photoretouch.R;
import com.liubowang.photoretouch.Text.AutofitTextView;

/**
 * Created by heshaobo on 2018/1/22.
 */

public class TextStyle1 extends TextStyle {

    private CircleView circleView1;
    private CircleView circleView2;
    private CircleView circleView3;
    private AutofitTextView autofitTextView;

    public TextStyle1(Context context) {
        super(context);
        init(context);
    }

    public TextStyle1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initAttr(context,attrs);
    }

    public TextStyle1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initAttr(context,attrs);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.text_type_1,this,true);
        circleView1 = findViewById(R.id.cv_circle_1);
        circleView1.setAlpha(0.2f);
        circleView2 = findViewById(R.id.cv_circle_2);
        circleView2.setAlpha(0.3f);
        circleView3 = findViewById(R.id.cv_circle_3);
        circleView3.setAlpha(0.6f);
        autofitTextView = findViewById(R.id.aftv_text_1_style_1);
    }

    private void initAttr(Context context,AttributeSet attributeSet){}

    private void setText(String text) {
        autofitTextView.setText(text);
    }

    private void setTextColor(int color){
        autofitTextView.setTextColor(color);
    }

    private void setCircleColor(int color){
        circleView1.setCircleColor(color);
        circleView2.setCircleColor(color);
        circleView3.setCircleColor(color);
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
        setCircleColor(secondColor);
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
