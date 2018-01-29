package com.liubowang.photoretouch.Adjust;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by heshaobo on 2018/1/16.
 */

public class ActionTextView extends TextView {
    public ActionTextView(Context context) {
        super(context);
    }

    public ActionTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ActionTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (textViewActionListener != null){
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN){
                textViewActionListener.onTouchDown();
                return true;
            }
            else if (event.getActionMasked() == MotionEvent.ACTION_UP){
                textViewActionListener.onTouchUp();
            }
        }

        return super.onTouchEvent(event);
    }


    private OnTextViewActionListener textViewActionListener ;

    public void setTextViewActionListener(OnTextViewActionListener textViewActionListener) {
        this.textViewActionListener = textViewActionListener;
    }

    public interface OnTextViewActionListener {
        void onTouchDown();
        void onTouchUp();
    }

}
