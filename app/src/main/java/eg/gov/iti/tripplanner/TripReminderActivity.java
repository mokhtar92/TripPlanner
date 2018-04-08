package eg.gov.iti.tripplanner;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import eg.gov.iti.tripplanner.adapters.ReminderNoteAdapter;
import eg.gov.iti.tripplanner.data.FirebaseHelper;
import eg.gov.iti.tripplanner.model.Trip;
import eg.gov.iti.tripplanner.utils.Definitions;

public class TripReminderActivity extends AppCompatActivity {

    private MediaPlayer mMediaPlayer;
    private Trip trip;

    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private String userId;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_trip_reminder);

        //SplashActivity.setFirstTimeFalse();

        mAuth = FirebaseAuth.getInstance();
//        mFirebaseDatabase = FirebaseDatabase.getInstance();
//        myRef = mFirebaseDatabase.getReference();
        user = mAuth.getCurrentUser();
        userId = user.getUid();

        TextView tripName = findViewById(R.id.reminder_trip_name_text_field);
        final TextView tripDate = findViewById(R.id.reminder_trip_date_text_field);
        TextView tripTime = findViewById(R.id.reminder_trip_time_text_field);

        ImageView startTripImageView = findViewById(R.id.reminder_start_trip_image_view);
        ImageView notNowTripImageView = findViewById(R.id.reminder_notNow_image_view);
        ImageView stopAlarmImageView = findViewById(R.id.reminder_cancel_image_view);
        final ImageView expandNotesList = findViewById(R.id.expand_notes_image_view);

        final RelativeLayout notesContainer = findViewById(R.id.notes_container_view);

        Intent receivedIntent = getIntent();
        if (receivedIntent != null) {
            trip = receivedIntent.getParcelableExtra("reminderTrip");
            if (trip != null) {

                Calendar c = Calendar.getInstance();
                Long unixTime = Long.parseLong(trip.getTripTime()) * 1000;
                c.setTimeInMillis(unixTime);

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH) + 1;
                int day = c.get(Calendar.DAY_OF_MONTH);

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                String test = sdf.format(unixTime).toString();

                tripName.setText(trip.getTripName());
                tripTime.setText(test);
                tripDate.setText(day + "/" + month + "/" + year);

                ListView notesListView = findViewById(R.id.notes_list_view);
                ReminderNoteAdapter adapter = new ReminderNoteAdapter(getApplicationContext(), trip.getNotes());
                notesListView.setAdapter(adapter);

                notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        CheckBox checkBox = view.findViewById(R.id.reminder_note_check_box);
                        if (!checkBox.isChecked()) {
                            checkBox.setChecked(true);
                        } else {
                            checkBox.setChecked(false);
                        }
                    }
                });
            }
        }

        startTripImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trip != null) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + trip.getEndLat() + "," + trip.getEndLong());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);

                    Intent notesIntent = new Intent(getApplicationContext(), NoteNotification.class);
                    notesIntent.putExtra("reminderTrip", trip);
                    notesIntent.putExtra("noteTrip", trip);

                    getApplicationContext().startService(notesIntent);

                    trip.setTripStatus(Definitions.STATUS_DONE);
                    //myRef.child("users").child(userId).child(trip.getFireBaseTripId()).setValue(trip);
                    FirebaseHelper.getInstance().updateTrip(trip, userId);
                    finish();
                }
            }
        });

        notNowTripImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trip != null) {
                    Calendar c = Calendar.getInstance();
                    Long unixTime = Long.parseLong(trip.getTripTime()) * 1000;
                    c.setTimeInMillis(unixTime);
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                    String test = sdf.format(unixTime).toString();

                    showNotification(getApplicationContext(), trip.getTripId(), trip.getTripName(), test);
                }
                finish();
            }
        });

        stopAlarmImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                }
                trip.setTripStatus(Definitions.STATUS_CANCELLED);
                //myRef.child("users").child(userId).child(trip.getFireBaseTripId()).setValue(trip);
                FirebaseHelper.getInstance().updateTrip(trip, userId);
                finish();
            }
        });

        expandNotesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notesContainer.getVisibility() == View.INVISIBLE) {
                    notesContainer.setVisibility(View.VISIBLE);
                    expandNotesList.setImageResource(R.drawable.ic_expand_less_black_48dp);

                } else {
                    notesContainer.setVisibility(View.INVISIBLE);
                    expandNotesList.setImageResource(R.drawable.ic_expand_more_black_48dp);
                }
            }
        });

        if (mMediaPlayer == null) {
            turnOnScreen(this);
            playSound(this, getAlarmUri());
        }

    }


    private void playSound(Context context, Uri alert) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, alert);
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Uri getAlarmUri() {
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if (alert == null) {
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            if (alert == null) {
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }

    private void turnOnScreen(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if (!isScreenOn) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
            wl.acquire(10000);

            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");
            wl_cpu.acquire(10000);
        }
    }

    private void showNotification(Context mContext, int notificationId, String title, String content) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext.getApplicationContext(), "notify_001");

        Intent intent = new Intent(mContext, TripReminderActivity.class);
        if (trip != null) {
            intent.putExtra("reminderTrip", trip);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.app_logo_notification)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setOngoing(true)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_logo));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBuilder.setPriority(Notification.PRIORITY_MAX);
        }

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(channel);
            }
        }

        if (mNotificationManager != null) {
            mNotificationManager.notify(notificationId, mBuilder.build());
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }
}
