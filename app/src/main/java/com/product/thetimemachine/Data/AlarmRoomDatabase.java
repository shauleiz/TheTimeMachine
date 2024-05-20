package com.product.thetimemachine.Data;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
      entities = {AlarmItem.class},
      version = 3,
      autoMigrations = {@AutoMigration(from = 1, to = 3), @AutoMigration(from = 2, to = 3)},
      exportSchema = true)

public abstract class AlarmRoomDatabase extends RoomDatabase {
   public abstract AlarmDao alarmDao();

   private static volatile AlarmRoomDatabase alarmRoomDatabase;
   private static final int NUMBER_OF_THREADS = 4;
   public static final ExecutorService databaseWriteExecutor =
         Executors.newFixedThreadPool(NUMBER_OF_THREADS);

   static void removeDB(Context context){
      Context context1 = context.createDeviceProtectedStorageContext();
      boolean deleted;
      // Brute Force removal of Room database
      String[] list =  context1.databaseList ();
      for (int i=0; i<list.length; i++)
         deleted = context1.deleteDatabase(list[i]);
   }
   public static AlarmRoomDatabase getDatabase(final Context context) {
      if (alarmRoomDatabase == null) {
         synchronized (AlarmRoomDatabase.class) {
            if (alarmRoomDatabase == null) {
               // Brute Force removal of Room database
               // removeDB( context);
               // Direct Boot supported: Place the Room BD in the device encrypted storage
               Context context1 = context.createDeviceProtectedStorageContext();
               Builder<AlarmRoomDatabase> db = Room.databaseBuilder(context1, AlarmRoomDatabase.class, "raw_alarm_database");
               alarmRoomDatabase = db.build();
            }
         }
      }
      return alarmRoomDatabase;
   }

   public static void insertAlarm(AlarmItem item, Context context){
      AlarmRoomDatabase db = getDatabase(context);
      AlarmDao alarmDao = db.alarmDao();
      AlarmRoomDatabase.databaseWriteExecutor.execute(() ->alarmDao.insert(item));
   }
}
