package com.example.thetimemachine;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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
    // Constructor
    public AlarmViewModel(){
        setUpAlarmValues = new SetUpAlarmValues();
    }

    // This Class holds the values of the alarm that is being added/modified
    public class SetUpAlarmValues{
        MutableLiveData<Integer> hour, minute;
        MutableLiveData<String> label;

        // Default constructor
        // TODO: Take Default values
        public SetUpAlarmValues() {
            this.hour = new MutableLiveData<Integer>();
            this.minute = new MutableLiveData<Integer>();
            this.label = new MutableLiveData<String>();

            this.hour.setValue(00);
            this.minute.setValue(00);
            this.label.setValue("");
        }

        public SetUpAlarmValues(int hour, int minute, String label, boolean newAlarm){
            this.hour = new MutableLiveData<Integer>();
            this.minute = new MutableLiveData<Integer>();
            this.label = new MutableLiveData<String>();

            if (newAlarm){ // Add new alarm
              // TODO: Get the default values and ignore the passed values
              this.hour.setValue(hour);
              this.minute.setValue(minute);
              this.label.setValue(label);
          }
          // TODO: Modify alarm
        }

        public void setHour(int hour) {
            this.hour.setValue(hour);
        }

        public void setMinute(int minute) {
            this.minute.setValue(minute);
        }

        public void setLabel(String label) {
            String t = this.label.getValue().toString();
            if (!(t.equals(label)))
                this.label.setValue(label);
        }

        public MutableLiveData<Integer> getHour() {
            return hour;
        }

        public MutableLiveData<Integer> getMinute() {
            return minute;
        }

        public MutableLiveData<String> getLabel() {
            return label;
        }
    }
    public void AddAlarm(Alarm alarm) {
    }
}
