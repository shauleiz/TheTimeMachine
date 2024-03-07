package com.example.thetimemachine.Data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;



/**
 * This is the repository of the Alarms and the relevant settings
 * The alarms are located in a LiveData list of 'rawAlarm' entries
 * The list is sorted by the key indicated by the settings.
 * This repository is written to by the UI.
 * It reads data from the ROOM by the constructor.
 * It writes data to the ROOM when data changes
 */
public class AlarmRepository {

   private final LiveData<List<AlarmItem>> liveAlarmItemList;
   //AlarmRoomDatabase alarmRoomDatabase;
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
    }

   public void UpdateAlarm(AlarmItem item)
   {
      AlarmRoomDatabase.databaseWriteExecutor.execute(() ->alarmDao.insert(item));
   }


    //public void UpdateAlarm(){}
    public LiveData<List<AlarmItem>> getAlarmList() {
       return liveAlarmItemList;}

}
