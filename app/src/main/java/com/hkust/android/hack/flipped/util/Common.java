package com.hkust.android.hack.flipped.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rui on 14-4-26.
 */
public class Common {
    public static String convertTime2Date(long longtime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = sdf.format(longtime);
        return str;
    }

    public static Date convertString2Date(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date ret = null;
        try {
            ret = sdf.parse(date);
        } catch (ParseException e) { }
        return ret;
    }
}
