package eg.gov.iti.tripplanner.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import eg.gov.iti.tripplanner.TripReminderActivity;
import eg.gov.iti.tripplanner.model.Trip;
import eg.gov.iti.tripplanner.utils.Definitions;
import eg.gov.iti.tripplanner.utils.TripManager;

public class TripRescheduleReceiver extends BroadcastReceiver {

    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private String userId;
    private FirebaseUser user;


    @Override
    public void onReceive(final Context context, Intent intent) {

        String action = intent.getAction();
        if (action != null) {
            if (action.equals("android.intent.action.LOCKED_BOOT_COMPLETED") || action.equals("android.intent.action.BOOT_COMPLETED") ||
                    action.equals("android.intent.action.QUICKBOOT_POWERON") || action.equals("android.intent.action.REBOOT")) {


                mAuth = FirebaseAuth.getInstance();
                mFirebaseDatabase = FirebaseDatabase.getInstance();
                myRef = mFirebaseDatabase.getReference();
                user = mAuth.getCurrentUser();
                if (user != null) {
                    userId = user.getUid();
                    myRef.child(userId).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Trip trip = dataSnapshot.getValue(Trip.class);
                            if (trip.getTripStatus() == Definitions.STATUS_UPCOMING) {
                                Intent alarmIntent = new Intent(context, TripReminderActivity.class);
                                alarmIntent.putExtra("reminderTrip", trip);
                                int currentTime = (int) (System.currentTimeMillis() / 1000);
                                int timeOfTrip = Integer.parseInt(trip.getTripTime());
                                TripManager.scheduleNewTrip(context, timeOfTrip, alarmIntent, timeOfTrip - currentTime);
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }
        }
    }

}
