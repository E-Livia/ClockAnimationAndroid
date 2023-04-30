package com.example.clockanimationandroid;

public class Alarm {
    private long mUniqueId;
    private int mHour;
    private int mMinute;
    private boolean mIsActive;

    public Alarm(long uniqueId, int hour, int minute, boolean isActive) {
        mUniqueId = uniqueId;
        mHour = hour;
        mMinute = minute;
        mIsActive = isActive;
    }

    public long getUniqueId() {
        return mUniqueId;
    }

    public void setUniqueId(int uniqueId) {
        mUniqueId = uniqueId;
    }

    public int getHour() {
        return mHour;
    }

    public void setHour(int hour) {
        mHour = hour;
    }

    public int getMinute() {
        return mMinute;
    }

    public void setMinute(int minute) {
        mMinute = minute;
    }

    public boolean isActive() {
        return mIsActive;
    }

    public void setActive(boolean isActive) {
        mIsActive = isActive;
    }
    @Override
    public String toString() {
        return "AlarmObject{" +
                "uniqueId=" + mUniqueId +
                ", hours=" + mHour +
                ", minutes=" + mMinute +
                '}';
    }
}
