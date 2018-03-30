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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eg.gov.iti.tripplanner.adapters.NoteAdapter;
import eg.gov.iti.tripplanner.model.Trip;

public class TripReminderActivity extends AppCompatActivity {

    private MediaPlayer mMediaPlayer;
    private Trip trip;
    private static boolean isFirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_trip_reminder);

        TextView tripName = findViewById(R.id.reminder_trip_name_text_field);
        TextView tripDate = findViewById(R.id.reminder_trip_date_text_field);
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
                tripName.setText(trip.getTripName());
                tripTime.setText(trip.getTripTime());
                tripDate.setText(trip.getTripDate());

                ListView notesListView = findViewById(R.id.notes_list_view);
                NoteAdapter adapter = new NoteAdapter(getApplicationContext(), trip.getNotes());
                notesListView.setAdapter(adapter);

                notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        CheckBox checkBox = view.findViewById(R.id.note_check_box);
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
                    finish();
                }
            }
        });

        notNowTripImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (trip != null) {
                    showNotification(getApplicationContext(), trip.getTripId(), trip.getTripName(), trip.getTripTime());
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

        if (isFirstTime) {
            if (mMediaPlayer == null) {
                turnOnScreen(this);
                playSound(this, getAlarmUri());
            }
            isFirstTime = false;
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
