package com.teachersspace.helpers;

import java.util.Calendar;

public class TimeFormatter {
    public static String formatTime(Calendar calendar) {
        String hours = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
        String minutes = Integer.toString(calendar.get(Calendar.MINUTE));
        return formatDigits(hours) + ":" + formatDigits(minutes);
    }

    public static String formatDigits(String s) {
        if (s.length() == 1) {
            return "0" + s;
        }
        return s;
    }
}
