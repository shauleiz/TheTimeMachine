package com.example.thetimemachine.Data;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


/**
 * This is the repository of the Alarms and the relevant settings
 * The alarms are located in a LiveData list of 'rawAlarm' entries
 * The list is sorted by the key indicated by the settings.
 * This repository is written to by the UI.
 * It reads data from the ROOM by the constructor.
 * It writes data to the ROOM when data changes
 */
public class AlarmRepository {

    private List<RawAlarmItem> rawAlarmItems;
    private MutableLiveData<List<RawAlarmItem>> liveRawAlarmItemList;
   AlarmRoomDatabase alarmRoomDatabase;
   AlarmDao alarmDao;

    public AlarmRepository(Application application){
        rawAlarmItems = new ArrayList<>();
        liveRawAlarmItemList = new MutableLiveData<>();

       // Room Database
       alarmRoomDatabase = AlarmRoomDatabase.getDatabase(application);
       alarmDao = alarmRoomDatabase.alarmDao();
       LiveData<List<AlarmRepository.RawAlarmItem>>  myList = alarmDao.getRawAlarm();
       rawAlarmItems =  alarmDao.getRawAlarm().getValue();
       if (rawAlarmItems == null)
          rawAlarmItems = new ArrayList<>();

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

            // Index >-1 means this item does exist in the list - replace it
            if (index >= 0)
                rawAlarmItems.set(index, item);
            else // Index <0 - add item to list
               rawAlarmItems.add(item);
        }
        // Update list
        liveRawAlarmItemList.setValue(rawAlarmItems);

        // Add item to Room Database
       AlarmRoomDatabase.databaseWriteExecutor.execute(() ->alarmDao.insert(item));
      LiveData<List<RawAlarmItem>> Items =  alarmDao.getRawAlarm(); // Debug
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
    public MutableLiveData<List<RawAlarmItem>> getAlarmList() {return liveRawAlarmItemList;}
   @Entity(tableName = "raw_alarm_table")
   public static class RawAlarmItem{

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
      @PrimaryKey
      @NonNull
        public long id;
        public boolean active=true; // TODO: Should be set by constructor
        public boolean oneTime = true;// TODO: Should be set by constructor
        public String label;
    }
}
