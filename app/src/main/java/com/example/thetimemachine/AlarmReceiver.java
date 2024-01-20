package com.example.thetimemachine;

import static com.example.thetimemachine.AlarmService.K_TYPE;
import static com.example.thetimemachine.AlarmService.SNOOZE;
import static com.example.thetimemachine.AlarmService.STOP;
import static com.example.thetimemachine.Data.AlarmItem.K_CSNOOZE;
import static com.example.thetimemachine.Data.AlarmItem.K_HOUR;
import static com.example.thetimemachine.Data.AlarmItem.K_LABEL;
import static com.example.thetimemachine.Data.AlarmItem.K_MINUTE;
import static com.example.thetimemachine.Data.AlarmItem.K_ONEOFF;
import static com.example.thetimemachine.Data.AlarmItem.K_WEEKDAYS;

import static com.example.thetimemachine.Data.AlarmItem.SUNDAY;
import static com.example.thetimemachine.Data.AlarmItem.MONDAY;
import static com.example.thetimemachine.Data.AlarmItem.TUESDAY;
import static com.example.thetimemachine.Data.AlarmItem.WEDNESDAY;
import static com.example.thetimemachine.Data.AlarmItem.THURSDAY;
import static com.example.thetimemachine.Data.AlarmItem.FRIDAY;
import static com.example.thetimemachine.Data.AlarmItem.SATURDAY;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.thetimemachine.Data.AlarmDao;
import com.example.thetimemachine.Data.AlarmItem;
import com.example.thetimemachine.Data.AlarmRoomDatabase;

import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {

   public static void snoozing(Context context, Intent intent) {
      // Stop current alarm service
      context.stopService(new Intent(context, AlarmService.class));

      // Create a new alarm item
      Bundle b = intent.getExtras();
      if (b==null) return;
      AlarmItem alarm = new AlarmItem(b);

      // Snooze for 10 minutes
      alarm.Exec();

      /* DEBUG */
      String s = String.format(Locale.US,"Receiver.snoozing(): Label=%s - Hour=%d - Minute=%d - Snooze Counter=%d",
            b.getString(K_LABEL), b.getInt(K_HOUR), b.getInt(K_MINUTE), b.getInt(K_CSNOOZE));
      Log.d("THE_TIME_MACHINE", s);

   }

   public static void stopping(Context context, Intent intent){

      // Stop current alarm service
      context.stopService(new Intent(context, AlarmService.class));

      // Create a new alarm item
      Bundle b = intent.getExtras();
      if (b==null) return;
      AlarmItem alarm = new AlarmItem(b);
      alarm.resetSnoozeCounter();
      if (alarm.isOneOff()) {
         // One-time alarm becomes Inactive after been stopped
         alarm.setActive(false);
         AlarmRoomDatabase db = AlarmRoomDatabase.getDatabase(context);
         AlarmDao alarmDao = db.alarmDao();
         AlarmRoomDatabase.databaseWriteExecutor.execute(() ->alarmDao.insert(alarm));
      }
      else
         // Schedule the next alarm
         alarm.Exec();

      /* DEBUG */
      Bundle ab = alarm.getBundle();
      String s = String.format("Receiver.stopping(): Label=%s - Hour=%d - Minute=%d - Snooze Counter=%d = Action=" + intent.getAction(),
            ab.getString(K_LABEL), ab.getInt(K_HOUR), ab.getInt(K_MINUTE), ab.getInt(K_CSNOOZE));
      Log.d("THE_TIME_MACHINE", s);   }

   private void startAlarmService(Context context, Intent intent) {

      Intent intentService = new Intent(context, AlarmService.class);
      intentService.putExtras(Objects.requireNonNull(intent.getExtras()));

      context.startForegroundService(intentService);

      /* DEBUG */
      Bundle b = intent.getExtras();
      String s = String.format(Locale.US, "startAlarmService(): Label=%s - Hour=%d - Minute=%d", b.getString(K_LABEL), b.getInt(K_HOUR), b.getInt(K_MINUTE));
      Log.i("THE_TIME_MACHINE", s);


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
      String type = intent.getStringExtra(K_TYPE);
      Log.i("THE_TIME_MACHINE", String.format("Action = %s", action));
      Log.i("THE_TIME_MACHINE", String.format("Type = %s", type));

      // TODO: Machine boot finished - must reschedule all active Alarms
      if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
         String toastText = "Alarm Reboot";
         Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
         //startRescheduleAlarmsService(context);
         Log.i("THE_TIME_MACHINE", "Alarm Reboot");

         // Stop the alarm by killing the Alarm Service
      } else if (Objects.equals(type, STOP)) {
         Log.i("THE_TIME_MACHINE", "Action is STOP");
         stopping(context, intent);
      } else if (Objects.equals(type, SNOOZE)) {
         Log.i("THE_TIME_MACHINE", "Action is SNOOZE");
         snoozing(context, intent);
      }
      // Message from Alarm manager - Get alarm parameters and start alarm service
      else {
         // Get Extra Data for Log
         Bundle inBundle = intent.getExtras();
         assert inBundle != null;
         String label = inBundle.getString(K_LABEL);
         int h = inBundle.getInt(K_HOUR, -1);
         int m = inBundle.getInt(K_MINUTE, -1);


         // Debug information
         String txt = "Label: " + label + "  " + h + ":" + m;
         Toast.makeText(context, txt, Toast.LENGTH_LONG).show();
         Log.i("THE_TIME_MACHINE", txt);

         // If alarm is intended to sound today then
         // Start the service that Displays Snooze/Stop Page
         // Sounds alarm and vibration
         if (isToday(inBundle))
            startAlarmService(context, intent);
      }
   }

   private boolean isToday(Bundle bundle){
      // If this is a one-off alarm then it is for today
      boolean oneOff = bundle.getBoolean(K_ONEOFF);
      if (oneOff)
         return true;

      // Get the list of weekdays to which the alarm is scheduled
      int weekdays = bundle.getInt(K_WEEKDAYS);

      // Get today's weekday
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(System.currentTimeMillis());
      int today = calendar.get(Calendar.DAY_OF_WEEK);

      Log.i("THE_TIME_MACHINE", String.format("isToday(): Today = %d :: Weekdays = %d", today, weekdays));

      // Compare
      switch (today){
         case Calendar.SUNDAY:
            if ((weekdays & SUNDAY) != 0)
               return true;
         case Calendar.MONDAY:
            if ((weekdays & MONDAY) != 0)
               return true;
         case Calendar.TUESDAY:
            if ((weekdays & TUESDAY) != 0)
               return true;
         case Calendar.WEDNESDAY:
            if ((weekdays & WEDNESDAY) != 0)
               return true;
         case Calendar.THURSDAY:
            if ((weekdays & THURSDAY) != 0)
               return true;
         case Calendar.FRIDAY:
            if ((weekdays & FRIDAY) != 0)
               return true;
         case Calendar.SATURDAY:
            if ((weekdays & SATURDAY) != 0)
               return true;

         default:
            return false;
      }
   }
}
