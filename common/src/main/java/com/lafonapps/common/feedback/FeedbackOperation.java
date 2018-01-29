package com.lafonapps.common.feedback;

import android.content.Context;
import android.util.Log;

import com.avos.avoscloud.AVCallback;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.SaveCallback;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by xiongzhifan on 2018/1/22.
 */

public class FeedbackOperation {

    private static final String TAG = "FeedbackOperation";

    private static final String AppInformation_Table = "AppInformation";
    private static final String DeviceInformation_Table = "DeviceInformation";
    private static final String FeedBack_Table = "Feedback";

    private static final String RecordTrack_Table = "RecordTrack";

    private static Context mContext;

    private OnStatusListener listener;

    private AVObject appInfoObj;
    private AVObject deviceInfoObj;
    private AVObject feedbackObj;

    public static void Configuration(Context context, String applicationID, String clientKey) {
        mContext = context;
        AVOSCloud.initialize(context, applicationID, clientKey, new AVCallback() {
            @Override
            protected void internalDone0(Object o, AVException e) {
                if (e != null) {
                    Log.e(TAG, "-------- FeedbackOperation.Configuration 初始化失败! -------");
                    e.printStackTrace();
                }
            }
        });
        AVOSCloud.setDebugLogEnabled(true);

        recorderTrack();
    }

    private static void recorderTrack() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isFindRecordTrackClass = true;

                try {
                    KeyInformation info = KeyInformation.getInstance(mContext);
                    AVQuery<AVObject> query = new AVQuery<AVObject>(RecordTrack_Table);
                    query.whereEqualTo("packageName", info.getAppPackgeName());

                    List<AVObject> items = query.find();

                    if (items != null && items.size() != 0) {

                        AVObject item = items.get(0);

                        JSONArray versions = item.getJSONArray("versions");
                        for (int i = 0; i < versions.length(); i++) {
                            String versionName = versions.getString(i);
                            if (versionName.equals(info.getAppVersionName())) {
                                return;
                            }
                        }

                        item.add("versions", info.getAppVersionName());

                        item.save();

                    }else {

//                        AVObject itemObj = new AVObject(RecordTrack_Table);
//                        itemObj.put("packageName", info.getAppPackgeName());
//                        itemObj.add("versions", info.getAppVersionName());
//                        itemObj.save();
                        creatRecordTrack();
                    }

                }catch (Exception e) {
                    e.printStackTrace();
                    isFindRecordTrackClass = false;
                }finally {
                    if (!isFindRecordTrackClass){
                        try {
                            creatRecordTrack();
                        } catch (AVException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }).start();

    }

    private static void creatRecordTrack() throws AVException {
        KeyInformation info = KeyInformation.getInstance(mContext);
        AVObject itemObj = new AVObject(RecordTrack_Table);
        itemObj.put("packageName", info.getAppPackgeName());
        itemObj.add("versions", info.getAppVersionName());
        itemObj.save();
    }

    public FeedbackOperation() {

        appInfoObj = new AVObject(AppInformation_Table);
        deviceInfoObj = new AVObject(DeviceInformation_Table);
        feedbackObj = new AVObject(FeedBack_Table);
    }

    // 设置发送反馈后的事件监听
    public void setListener(OnStatusListener listener) {
        this.listener = listener;
    }

    // 添加反馈内容
    public FeedbackOperation putFeedbackContent(String content) {
        feedbackObj.put("content", content);
        return this;
    }

    public FeedbackOperation putFeedbackContent(List<String> content) {
        feedbackObj.put("content", content);
        return this;
    }

    // 添加联系方式
    public FeedbackOperation putContact(String contact) {
        feedbackObj.put("contact", contact);
        return this;
    }
    //用户姓名
    public FeedbackOperation putUserName(String userName){
        feedbackObj.put("userName", userName);
        return this;
    }

    // 发送反馈消息
    public void send() {

        KeyInformation info = KeyInformation.getInstance(mContext);
        deviceInfoObj.put("brand", info.getPhoneBrand());
        deviceInfoObj.put("model", info.getPhoneModel());
        deviceInfoObj.put("manufacturer", info.getPhoneManufacturer());
        deviceInfoObj.put("buildLevel", info.getBuildLevel());
        deviceInfoObj.put("buildVersion", info.getBuildVersion());

        appInfoObj.put("appName", info.getAppName());
        appInfoObj.put("versionCode", info.getAppVersionCode());
        appInfoObj.put("versionName", info.getAppVersionName());
        appInfoObj.put("packageName", info.getAppPackgeName());

        feedbackObj.put("deviceInfo", deviceInfoObj);

        feedbackObj.put("appInfo", appInfoObj);

        feedbackObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e != null) {
                    sendFailed(e);
                }else {
                    sendSuccessful();
                }
            }
        });
    }

    // 发送失败消息
    private void sendFailed(Exception e) {
        if (listener != null) {
            listener.onSendFailed(e);
        }
    }

    // 发送成功消息
    private void sendSuccessful() {
        if (listener != null) {
            listener.onSendSuccessful();
        }
    }


    public static interface OnStatusListener {
        public void onSendFailed(Exception e);
        public void onSendSuccessful();
    }
}
