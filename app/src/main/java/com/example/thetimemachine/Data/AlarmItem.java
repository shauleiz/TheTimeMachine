package com.example.thetimemachine.Data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;

// This class hold an item in the Alarm List
@Entity(tableName = "raw_alarm_table")
public class AlarmItem {
   @PrimaryKey
   @NonNull
   private long createTime;
   private int hour, minute;
   private String label;
   private boolean active;


   // Constructor of Alarm Item - create time calculated internally
   public AlarmItem(int _hour, int _minute, String _label, boolean _active) {
      hour = _hour;
      minute = _minute;
      label = _label;
      active = _active;

      // Sanity check
      if (hour < 0 || hour > 23 || minute < 0 || minute > 59)
         active = false;

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

      // Sanity check
      if (hour < 0 || hour > 23 || minute < 0 || minute > 59)
         active = false;

      // Add time of creation (of original alarm) in milliseconds
      createTime = _createTime;
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

   public void setActive(boolean active) {
      this.active = active;
   }

   public void setHour(int hour) {this.hour = hour;}

   public void setLabel(String label) {this.label = label;}

   public void setMinute(int minute) {this.minute = minute;}

   public void setCreateTime(long createTime) {this.createTime = createTime;}

}