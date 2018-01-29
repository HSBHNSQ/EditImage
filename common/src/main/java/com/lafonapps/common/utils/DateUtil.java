package com.lafonapps.common.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by chenjie on 2018/1/23.
 */

public class DateUtil {

    /**
     * end比begin多的天数。这种方式只是通过日期来进行比较两个日期的相差天数的比较，没有精确到相差到一天的时间。适合纯粹通过日期（年月日）判断相差天数
     * @param begin
     * @param end
     * @return
     */
    public static long getDayOfDistance(Date begin, Date end) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(begin);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(end);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) {  //同一年
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {   //闰年
                    timeDistance += 366;
                } else {   //不是闰年
                    timeDistance += 365;
                }
            }
            return timeDistance + (day2 - day1);
        } else {   //不同年
            return day2 - day1;
        }
    }

    /**
     * 通过时间秒毫秒数判断两个时间的天数间隔
     * @param date1
     * @param date2
     * @return
     */
    public static long getDayOfDistanceByMillisecond(Date date1,Date date2)
    {
        long days = ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        return days;
    }

}
