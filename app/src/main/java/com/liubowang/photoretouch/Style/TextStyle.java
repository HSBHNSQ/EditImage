package com.liubowang.photoretouch.Style;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by heshaobo on 2018/1/24.
 */

public class TextStyle extends ConstraintLayout  implements TextStyleInterface {
    public TextStyle(Context context) {
        super(context);
    }

    public TextStyle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextStyle(Context context, AttributeSet attrs, int defStyleAttr) {
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


}
