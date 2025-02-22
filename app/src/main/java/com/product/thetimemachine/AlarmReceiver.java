package com.product.thetimemachine;

import static com.product.thetimemachine.AlarmService.K_TYPE;
import static com.product.thetimemachine.AlarmService.SNOOZE;
import static com.product.thetimemachine.AlarmService.STOP;
import static com.product.thetimemachine.Data.AlarmItem.FRIDAY;
import static com.product.thetimemachine.Data.AlarmItem.K_CSNOOZE;
import static com.product.thetimemachine.Data.AlarmItem.K_HOUR;
import static com.product.thetimemachine.Data.AlarmItem.K_LABEL;
import static com.product.thetimemachine.Data.AlarmItem.K_MINUTE;
import static com.product.thetimemachine.Data.AlarmItem.K_ONE_OFF;
import static com.product.thetimemachine.Data.AlarmItem.K_WEEKDAYS;
import static com.product.thetimemachine.Data.AlarmItem.MONDAY;
import static com.product.thetimemachine.Data.AlarmItem.SATURDAY;
import static com.product.thetimemachine.Data.AlarmItem.SUNDAY;
import static com.product.thetimemachine.Data.AlarmItem.THURSDAY;
import static com.product.thetimemachine.Data.AlarmItem.TUESDAY;
import static com.product.thetimemachine.Data.AlarmItem.WEDNESDAY;
import static com.product.thetimemachine.Data.AlarmRoomDatabase.insertAlarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.product.thetimemachine.Data.AlarmItem;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
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

      alarm.incSnoozeCounter();
      alarm.Exec();

      /* DEBUG */
      String s = String.format(Locale.US,"Receiver.snoozing(): Label=%s - Hour=%d - Minute=%d - Snooze Counter=%d",
            b.getString(K_LABEL), b.getInt(K_HOUR), b.getInt(K_MINUTE), b.getInt(K_CSNOOZE));
      Log.d("THE_TIME_MACHINE", s);

      // Mark alarm as not ringing
      alarm.setRinging(false);

      // Force update of UI
      insertAlarm(alarm,  context);

   }

   public static void stopping(Context context, Intent intent){

      // Stop current alarm service
      context.stopService(new Intent(context, AlarmService.class));

      // Create a new alarm item
      Bundle b = intent.getExtras();
      if (b==null) return;
      AlarmItem alarm = new AlarmItem(b);
      alarm.resetSnoozeCounter();

         // One-time alarm becomes Inactive after been stopped
      if (alarm.isOneOff())
         alarm.setActive(false);

      // Mark alarm as not ringing
      alarm.setRinging(false);

      insertAlarm(alarm,  context);

      /* DEBUG */
      Bundle ab = alarm.getBundle();
      String s = String.format("Receiver.stopping(): Label=%s - Hour=%d - Minute=%d - Snooze Counter=%d = Action=" + intent.getAction(),
            ab.getString(K_LABEL), ab.getInt(K_HOUR), ab.getInt(K_MINUTE), ab.getInt(K_CSNOOZE));
      Log.d("THE_TIME_MACHINE", s);   }

   private void startAlarmService(Context context, Intent intent) {

      Intent intentService = new Intent(context, AlarmService.class);
      intentService.putExtras(Objects.requireNonNull(intent.getExtras()));

      context.startForegroundService(intentService);

      Bundle b = intent.getExtras();
      if (b==null) return;

      // Mark this alarm as ringing
      AlarmItem alarm = new AlarmItem(b);
      alarm.setRinging(true);
      insertAlarm(alarm,  context);

      /* DEBUG */
      String s = String.format(Locale.US, "startAlarmService(): Label=%s - Hour=%d - Minute=%d", b.getString(K_LABEL), b.getInt(K_HOUR), b.getInt(K_MINUTE));
      Log.i("THE_TIME_MACHINE", s);


   }

   private void booting(Context context){
      Log.d("THE_TIME_MACHINE", "booting()");
      Intent intentService = new Intent(context, BootService.class);
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
      String type = intent.getStringExtra(K_TYPE);
      Log.i("THE_TIME_MACHINE", String.format("Action = %s", action));
      Log.i("THE_TIME_MACHINE", String.format("Type = %s", type));

      // TODO: Machine boot finished - must reschedule all active Alarms
      if (Intent.ACTION_BOOT_COMPLETED.equals(action) ||
            "android.intent.action.LOCKED_BOOT_COMPLETED".equals(action) ||
            Intent.ACTION_PACKAGE_REPLACED.equals(action) ||
            Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)) {
         //String toastText = "Alarm Reboot";
         booting(context);
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
      boolean oneOff = bundle.getBoolean(K_ONE_OFF);
      if (oneOff)
         return true;

      // Get the list of weekdays to which the alarm is scheduled
      int weekdays = bundle.getInt(K_WEEKDAYS);

      // Get today's weekday
      DayOfWeek today = LocalDateTime.now().getDayOfWeek();

      // Compare
      switch (today){
         case SUNDAY:
            if ((weekdays & SUNDAY) != 0)
               return true;
         case MONDAY:
            if ((weekdays & MONDAY) != 0)
               return true;
         case TUESDAY:
            if ((weekdays & TUESDAY) != 0)
               return true;
         case WEDNESDAY:
            if ((weekdays & WEDNESDAY) != 0)
               return true;
         case THURSDAY:
            if ((weekdays & THURSDAY) != 0)
               return true;
         case FRIDAY:
            if ((weekdays & FRIDAY) != 0)
               return true;
         case SATURDAY:
            if ((weekdays & SATURDAY) != 0)
               return true;

         default:
            return false;
      }
   }
}
