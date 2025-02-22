package com.product.thetimemachine;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;

import com.product.thetimemachine.Data.AlarmItem;
import com.product.thetimemachine.Data.AlarmRepository;

import java.util.List;

public class BootService extends LifecycleService{

   @Override
   public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
      super.onStartCommand(intent, flags, startId);

      Log.d("THE_TIME_MACHINE", "onStartCommand(): StartId="+ startId);
      //android.os.Debug.waitForDebugger();
      AlarmRepository repo = new AlarmRepository(getApplication());


      // Create repository observer.Will be twice called on BOOT
      // On machine boot (LOCKED_BOOT_COMPLETED) and after the user enters the
      // credentials (LOCKED_BOOT_COMPLETED)
      repo.getAlarmList().observe(this, alarms -> {
         if (alarms == null)
            Log.d("THE_TIME_MACHINE", "onStartCommand(): alarms  is null");
         else Log.d("THE_TIME_MACHINE", "onStartCommand(): alarms size=" + alarms.size());
         if (alarms != null) {
            for ( AlarmItem item : alarms) {
               if (item.isActive()) {
                  item.Exec();
                  Log.d("THE_TIME_MACHINE", "Schedule Alarm: " + item.getLabel());
               }
            }
         }
         stopSelf();
      });

      return START_STICKY;
   }

   @Nullable
   @Override
   public IBinder onBind(@NonNull Intent intent) {
      return super.onBind(intent);
   }

   @Override
   public void onDestroy() {
      Log.d("THE_TIME_MACHINE", "onDestroy()");
      super.onDestroy();
   }


}
