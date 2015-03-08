package es.uem.david.samuel.nacho.yepnsd.utils;

import android.text.format.DateUtils;

import java.util.Date;

/**
 * Created by David on 08/03/2015.
 *
 * @author david.sancho
 */
public class Util {


    public static String convertDate(Date createdAt) {
        long now = new Date().getTime();
        return DateUtils.getRelativeTimeSpanString(
                createdAt.getTime(),
                now,
                DateUtils.SECOND_IN_MILLIS).toString();
    }
}
