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
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

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

        Intent receivedIntent = getIntent();
        if (receivedIntent != null) {
            trip = receivedIntent.getParcelableExtra("reminderTrip");
            if (trip != null) {
                tripName.setText(trip.getTripName());
                tripTime.setText(trip.getTripTime());
                tripDate.setText(trip.getTripDate());
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
//                    showNotification(getApplicationContext(), trip.getTripId(), trip.getTripName(), trip.getTripTime());
                    show2(getApplicationContext());
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

    private void showNotification(Context context, int notificationId, String title, String content) {
        Intent intent = new Intent(context, TripReminderActivity.class);
        if (trip != null) {
            intent.putExtra("reminderTrip", trip);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.app_logo)
                .setAutoCancel(true)
                .setOngoing(true)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_logo));

        Notification notification;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        } else {
            notification = builder.getNotification();
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notification);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    private void show2(Context mContext){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext.getApplicationContext(), "notify_001");
        Intent ii = new Intent(mContext.getApplicationContext(), TripReminderActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("AhAhmed");
        bigText.setBigContentTitle("Today's Bible Verse");
        bigText.setSummaryText("Text in detail");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("Your Title");
        mBuilder.setContentText("Your text");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }

        mNotificationManager.notify(0, mBuilder.build());
    }
}
