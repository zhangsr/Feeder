package me.zsr.feeder.util;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Date;

import me.zsr.feeder.App;
import me.zsr.feeder.R;

/**
 * @description:
 * @author: Zhangshaoru
 * @date: 15-7-8
 */
public class DateUtil {

    // TODO: 15-7-9 support en
    public static CharSequence formatDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date yestoday = calendar.getTime();
        calendar.add(Calendar.DATE, -1);
        Date beforeYestoday = calendar.getTime();
        if (isSameDay(date, today)) {
            return App.getInstance().getResources().getString(R.string.today);
        } else if (isSameDay(date, yestoday)) {
            return App.getInstance().getResources().getString(R.string.yestoday);
        } else if (isSameDay(date, beforeYestoday)) {
            return App.getInstance().getResources().getString(R.string.before_yestoday);
        } else {
            return DateFormat.format("yyyy年MM月dd日 EEEE", date);
        }
    }

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}
