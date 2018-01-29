package com.lafonapps.common.feedback;

import android.app.Activity;
import android.app.Application;

import com.lafonapps.common.Common;
import com.lafonapps.common.R;
import com.lafonapps.common.feedback.view.PromptDialog;
import com.lafonapps.common.feedback.view.SuggestionDialog;
import com.lafonapps.common.feedback.view.UserFeelDialog;
import com.lafonapps.common.rate.AppRater;

import java.util.List;

/**
 * Created by heshaobo on 2018/1/27.
 */

public class FeedBack {

    private static Application mApplication;


    public static void initialize(Application application, String applicationID, String clientKey){
        mApplication = application;
        FeedbackOperation.Configuration(application,applicationID,clientKey);
    }

    public static List<String> suggestionItems ;

    private static void setQQ(String qq){
        JumpContactOperation.SetQQ(qq);
    }

    private static void setEmail(String email){
        JumpContactOperation.SetEmail(email);
    }

    public static void showUserFeelDialog(final Activity activity){

        UserFeelDialog userFeelDialog = new UserFeelDialog(activity);
        userFeelDialog.setBackgroundResource(R.drawable.ic_comment_one);
        String content = activity.getString(R.string.think_this_app_feel, Common.getAppDisplayName());
        userFeelDialog.setContent(content);
        userFeelDialog.setPositiveTitle(activity.getString(R.string.easy_to_use));
        userFeelDialog.setNegativeTitle(activity.getString(R.string.not_easy_to_use));
        userFeelDialog.setPositiveUserCallBack(new CallBack() {
            @Override
            public void run() {
                showRateDialog(activity);
            }
        });

        userFeelDialog.setNegativeCallBack(new CallBack() {
            @Override
            public void run() {
                showSuggestionDialog(activity);
            }
        });
        userFeelDialog.show();
    }

    public static void showRateDialog(final Activity activity){
        UserFeelDialog rateDialog = new UserFeelDialog(activity);
        rateDialog.setBackgroundResource(R.drawable.ic_comment_two);
        String content = activity.getString(R.string.go_app_store_rate);
        rateDialog.setContent(content);
        rateDialog.setPositiveTitle(activity.getString(R.string.rate_rate_button_title));
        rateDialog.setNegativeTitle(activity.getString(R.string.can_ren_ju_jue));
        rateDialog.setCancelable(false);
        rateDialog.setPositiveUserCallBack(new CallBack() {
            @Override
            public void run() {
                AppRater appRater = new AppRater();
                appRater.goRating(activity);
            }
        });

        rateDialog.setNegativeCallBack(new CallBack() {
            @Override
            public void run() {

            }
        });
        rateDialog.show();
    }

    public static void showSuggestionDialog(final Activity activity){
        SuggestionDialog dialog = new SuggestionDialog(activity);
        dialog.setSuccessCallBack(new CallBack() {
            @Override
            public void run() {
                showThanksSuggestionDialog(activity);
            }
        });
        dialog.show();
    }

    public static void showThanksSuggestionDialog(Activity activity){
        PromptDialog dialog = new PromptDialog(activity);
        dialog.show();
    }




}
