package com.lafonapps.common.feedback.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lafonapps.common.R;

/**
 * Created by heshaobo on 2018/1/29.
 */

public class PromptDialog extends AlertDialog {


    public PromptDialog(@NonNull Context context) {
        super(context);
    }

    protected PromptDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected PromptDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private TextView contentTv;
    private LinearLayout bg;
    private ImageView cancleIv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prompt_layout);


        contentTv = findViewById(R.id.tv_content_prompt);
        if (promptString != null){
            contentTv.setText(promptString);
        }
        bg = findViewById(R.id.ll_background_feel);
        if (bgResId != null){
            bg.setBackgroundResource(bgResId);
        }
        cancleIv = findViewById(R.id.iv_cancel_prompt);
        cancleIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
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

    private String promptString;
    public void setPrompt(String content){
        promptString = content;
        if (contentTv != null){
            contentTv.setText(content);
        }

    }
}
