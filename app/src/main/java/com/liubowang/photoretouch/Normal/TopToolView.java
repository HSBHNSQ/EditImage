package com.liubowang.photoretouch.Normal;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.liubowang.photoretouch.R;

/**
 * Created by heshaobo on 2017/11/14.
 */

public class TopToolView extends LinearLayout {

    private ImageButton mBackButton;
    private ImageButton mUndoButton;
    private ImageButton mRedoButton;
    private ImageButton mResetButton;
    private ImageButton mSaveButton;

    private OnTopActionListener mActionListener ;

    public TopToolView(Context context) {
        super(context);
        init(context);
    }

    public TopToolView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TopToolView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TopToolView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.view_top_tool,this,true);
        mBackButton = findViewById(R.id.ib_back_top);
        mUndoButton = findViewById(R.id.ib_undo_top);
        mRedoButton = findViewById(R.id.ib_redo_top);
        mResetButton = findViewById(R.id.ib_reset_top);
        mSaveButton = findViewById(R.id.ib_save_top);
        mBackButton.setOnClickListener(mButtonClick);
        mUndoButton.setOnClickListener(mButtonClick);
        mRedoButton.setOnClickListener(mButtonClick);
        mResetButton.setOnClickListener(mButtonClick);
        mSaveButton.setOnClickListener(mButtonClick);
    }

    public void setActionListener(OnTopActionListener actionListener) {
        this.mActionListener = actionListener;
    }

    public void setUndoEnable(boolean enable){
        mUndoButton.setEnabled(enable);
    }
    public void setRedoEnable(boolean enable){
        mRedoButton.setEnabled(enable);
    }

    private View.OnClickListener mButtonClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mActionListener == null)
                return;
            switch (view.getId()){
                case R.id.ib_back_top:
                    mActionListener.onBackClick();
                    break;
                case R.id.ib_undo_top:
                    mActionListener.onUndoClick();
                    break;
                case R.id.ib_redo_top:
                    mActionListener.onRedoClick();
                    break;
                case R.id.ib_reset_top:
                    mActionListener.onResetClick();
                    break;
                case R.id.ib_save_top:
                    mActionListener.onSaveClick();
                    break;
            }
        }
    };

    public interface OnTopActionListener {
        void onBackClick();
        void onUndoClick();
        void onRedoClick();
        void onResetClick();
        void onSaveClick();
    }

}
