package com.example.thetimemachine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {

   private void startAlarmService(Context context, Intent intent) {
      Intent intentService = new Intent(context, AlarmService.class);
      intentService.putExtra("LABEL", intent.getStringExtra("LABEL")); // TODO: Replace by XML string
      intentService.putExtra("HOUR", intent.getIntExtra("HOUR", -1));
      intentService.putExtra("MINUTE", intent.getIntExtra("MINUTE", -1));
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
         context.startForegroundService(intentService);
      else
         context.startService(intentService);
   }

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
      Log.i("THE_TIME_MACHINE", String.format("Action = %s", action));

      // Just to prevent a crash
      if (action == null)
         return;

      switch (action){
         case Intent.ACTION_BOOT_COMPLETED:
            // TODO: Machine boot finished - must reschedule all active Alarms
            String toastText = String.format("Alarm Reboot");
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            //startRescheduleAlarmsService(context);
            Log.i("THE_TIME_MACHINE", "Alarm Reboot");
            break;

         case "action_stop":
            // Stop the alarm by killing the Alarm Service
            Log.i("THE_TIME_MACHINE", "Action is STOP");
            context.stopService(new Intent(context, AlarmService.class));
            break;

         case "action_snooze":
            String sLabel = intent.getStringExtra("LABEL");
            String stxt = new String("Snooze Label: " + sLabel);
            Log.i("THE_TIME_MACHINE", stxt);
            break;

         case "action_alarm":
            // Message from Alarm manager - Get alarm parameters and start alarm service

            // Get Extra Data
            String Label = intent.getStringExtra("LABEL");
            int h = intent.getIntExtra("HOUR", -1);
            int m = intent.getIntExtra("MINUTE", -1);

            // Debug information
            String txt = new String("Label: " + Label + "  "+ h + ":" + m);
            Toast.makeText(context, txt, Toast.LENGTH_LONG).show();
            Log.i("THE_TIME_MACHINE", txt);

            // Start the service that Displays Snooze/Stop Page
            // and Sounds alarm and vibration
            startAlarmService( context,  intent);

         default:
            Log.w("THE_TIME_MACHINE", "Action not recognized");
      }

   }
}
