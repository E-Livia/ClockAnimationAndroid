package com.example.clockanimationandroid;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

public class AlarmListAdapter extends ArrayAdapter<Alarm> {
    private SharedPreferences sharedPreferences;

    public AlarmListAdapter(Context context, ArrayList<Alarm> alarmList, SharedPreferences sharedPreferences) {
        super(context, 0, alarmList);
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Alarm alarm = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.alarm_item, parent, false);
        }
        TextView timeTextView = convertView.findViewById(R.id.alarm_time);
        Switch alarmSwitch = convertView.findViewById(R.id.switch_alarm);
        alarmSwitch.setChecked(alarm.isActive());

        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Modify the corresponding AlarmObject in the mAlarmsList
                alarm.setActive(isChecked);
                if (isChecked == true) {
                    alarmSwitch.setText("On");
                } else {
                    alarmSwitch.setText("Off");
                }
                // Update the SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("ALARM STATUS_" + alarm.getUniqueId(), isChecked);
                editor.apply();
            }
        });
        // Populate the data into the template view using the data object
        timeTextView.setText(alarm.getHour() + ":" + alarm.getMinute());
        return convertView;
    }
}
