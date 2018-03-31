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
        String action = intent.getAction();
        if (action != null) {
            if (action.equals("android.intent.action.LOCKED_BOOT_COMPLETED") || action.equals("android.intent.action.BOOT_COMPLETED") ||
                    action.equals("android.intent.action.QUICKBOOT_POWERON") || action.equals("android.intent.action.REBOOT"))

                Toast.makeText(context, "Reboot detected..TripPlanner", Toast.LENGTH_LONG).show();
        }
    }
}
