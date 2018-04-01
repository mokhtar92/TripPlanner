package eg.gov.iti.tripplanner.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import eg.gov.iti.tripplanner.TripReminderActivity;

/**
 * Created by Ahmed_Mokhtar on 3/27/2018.
 */

public class TripManager {

    public static void scheduleNewTrip(Context context, int tripId, Intent intent, int tripTimeInSeconds) {

        if (tripTimeInSeconds > 0) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, tripTimeInSeconds);

            PendingIntent pendingIntent =
                    PendingIntent.getActivity(context, tripId, intent, PendingIntent.FLAG_IMMUTABLE);

            AlarmManager manager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
            manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        }
    }


    public static void cancelScheduledTrip(Context context, int tripId, Intent intent) {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 12);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, tripId, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager manager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

        manager.cancel(pendingIntent);
        pendingIntent.cancel();
    }


    public static void re_scheduleTrip(Context context, int tripId, Intent intent, int tripTimeInSeconds) {
        cancelScheduledTrip(context, tripId, intent);
        scheduleNewTrip(context, tripId, intent, tripTimeInSeconds);
    }
}
