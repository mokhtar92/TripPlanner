package eg.gov.iti.tripplanner.receivers;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import eg.gov.iti.tripplanner.TripReminderActivity;
import eg.gov.iti.tripplanner.model.Trip;
import eg.gov.iti.tripplanner.utils.TripManager;

public class TripRescheduleReceiver extends BroadcastReceiver {

    List<Trip> allTrips = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            if (action.equals("android.intent.action.LOCKED_BOOT_COMPLETED") || action.equals("android.intent.action.BOOT_COMPLETED") ||
                    action.equals("android.intent.action.QUICKBOOT_POWERON") || action.equals("android.intent.action.REBOOT")) {

                //Set all alarms here
                for (Trip trip : allTrips) {
                    Intent alarmIntent = new Intent(context, TripReminderActivity.class);
                    alarmIntent.putExtra("reminderTrip", trip);
                    int currentTime = (int) (System.currentTimeMillis() / 1000);
                    int timeOfTrip = Integer.parseInt(trip.getTripTime());
                    TripManager.scheduleNewTrip(context, timeOfTrip, alarmIntent, timeOfTrip - currentTime);
                }
            }
        }
    }
}
