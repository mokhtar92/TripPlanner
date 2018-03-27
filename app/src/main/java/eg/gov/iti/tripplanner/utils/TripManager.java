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

    public static void scheduleNewTrip(Context context, int tripId, int tripTimeInSeconds) {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, tripTimeInSeconds);

        Intent intent = new Intent(context, TripReminderActivity.class);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, tripId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }


    public static void cancelScheduledTrip(Context context, int tripId) {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 12);

        Intent intent = new Intent(context, TripReminderActivity.class);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, tripId, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager manager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

        manager.cancel(pendingIntent);
        pendingIntent.cancel();
    }
}
