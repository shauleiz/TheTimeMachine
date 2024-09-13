package com.product.thetimemachine.Data;

import static com.product.thetimemachine.AlarmService.ALARM;
import static com.product.thetimemachine.AlarmService.K_TYPE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.product.thetimemachine.AlarmReceiver;
import com.product.thetimemachine.Application.TheTimeMachineApp;
import com.product.thetimemachine.R;
import com.product.thetimemachine.ui.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;


/*
   This class hold an item in the Alarm List
   The object contains all there is to know about the alarm:
   - Exact Time
   - Time of original creation: Used as unique ID
   - Label (optional) to display with the alarm
   - Status (active)
   - Type (one-off or repeating or on a future explicit date)
   - Week days (For repeating alarm)
   - Exact date (optional) - for future alarm set on an explicit date
   - Preferences related to alarm sound and vibration pattern
   
   The alarm items are saved in the ROOM database as a table ("raw_alarm_table")
*/
@Entity(tableName = "raw_alarm_table")
public class AlarmItem {
   @PrimaryKey
   @NonNull
   public long createTime;
   public int hour, minute, snoozeCounter;
   public String label;
   public boolean active, oneOff;
   public int weekDays;

   public int dayOfMonth, month, year;
   public boolean futureDate;

   public String ringRepeat, ringDuration, snoozeDuration;
   public String vibrationPattern, alarmSound, gradualVolume;

   @ColumnInfo(name = "genStatus", defaultValue = "0")
   public int genStatus;

   public String exceptionDatesStr;

   /** Key Strings for saving alarm data in bundle  - A key per AlarmItem variable  **/
   public static final String K_HOUR = "HOUR";
   public static final String K_MINUTE = "MINUTE";
   public static final String K_LABEL = "LABEL";
   public static final String K_ACTIVE = "ACTIVE";
   public static final String K_CTIME = "C_TIME";
   public static final String K_CSNOOZE = "COUNTER_SNOOZE";

   public static final String K_WEEKDAYS = "WEEKDAYS";
   public static final String K_ONEOFF = "ONE_OFF";
   public static final String K_DAYOFMONTH = "DAY_OF_MONTH";
   public static final String K_MONTH = "MONTH";
   public static final String K_YEAR = "YEAR";
   public static final String K_FDATE = "FUTURE_DATE";

   public static final String K_EXCEPTION = "EXCEPTION";




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

   // GenStatus flags
   public static final int RINGING = 0x1;



   /***
    * Constructors
    * <p>
    * AlarmItem(@NonNull Bundle inBundle):
    * Create duplicate of an AlarmItem object from a bundle
    * <p>
    * AlarmItem(int _hour, int _minute, String _label, boolean _active):
    * Create a default AlarmItem object from some basic info -
    * the creation time is calculated by the constructor.
    * Other values are set to default values
    * Preferences are set to the app default values
    * <p>
    * AlarmItem(int _hour, int _minute, String _label, boolean _active, long _createTime):
    * Create a dummy duplicate
    * <p>
    * public AlarmItem() : Place holder only
   ***/
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
      dayOfMonth = inBundle.getInt(K_DAYOFMONTH,0);
      month = inBundle.getInt(K_MONTH,0);
      year = inBundle.getInt(K_YEAR,0);
      futureDate = inBundle.getBoolean(K_FDATE, false);
      exceptionDatesStr = inBundle.getString(K_EXCEPTION);

      // Sanity check
      if (hour < 0 || hour > 23 || minute < 0 || minute > 59)
         active = false;

      // Create Time
      if (createTime<0){
         Calendar calendar = Calendar.getInstance();
         createTime = calendar.getTimeInMillis();
      }

      // Preferences
      Context context = TheTimeMachineApp.appContext;
      ringDuration = inBundle.getString(context.getString(R.string.key_ring_duration), "");
      ringRepeat = inBundle.getString(context.getString(R.string.key_ring_repeat), "");
      snoozeDuration = inBundle.getString(context.getString(R.string.key_snooze_duration), "");
      vibrationPattern = inBundle.getString(context.getString(R.string.key_vibration_pattern), "");
      alarmSound = inBundle.getString(context.getString(R.string.key_alarm_sound), "");
      gradualVolume = inBundle.getString(context.getString(R.string.key_gradual_volume), "");

   }

   // Constructor of Alarm Item - create time calculated internally
   public AlarmItem(int _hour, int _minute, String _label, boolean _active) {
      hour = _hour;
      minute = _minute;
      label = _label;
      active = _active;

      // Uninitialized Snooze Counter
      setSnoozeCounter(0);

      // Sanity check
      if (hour < 0 || hour > 23 || minute < 0 || minute > 59)
         active = false;

      // Default: One-Off (All weekdays disabled)
      weekDays = 0;
      oneOff = true;

      // Default: No future date
      dayOfMonth = 0;
      month = 0;
      year = 0;
      futureDate = false;

      // Add time of creation in milliseconds
      Calendar calendar = Calendar.getInstance();
      createTime = calendar.getTimeInMillis();

      // Get preferences from app-defaults
      resetsetPreferences();
   }

   // Constructor of Alarm Item - create time pushed from outside
   public AlarmItem(int _hour, int _minute, String _label, boolean _active, long _createTime) {
      hour = _hour;
      minute = _minute;
      label = _label;
      active = _active;

      // Uninitialized Snooze Counter
      setSnoozeCounter(0);

      // Sanity check
      if (hour < 0 || hour > 23 || minute < 0 || minute > 59)
         active = false;

      // Add time of creation (of original alarm) in milliseconds
      createTime = _createTime;

      // Default: One-Off (All weekdays disabled)
      weekDays = 0;
      oneOff = true;

      // Default: No future date
      dayOfMonth = 0;
      month = 0;
      year = 0;
      futureDate = false;

      // Get preferences from app-defaults
      resetsetPreferences();
   }

   public AlarmItem() {}

   // Getters
   public int getHour() {
      return hour;
   }

   public int getMinute() {
      return minute;
   }

   public String getLabel() {return label;}

   public boolean isActive() {
      return active;
   }

   public long getCreateTime() {
      return createTime;
   }

   public int getSnoozeCounter(){
      return snoozeCounter;
   }

   public int getDayOfMonth() {return dayOfMonth;}

   public int getMonth() {return month;}

   public int getYear() {return year;}
   public boolean isFutureDate() {return futureDate;}

   // Getters (Preferences)

   public String getRingDuration() {
      return ringDuration;
   }
   public String getRingRepeat() {
      return ringRepeat;
   }
   public String getSnoozeDuration() {return snoozeDuration;}
   public String getVibrationPattern() {
      return vibrationPattern;
   }
   public boolean isVibrationActive() {
      return !getVibrationPattern().equals("none");
   }
   public String getAlarmSound() {
      return alarmSound;
   }

   public boolean isAlarmMute(){
      return getAlarmSound().equals("silent");
   }
   public String getGradualVolume() {
      return gradualVolume;
   }

   public boolean isOneOff(){ return oneOff;}
   public int getWeekDays(){return weekDays;}

   public void setActive(boolean active) {
      this.active = active;
      //resetSnoozeCounter();
   }

   public int getGenStatus() {return genStatus;}

   public String getExceptionDatesStr() {return exceptionDatesStr;}

   public boolean isRinging(){
      int stat = getGenStatus();
      return (stat & RINGING) > 0;
   }

   // Setters
   public void setHour(int hour) {this.hour = hour;}

   public void setLabel(String label) {this.label = label;}

   public void setMinute(int minute) {this.minute = minute;}

   public void setCreateTime(long createTime) {this.createTime = createTime;}

   public void setSnoozeCounter(int snoozeCounter){ this.snoozeCounter = snoozeCounter;}


   public void resetSnoozeCounter(){setSnoozeCounter(0);}

   public void incSnoozeCounter(){ setSnoozeCounter(getSnoozeCounter()+1);}

   // Set/reset One-of bit
   public void setOneOff(boolean set){ oneOff = set;}

   // Copy the days mask without affecting the one-off bit
   public void setWeekDays(int mask){ weekDays = mask;}

   public void setDayOfMonth(int dayOfMonth) {this.dayOfMonth = dayOfMonth;}

   public void setMonth(int month) {this.month = month;}

   public void setYear(int year) {this.year = year;}

   public void setFutureDate(boolean futureDate) {this.futureDate = futureDate;}

   // Setters (Preferences)

   public void setRingDuration(String val) {
      ringDuration = val;
   }

   public void setRingRepeat(String val) {
      ringRepeat = val;
   }

   public void setSnoozeDuration(String val) {
      snoozeDuration = val;
   }

   public void setVibrationPattern(String val) {vibrationPattern = val;}

   public void setAlarmSound(String val) {
      alarmSound = val;
   }

   public void setGradualVolume(String val) {
      gradualVolume = val;
   }

   public void setGenStatus(int genStatus) {this.genStatus = genStatus;}

   public void setExceptionDatesStr(String exceptionDatesStr) {this.exceptionDatesStr = exceptionDatesStr;}

   public void setRinging(boolean ringing){
      int stat = getGenStatus();
      if (ringing)
         stat |= RINGING;
      else
         stat &= (~RINGING);
      setGenStatus(stat);
   }

   /*** Helper Functions ***/

   /*
   * Encapsulate the AlarmItem in a bundle.
   * To be used to construct a duplicate AlarmItem
   */
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
      b.putInt(K_DAYOFMONTH,dayOfMonth);
      b.putInt(K_MONTH,month);
      b.putInt(K_YEAR,year);
      b.putBoolean(K_FDATE,futureDate);
      b.putString(K_EXCEPTION,exceptionDatesStr);

      // Preferences
      Context context = TheTimeMachineApp.appContext;
      b.putString(context.getString(R.string.key_ring_duration), ringDuration);
      b.putString(context.getString(R.string.key_ring_repeat), ringRepeat);
      b.putString(context.getString(R.string.key_snooze_duration), snoozeDuration);
      b.putString(context.getString(R.string.key_vibration_pattern), vibrationPattern);
      b.putString(context.getString(R.string.key_alarm_sound), alarmSound);
      b.putString(context.getString(R.string.key_gradual_volume), gradualVolume);

      return b;
   }

   /*  Convert alarm time to milliseconds    */
   public long alarmTimeInMillis() {
      // Get the time for the next Alarm, in Milliseconds
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(System.currentTimeMillis());
      calendar.set(Calendar.HOUR_OF_DAY, hour);
      calendar.set(Calendar.MINUTE, minute);
      calendar.set(Calendar.SECOND, 0);
      calendar.set(Calendar.MILLISECOND, 0);

      // If this a future date by given calendar date
      if (isFutureDate() && getDayOfMonth()>0 && getMonth()>0 && getYear()>0){
         calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
         calendar.set(Calendar.MONTH,month);
         calendar.set(Calendar.YEAR,year);
      }
      // If this is a repeating alarm then the base date is now.
      /*else if (!isOneOff()){
         calendar.set(Calendar.DAY_OF_MONTH,0);
         calendar.set(Calendar.MONTH,0);
         calendar.set(Calendar.YEAR,0);
      }*/

      // Calculate the snooze delay (if needed)
      int snoozeDelay;
      if (snoozeCounter >0)
         snoozeDelay = Str2Int_SnoozeDuration(); // Preference
      else
         snoozeDelay = 0;

      // Everytime this alarm goes off - snoozeCounter is incremented
      // The alarm should then scheduled to snoozeAdd
      // The next time it will go off will be after the next delay
      if (snoozeDelay >0)
         calendar.setTimeInMillis(System.currentTimeMillis() + snoozeDelay);

      // if alarm time has already passed, increment day by 1
      if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
         calendar.setTimeInMillis(calendar.getTimeInMillis()+ DAY_IN_MILLIS);
      }

      return calendar.getTimeInMillis();
   }


   /* Get the weekday of the currently assigned alarm time
    Convert it to Zero/Sunday based number: Sun=0 --> Sat=6 */
   public int getWeekdayOfNextAlarm(){
      long alarmTime = nextAlarmTimeInMillis();
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(alarmTime);
      return cal.get(Calendar.DAY_OF_WEEK) - 1;
   }

   /*   Get the time of the next alarm in milliseconds    */
   public long nextAlarmTimeInMillis(){

      long alarmTime = alarmTimeInMillis();
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
         for (int i = 0; i < 100; i++) { // 100 is just a large number
            int nDay = (dayOfWeek +i)%7;
            if ((weekDays & days[nDay]) == 0) continue;
            long out = i * (long)DAY_IN_MILLIS + alarmTime;

            // Check if this time falls in one of the dates in the list of exceptions
            if (!isDateException(out))
               return out;
         }
      return alarmTime;
   }

   // Compare given time (in millis) with the dates in the list of exceptions
   // If given time falls in one of the dates in the list - return true
   private boolean isDateException(long timeInMillis){
      // Is there a valid exception list?
      if (exceptionDatesStr==null || exceptionDatesStr.isEmpty())
         return false;

      // Convert timeInMillis to date in YYYYMMDD format
      int y, m, d;
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(timeInMillis);
      y = calendar.get(Calendar.YEAR);
      m = calendar.get(Calendar.MONTH)+1;
      d = calendar.get(Calendar.DAY_OF_MONTH);
      String dateStr = Integer.toString(d+100*m+10000*y);

      // Search the exceptionDatesStr for this string
      int index = exceptionDatesStr.indexOf(dateStr);
      if (index == -1) // Not found
         return false;

      return true;
   }

   /* True if the alarm is set for today */
   public boolean isToday(){
      // Get time of today's midnight (minus a second)
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(System.currentTimeMillis());
      calendar.set(Calendar.HOUR_OF_DAY, 23);
      calendar.set(Calendar.MINUTE, 59);
      calendar.set(Calendar.SECOND, 59);

      return  (calendar.getTimeInMillis() >= alarmTimeInMillis());

   }

   /* True if the alarm is set for tomorrow */
   public boolean isTomorrow(){
      long today_midnight, tomorrow_midnight, tomorrow_last_sec, alarm_time;
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(System.currentTimeMillis());

      // Get today's time in millis at 0:00:00.
      // Then add 24h to get the time of the beginning of tomorrow.
      // Then add additional 24h-1sec to get the time of the end of tomorrow.
      calendar.set(Calendar.HOUR_OF_DAY, 0);
      calendar.set(Calendar.MINUTE, 0);
      calendar.set(Calendar.SECOND, 0);
      today_midnight = calendar.getTimeInMillis();
      tomorrow_midnight = today_midnight+24*60*60*1000;
      tomorrow_last_sec = tomorrow_midnight+24*60*60*1000-1000;
      alarm_time = alarmTimeInMillis();
      return alarm_time >= tomorrow_midnight && alarm_time <= tomorrow_last_sec;
   }

   // Copy Global preferences to Item preferences
   public void resetsetPreferences() {
      // Get context and Default Shared Preferences
      Context context = TheTimeMachineApp.appContext;
      SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

      // Set preferences as strings
      setRingDuration(preferences.getString(context.getString(R.string.key_ring_duration), ""));
      setRingRepeat(preferences.getString(context.getString(R.string.key_ring_repeat), ""));
      setSnoozeDuration(preferences.getString(context.getString(R.string.key_snooze_duration), ""));
      setVibrationPattern(preferences.getString(context.getString(R.string.key_vibration_pattern), ""));
      setAlarmSound(preferences.getString(context.getString(R.string.key_alarm_sound), ""));
      setGradualVolume(preferences.getString(context.getString(R.string.key_gradual_volume), ""));

   }

   // Preferences: Convert from Menu-String to real Value
   // Preference: Duration of ringing until it stops
   // Returns:
   // Duration in milliseconds (Default is 30000)
   public int Str2Int_ring_duration(){
         // String is of type 15000Seconds
         if (ringDuration.length() < 4){
            return 30000;
      }
      String intValue = ringDuration.replaceAll("[^0-9]", "");
      return Integer.parseInt(intValue)*1000;
   }

   public static int Str2Int_ring_duration(String ringDuration){
      // String is of type 15000Seconds
      if (ringDuration.length() < 4){
         return 30000;
      }
      String intValue = ringDuration.replaceAll("[^0-9]", "");
      return Integer.parseInt(intValue)*1000;
   }
   public int Str2Int_ring_repeat(){
      // String is of type 15000Seconds
      if (ringRepeat.length()<2)
         return 5;

      // Extract the numeral part
      String intValue = ringRepeat.replaceAll("[^0-9]", "");
      return Integer.parseInt(intValue);
   }

   public int Str2Int_SnoozeDuration(){
      // String is of type 15000Seconds
      if (snoozeDuration.length() < 4)
         return 60000;

      // Extract the numeral part and convert from seconds to milliseconds
      String intValue = snoozeDuration.replaceAll("[^0-9]", "");
      return Integer.parseInt(intValue)*1000;
   }

   public static int Str2Int_SnoozeDuration(String snoozeDuration){
      // String is of type 15000Seconds
      if (snoozeDuration.length() < 4)
         return 60000;

      // Extract the numeral part and convert from seconds to milliseconds
      String intValue = snoozeDuration.replaceAll("[^0-9]", "");
      return Integer.parseInt(intValue)*1000;
   }

   public static int Str2Int_ring_repeat(String ringRepeat){
      // String is of type 15000Seconds
      if (ringRepeat.length()<2)
         return 5;

      // Extract the numeral part
      String intValue = ringRepeat.replaceAll("[^0-9]", "");
      return Integer.parseInt(intValue);
   }

   public String Str2Int_vibration_pattern(){
      if (vibrationPattern.isEmpty())
         return "none";
      else
         return vibrationPattern;
   }

   public   String Str2Int_alarm_sound(){
      if ( alarmSound.isEmpty())
         return "oversimplified_alarm_clock_113180";
      else
         return alarmSound;
   }

   public static   String Str2Int_vibration_pattern(String pattern){
      if (pattern == null || pattern.isEmpty())
         return "none";
      else
         return pattern;
   }
   public int Str2Int_gradual_volume(){
      // String is of type 15000Seconds
      if (gradualVolume.length()<2)
         return 30000;

      // Extract the numeral part
      String intValue = gradualVolume.replaceAll("[^0-9]", "");
      return Integer.parseInt(intValue) * 1000;
   }

   public  static String Str2Int_alarm_sound(String alarmSound){

      if ( alarmSound.isEmpty())
         return "oversimplified_alarm_clock_113180";
      else
         return alarmSound;
   }

   public static int  Str2Int_gradual_volume(String gradualVolume){
      // String is of type 15000Seconds
      if (gradualVolume.length()<2)
         return 30000;

      // Extract the numeral part
      String intValue = gradualVolume.replaceAll("[^0-9]", "");
      return Integer.parseInt(intValue) * 1000;
   }

   public static String strGetDurationToAlarm(long alarmTime) {
      String strH, strD, strM;
      final long DaysInMillis = 24*60*60*1000;
      final long HoursInMillis = 60*60*1000;
      final long MinutesInMillis = 60*1000;
      Context context = TheTimeMachineApp.appContext;


      // Get Current Time in Millis
      long tNow= (new Date()).getTime();

      // Calculate duration in millis
      long duration = alarmTime + 60000 - tNow;

      if (duration < 12000)
         return "Alarm scheduled for now";

      // Calculate D, H, M
      long days = duration / DaysInMillis;
      long hours = (duration - days*DaysInMillis)/ HoursInMillis;
      long minutes = (duration - days*DaysInMillis - hours*HoursInMillis)/ MinutesInMillis;

      // Create the print string.

      // When Alarm is scheduled for more than 2 days from now, prind the date and time
      if (days > 2) {
         SimpleDateFormat format = new SimpleDateFormat(context.getString(R.string.date_format), Locale.US);
         Date date = new Date(alarmTime);
         return context.getString(R.string.alarm_scheduled_for) + format.format(date);
      }

      // Alarm time is set for 10 days, 11 hours and 5 Minutes.
      // Values are rounded down to the minute (no seconds and millis)
      // Value 0 is ignored.
      // Value 1 is without the plural

      if (days>1)
         strD = days +context.getString(R.string.day_plural);
      else if (days==1)
         strD = context.getString(R.string.day_singular);
      else
         strD = "";

      if (hours>1)
         strH = hours +context.getString(R.string.hour_plural);
      else if (hours==1)
         strH = context.getString(R.string.hour_singular);
      else
         strH = "";

      if (minutes>1)
         strM = minutes +context.getString(R.string.minute_plural);
      else if (minutes==1)
         strM = context.getString(R.string.minute_singular);
      else
         strM = "";


      return context.getString(R.string.alarm_scheduled_for) + strD  + strH + strM + context.getString(R.string.from_now);
   }


   /********** Execute an alarm action according to alarm state **********
   * Actions:
   * Schedule - alarmManager.setExactAndAllowWhileIdle to the correct time marking the alarm
   * with (int)createTime. This will override former alarm set with the same createTime.
   * <p>
   * Cancel - alarmManager.cancel marked with (int)createTime.
   * This will remove former alarm set with the same createTime.
   *<p>
   * Snooze - Change alarm time to current time + delay and increase snooze counter.
   * When snooze counter is over a certain number, alarm becomes inactive and thus canceled.
   *<p>
   * Calculating the time of the alarm (Original, not snooze):
   *  Case: One Off:
   *  1. Calculate alarm time based on current time, then replace Hour+Minute and
   *     set Seconds+millis to zero.
   *  2. If alarm time is smaller than current time then set the alarm for tomorrow:
   *     add 24*60*60*1000 Milliseconds to alarm time
   *<p>
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
   *<p>
   *  Snooze & Auto-Snooze
   *  By snooze I refer to the case where the user activates SNOOZE while
   *  the Alarm Service is running. This triggers scheduling a new alarm at a later time.
   *  The Hour+Minute value of the Alarm is not changed.
   *  The number of times the user can Snooze is unlimited
   *<p>
   *  By Auto-Snooze I refer to the case where the Alarm Service activates SNOOZE when it reaches
   *  timeout. This triggers scheduling a new alarm at a later time.
   *  The Hour+Minute value of the Alarm is not changed.
   *  The number of times that the item can auto-snooze is limited.
   *  When the limit has reached the alarm in deactivated and canceled.
   *<p>
   *  The Snooze Counter member variable is set to 0 when an alarm is created.
   *  The counter is incremented every time alarmManager.setExactAndAllowWhileIdle is called.
   *  The counter is reset to 0 when the user activates an alarm.
    *<p>
    *  In Snooze the alarm time is calculated as an offset from the
    *  time 'Snooze' was pressed. The offset is delay*snoozecounter.
    *  In the case of auto-snooze, when the snooze counter reaches the limit -
    *  the alarm is deactivated
    *
    *<p>
    *  In auto-snooze the alarm time is calculated as an offset from the
    *  time the ringing stopped. The offset is delay*snoozecounter.
    *  When the snooze counter reaches the limit -
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

      // Extract id from alarm's creation time
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




         //  Increment Snooze Counter
         //  incSnoozeCounter();

         // If recurring alarm - find the nearest one
         if (!isOneOff() && (weekDays!=0))
            alarmTime = nextAlarmTimeInMillis();
         else
            // Time of coming alarm (One-Off, possibly snoozing, possibly a future date)
            alarmTime = alarmTimeInMillis();



         // To the intent, add EXTRAS that hold the entire alarm data in a bundle
         // Also, mark the bundle as ALARM
         intent.putExtras(getBundle());
         intent.putExtra(K_TYPE, ALARM);

         // Encapsulate the intent inside a Pending intent
         // The pending intent is signed with the Alarm ID
         PendingIntent alarmIntent = PendingIntent.getBroadcast(
               context,
               id,
               intent,
               PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

         // Create a random number for request code
         Random random = new Random();
         int requestCode = random.nextInt(10000000);


         // TODO: Notification does not show - not really needed
         // What to do when user clicks on the Alarm Clock notification
         Intent targetIntent = new Intent(context , MainActivity.class);
         PendingIntent targetPending = PendingIntent.getActivity(
               context, requestCode, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);


         // Set Alarm Clock
         AlarmManager.AlarmClockInfo ac= new AlarmManager.AlarmClockInfo(alarmTime,  targetPending);
         alarmManager.setAlarmClock(ac, alarmIntent);


         // Toast & Log
         Calendar calendar = Calendar.getInstance();
         calendar.setTimeInMillis(alarmTime);
         SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a", Locale.US);
         String toastText = String.format(context.getResources().getString(R.string.msg_alarm_set), hour, minute);
         Toast.makeText(context,  strGetDurationToAlarm(alarmTime), Toast.LENGTH_LONG).show();
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