package com.example.clockanimationandroid;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID ="alarm_channel";
    private static final String CHANNEL_NAME = "Alarm Channel";
    private static final String CHANNEL_DESC = "Channel for Alarm notifications";
    private MediaPlayer mediaPlayer;
    private long uniqueId;

    private Handler mHandler = new Handler();
    private Runnable mStopPlayerTask = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        //using a unique id to not have more alarms at the same our, not knowing which one should start or be updated
        uniqueId=intent.getLongExtra("uniqueId",0);
        SharedPreferences prefs = context.getSharedPreferences("Alarms", Context.MODE_PRIVATE);
        int hour=prefs.getInt("ALARM HOUR_"+uniqueId,0);
        int minute=prefs.getInt("ALARM MINUTE_"+uniqueId,0);

        boolean alarmStatus = prefs.getBoolean("ALARM STATUS_"+uniqueId, false);
        if(alarmStatus) {
            //creating the notification channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription(CHANNEL_DESC);
                NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
            }

            // creating MediaPlayer
            mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            mHandler.postDelayed(mStopPlayerTask, TimeUnit.SECONDS.toMillis(10)); // Oprirea MediaPlayer dupa 10 secunde

            // creating the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle("Alarm!")
                    .setContentText("It's time to get up! " + hour + ":" + minute)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            // displaying the notification
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify((int)uniqueId, builder.build());

            //alarm management
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent1 = new Intent(context, AlarmReceiver.class);
            intent1.putExtra("uniqueId",uniqueId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int)uniqueId, intent1, PendingIntent.FLAG_IMMUTABLE);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            alarmManager.cancel(pendingIntent);
            prefs.edit().putBoolean("ALARM STATUS_" + uniqueId, false).apply();
        }
    }

    public void stopAlarm(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int)uniqueId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        mediaPlayer.stop();
        mediaPlayer.release();

        SharedPreferences.Editor editor = context.getSharedPreferences("Alarms", Context.MODE_PRIVATE).edit();
        editor.putBoolean("ALARM STATUS_"+uniqueId, false);
        editor.apply();
    }

}
