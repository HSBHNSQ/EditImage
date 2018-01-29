package com.lafonapps.common.rate;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.lafonapps.common.Common;
import com.lafonapps.common.R;

import java.util.Locale;

/**
 * Created by liujun on 2017/7/6.
 */

public class FeedbackSender {

    public static void sendEmail(String address, Context context) {
        String title = context.getResources().getString(R.string.issues_and_suggestions);

        String appName = Common.getAppDisplayName();
        String appVersionName = Common.getAppVersionName();
        String deviceModel = Build.MODEL;
//        String systemVersion = Build.DISPLAY;
        String androidVersion = Build.VERSION.RELEASE;
        String languageCode = Locale.getDefault().toString();;

        String subject = title + " - " + appName + "(" + appVersionName + ")" + "(" + deviceModel + "," + androidVersion + "," + languageCode + ")";
        String content = context.getResources().getString(R.string.please_describe_your_issues_or_suggestions_here);

        // 底部弹窗中的标题
        String chooserTitle = context.getResources().getString(R.string.select_mail_client);

        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        email.putExtra(Intent.EXTRA_SUBJECT, subject); // 主题
        email.putExtra(Intent.EXTRA_TEXT, content); // 文本信息
        try {
            context.startActivity(Intent.createChooser(email, chooserTitle)); // "Choose an Email Client"
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
