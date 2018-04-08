package eg.gov.iti.tripplanner.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.List;

import eg.gov.iti.tripplanner.TripReminderActivity;
import eg.gov.iti.tripplanner.data.TripDbAdapter;
import eg.gov.iti.tripplanner.model.Trip;
import eg.gov.iti.tripplanner.utils.TripManager;

public class TripRescheduleReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        String action = intent.getAction();
        Toast.makeText(context, "function begin", Toast.LENGTH_SHORT).show();
        if (action != null) {
            TripDbAdapter dbAdapter = new TripDbAdapter(context);
            List<Trip> trips = dbAdapter.getUpcomingTrips();
            Toast.makeText(context, "inside if", Toast.LENGTH_SHORT).show();

            for (Trip trip : trips) {
                //Set alarm here
                Toast.makeText(context, trip.getTripName(), Toast.LENGTH_SHORT).show();
                Intent alarmIntent = new Intent(context, TripReminderActivity.class);
                alarmIntent.putExtra("reminderTrip", trip);
                int currentTime = (int) (System.currentTimeMillis() / 1000);
                int timeOfTrip = Integer.parseInt(trip.getTripTime());
                TripManager.scheduleNewTrip(context, timeOfTrip, alarmIntent, timeOfTrip - currentTime);
            }
        }
    }
}
