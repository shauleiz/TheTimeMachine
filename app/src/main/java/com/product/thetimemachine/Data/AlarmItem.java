package com.product.thetimemachine.Data;

import static com.product.thetimemachine.AlarmService.ALARM;
import static com.product.thetimemachine.AlarmService.K_TYPE;
import static com.product.thetimemachine.Application.TheTimeMachineApp.mainActivity;

import static java.time.ZoneId.systemDefault;

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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.ChronoField;
import java.util.Locale;
import java.util.Random;
import java.time.LocalDateTime;


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
   /**
    * Key Strings for saving alarm data in bundle  - A key per AlarmItem variable
    **/
   public static final String K_HOUR = "HOUR";
   public static final String K_MINUTE = "MINUTE";
   public static final String K_LABEL = "LABEL";
   public static final String K_ACTIVE = "ACTIVE";
   public static final String K_CTIME = "C_TIME";
   public static final String K_CSNOOZE = "COUNTER_SNOOZE";
   public static final String K_WEEKDAYS = "WEEKDAYS";
   public static final String K_ONE_OFF = "ONE_OFF";
   public static final String K_DAY_OF_MONTH = "DAY_OF_MONTH";
   public static final String K_MONTH = "MONTH";
   public static final String K_YEAR = "YEAR";
   public static final String K_F_DATE = "FUTURE_DATE";
   public static final String K_EXCEPTION = "EXCEPTION";
   // Masks for days of the week.
   // If ONE_OFF is set, the rest of the fields are to be ignored
   // public static final int ONE_OFF = 0x80;
   public static final int SUNDAY = 0x01;
   public static final int MONDAY = 0x02;
   public static final int TUESDAY = 0x04;
   public static final int WEDNESDAY = 0x08;
   public static final int THURSDAY = 0x10;
   public static final int FRIDAY = 0x20;
   public static final int SATURDAY = 0x40;
   public static final int DAY_IN_MILLIS = 24 * 60 * 60 * 1000;
   // GenStatus flags
   public static final int RINGING = 0x1;
   @PrimaryKey
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
   public AlarmItem(@NonNull Bundle inBundle) {
      hour = inBundle.getInt(K_HOUR);
      minute = inBundle.getInt(K_MINUTE);
      label = inBundle.getString(K_LABEL);
      active = inBundle.getBoolean(K_ACTIVE);
      createTime = inBundle.getLong(K_CTIME, -1);
      oneOff = inBundle.getBoolean(K_ONE_OFF, true);
      weekDays = inBundle.getInt(K_WEEKDAYS, 0);
      snoozeCounter = inBundle.getInt(K_CSNOOZE, 0);
      dayOfMonth = inBundle.getInt(K_DAY_OF_MONTH, 0);
      month = inBundle.getInt(K_MONTH, 0);
      year = inBundle.getInt(K_YEAR, 0);
      futureDate = inBundle.getBoolean(K_F_DATE, false);
      exceptionDatesStr = inBundle.getString(K_EXCEPTION);

      // Sanity check
      if (hour < 0 || hour > 23 || minute < 0 || minute > 59)
         active = false;

      // Create Time
      if (createTime < 0)
         createTime = Instant.now().toEpochMilli();


      // Preferences
      Context context = TheTimeMachineApp.appContext;
      ringDuration = inBundle.getString(mainActivity.getString(R.string.key_ring_duration), "");
      ringRepeat = inBundle.getString(mainActivity.getString(R.string.key_ring_repeat), "");
      snoozeDuration = inBundle.getString(mainActivity.getString(R.string.key_snooze_duration), "");
      vibrationPattern = inBundle.getString(mainActivity.getString(R.string.key_vibration_pattern), "");
      alarmSound = inBundle.getString(mainActivity.getString(R.string.key_alarm_sound), "");
      gradualVolume = inBundle.getString(mainActivity.getString(R.string.key_gradual_volume), "");

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
      createTime = Instant.now().toEpochMilli();


      // Get preferences from app-defaults
      getPreferences();
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
      getPreferences();
   }

   public AlarmItem() {
   }

   public static int Str2Int_ring_duration(String ringDuration) {
      // String is of type 15000Seconds
      if (ringDuration.length() < 4) {
         return 30000;
      }
      String intValue = ringDuration.replaceAll("[^0-9]", "");
      return Integer.parseInt(intValue) * 1000;
   }

   public static int Str2Int_SnoozeDuration(String snoozeDuration) {
      // String is of type 15000Seconds
      if (snoozeDuration.length() < 4)
         return 60000;

      // Extract the numeral part and convert from seconds to milliseconds
      String intValue = snoozeDuration.replaceAll("[^0-9]", "");
      return Integer.parseInt(intValue) * 1000;
   }

   public static int Str2Int_ring_repeat(String ringRepeat) {
      // String is of type 15000Seconds
      if (ringRepeat.length() < 2)
         return 5;

      // Extract the numeral part
      String intValue = ringRepeat.replaceAll("[^0-9]", "");
      return Integer.parseInt(intValue);
   }

   public static String Str2Int_vibration_pattern(String pattern) {
      if (pattern == null || pattern.isEmpty())
         return "none";
      else
         return pattern;
   }

   public static String Str2Int_alarm_sound(String alarmSound) {

      if (alarmSound.isEmpty())
         return "oversimplified_alarm_clock_113180";
      else
         return alarmSound;
   }

   public static int Str2Int_gradual_volume(String gradualVolume) {
      // String is of type 15000Seconds
      if (gradualVolume.length() < 2)
         return 30000;

      // Extract the numeral part
      String intValue = gradualVolume.replaceAll("[^0-9]", "");
      return Integer.parseInt(intValue) * 1000;
   }

   public static String strGetDurationToAlarm(long alarmTime) {
      String strH, strD, strM;
      final long DaysInMillis = 24 * 60 * 60 * 1000;
      final long HoursInMillis = 60 * 60 * 1000;
      final long MinutesInMillis = 60 * 1000;
      Context context = TheTimeMachineApp.appContext;


      // Get Current Time in Millis
      long tNow = Instant.now().toEpochMilli();

      // Calculate duration in millis
      long duration = alarmTime + 60000 - tNow;

      if (duration < 12000)
         return mainActivity.getString(R.string.alarm_scheduled_for_now);

      // Calculate D, H, M
      long days = duration / DaysInMillis;
      long hours = (duration - days * DaysInMillis) / HoursInMillis;
      long minutes = (duration - days * DaysInMillis - hours * HoursInMillis) / MinutesInMillis;

      // Create the print string.

      // When Alarm is scheduled for more than 2 days from now, print the date and time
      if (days > 2) {
         LocalDateTime date = Instant.ofEpochMilli(alarmTime).atZone(systemDefault()).toLocalDateTime();
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern(mainActivity.getString(R.string.date_format));
         return mainActivity.getString(R.string.alarm_scheduled_for) + date.format(formatter);
      }

      // Alarm time is set for 10 days, 11 hours and 5 Minutes.
      // Values are rounded down to the minute (no seconds and millis)
      // Value 0 is ignored.
      // Value 1 is without the plural

      if (days > 1)
         strD = days + mainActivity.getString(R.string.day_plural);
      else if (days == 1)
         strD = mainActivity.getString(R.string.day_singular);
      else
         strD = "";

      if (hours > 1)
         strH = hours + mainActivity.getString(R.string.hour_plural);
      else if (hours == 1)
         strH = mainActivity.getString(R.string.hour_singular);
      else
         strH = "";

      if (minutes > 1)
         strM = minutes + mainActivity.getString(R.string.minute_plural);
      else if (minutes == 1)
         strM = mainActivity.getString(R.string.minute_singular);
      else
         strM = "";


      return mainActivity.getString(R.string.alarm_scheduled_for) + strD + strH + strM + mainActivity.getString(R.string.from_now);
   }

   public static String strGetDurationToAlarm(LocalDateTime alarmTime) {
      String strH, strD, strM;
      final Context context = TheTimeMachineApp.appContext;
      final LocalDateTime now = LocalDateTime.now();
      final long DaysInMillis = 24 * 60 * 60 * 1000;
      final long HoursInMillis = 60 * 60 * 1000;
      final long MinutesInMillis = 60 * 1000;


      // Calculate duration in millis
      long duration = now.until(alarmTime, ChronoUnit.MILLIS);
      if (duration < 120000)
         return mainActivity.getString(R.string.alarm_scheduled_for_now);

      // Calculate D, H, M
      long days = duration / DaysInMillis;
      long hours = (duration - days * DaysInMillis) / HoursInMillis;
      long minutes = (duration - days * DaysInMillis - hours * HoursInMillis) / MinutesInMillis;

      // Create the print string.

      // When Alarm is scheduled for more than 2 days from now, print the date and time
      if (days > 2) {
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern(mainActivity.getString(R.string.date_format));
         return mainActivity.getString(R.string.alarm_scheduled_for) + alarmTime.format(formatter);
      }

      // Alarm time is set for 10 days, 11 hours and 5 Minutes.
      // Values are rounded down to the minute (no seconds and millis)
      // Value 0 is ignored.
      // Value 1 is without the plural

      if (days > 1)
         strD = days +  mainActivity.getString(R.string.day_plural);
      else if (days == 1)
         strD = mainActivity.getString(R.string.day_singular);
      else
         strD = "";

      if (hours > 1)
         strH = hours + mainActivity.getString(R.string.hour_plural);
      else if (hours == 1)
         strH = mainActivity.getString(R.string.hour_singular);
      else
         strH = "";

      if (minutes > 1)
         strM = minutes + mainActivity.getString(R.string.minute_plural);
      else if (minutes == 1)
         strM = mainActivity.getString(R.string.minute_singular);
      else
         strM = "";


      return mainActivity.getString(R.string.alarm_scheduled_for) + strD + strH + strM + mainActivity.getString(R.string.from_now);
   }

   // Getters
   public int getHour() {
      return hour;
   }

   // Setters
   public void setHour(int hour) {
      this.hour = hour;
   }

   // Getters (Preferences)

   public int getMinute() {
      return minute;
   }

   public String getLabel() {
      return label;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   public boolean isActive() {
      return active;
   }

   public void setActive(boolean active) {
      this.active = active;
      //resetSnoozeCounter();
   }

   public long getCreateTime() {
      return createTime;
   }

   public int getSnoozeCounter() {
      return snoozeCounter;
   }

   public void setSnoozeCounter(int snoozeCounter) {
      this.snoozeCounter = snoozeCounter;
   }

   public int getDayOfMonth() {
      return dayOfMonth;
   }

   public void setDayOfMonth(int dayOfMonth) {
      this.dayOfMonth = dayOfMonth;
   }

   public int getMonth() {
      return month;
   }

   public void setMonth(int month) {
      this.month = month;
   }

   public int getYear() {
      return year;
   }

   public void setYear(int year) {
      this.year = year;
   }

   public boolean isFutureDate() {
      return futureDate;
   }

   public String getRingDuration() {
      return ringDuration;
   }

   public void setRingDuration(String val) {
      ringDuration = val;
   }

   public String getRingRepeat() {
      return ringRepeat;
   }

   public void setRingRepeat(String val) {
      ringRepeat = val;
   }

   public String getSnoozeDuration() {
      return snoozeDuration;
   }

   // Copy the days mask without affecting the one-off bit
   // public void setWeekDays(int mask){ weekDays = mask;}

   public void setSnoozeDuration(String val) {
      snoozeDuration = val;
   }

   public String getVibrationPattern() {
      return vibrationPattern;
   }

   public void setVibrationPattern(String val) {
      vibrationPattern = val;
   }

   //public void setFutureDate(boolean futureDate) {this.futureDate = futureDate;}

   // Setters (Preferences)

   public boolean isVibrationActive() {
      return !getVibrationPattern().equals("none");
   }

   public String getAlarmSound() {
      return alarmSound;
   }

   public void setAlarmSound(String val) {
      alarmSound = val;
   }

   public boolean isAlarmMute() {
      return getAlarmSound().equals("silent");
   }

   public String getGradualVolume() {
      return gradualVolume;
   }

   public void setGradualVolume(String val) {
      gradualVolume = val;
   }

   public boolean isOneOff() {
      return oneOff;
   }

   // public void setExceptionDatesStr(String exceptionDatesStr) {this.exceptionDatesStr = exceptionDatesStr;}

   // Set/reset One-of bit
   public void setOneOff(boolean set) {
      oneOff = set;
   }

   public int getWeekDays() {
      return weekDays;
   }

   public int getGenStatus() {
      return genStatus;
   }

   public void setGenStatus(int genStatus) {
      this.genStatus = genStatus;
   }

   public String getExceptionDatesStr() {
      return exceptionDatesStr;
   }

   public boolean isRinging() {
      int stat = getGenStatus();
      return (stat & RINGING) > 0;
   }

   public void setRinging(boolean ringing) {
      int stat = getGenStatus();
      if (ringing)
         stat |= RINGING;
      else
         stat &= (~RINGING);
      setGenStatus(stat);
   }

   public void resetSnoozeCounter() {
      setSnoozeCounter(0);
   }

   public void incSnoozeCounter() {
      setSnoozeCounter(getSnoozeCounter() + 1);
   }

   /*** Helper Functions ***/

   /*
    * Encapsulate the AlarmItem in a bundle.
    * To be used to construct a duplicate AlarmItem
    */
   public Bundle getBundle() {
      Bundle b = new Bundle();

      b.putInt(K_HOUR, hour);
      b.putInt(K_MINUTE, minute);
      b.putString(K_LABEL, label);
      b.putBoolean(K_ACTIVE, active);
      b.putLong(K_CTIME, createTime);
      b.putBoolean(K_ONE_OFF, oneOff);
      b.putInt(K_WEEKDAYS, weekDays);
      b.putInt(K_CSNOOZE, snoozeCounter);
      b.putInt(K_DAY_OF_MONTH, dayOfMonth);
      b.putInt(K_MONTH, month);
      b.putInt(K_YEAR, year);
      b.putBoolean(K_F_DATE, futureDate);
      b.putString(K_EXCEPTION, exceptionDatesStr);

      // Preferences
      Context context = TheTimeMachineApp.appContext;
      b.putString(mainActivity.getString(R.string.key_ring_duration), ringDuration);
      b.putString(mainActivity.getString(R.string.key_ring_repeat), ringRepeat);
      b.putString(mainActivity.getString(R.string.key_snooze_duration), snoozeDuration);
      b.putString(mainActivity.getString(R.string.key_vibration_pattern), vibrationPattern);
      b.putString(mainActivity.getString(R.string.key_alarm_sound), alarmSound);
      b.putString(mainActivity.getString(R.string.key_gradual_volume), gradualVolume);

      return b;
   }

   /*  Convert alarm time to LocalDateTime    */
   public LocalDateTime alarmTimeInLocalDateTime() {

      // Get the time for the next Alarm

      LocalDateTime dateTime = LocalDateTime.now();
      try {
         dateTime = dateTime.withHour(hour).withMinute(minute).withSecond(0)
               .with(ChronoField.MILLI_OF_SECOND, 0);
      } catch (DateTimeException e) {
         Log.e("THE_TIME_MACHINE", "alarmTimeInLocalDateTime()[1] e = " + e);
         e.printStackTrace();
      }

      // If this a future date by given calendar date
           try {
         if (isFutureDate() && getDayOfMonth() > 0 && getYear() > 0) {
            dateTime = LocalDateTime.of(year, month + 1, dayOfMonth, hour, minute, 0, 0);
         }
   } catch (DateTimeException e) {
         Log.e("THE_TIME_MACHINE", "alarmTimeInLocalDateTime()[2] e = " + e);
         e.printStackTrace();
   }


      /*  */


      // Calculate the snooze delay (if needed)
      int snoozeDelay;
      if (snoozeCounter > 0)
         snoozeDelay = Str2Int_SnoozeDuration(); // Preference
      else
         snoozeDelay = 0;

      // Everytime this alarm goes off - snoozeCounter is incremented
      // The alarm should then scheduled to snoozeAdd
      // The next time it will go off will be after the next delay
      if (snoozeDelay > 0)
         dateTime = LocalDateTime.now().plusSeconds(snoozeDelay / 1000);


      // if alarm time has already passed, increment day by 1
      if (dateTime.isBefore(LocalDateTime.now()) && !isFutureDate())
         dateTime = dateTime.plusDays(1);


      return dateTime;
   }

   /*  Convert alarm time to LocalDateTime    */
   public LocalDateTime getAlarmTime() {


      LocalDateTime now = LocalDateTime.now();
      LocalDateTime dateTime = null;

      try {
         if (isFutureDate() && getYear() > 0)
            dateTime = LocalDateTime.of(year, month + 1, dayOfMonth, hour, minute);
         else
            dateTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), hour, minute);
      } catch (DateTimeException e) {
         Log.d("THE_TIME_MACHINE", "getAlarmTime() id=" + createTime + " day/month/year " + dayOfMonth + "/" + month + "/" + year);
         Log.e("THE_TIME_MACHINE", "getAlarmTime() e=" + e);
         e.printStackTrace();
         return now;
      }


      // Calculate the snooze delay (if needed)
      int snoozeDelay;
      if (snoozeCounter > 0)
         snoozeDelay = Str2Int_SnoozeDuration(); // Preference
      else
         snoozeDelay = 0;

      // Everytime this alarm goes off - snoozeCounter is incremented
      // The alarm should then scheduled to snoozeAdd
      // The next time it will go off will be after the next delay
      if (snoozeDelay > 0)
         dateTime = LocalDateTime.now().plusSeconds(snoozeDelay / 1000);


      // if alarm time has already passed, increment day by 1
      if (dateTime.isBefore(LocalDateTime.now()) && !isFutureDate()) {
         dateTime = dateTime.plusDays(1);
      }

      return dateTime;
   }

   // Preferences: Convert from Menu-String to real Value
   // Preference: Duration of ringing until it stops
   // Returns:
   // Duration in milliseconds (Default is 30000)

   /* Get the weekday of the currently assigned alarm time
 Convert it to Zero/Sunday based number: Sun=0 --> Sat=6 */
   public int getWeekdayOfNextAlarm() {
      LocalDateTime alarmTime = nextAlarmTimeInLocalDateTime();
      return alarmTime.getDayOfWeek().getValue();
   }

   /*   Get the time of the next alarm in LocalDateTime    */
   public LocalDateTime nextAlarmTimeInLocalDateTime() {

      LocalDateTime alarmTime = alarmTimeInLocalDateTime();

      // Get the weekday of the currently assigned alarm time
      // Convert it to Zero/Sunday based number: Sun=0 --> Sat=6
      int dayOfWeek = alarmTime.getDayOfWeek().getValue();

      // For each Day in weekDays (Only the ones that are ON):
      // - Convert it to a number nDay ( One/Sunday based number: Sun=1 --> Sat=7)
      // - Calculate nDay as the diff between this number and datOfWeek
      // - If nDay is smaller than dayOfWeek add 7 to it
      // - Multiple nDay by the number of milliseconds in a day and add it to calendar time
      // - Set the alarm manager with this calender time
      int[] days = {SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY};
      for (int i = 0; i < 100; i++) { // 100 is just a large number
         int nDay = (dayOfWeek + i) % 7;
         if ((weekDays & days[nDay]) == 0) continue;
         LocalDateTime out = alarmTime.plusDays(i);

         // Check if this time falls in one of the dates in the list of exceptions
         if (!isDateException(out))
            return out;
      }
      return alarmTime;
   }

   // Compare given time (in millis) with the dates in the list of exceptions
   // If given time falls in one of the dates in the list - return true
   private boolean isDateException(LocalDateTime alarmTime) {
      // Is there a valid exception list?
      if (exceptionDatesStr == null || exceptionDatesStr.isEmpty())
         return false;

      // Convert timeInMillis to date in YYYYMMDD format
      int y, m, d;
      y = alarmTime.getYear();
      m = alarmTime.getMonthValue();
      d = alarmTime.getDayOfMonth();
      String dateStr = Integer.toString(d + 100 * m + 10000 * y);

      // Search the exceptionDatesStr for this string
      int index = exceptionDatesStr.indexOf(dateStr);
      // Not found
      return index != -1;
   }

   /* True if the alarm is set for today */
   public boolean isToday() {
      // If alarm is ringing then it must be today
      if (isRinging()) return true;

      // Get time of today's midnight (minus a second)
      LocalDateTime now = LocalDateTime.now();
      LocalDateTime midnight = now.withHour(23).withMinute(59).withSecond(59);

      return (!getAlarmTime().isAfter(midnight));
   }

   /* True if the alarm is set for tomorrow */
   public boolean isTomorrow() {

      LocalDateTime now = LocalDateTime.now();
      // Get today's time in millis at 0:00:00.
      // Then add 24h to get the time of the beginning of tomorrow.
      // Then add additional 24h-1sec to get the time of the end of tomorrow.
      LocalDateTime today_midnight = now.withHour(0).withMinute(0).withSecond(0).withNano(0);
      LocalDateTime tomorrow_midnight = today_midnight.plusDays(1);
      LocalDateTime tomorrow_last_sec = tomorrow_midnight.plusDays(1).minusSeconds(1);
      LocalDateTime alarm_time = getAlarmTime();
      return !alarm_time.isBefore(tomorrow_midnight) && !alarm_time.isAfter(tomorrow_last_sec);
   }

   /* True if the alarm is explicitly set to the past */
   public boolean isNotInThePast() {

/**/
      // When date not explicit -> not in the past
      if (getYear() == 0  || getDayOfMonth() == 0)
         return true;

      // Get time current time
      LocalDateTime now = LocalDateTime.now();

      // Convert alarm time to to Calendar
      LocalDateTime alarmTime = LocalDateTime.of(getYear(), getMonth()+1, getDayOfMonth(), getHour(), getMinute(), 30);
      Log.d("THE_TIME_MACHINE", "isNotInThePast() return  " + (!alarmTime.isBefore(now)));
      return !alarmTime.isBefore(now);
   }

   /*If alarm is explicitly set to the past then change the explicit date to Today or tomorrow */
   public void recalculateDate() {
      Log.d("THE_TIME_MACHINE", "recalculateDate()[1]");
      if (isNotInThePast()) return;
      Log.d("THE_TIME_MACHINE", "recalculateDate()[2]");

      // Get time current time
      LocalDateTime now = LocalDateTime.now();
      Log.d("THE_TIME_MACHINE", "recalculateDate()[3] getHour=" + getHour() + " ; getMinute=" + getMinute() + " ; getDayOfMonth " + getDayOfMonth());

      // Check if the alarm can still ring today
      if ((now.getMinute() + now.getHour() * 24) > (getMinute() + getHour() * 24))
         now.plusDays(1);

      setDayOfMonth(now.getDayOfMonth());
      setMonth(now.getMonth().getValue()-1);
      setYear(now.getYear());

      Log.d("THE_TIME_MACHINE", "recalculateDate()[4] getHour=" + getHour() + " ; getMinute=" + getMinute() + " ; getDayOfMonth " + getDayOfMonth());
   }

   // Copy Global preferences to Item preferences
   public void getPreferences() {
      // Get context and Default Shared Preferences
      Context context = TheTimeMachineApp.appContext;
      SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

      // Set preferences as strings
      setRingDuration(preferences.getString(mainActivity.getString(R.string.key_ring_duration), ""));
      setRingRepeat(preferences.getString(mainActivity.getString(R.string.key_ring_repeat), ""));
      setSnoozeDuration(preferences.getString(mainActivity.getString(R.string.key_snooze_duration), ""));
      setVibrationPattern(preferences.getString(mainActivity.getString(R.string.key_vibration_pattern), ""));
      setAlarmSound(preferences.getString(mainActivity.getString(R.string.key_alarm_sound), ""));
      setGradualVolume(preferences.getString(mainActivity.getString(R.string.key_gradual_volume), ""));

   }

   public int Str2Int_SnoozeDuration() {
      // String is of type 15000Seconds
      if (snoozeDuration.length() < 4)
         return 60000;

      // Extract the numeral part and convert from seconds to milliseconds
      String intValue = snoozeDuration.replaceAll("[^0-9]", "");
      return Integer.parseInt(intValue) * 1000;
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
    *  time 'Snooze' was pressed. The offset is delay*snooze_counter.
    *  In the case of auto-snooze, when the snooze counter reaches the limit -
    *  the alarm is deactivated
    *
    *<p>
    *  In auto-snooze the alarm time is calculated as an offset from the
    *  time the ringing stopped. The offset is delay*snooze_counter.
    *  When the snooze counter reaches the limit -
    *  the alarm is deactivated
    *
    */
   public void Exec() {
      LocalDateTime alarmTime;
      // Things that are common to all/most tasks

      // Get context
      Context context = TheTimeMachineApp.appContext;
      if (context == null) return;

      // Get Alarm Manager
      AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

      // Create an intent for the Alarm Receiver then
      Intent intent = new Intent(context, AlarmReceiver.class);

      // Extract id from alarm's creation time
      int id = (int) createTime;

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
         if (!isOneOff() && (weekDays != 0))
            alarmTime = nextAlarmTimeInLocalDateTime();
         else
            // Time of coming alarm (One-Off, possibly snoozing, possibly a future date)
            alarmTime = alarmTimeInLocalDateTime();


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
         Intent targetIntent = new Intent(context, MainActivity.class);
         PendingIntent targetPending = PendingIntent.getActivity(
               context, requestCode, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);


         // Set Alarm Clock (Convert alarm LocalDateTime to millis since Epoch)
         long milli = alarmTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
         AlarmManager.AlarmClockInfo ac = new AlarmManager.AlarmClockInfo(milli, targetPending);
         alarmManager.setAlarmClock(ac, alarmIntent);


         // Toast
         Toast.makeText(context, strGetDurationToAlarm(alarmTime), Toast.LENGTH_LONG).show();
      }
      else {
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
         String toastText = String.format(mainActivity.getResources().getString(R.string.msg_alarm_canceled), hour, minute);
         Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
         Log.d("THE_TIME_MACHINE", "Exec(): " + toastText);
      }
   }

}