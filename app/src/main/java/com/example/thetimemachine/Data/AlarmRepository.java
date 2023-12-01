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

    //private List<RawAlarmItem> rawAlarmItems;
   private LiveData<List<AlarmItem>> liveAlarmItemList;
   AlarmRoomDatabase alarmRoomDatabase;
   AlarmDao alarmDao;

    public AlarmRepository(Application application){
       AlarmRoomDatabase db = AlarmRoomDatabase.getDatabase(application);
       alarmDao = db.alarmDao();
       liveAlarmItemList = alarmDao.getAlarms();

    }

   // Input is Alarm details
    public void AddAlarm(AlarmItem item){
        // Add item to Room Database
       AlarmRoomDatabase.databaseWriteExecutor.execute(() ->alarmDao.insert(item));
    }
    public void DeleteAlarm(AlarmItem item){
       AlarmRoomDatabase.databaseWriteExecutor.execute(() ->alarmDao.delete(item));
        // Find entry by ID. If found then remove it
       // int index = FindItemById(_id);
       // if (index >= 0)
        //    AlarmItems.remove(index);
      //  liveRawAlarmItemList.setValue(rawAlarmItems);
    }

   public void UpdateAlarm(AlarmItem item)
   {
      AlarmRoomDatabase.databaseWriteExecutor.execute(() ->alarmDao.insert(item));
   }

   /* // Return index of item that corresponds to the given ID
    // Return -1 if not found
    private int FindItemById(long id) {
        for (int i = 0; i < rawAlarmItems.size(); i++)
            if (id == rawAlarmItems.get(i).id)
                return i;
        return -1;
    }*/

    //public void UpdateAlarm(){}
    public LiveData<List<AlarmItem>> getAlarmList() {
       return liveAlarmItemList;}
   /*
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

        public long id;
        public boolean active=true; // TODO: Should be set by constructor
        public boolean oneTime = true;// TODO: Should be set by constructor
        public String label;
    }*/
}
