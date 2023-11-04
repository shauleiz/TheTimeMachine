package com.example.thetimemachine;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Calendar;
import java.util.ArrayList;

/*
 *      Alarm Clock ViewModel Class
 *  Holds data for the Alarm Clock UI
 *  - Alarm Clock activity
 *  - Alarm Clock Setting fragment
 *
 *  Also interacts with the backup data base (Room)
 */
public class AlarmViewModel extends ViewModel {

    public SetUpAlarmValues setUpAlarmValues;
    public MutableLiveData<ArrayList<AlarmItem>> LivealarmList;
    private ArrayList<AlarmItem> AlarmList;

    // Constructor
    public AlarmViewModel() {
        setUpAlarmValues = new SetUpAlarmValues();
        LivealarmList = new MutableLiveData<ArrayList<AlarmItem>>();
        AlarmList = new ArrayList<AlarmItem>();
    }

    public void AddAlarm(int _hour, int _minute, String _label, boolean _active) {
        AlarmItem item = new AlarmItem(_hour, _minute, _label, _active);
        AlarmList.add(item);
        LivealarmList.setValue(AlarmList);
    }

    public void UpdateAlarm(int _hour, int _minute, String _label, boolean _active, int _position) {
        AlarmList = LivealarmList.getValue();
        AlarmItem item = AlarmList.get(_position);
        item.hour = _hour;
        item.minute = _minute;
        item.label = _label;
        LivealarmList.setValue(AlarmList);
    }

    public MutableLiveData<ArrayList<AlarmItem>> getAlarmList() {
        return LivealarmList;
    }

    // This class hold an item in the Alarm List
    public class AlarmItem {
        long createtime;
        private int hour, minute;
        private String label;
        private boolean active;

        public AlarmItem(int _hour, int _minute, String _label, boolean _active) {
            hour = _hour;
            minute = _minute;
            label = _label;
            active = _active;

            // Sanity check
            if (hour < 0 || hour > 23 || minute < 0 || minute > 59)
                active = false;

            // Add time of creation in milliseconds
            Calendar calendar = Calendar.getInstance();
            createtime = calendar.getTimeInMillis();
        }

        // TODO: Implement getters and setters

        public int getHour() {
            return hour;
        }

        public int getMinute() {
            return minute;
        }

        public String getLabel() {
            return label;
        }

        public boolean isActive() {
            return active;
        }
    }

    // This Class holds the values of the alarm that is being added/modified
    public class SetUpAlarmValues {
        MutableLiveData<Integer> hour, minute;
        MutableLiveData<String> label;

        // Default constructor
        public SetUpAlarmValues() {
            this.hour = new MutableLiveData<Integer>();
            this.minute = new MutableLiveData<Integer>();
            this.label = new MutableLiveData<String>();
            ResetValues();
        }

        public SetUpAlarmValues(int hour, int minute, String label, boolean newAlarm) {
            this.hour = new MutableLiveData<Integer>();
            this.minute = new MutableLiveData<Integer>();
            this.label = new MutableLiveData<String>();

            if (newAlarm) { // Add new alarm
                // TODO: Get the default values and ignore the passed values
                this.hour.setValue(hour);
                this.minute.setValue(minute);
                this.label.setValue(label);
            }
            // TODO: Modify alarm
        }

        public void ResetValues(){
            // TODO: Reset to user defined values
            hour.setValue(00);
            minute.setValue(00);
            label.setValue("");
        }

        public void GetValuesFromList(int position){
            // Sanity check
            if (position<0)
                return;
            hour.setValue(AlarmList.get(position).getHour());
            minute.setValue(AlarmList.get(position).getMinute());
            label.setValue(AlarmList.get(position).getLabel());
        }

        /* Getter Methods */
        public MutableLiveData<Integer> getHour() {
            return hour;
        }

        // Reset Alarm
        /* Setter Methods*/
        public void setHour(int hour) {
            this.hour.setValue(hour);
        }

        public MutableLiveData<Integer> getMinute() {
            return minute;
        }

        public void setMinute(int minute) {
            this.minute.setValue(minute);
        }

        public MutableLiveData<String> getLabel() {
            return label;
        }

        public void setLabel(String label) {
            String t = this.label.getValue().toString();
            if (!(t.equals(label)))
                this.label.setValue(label);
        }

    }
}
