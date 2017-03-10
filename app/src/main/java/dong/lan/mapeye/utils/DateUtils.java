/*
 *
 *   Copyright (C) 2016 author : 梁桂栋
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   Email me : stonelavender@hotmail.com
 *
 */

package dong.lan.mapeye.utils;

import android.icu.util.DateInterval;
import android.text.style.TtsSpan;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import dong.lan.mapeye.common.MonitorManager;

/**
 * Created by 梁桂栋 on 16-12-3 ： 下午10:18.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class DateUtils {
    private DateUtils() {
    }


    public static String format(Date date, String pattern) {
        try {
            return new SimpleDateFormat(pattern, Locale.CHINA).format(date);
        } catch (Exception e) {
            return "";
        }
    }

    private static final long INTERVAL_IN_MILLISECONDS = 30 * 1000;

    private static final int YEAR = 365 * 24 * 60 * 60;// 年
    private static final int MONTH = 30 * 24 * 60 * 60;// 月
    private static final int DAY = 24 * 60 * 60;// 天
    private static final int HOUR = 60 * 60;// 小时
    private static final int MINUTE = 60;// 分钟

    /**
     * 根据时间戳获取描述性时间，如3分钟前，1天前
     *
     * @param timestamp 时间戳 单位为毫秒
     * @return 时间字符串
     */
    public static String getDescriptionTimeFromTimestamp(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long timeGap = (currentTime - timestamp) / 1000;// 与现在时间相差秒数
        String timeStr = null;
        if (timeGap > YEAR) {
            timeStr = timeGap / YEAR + "年前";
        } else if (timeGap > MONTH) {
            timeStr = timeGap / MONTH + "个月前";
        } else if (timeGap > DAY) {// 1天以上
            timeStr = timeGap / DAY + "天前";
        } else if (timeGap > HOUR) {// 1小时-24小时
            timeStr = timeGap / HOUR + "小时前";
        } else if (timeGap > MINUTE) {// 1分钟-59分钟
            timeStr = timeGap / MINUTE + "分钟前";
        } else {// 1秒钟-59秒钟
            timeStr = "刚刚";
        }
        return timeStr;
    }

    /**
     * 获取聊天时间：因为sdk的时间默认到秒故应该乘1000
     */
    public static String getChatTime(long timesamp) {

        return (String) android.text.format.DateUtils.getRelativeTimeSpanString(timesamp);

//        String result = "";
//        SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.CHINA);
//        Date today = new Date(System.currentTimeMillis());
//        Date otherDay = new Date(timesamp);
//        int n = Integer.parseInt(sdf.format(today));
//        int m = Integer.parseInt(sdf.format(otherDay));
//
//        int temp = n - m;
//        if (temp == 0)
//            result = "今天 " + getHourAndMin(timesamp);
//        else if (temp == 1)
//            result = "昨天 " + getHourAndMin(timesamp);
//        else if (temp == 2)
//            result = "前天 " + getHourAndMin(timesamp);
//        else
//            result = getTime(timesamp);
//        return result;
    }

    private static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.CHINA);
        return format.format(new Date(time));
    }

    private static String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.CHINA);
        return format.format(new Date(time));
    }

    public static String getTime(long time, String patter) {
        SimpleDateFormat format = new SimpleDateFormat(patter, Locale.CHINA);
        return format.format(new Date(time));
    }

    public static String getTimestampString(long time) {
        Locale curLocale = MonitorManager.instance().getContext().getResources().getConfiguration().locale;

        boolean isChinese = curLocale.equals(Locale.SIMPLIFIED_CHINESE);

        String format;
        Date messageDate = new Date(time);
        if (isSameDay(time)) {
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(messageDate);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            format = "YY-MM-dd HH:mm";

            if (hour > 17) {
                if (isChinese) {
                    format = "晚上 hh:mm";
                }

            } else if (hour >= 0 && hour <= 6) {
                if (isChinese) {
                    format = "凌晨 hh:mm";
                }
            } else if (hour > 11 && hour <= 17) {
                if (isChinese) {
                    format = "下午 hh:mm";
                }

            } else {
                if (isChinese) {
                    format = "上午 hh:mm";
                }
            }
        } else if (isYesterday(time)) {
            if (isChinese) {
                format = "昨天 HH:mm";
            } else {
                format = "MM-dd HH:mm";
            }

        } else {
            if (isChinese) {
                format = "M月d日 HH:mm";
            } else {
                format = "MM-dd HH:mm";
            }
        }

        if (isChinese) {
            return new SimpleDateFormat(format, Locale.CHINA).format(messageDate);
        } else {
            return new SimpleDateFormat(format, Locale.US).format(messageDate);
        }
    }

    public static boolean isCloseEnough(long time1, long time2) {
        long delta = time1 - time2;
        if (delta < 0) {
            delta = -delta;
        }
        return delta < INTERVAL_IN_MILLISECONDS;
    }

    private static boolean isSameDay(long inputTime) {
        long d = System.currentTimeMillis() / DAY;
        return d == inputTime / DAY;
    }

    private static boolean isYesterday(long inputTime) {
        long d = System.currentTimeMillis() / DAY;
        long d1 = inputTime / DAY;
        return d - d1 == 1;
    }

    public static Date StringToDate(String dateStr, String formatStr) {
        DateFormat format = new SimpleDateFormat(formatStr, Locale.CHINA);
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String toTime(int timeLength) {
        timeLength /= 1000;
        int minute = timeLength / 60;
        int hour = 0;
        if (minute >= 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        int second = timeLength % 60;
        return String.format(Locale.CHINA, "%02d:%02d", minute, second);
    }

    public static String toTimeBySecond(int timeLength) {
        int minute = timeLength / 60;
        int hour = 0;
        if (minute >= 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        int second = timeLength % 60;
        return String.format(Locale.CHINA, "%02d:%02d", minute, second);
    }


    public static String getTimestampStr() {
        return Long.toString(System.currentTimeMillis());
    }

}
