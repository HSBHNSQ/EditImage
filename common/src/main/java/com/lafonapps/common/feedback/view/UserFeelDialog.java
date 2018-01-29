package com.lafonapps.common.feedback.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lafonapps.common.R;
import com.lafonapps.common.feedback.CallBack;

/**
 * Created by heshaobo on 2018/1/27.
 */

public class UserFeelDialog extends AlertDialog {


    private CallBack negativeCallBack;
    private CallBack positiveUserCallBack;
    private TextView negativeTv;
    private TextView positiveTv;
    private TextView contentTv;
    private LinearLayout bg;

    public UserFeelDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    protected UserFeelDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected UserFeelDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context){

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_feel_layout);
        positiveTv = findViewById(R.id.tv_positive_feel);
        if (positiveTitle != null){
            positiveTv.setText(positiveTitle);
        }
        positiveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positiveUserCallBack != null){
                    positiveUserCallBack.run();
                }
                dismiss();
            }
        });
        negativeTv = findViewById(R.id.tv_negative_feel);
        if (negativeTitle != null){
            negativeTv.setText(negativeTitle);
        }
        negativeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (negativeCallBack != null){
                    negativeCallBack.run();
                }
                dismiss();
            }
        });

        contentTv = findViewById(R.id.tv_content_feel);
        if (contentString != null){
            contentTv.setText(contentString);
        }
        bg = findViewById(R.id.ll_background_feel);
        if (bgResId != null){
            bg.setBackgroundResource(bgResId);
        }
        try {
            getWindow().setBackgroundDrawable(null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Integer bgResId = null;
    public void setBackgroundResource(int resId) {
        bgResId = resId;
        if (bg != null){
            bg.setBackgroundResource(resId);
        }

    }

    private String contentString;
    public void setContent(String content){
         contentString = content;
        if (contentTv != null){
            contentTv.setText(content);
        }

    }

    private String negativeTitle;
    public void setNegativeTitle(String negativeTitle){
        this.negativeTitle = negativeTitle;
        if (negativeTv != null){
            negativeTv.setText(negativeTitle);
        }
    }

    private String positiveTitle;
    public void setPositiveTitle(String positiveTitle){
        this.positiveTitle = positiveTitle;
        if (positiveTv != null){
            positiveTv.setText(positiveTitle);
        }

    }

    public void setNegativeCallBack(CallBack negativeCallBack) {
        this.negativeCallBack = negativeCallBack;
    }

    public void setPositiveUserCallBack(CallBack positiveUserCallBack) {
        this.positiveUserCallBack = positiveUserCallBack;
    }
}
