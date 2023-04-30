package com.example.clockanimationandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class AlarmActivity extends AppCompatActivity {

    private ListView alarmListView;
    private List<Alarm> alarmsList;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        Button newAlarmButton = findViewById(R.id.newAlarmButton);
        newAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AlarmActivity.this, SetAlarmActivity.class);
                startActivity(intent);
            }
        });

        alarmListView=findViewById(R.id.alarmsList);
        alarmsList = new ArrayList<>();

        // Get all the alarms from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefsFile", Context.MODE_PRIVATE);
        Map<String, ?> alarms = sharedPreferences.getAll();

        // Loop through all the alarms and add them to the list
        for (Map.Entry<String, ?> entry : alarms.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // check if it s time or on/off method
            if (value instanceof Integer) {
                int intValue = (Integer) value;
                if (key.contains("hour")) {
                    String uniquekey = key.substring(key.lastIndexOf("_") + 1);
                    long uniqueId = Long.parseLong(uniquekey);
                    Alarm alarm = getAlarmById(uniqueId);
                    if (alarm != null) {
                        alarm.setHour(intValue);
                    } else {
                        alarm = new Alarm(uniqueId, intValue, 0, true);
                        alarmsList.add(alarm);
                    }
                } else if (key.contains("minute")) {
                    String uniquekey = key.substring(key.lastIndexOf("_") + 1);
                    long uniqueId = Long.parseLong(uniquekey);
                    Alarm alarm = getAlarmById(uniqueId);
                    if (alarm != null) {
                        alarm.setMinute(intValue);
                    } else {
                        alarm = new Alarm(uniqueId, 0, intValue, true);
                        alarmsList.add(alarm);
                    }
                }
            } else if (value instanceof Boolean) {
                boolean boolValue = (Boolean) value;
                String uniquekey = key.substring(key.lastIndexOf("_") + 1);
                long uniqueId = Long.parseLong(uniquekey);
                Alarm alarm = getAlarmById(uniqueId);
                if (alarm != null) {
                    alarm.setActive(boolValue);
                } else {
                    alarm = new Alarm(uniqueId, 0, 0, boolValue);
                    alarmsList.add(alarm);
                }
            }
        }


        Collections.sort(alarmsList, new Comparator<Alarm>() {
            @Override
            public int compare(Alarm a1, Alarm a2) {
                return Long.compare(a1.getUniqueId(), a2.getUniqueId());
            }
        });
        ListView listView = findViewById(R.id.alarmsList);
        AlarmListAdapter adapter = new AlarmListAdapter(this, (ArrayList<Alarm>) alarmsList,sharedPreferences);

        // set adapter for ListView
        listView.setAdapter(adapter);
    }

    private Alarm getAlarmById(long id) {
        for (Alarm alarm : alarmsList) {
            if (alarm.getUniqueId() == id) {
                return alarm;
            }
        }
        return null;
    }
}
