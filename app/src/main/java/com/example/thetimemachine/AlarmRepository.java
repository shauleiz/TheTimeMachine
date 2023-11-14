package com.example.thetimemachine;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Calendar;
import androidx.annotation.NonNull;
import androidx.room.Entity;


/**
 * This is the repository of the Alarms and the relevant settings
 * The alarms are located in a LiveData list of 'rawAlarm' entries
 * The list is sorted by the key indicated by the settings.
 * This repository is written to by the UI.
 * It reads data from the ROOM by the constructor.
 * It writes data to the ROOM when data changes
 */
public class AlarmRepository {

    private ArrayList<RawAlarmItem> rawAlarmItems;
    private MutableLiveData<ArrayList<RawAlarmItem>> liveRawAlarmItemList;


    public AlarmRepository(){
        rawAlarmItems = new ArrayList<>();
        liveRawAlarmItemList = new MutableLiveData<>();
    }

   // Input is Alarm details
   // If ID does NOT exist - create new entry and add it to the list
   // If ID DOES exist - change entry values and put it back on the list
    public void AddAlarm(int _hour, int _minute, String _label, boolean _active, long _id){
        // Create the item to add/replace
        RawAlarmItem item = new RawAlarmItem( _hour,  _minute,  _label,  _active,  _id);

        // If list not empty - check if item exist (same ID)
        if (rawAlarmItems.size()>=0) {
            long inId = _id;
            int index = FindItemById(inId);

            // Index >-1 means this item does exist in the list - remove it
            if (index >= 0)
                rawAlarmItems.remove(index);
        }
        // Add item
        rawAlarmItems.add(item);
        liveRawAlarmItemList.setValue(rawAlarmItems);

    }
    public void DeleteAlarm(long _id){
        // Empty list? NO-OP
        if (rawAlarmItems.size()<0)
            return;

        // Find entry by ID. If found then remove it
        int index = FindItemById(_id);
        if (index >= 0)
            rawAlarmItems.remove(index);
        liveRawAlarmItemList.setValue(rawAlarmItems);
    }

    // Return index of item that corresponds to the given ID
    // Return -1 if not found
    private int FindItemById(long id) {
        for (int i = 0; i < rawAlarmItems.size(); i++)
            if (id == rawAlarmItems.get(i).id)
                return i;
        return -1;
    }

    //public void UpdateAlarm(){}
    public MutableLiveData<ArrayList<RawAlarmItem>> getAlarmList() {return liveRawAlarmItemList;}
   //@Entity(tableName = "alarm_table")
   public class RawAlarmItem{

        public RawAlarmItem(){};

        // Constructor
        // Creates a Raw Alarm entry
        public RawAlarmItem(int _hour, int _minute, String _label, boolean _active, long _id){

            // Convert time HH:MM to minutes-since-midnight
            minutesSinceMidnight = _hour*60+_minute;

            // Copy rest of items
            id = _id;
            label = _label;
            active = _active;
        };
        public int minutesSinceMidnight;
        public long id;
        public boolean active=true; // TODO: Should be set by constructor
        public boolean oneTime = true;// TODO: Should be set by constructor
        public String label;
    }
}
