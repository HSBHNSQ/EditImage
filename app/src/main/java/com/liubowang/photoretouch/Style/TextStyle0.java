package com.liubowang.photoretouch.Style;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by heshaobo on 2018/1/20.
 */

public class TextStyle0 extends TextStyle {




    public TextStyle0(Context context) {
        super(context);
    }

    public TextStyle0(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextStyle0(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void setMainText(String text) {

    }

    @Override
    public void setSecondText(String text) {

    }

    @Override
    public void setMainColor(int mainColor) {

    }

    @Override
    public void setSecondColor(int secondColor) {

    }


    private OnAutofitTextViewClickListener onAutofitTextViewClickListener ;

    public void setOnAutofitTextViewClickListener(OnAutofitTextViewClickListener onAutofitTextViewClickListener) {
        this.onAutofitTextViewClickListener = onAutofitTextViewClickListener;
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (onAutofitTextViewClickListener != null){
                onAutofitTextViewClickListener.onAutofitTextClick(null);
            }
        }
    };
}
