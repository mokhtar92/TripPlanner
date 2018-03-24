package eg.gov.iti.tripplanner.receivers;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

import eg.gov.iti.tripplanner.TripReminderActivity;

public class TripRescheduleReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Reboot detected..TripPlanner", Toast.LENGTH_LONG).show();
    }


    private void rescheduleUpcomingTrips(Context context, int tripId, long tripTime) {
        Toast.makeText(context, "Booted..  TripPlanner", Toast.LENGTH_LONG).show();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 5);

        Intent intent = new Intent(context, TripReminderActivity.class);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, tripId, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }
}
