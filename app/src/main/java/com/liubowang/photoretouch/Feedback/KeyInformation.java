package com.liubowang.photoretouch.Feedback;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by xiongzhifan on 2018/1/22.
 *
 * @author xiongzhifan
 */

public class KeyInformation {

    private static class Holder {
        private static KeyInformation instance = new KeyInformation();
    }

    private KeyInformation() {}

    private static Context mContext;

    public static synchronized KeyInformation getInstance(Context context) {
        mContext = context;
        return Holder.instance;
    }


    /***
     * 获取当前设备品牌
     * @return 设备品牌
     */
    public String getPhoneBrand() {
        return Build.BRAND;
    }

    /***
     * 获取当前设备型号
     * @return 设备型号
     */
    public String getPhoneModel() {
        return Build.MODEL;
    }

    /***
     * 获取设备生产厂家
     * @return 生产厂家
     */
    public String getPhoneManufacturer() {
        return Build.MANUFACTURER;
    }

    /***
     * 获取当前Android API 等级(22, 23 ...)
     * @return
     */
    public String getBuildLevel() {
        return String.valueOf(Build.VERSION.SDK_INT);
    }

    /***
     * 获取设备Android 版本(4.4, 5.0 ...)
     * @return
     */
    public String getBuildVersion() {
        return  Build.VERSION.RELEASE;
    }


    /***
     * 获取App名称
     * @return
     */
    public String getAppName()
    {
        try
        {
            PackageManager packageManager = mContext.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    mContext.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return mContext.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 获取App包名
     * @return
     */
    public String getAppPackgeName() {
        return mContext.getPackageName();
    }

    /***
     * 获取Build.gradle中的versionCode
     * @return versionCode
     */
    public String getAppVersionCode() {
        String versionCode = "-1";
        try {
            PackageInfo packageInfo = getPackageInfo(mContext);
            versionCode = String.valueOf(packageInfo.versionCode);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /***
     * 获取Build.gradle中的versionName
     * @return versionName
     */
    public String getAppVersionName() {
        String versionName = "-1";
        try {
            PackageInfo packageInfo = getPackageInfo(mContext);
            versionName = packageInfo.versionName;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    static private PackageInfo getPackageInfo(Context context) throws Exception {
        PackageInfo pi = null;
        PackageManager pm = context.getPackageManager();
        pi = pm.getPackageInfo(context.getPackageName(),
                PackageManager.GET_CONFIGURATIONS);
        return pi;
    }


    /***
     * 生成并返回一个唯一标示符
     * @return 唯一标识符
     */
    public String getUniqueId(){
        String androidID = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        String id = androidID + Build.SERIAL;
        try {
            return toMD5(id);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return id;
        }
    }

    /**
     * md5 加密
     */
    private static String toMD5(String text) throws NoSuchAlgorithmException {
        //获取摘要器 MessageDigest
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        //通过摘要器对字符串的二进制字节数组进行hash计算
        byte[] digest = messageDigest.digest(text.getBytes());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            //循环每个字符 将计算结果转化为正整数;
            int digestInt = digest[i] & 0xff;
            //将10进制转化为较短的16进制
            String hexString = Integer.toHexString(digestInt);
            //转化结果如果是个位数会省略0,因此判断并补0
            if (hexString.length() < 2) {
                sb.append(0);
            }
            //将循环结果添加到缓冲区
            sb.append(hexString);
        }
        //返回整个结果
        return sb.toString();
    }

}
