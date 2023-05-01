package com.example.clockanimationandroid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class SetAlarmActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private Button saveButton;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        timePicker = findViewById(R.id.timePicker);
        saveButton = findViewById(R.id.saveButton);

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        // Generate a unique identifier for the new alarm
        long uniqueId = System.currentTimeMillis();
        intent.putExtra("uniqueId", uniqueId); // Add uniqueId as an extra
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) uniqueId, intent, PendingIntent.FLAG_IMMUTABLE);

        // Get the AlarmManager system service
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                // Save the new alarm to SharedPreferences using the unique identifier as the key
                SharedPreferences sharedPreferences = getSharedPreferences("Alarms", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("ALARM HOUR_" + uniqueId, hour);
                editor.putInt("ALARM MINUTE_" + uniqueId, minute);
                editor.putBoolean("ALARM STATUS_"+uniqueId,true);
                editor.apply();

                // Set the alarm
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                long timeInMillis = calendar.getTimeInMillis();
                long currentTimeInMillis = System.currentTimeMillis();

                if (timeInMillis < currentTimeInMillis) {
                    timeInMillis += 86400000; // Add one day
                }

                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);

                Toast.makeText(getApplicationContext(), "New alarm set for " + hour + ":" + minute, Toast.LENGTH_LONG).show();
                finish(); // Close the activity
            }
        });
    }
}
