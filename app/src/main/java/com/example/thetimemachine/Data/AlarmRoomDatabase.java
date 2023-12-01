package com.example.thetimemachine.Data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {AlarmItem.class}, version = 1, exportSchema = false)
public abstract class AlarmRoomDatabase extends RoomDatabase {
   public abstract AlarmDao alarmDao();

   private static volatile AlarmRoomDatabase alarmRoomDatabase;
   private static final int NUMBER_OF_THREADS = 4;
   static final ExecutorService databaseWriteExecutor =
         Executors.newFixedThreadPool(NUMBER_OF_THREADS);

   static AlarmRoomDatabase getDatabase(final Context context) {
      if (alarmRoomDatabase == null) {
         synchronized (AlarmRoomDatabase.class) {
            if (alarmRoomDatabase == null) {
               Context context1 = context.getApplicationContext();
               Builder<AlarmRoomDatabase> db = Room.databaseBuilder(context1, AlarmRoomDatabase.class, "raw_alarm_database");
               alarmRoomDatabase = db.build();
            }
         }
      }
      return alarmRoomDatabase;
   }
}
