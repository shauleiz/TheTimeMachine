package com.example.thetimemachine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {


   /*
   * Receive broadcast messages destined to this Application
   * The Application does not have to run for this method to be activated
   * Messages Received:
   * + System message 'ACTION_BOOT_COMPLETED' is registered in file AndroidManifest.xml
   * + App messages where the intent destination is 'AlarmReceiver.class'
   *
   * */
   @Override
   public void onReceive(Context context, Intent intent) {
      String action = intent.getAction();

      // Machine boot finished - must reschedule all active Alarms
      if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
         String toastText = String.format("Alarm Reboot");
         Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
         //startRescheduleAlarmsService(context);
         Log.i("SIMPLE_ALARM_CLOCK", "Alarm Reboot");
      }
      // Message from Alarm manager - Get alarm parameters and start alarm service
      else {
         // Get Extra Data
         String Label = intent.getStringExtra("LABEL");
         int h = intent.getIntExtra("HOUR", -1);
         int m = intent.getIntExtra("MINUTE", -1);

         // Debug information
         String txt = new String("Label: " + Label + h + ":" + m);
         Toast.makeText(context, txt, Toast.LENGTH_LONG).show();
         Log.i("SIMPLE_ALARM_CLOCK", txt);
      }
   }
}
