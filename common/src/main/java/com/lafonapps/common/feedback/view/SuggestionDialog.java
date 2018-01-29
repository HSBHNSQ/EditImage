package com.lafonapps.common.feedback.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lafonapps.common.R;
import com.lafonapps.common.feedback.CallBack;
import com.lafonapps.common.feedback.FeedBack;
import com.lafonapps.common.feedback.FeedbackOperation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heshaobo on 2018/1/27.
 */

public class SuggestionDialog extends AlertDialog {

    private static final String TAG  = SuggestionDialog.class.getSimpleName();
    private ListView suggestionLv ;
    private EditText otherSuggestionEt;
    private EditText userNameEt;
    private EditText userContactEt;
    private Button tiJiaoButton;
    private Button cancelButton;
    private ProgressBar progressBar;
    private CallBack successCallBack;
    private Context context;
    private List<SuggestionItem> dataSource = new ArrayList<>();
    public SuggestionDialog(@NonNull Context context) {
        super(context);
        this.context =context;
    }

    protected SuggestionDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context =context;
    }

    protected SuggestionDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context =context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_back_layout);
        try {
            getWindow().setBackgroundDrawable(null);
        }catch (Exception e){
            e.printStackTrace();
        }

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        initData();
        initUI();


    }


    private void initUI(){
        otherSuggestionEt =findViewById(R.id.et_suggestion_edit);
        userContactEt = findViewById(R.id.et_user_contact);
        userNameEt = findViewById(R.id.et_user_name);
        tiJiaoButton = findViewById(R.id.b_ti_jiao);
        tiJiaoButton.setOnClickListener(buttonClickListener);
        cancelButton = findViewById(R.id.b_cancel);
        cancelButton.setOnClickListener(buttonClickListener);
        progressBar = findViewById(R.id.pb_progress_bar);
        suggestionLv = findViewById(R.id.lv_suggestion_list);
        suggestionLv.setAdapter(suggestionAdapter);
    }
    private void initData(){
        if (FeedBack.suggestionItems == null || FeedBack.suggestionItems.size() == 0){
            FeedBack.suggestionItems = defaultSuggestion();
        }
        dataSource = new ArrayList<>();
        for (String suggestionName: FeedBack.suggestionItems){
            SuggestionItem item = new SuggestionItem();
            item.title = suggestionName;
            item.isSelected = false;
            dataSource.add(item);
        }
    }

    private boolean isCommit = false;
    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == tiJiaoButton){
                tijiao();
            }
            else if (v == cancelButton){
                dismiss();
            }

        }
    };

    public void setSuccessCallBack(CallBack successCallBack) {
        this.successCallBack = successCallBack;
    }

    private void tijiao(){
        if (isCommit) return;
        progressBar.setVisibility(View.VISIBLE);
        FeedbackOperation feedbackOperation = new FeedbackOperation();
        feedbackOperation.setListener(new FeedbackOperation.OnStatusListener() {
            @Override
            public void onSendFailed(Exception e) {
                Toast.makeText(
                        context,
                        context.getString(R.string.ti_jiao_faile),
                        Toast.LENGTH_SHORT).show();
                isCommit = false;
                progressBar.setVisibility(View.GONE);
                cancelButton.setVisibility(View.VISIBLE);
                tiJiaoButton.setText(context.getString(R.string.ti_jiao_again));
            }

            @Override
            public void onSendSuccessful() {
                Toast.makeText(
                        context,
                        context.getString(R.string.ti_jiao_success),
                        Toast.LENGTH_SHORT).show();
                isCommit = false;
                progressBar.setVisibility(View.GONE);
                if (successCallBack != null){
                    successCallBack.run();
                }
                dismiss();
            }
        });
        String userName = userNameEt.getText().toString();
        String contact = userContactEt.getText().toString();
        List<String> content = new ArrayList<>();
        for (SuggestionItem item:dataSource){
            if (item.isSelected){
                content.add(item.title);
            }
        }
        content.add(context.getString(R.string.other_suggestion) +":"+otherSuggestionEt.getText().toString());
        feedbackOperation
                .putUserName(userName)
                .putContact(contact)
                .putFeedbackContent(content)
                .send();
    }
    private BaseAdapter suggestionAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return dataSource.size();
        }

        @Override
        public Object getItem(int position) {
            return dataSource.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.suggestion_item, null);
                holder = new ViewHolder();
                holder.textView = (TextView) convertView.findViewById(R.id.tv_suggestion);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.cb_suggestion_check);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final SuggestionItem item = (SuggestionItem) getItem(position);
            holder.textView.setText(item.title);

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    item.isSelected = isChecked;
                    Log.d(TAG,"item.isSelected:" + isChecked);
                }
            });

            final ViewHolder finalHolder = holder;
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item.isSelected = !item.isSelected;
                    finalHolder.checkBox.setChecked(item.isSelected);
                }
            });
            holder.checkBox.setChecked(item.isSelected);
            return convertView;
        }
    };

    class SuggestionItem {
        public String title;
        public boolean isSelected = false;
    }

    static class ViewHolder{
        public TextView textView;
        public CheckBox checkBox;
    }

    private List<String> defaultSuggestion(){
        List<String> su = new ArrayList<>();
        su.add(context.getString(R.string.ad_too_more));
        su.add(context.getString(R.string.application_too_simple));
        su.add(context.getString(R.string.rough_interface_design));
        su.add(context.getString(R.string.interaction_not_reasonable));
        su.add(context.getString(R.string.not_easy_to_use));
        return su;
    }
}
