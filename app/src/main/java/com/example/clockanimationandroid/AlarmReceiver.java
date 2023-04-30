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

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID ="alarm_channel";
    private static final String CHANNEL_NAME = "Alarm Channel";
    private static final String CHANNEL_DESC = "Channel for Alarm notifications";
    private static final String PREFS_NAME = "MyPrefsFile";
    private MediaPlayer mediaPlayer;
    private long uniqueId;

    @Override
    public void onReceive(Context context, Intent intent) {
        //using a unique id to not have more alarms at the same our, not knowing which one should start or be updated
        uniqueId=intent.getLongExtra("uniqueId",0);
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int hour=prefs.getInt("ALARM HOUR"+uniqueId,0);
        int minute=prefs.getInt("ALARM MINUTE"+uniqueId,0);

        boolean alarmStatus = prefs.getBoolean("ALARM STATUS"+uniqueId, false);
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

            // creating the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle("Alarm!")
                    .setContentText("It's time to get up! " + hour + ":" + minute)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(false);

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
        }
    }

    public void stopAlarm(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int)uniqueId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        mediaPlayer.stop();
        mediaPlayer.release();

        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean("ALARM STATUS"+uniqueId, false);
        editor.apply();
    }

}
