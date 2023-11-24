package com.example.thetimemachine.Data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AlarmDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   void insert(AlarmRepository.RawAlarmItem rawAlarm);

   @Update
   void update(AlarmRepository.RawAlarmItem rawAlarm);

   @Query("SELECT * from raw_alarm_table ORDER By id Asc")
   LiveData<List<AlarmRepository.RawAlarmItem>> getRawAlarm();

   @Query("DELETE from raw_alarm_table")
   void deleteAll();
}
