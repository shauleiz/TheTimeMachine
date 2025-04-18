package com.product.thetimemachine.Data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AlarmDao {
   @Insert(onConflict = OnConflictStrategy.REPLACE)
   void insert(AlarmItem alarm);

   @Update
   void update(AlarmItem alarm);

   @Query("SELECT * from raw_alarm_table ORDER By createTime Asc")
   LiveData<List<AlarmItem>> getAlarms();

   @Delete
   void delete(AlarmItem alarm);
}
