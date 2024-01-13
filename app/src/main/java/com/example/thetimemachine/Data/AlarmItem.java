package com.example.thetimemachine.Data;

import static com.example.thetimemachine.AlarmService.ALARM;
import static com.example.thetimemachine.AlarmService.K_TYPE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.thetimemachine.AlarmReceiver;
import com.example.thetimemachine.Application.TheTimeMachineApp;
import com.example.thetimemachine.R;

import java.util.Calendar;

// This class hold an item in the Alarm List
@Entity(tableName = "raw_alarm_table")
public class AlarmItem {
   @PrimaryKey
   @NonNull
   private long createTime;
   private int hour, minute, snoozeCounter;
   private String label;
   private boolean active, oneOff;
   private int weekDays;

   public static final String K_HOUR = "HOUR";
   public static final String K_MINUTE = "MINUTE";
   public static final String K_LABEL = "LABEL";
   public static final String K_ACTIVE = "ACTIVE";
   public static final String K_CTIME = "C_TIME";
   public static final String K_CSNOOZE = "COUNTER_SNOOZE";

   public static final String K_WEEKDAYS = "WEEKDAYS";
   public static final String K_ONEOFF = "ONE_OFF";

   // Masks for days of the week.
   // If ONEOFF is set, the rest of the fields are to be ignored
   // public static final int ONEOFF = 0x80;
   public static final int SUNDAY = 0x01;
   public static final int MONDAY = 0x02;
   public static final int TUESDAY = 0x04;
   public static final int WEDNESDAY = 0x08;
   public static final int THURSDAY = 0x10;
   public static final int FRIDAY = 0x20;
   public static final int SATURDAY = 0x40;




   // Constructor of Alarm Item - Create from a bundle
   public AlarmItem(@NonNull Bundle inBundle){
      hour = inBundle.getInt(K_HOUR);
      minute = inBundle.getInt(K_MINUTE);
      label = inBundle.getString(K_LABEL);
      active = inBundle.getBoolean(K_ACTIVE);
      createTime = inBundle.getLong(K_CTIME, -1);
      oneOff = inBundle.getBoolean(K_ONEOFF, true);
      weekDays = inBundle.getInt(K_WEEKDAYS, 0);

      // Uninitialized Snooze Counter
      snoozeCounter = -1;

      // Sanity check
      if (hour < 0 || hour > 23 || minute < 0 || minute > 59)
         active = false;

      if (createTime<0){
         Calendar calendar = Calendar.getInstance();
         createTime = calendar.getTimeInMillis();
      }

   }

                    // Constructor of Alarm Item - create time calculated internally
   public AlarmItem(int _hour, int _minute, String _label, boolean _active) {
      hour = _hour;
      minute = _minute;
      label = _label;
      active = _active;

      // Uninitialized Snooze Counter
      snoozeCounter = -1;

      // Sanity check
      if (hour < 0 || hour > 23 || minute < 0 || minute > 59)
         active = false;

      // Default: One-Off (All weekdays disabled)
      weekDays = 0;
      oneOff = true;

      // Add time of creation in milliseconds
      Calendar calendar = Calendar.getInstance();
      createTime = calendar.getTimeInMillis();
   }

   // Constructor of Alarm Item - create time pushed from outside
   public AlarmItem(int _hour, int _minute, String _label, boolean _active, long _createTime) {
      hour = _hour;
      minute = _minute;
      label = _label;
      active = _active;

      // Uninitialized Snooze Counter
      snoozeCounter = -1;

      // Sanity check
      if (hour < 0 || hour > 23 || minute < 0 || minute > 59)
         active = false;

      // Add time of creation (of original alarm) in milliseconds
      createTime = _createTime;

      // Default: One-Off (All weekdays disabled)
      weekDays = 0;
      oneOff = true;
   }

   public AlarmItem() {}

   public int getHour() {
      return hour;
   }

   public int getMinute() {
      return minute;
   }

   public String getLabel() {
      return label;
   }

   public boolean isActive() {
      return active;
   }

   public long getCreateTime() {
      return createTime;
   }

   public int getSnoozeCounter(){
      return snoozeCounter;
   }



   public Bundle getBundle(){
      Bundle b = new Bundle();

      b.putInt(K_HOUR, hour);
      b.putInt(K_MINUTE, minute);
      b.putString(K_LABEL,label);
      b.putBoolean(K_ACTIVE, active);
      b.putLong(K_CTIME,createTime);
      b.putBoolean(K_ONEOFF, oneOff);
      b.putInt(K_WEEKDAYS,weekDays);
      return b;
   }

   // This is a One-Off alarm if the ONEOFF bit is set or (by default) none of the days is set
   public boolean isOneOff(){ return oneOff;}
   public int getWeekDays(){return weekDays;}

   public void setActive(boolean active) {
      this.active = active;
   }

   public void setHour(int hour) {this.hour = hour;}

   public void setLabel(String label) {this.label = label;}

   public void setMinute(int minute) {this.minute = minute;}

   public void setCreateTime(long createTime) {this.createTime = createTime;}

   public void setSnoozeCounter(int snoozeCounter){ this.snoozeCounter = snoozeCounter;}

   public int incSnoozeCounter(){return snoozeCounter++;}

   // Set/reset One-of bit
   public void setOneOff(boolean set){ oneOff = set;}

   // Copy the days mask without affecting the one-off bit
   public void setWeekDays(int mask){ weekDays = mask;}

   private long alarmTimeInMillis() {
      // Get the time for the next Alarm, in Milliseconds
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(System.currentTimeMillis());
      calendar.set(Calendar.HOUR_OF_DAY, hour);
      calendar.set(Calendar.MINUTE, minute);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);

      // if alarm time has already passed, increment day by 1
      if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
         calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
      }

      return calendar.getTimeInMillis();
   }

   public boolean isToday(){
      // Get time of today's midnight (minus a second)
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(System.currentTimeMillis());
      calendar.set(Calendar.HOUR_OF_DAY, 23);
      calendar.set(Calendar.MINUTE, 59);
      calendar.set(Calendar.SECOND, 59);

      return  (calendar.getTimeInMillis() >= alarmTimeInMillis());

   }

   public void Snooze(int delay){
      // Increment the snooze counter
      incSnoozeCounter();

      // Make sure that the snooze action does not go on forever
      if (snoozeCounter>6) return; // TODO: Allow user to setup the snooze limit

      // Add Delay to the alarm schedule (Increments by 'delay' Minutes with every snooze)
      minute+=delay;
      if (minute>59){
         minute-=delay;
         hour++;
         if (hour==24)
            hour = 0;
      }

      Schedule();
   }



   /*
    * Schedule a new alarm
    * */
   public void Schedule() {
      AlarmManager alarmManager;

      Context context = TheTimeMachineApp.appContext;
      if (context == null) return;

      // Get Alarm Manager and test (For newer versions) if device supports Exact Alarms
      // TODO: If not supported - do something or abort
      alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
         boolean isExact = alarmManager.canScheduleExactAlarms();
         if (isExact)
            Log.d("THE_TIME_MACHINE", "Exact Alarm - SUPPORTED");
         else
            Log.w("THE_TIME_MACHINE", "Exact Alarm - NOT SUPPORTED");
      }


      // Create an intent that will hold all the necessary EXTRA data
      // Encapsulate the intent inside a Pending intent
      Intent intent = new Intent(context, AlarmReceiver.class);

      intent.putExtras(getBundle());
      intent.putExtra(K_TYPE, ALARM);

      // TODO: Pass additional info such as days of week and status of Alarm
      PendingIntent alarmIntent = PendingIntent.getBroadcast(context,
            (int)createTime,
            intent,
            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

      // Get the time for the next Alarm, in Milliseconds
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(System.currentTimeMillis());
      calendar.set(Calendar.HOUR_OF_DAY, hour);
      calendar.set(Calendar.MINUTE, minute);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);

      // if alarm time has already passed, increment day by 1
      if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
         calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
      }

      // Set Alarm Clock
       alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.getTimeInMillis(),
            alarmIntent);

      // Toast and Log
      // TODO: Change toast to tell user the duration until the alarm goes off
      String toastText = String.format(context.getResources().getString(R.string.msg_alarm_set), hour, minute);
      Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
      Log.d("THE_TIME_MACHINE", toastText);
   }

   // Cancel an Alarm
   public void cancelAlarm() {

      Context context = TheTimeMachineApp.appContext;
      if (context == null) return;

      // Get Alarm Manager and test (For newer versions) if device supports Exact Alarms
      // TODO: If not supported - do something or abort
      AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
         boolean isExact = alarmManager.canScheduleExactAlarms();
         if (isExact)
            Log.d("THE_TIME_MACHINE", "Exact Alarm - SUPPORTED");
         else
            Log.w("THE_TIME_MACHINE", "Exact Alarm - NOT SUPPORTED");
      }


      // Create an intent then
      // Encapsulate the intent inside a Pending intent
      Intent intent = new Intent(context, AlarmReceiver.class);
      PendingIntent alarmIntent = PendingIntent.getBroadcast(context,
            (int)createTime,
            intent,
            PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

      // Cancel
      alarmManager.cancel(alarmIntent);
      //this.started = false;

      // Toast and Log
      String toastText = String.format(context.getResources().getString(R.string.msg_alarm_canceled), hour, minute);
      Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
      Log.d("THE_TIME_MACHINE", toastText);
   }

}