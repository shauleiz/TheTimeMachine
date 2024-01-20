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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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

   public static final int DAY_IN_MILLIS = 24*60*60*1000;

   // 2 Minutes Snooze Delay
   public static final int SNOOZE_DELAY = 2*60*1000; // TODO: Replace by a configurable value



   // Constructor of Alarm Item - Create from a bundle
   public AlarmItem(@NonNull Bundle inBundle){
      hour = inBundle.getInt(K_HOUR);
      minute = inBundle.getInt(K_MINUTE);
      label = inBundle.getString(K_LABEL);
      active = inBundle.getBoolean(K_ACTIVE);
      createTime = inBundle.getLong(K_CTIME, -1);
      oneOff = inBundle.getBoolean(K_ONEOFF, true);
      weekDays = inBundle.getInt(K_WEEKDAYS, 0);
      snoozeCounter = inBundle.getInt(K_CSNOOZE,0);

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
      snoozeCounter = 0;

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
      snoozeCounter = 0;

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
      b.putInt(K_CSNOOZE,snoozeCounter);
      return b;
   }


   public boolean isOneOff(){ return oneOff;}
   public int getWeekDays(){return weekDays;}

   public void setActive(boolean active) {
      this.active = active;
      resetSnoozeCounter();
   }

   public void setHour(int hour) {this.hour = hour;}

   public void setLabel(String label) {this.label = label;}

   public void setMinute(int minute) {this.minute = minute;}

   public void setCreateTime(long createTime) {this.createTime = createTime;}

   public void setSnoozeCounter(int snoozeCounter){ this.snoozeCounter = snoozeCounter;}


   public void resetSnoozeCounter(){ this.snoozeCounter = 0;}

   public void incSnoozeCounter(){ snoozeCounter++;}

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

      // Everytime this alarm goes off - snoozeCounter is incremented
      // The alarm should then scheduled to snooze
      // The next time it will go off will be after the next delay
      calendar.setTimeInMillis(calendar.getTimeInMillis() + (long) snoozeCounter *SNOOZE_DELAY);

      // if alarm time has already passed, increment day by 1
      if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
         calendar.setTimeInMillis(calendar.getTimeInMillis()+ DAY_IN_MILLIS);
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

   /********** Execute an alarm action according to alarm state **********
   * Actions:
   * Schedule - alarmManager.setExactAndAllowWhileIdle to the correct time marking the alarm
   * with (int)createTime. This will override former alarm set with the same createTime.
   *
   * Cancel - alarmManager.cancel marked with (int)createTime.
   * This will remove former alarm set with the same createTime.
   *
   * Snooze - Change alarm time to current time + delay and increase snooze counter.
   * When snooze counter is over a certain number, alarm becomes inactive and thus canceled.
   *
   * Calculating the time of the alarm (Original, not snooze):
   *  Case: One Off:
   *  1. Calculate alarm time based on current time, then replace Hour+Minute and
   *     set Seconds+millis to zero.
   *  2. If alarm time is smaller than current time then set the alarm for tomorrow:
   *     add 24*60*60*1000 Milliseconds to alarm time
   *
   *  Case Recurring:
   *  + Verify that the weekdays holds a valid pattern (128> weekdays >0)
   *  + Calculate alarm time based on current time, then replace Hour+Minute and
   *    set Seconds+millis to zero.
   *  + If alarm time is smaller than current time then set the alarm for tomorrow:
   *    add 24*60*60*1000 Milliseconds (dayInMillis) to alarm time.
   *  + Calculate the weekday of the alarm time.
   *  + From weekdays, get the first day that is equal or bigger than the weekday of the alarm time.
   *    Note that the day might be from the beginning of the next week
   *  + Calculate the diff between the days ( 0 =< diff <7), multiply by dayInMillis
   *
   *  Snooze & Auto-Snooze
   *  By snooze I refer to the case where the user activates SNOOZE while
   *  the Alarm Service is running. This triggers scheduling a new alarm at a later time.
   *  The Hour+Minute value of the Alarm is not changed.
   *  The number of times the user can Snooze is unlimited
   *
   *  By Auto-Snooze I refer to the case where the Alarm Service activates SNOOZE when it reaches
   *  timeout. This triggers scheduling a new alarm at a later time.
   *  The Hour+Minute value of the Alarm is not changed.
   *  The number of times that the item can auto-snooze is limited.
   *  When the limit has reached the alarm in deactivated and canceled.
   *
   *  The Snooze Counter member variable is set to 0 when an alarm is created.
   *  The counter is incremented every time alarmManager.setExactAndAllowWhileIdle is called.
   *  The counter is reset to 0 when the user activates an alarm.
   *
   *  In both Snooze & Auto-Snooze the alarm time is calculated as an offset from the
   *  original alarm time. The offset is delay*snoozecounter.
   *  In the case of auto-snooze, when the snooze counter reaches the limit -
   *  the alarm is deactivated
   *
   */
   public void Exec(){
      long alarmTime;
      // Things that are common to all/most tasks

      // Get context
      Context context = TheTimeMachineApp.appContext;
      if (context == null) return;

      // Get Alarm Manager
      AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

      // Create an intent for the Alarm Receiver then
      Intent intent = new Intent(context, AlarmReceiver.class);
      int id = (int)createTime;


      ////////// Schedule or Cancel?
      if (isActive()) {
         ///// Schedule Alarm

         // Verify exact alarm is supported - for debugging purpose
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            boolean isExact = alarmManager.canScheduleExactAlarms();
            if (isExact)
               Log.d("THE_TIME_MACHINE", "Exact Alarm - SUPPORTED");
            else
               Log.w("THE_TIME_MACHINE", "Exact Alarm - NOT SUPPORTED");
         }


         // Time of coming alarm (One-Off, possibly snoozing)
         alarmTime = alarmTimeInMillis();

         // If recurring alarm - find the nearest one
         if (!isOneOff() && (weekDays!=0)) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(alarmTime);

            // Get the weekday of the currently assigned alarm time
            // Convert it to Zero/Sunday based number: Sun=0 --> Sat=6
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;

            // For each Day in weekDays (Only the ones that are ON):
            // - Convert it to a number nDay ( One/Sunday based number: Sun=1 --> Sat=7)
            // - Calculate nDay as the diff between this number and datOfWeek
            // - If nDay is smaller than dayOfWeek add 7 to it
            // - Multiple nDay by the number of milliseconds in a day and add it to calendar time
            // - Set the alarm manager with this calender time
            int[] days = {SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY};
            for (int i = 0; i < days.length; i++) {
               int nDay = (dayOfWeek +i)%7;
               if ((weekDays & days[nDay]) == 0) continue;
               alarmTime = i * DAY_IN_MILLIS + cal.getTimeInMillis();
               break;
            }
         }

         //  Increment Snooze Counter
         // To the intent, add EXTRAS that hold the entire alarm data in a bundle
         // Also, mark the bundle as ALARM
         snoozeCounter++;
         intent.putExtras(getBundle());
         intent.putExtra(K_TYPE, ALARM);

         // Encapsulate the intent inside a Pending intent
         // The pending intent is signed with the Alarm ID
         PendingIntent alarmIntent = PendingIntent.getBroadcast(
               context,
               id,
               intent,
               PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

         // Set Alarm Clock
         alarmManager.setExactAndAllowWhileIdle(
               AlarmManager.RTC_WAKEUP,
               alarmTime,
               alarmIntent);



         // Toast & Log
         Calendar calendar = Calendar.getInstance();
         calendar.setTimeInMillis(alarmTime);
         SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a", Locale.US);
         String toastText = String.format(context.getResources().getString(R.string.msg_alarm_set), hour, minute);
         Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
         Log.d("THE_TIME_MACHINE", "Exec(): " + toastText + " " + format.format(calendar.getTime()) );

      }
      else{
         ///// Cancel Alarm

         // Encapsulate the intent inside a Pending intent
         // The pending intent is signed with the Alarm ID
         PendingIntent alarmIntent = PendingIntent.getBroadcast(
               context,
               id,
               intent,
               PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

         // Cancel
         alarmManager.cancel(alarmIntent);

         // Toast and Log
         String toastText = String.format(context.getResources().getString(R.string.msg_alarm_canceled), hour, minute);
         Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
         Log.d("THE_TIME_MACHINE", "Exec(): " + toastText);
      }
   }




}