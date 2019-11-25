package chatapp.beast.firebasechat;

import android.app.Application;
import android.content.Context;

import com.google.firebase.database.ServerValue;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class LastSeen extends Application {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    public static String getTimeStamp(long time)
    {
        long now = System.currentTimeMillis();
        DateFormat dateFormat=null;
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }
        if (time > now || time <= 0) {
            return "Futuristic Void";
        }
        final long diff = now - time;
if (diff<DAY_MILLIS)
{
    dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

}else if (diff<DAY_MILLIS*365)
{
    //month
    dateFormat = new SimpleDateFormat("hh:mm a dd MMM ", Locale.getDefault());

}else {
    dateFormat = new SimpleDateFormat("hh:mm a dd-MMM-yy", Locale.getDefault());

}      return dateFormat.format(new Date(time)).toString();


    }

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Active few Seconds Ago";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

}
