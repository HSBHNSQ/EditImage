package com.liubowang.photoretouch.Feedback;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.liubowang.photoretouch.R;

public class FeedBackActicity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back_acticity);
        initUI();
    }

    private ImageView backIv;
    private EditText suggestionEt;
    private EditText userNameEt;
    private EditText userPhoneEt;
    private Button tiJiaoButton;
    private TextView qqCTv;
    private TextView emailCTv;

    private void initUI(){
        backIv = (ImageView) findViewById(R.id.iv_back_feed);
        backIv.setOnClickListener(viewClickListener);
        suggestionEt = (EditText) findViewById(R.id.et_suggestion_content_feed);
        userNameEt = (EditText) findViewById(R.id.et_user_name_feed);
        userPhoneEt = (EditText) findViewById(R.id.et_user_tel_feed);
        tiJiaoButton = (Button) findViewById(R.id.b_ti_jiao_feed);
        tiJiaoButton.setOnClickListener(viewClickListener);
        qqCTv = (TextView) findViewById(R.id.tv_qq_c_feed);
        qqCTv.setOnClickListener(viewClickListener);
        emailCTv = (TextView) findViewById(R.id.tv_email_c_feed);
        emailCTv.setOnClickListener(viewClickListener);
    }

    private View.OnClickListener viewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == backIv){
                backClick();
            }
            else if (v == tiJiaoButton){
                tijiaoClick();
            }
            else if(v == qqCTv){
                qqCClick();
            }
            else if(v == emailCTv){
                emailCClick();
            }
        }
    };

    private void backClick(){
        finish();
    }
    private void tijiaoClick(){
        FeedbackOperation feedbackOperation = new FeedbackOperation();
        feedbackOperation.setListener(new FeedbackOperation.OnStatusListener() {
            @Override
            public void onSendFailed(Exception e) {
                Toast.makeText(
                        FeedBackActicity.this,
                        getString(R.string.ei_ti_jiao_faile),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSendSuccessful() {
                Toast.makeText(
                        FeedBackActicity.this,
                        getString(R.string.ei_ti_jiao_success),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        String contact = userNameEt.getText().toString() +":" + userPhoneEt.getText().toString();
        feedbackOperation
                .putContact(contact)
                .putFeedbackContent(suggestionEt.getText().toString())
                .send();

    }
    private void qqCClick(){
        JumpContactOperation jumpContactOperation = new JumpContactOperation(FeedBackActicity.this);
        if (JumpContactOperation.installQQ(this)){
            jumpContactOperation.jumpQQ();
        }else {
            Toast.makeText(
                    FeedBackActicity.this,
                    getString(R.string.ei_no_QQ),
                    Toast.LENGTH_SHORT).show();
        }

    }
    private void emailCClick(){
        JumpContactOperation jumpContactOperation = new JumpContactOperation(FeedBackActicity.this);
        jumpContactOperation.jumpEmail();
    }




}
