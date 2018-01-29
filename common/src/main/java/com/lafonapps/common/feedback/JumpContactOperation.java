package com.lafonapps.common.feedback;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.util.List;

/**
 * Created by xiongzhifan on 2018/1/22.
 */

public class JumpContactOperation {

    private static String TARGET_QQ_NUM = "";
    private static String TARGET_EMAIL = "";

    private static final String QQ_PACKAGENAME = "com.tencent.mobileqq";

    private Context mContext;

    public JumpContactOperation(Context context) {
        mContext = context;
    }

    /***
     * 跳转到QQ 客服聊天界面, 调用这个方法必须提前带调用 SetQQ(String qq) 方法
     */
    public void jumpQQ() {
        jumpQQ(TARGET_QQ_NUM);
    }

    /***
     * 跳转到QQ 客服聊天界面
     * @param qqNum
     */
    public void jumpQQ(String qqNum) {
        String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + qqNum;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        mContext.startActivity(intent);
    }

    /***
     * 跳转邮箱反馈意见, 调用这个方法必须提前调用 SetEmail(String email) 方法
     */
    public void jumpEmail() {
        jumpEmail(TARGET_EMAIL);
    }

    /***
     * 跳转邮箱反馈意见
     * @param email
     */
    public void jumpEmail(String email) {

        Uri uri = Uri.parse("mailto:test@text.com");
        String[] emails = {email};
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);

        intent.putExtra(Intent.EXTRA_CC, emails);

        KeyInformation information = KeyInformation.getInstance(mContext);
        String subject = information.getAppName() + "<" + information.getAppVersionCode() + ", " + information.getAppVersionName() + ">";
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);

        mContext.startActivity(Intent.createChooser(intent, "请选择邮件类应用"));

    }

    /***
     * 设置全局QQ号
     * @param qq
     */
    public static void SetQQ(String qq) {
        TARGET_QQ_NUM = qq;
    }

    /***
     * 设置全局Email
     * @param email
     */
    public static void SetEmail(String email) {
        TARGET_EMAIL = email;
    }

    // 判断是否安装QQ
    public static boolean installQQ(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        // 获取已经安装程序的包信息
        List<PackageInfo> infos = packageManager.getInstalledPackages(0);
        if (infos == null) {
            return false;
        }
        for (int i = 0; i < infos.size(); i++) {
            String pkgName = infos.get(i).packageName;
            if (pkgName.equals(QQ_PACKAGENAME)) {
                return true;
            }
        }
        return false;
    }

}
