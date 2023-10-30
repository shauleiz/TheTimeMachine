package com.example.thetimemachine;

import java.util.Calendar;

/**
 *  Class that holds a single alarm item
 */
public class Alarm {
    private String label;
    private int alarmId;
    private int hour, minute;

    // Constructor - Structure that  holds definition of a single alarm entry
    public Alarm(int hour, int minute, String label){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        this.alarmId = calendar.get(Calendar.MILLISECOND);
        this.hour =  hour;
        this.minute = minute;
        this.label = label;
    }

    // Getter functions
    public int getAlarmId() {
        return alarmId;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public String getLabel() {
        return label;
    }
}
