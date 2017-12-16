package com.liubowang.photoretouch.Normal;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.liubowang.photoretouch.R;
import com.liubowang.photoretouch.Utils.DisplayUtil;

/**
 * Created by heshaobo on 2017/10/26.
 */

public class TextSeekView extends LinearLayout {

    private TextView textView;
    private TextView valueText;
    private TextView centerText;
    private SeekBar seekBar;
    private int textColor;
    private int seekMax;
    private int progress;
    private String text;
    private int textSize;
    private OnTextSeekValueChangedListener onTextSeekValueChangedListener;
    public TextSeekView(Context context) {
        super(context);
        initSubViews(context);
    }

    public TextSeekView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initSubViews(context);
        initAttributeSet(context,attrs);
    }

    public TextSeekView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSubViews(context);
        initAttributeSet(context,attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TextSeekView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initSubViews(context);
        initAttributeSet(context,attrs);
    }

    private void initAttributeSet(Context context, AttributeSet attributeSet){
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                                                    attributeSet,
                                                    R.styleable.TextSeekView,
                                                            0,
                                                            0);

        textColor = typedArray.getColor(R.styleable.TextSeekView_tsv_text_color, Color.parseColor("#000000"));
        textView.setTextColor(textColor);
        valueText.setTextColor(textColor);
        centerText.setTextColor(textColor);
        seekMax = typedArray.getInteger(R.styleable.TextSeekView_tsv_seek_max,100);
        seekBar.setMax(seekMax);
        progress = typedArray.getInteger(R.styleable.TextSeekView_tsv_progress,50);
        seekBar.setProgress(progress);
        text = typedArray.getString(R.styleable.TextSeekView_tsv_text);
        textView.setText(text);
        int defaultTextSize = DisplayUtil.spTopx(context, (float) 10);
        textSize = typedArray.getDimensionPixelSize(R.styleable.TextSeekView_tsv_text_size,defaultTextSize);
        int sp = DisplayUtil.pxTosp(context,textSize);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,sp);
        valueText.setTextSize(TypedValue.COMPLEX_UNIT_SP,sp);
        centerText.setTextSize(TypedValue.COMPLEX_UNIT_SP,sp);
        typedArray.recycle();
    }

    private void initSubViews(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_text_seek,this,true);
        textView = findViewById(R.id.tv_text_tsv);
        seekBar = findViewById(R.id.sb_seek_tsv);
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        valueText = findViewById(R.id.tv_value_tsv);
        centerText = findViewById(R.id.tv_center_text_tsv);
    }

    public void setOnTextSeekValueChangedListener(OnTextSeekValueChangedListener onTextSeekValueChangedListener) {
        this.onTextSeekValueChangedListener = onTextSeekValueChangedListener;
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            progress = i;
            valueText.setText(i+"");
            if (onTextSeekValueChangedListener != null){
                onTextSeekValueChangedListener.onValueChange(TextSeekView.this,i,b);
            }
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (onTextSeekValueChangedListener != null){
                onTextSeekValueChangedListener.onStopTrackingTouch(TextSeekView.this,seekBar.getProgress());
            }
        }
    };

    public void setText(String text) {
        this.text = text;
        textView.setText(text);
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        textView.setTextColor(textColor);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        seekBar.setProgress(progress);
    }

    public void setSeekMax(int seekMax) {
        this.seekMax = seekMax;
        seekBar.setMax(seekMax);
    }

    public int getProgress() {
        return seekBar.getProgress();
    }

    interface OnTextSeekValueChangedListener{
        void onValueChange(TextSeekView textSeekView, int value, boolean b);
        void onStopTrackingTouch(TextSeekView textSeekView,int value);
    }

}
