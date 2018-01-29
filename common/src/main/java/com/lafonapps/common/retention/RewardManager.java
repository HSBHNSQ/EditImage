package com.lafonapps.common.retention;

import android.util.Log;

import com.lafonapps.common.preferences.Preferences;

import java.util.Calendar;

/**
 * 管理连续使用奖励的业务逻辑类
 * Created by chenjie on 2018/1/23.
 */

public class RewardManager {

    public static final String TAG = RewardManager.class.getCanonicalName();
    private final static RewardManager sharedManager = new RewardManager();
    private Preferences preferences;

    private RewardManager() {
        preferences = Preferences.getSharedPreference();
    }

    public static RewardManager getSharedManager() {
        return sharedManager;
    }

    /**
     * 今天是否已签到
     *
     * @return
     */
    public boolean hasUsedToday() {

        boolean hasUsedToday = (boolean) preferences.getValue(getTodayString(), false);
        return hasUsedToday;
    }

    /**
     * 签到，每天最多只能签到一次。
     */
    public void usedToday() {
        boolean isCutToday = (boolean) preferences.getValue(getTodayString(), false);
        if (isCutToday) {
            //已经签到
            Log.v("已经签到", "");
        } else {
            Log.v("正在签到", "");
            preferences.putValue(getTodayString(), true);
            int index = (int) preferences.getValue("todayindex", 0);
            index++;
            preferences.putValue("todayindex", index);
        }
    }

    /**
     * 累计签到次数。中间不中断
     *
     * @return
     */
    public long totalUsedDays() {
        int index = (int) preferences.getValue("todayindex", 0);
        return index;
    }

    /**
     * 是否获取到指定的奖励
     *
     * @param rewardName 奖励名称，需要根据产品自行定义
     * @return
     */
    public boolean hasReward(String rewardName) {
        boolean hasReward = (boolean) preferences.getValue(rewardName, false);
        return hasReward;
    }

    /**
     * 解锁指定的奖励
     *
     * @param rewardName 奖励名称，需要根据产品自行定义
     */
    public void unlockReward(String rewardName) {
        boolean isUnlockReward = (boolean) preferences.getValue(rewardName, false);
        if (isUnlockReward) {
            //解锁指定奖励
        } else {
            //未解锁指定奖励
            preferences.putValue(rewardName, true);
        }
    }

    /**
     * 获取当天的日期字符串
     *
     * @return
     */
    private String getTodayString() {
        //获取系统的日期
        Calendar calendar = Calendar.getInstance();
        //年
        int year = calendar.get(Calendar.YEAR);
        //月
        int month = calendar.get(Calendar.MONTH);
        //日
        int day = calendar.get(Calendar.DAY_OF_MONTH) + 2;
        return year + month + day + "";
    }

}
