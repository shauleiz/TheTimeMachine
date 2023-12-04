package com.example.thetimemachine;


import android.app.AlarmManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.example.thetimemachine.Data.AlarmItem;

// Schedule/Cancel Alarms using Alarm Manager
public class Scheduler {

   private Context context;
   private AlarmManager alarmManager;

   public Scheduler(Context context){
      this.context = context;

      // Get Alarm Manager and test (For newer versions) if device supports Exact Alarms
      // TODO: If not supported - do something or abort
      alarmManager = context.getSystemService(AlarmManager.class);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
         boolean isExact = alarmManager.canScheduleExactAlarms();
         if (isExact)
            Log.d("Alarm", "Exact Alarm - SUPPORTED");
         else
            Log.w("Alarm", "Exact Alarm - NOT SUPPORTED");
      }
   }

   /*
   * Schedule a new alarm
   * All data is encapsulated in the AlarmItem
   * */
   public void Schedule(AlarmItem alarm) {
   }
   public void Cancel(AlarmItem alarm) {
   }

}
