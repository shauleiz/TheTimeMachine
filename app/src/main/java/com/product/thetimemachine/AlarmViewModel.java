package com.product.thetimemachine;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.product.thetimemachine.Application.TheTimeMachineApp;
import com.product.thetimemachine.Data.AlarmItem;
import com.product.thetimemachine.Data.AlarmRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/*
 *      Alarm Clock ViewModel Class
 *  Holds data for the Alarm Clock UI
 *  - Alarm Clock activity
 *  - Alarm Clock Setting fragment
 *
 *  Also interacts with the backup data base (Room)
 */
public class AlarmViewModel extends AndroidViewModel {

   public final SetUpAlarmValues setUpAlarmValues;
   public final LiveData<List<AlarmItem>> LiveAlarmList;
   private List<AlarmItem> AlarmList;
   private AlarmRepository repo;

   private final MutableLiveData<ArrayList<Integer>> LiveSelectedItems;
   private final ArrayList<Integer> selectedItems;

 // Observer<List<AlarmRepository.RawAlarmItem>> ObsFrevr;

   // Constructor
   public AlarmViewModel(Application application) {
      super(application);

      setUpAlarmValues = new SetUpAlarmValues();
      repo = new AlarmRepository(application);
      LiveAlarmList = repo.getAlarmList();
      selectedItems = new ArrayList<>();
      LiveSelectedItems = new MutableLiveData<>();
   }
   @Override
   protected void onCleared() {
      repo = null;
      // TODO: Fix this: LiveAlarmList.removeObserver((Observer<ArrayList<AlarmItem>>) ObsFrevr);
      super.onCleared();
   }

   public void AddAlarm(AlarmItem item) {
      // Repository
      repo.AddAlarm( item);
   }

   public void DeleteAlarm(AlarmItem item){
      // Remove from list of selected alarms
      int id = (int)item.getCreateTime();
      int index = selectedItems.indexOf(id);
      Log.d("THE_TIME_MACHINE", "DeleteAlarm(): by item ");
      if (index >=0) {
         selectedItems.remove(index);
         LiveSelectedItems.setValue(selectedItems);
      }

      // Get alarm item and cancel the alarm
      item.setActive(false);
      item.Exec();

      // top alarm in case it is ringing
      Bundle b = item.getBundle();
      Context context = TheTimeMachineApp.appContext;
      Intent stopIntent = new Intent(context, AlarmService.class);
      stopIntent.putExtras(b);
      AlarmReceiver.stopping(context, stopIntent );


      // Repository
      repo.DeleteAlarm(item);
   }

   public void UpdateAlarm(AlarmItem item) {
      // Repository
      repo.UpdateAlarm(item);
   }

   public AlarmItem getAlarmItemById(int id){

      // Get List
      AlarmList = LiveAlarmList.getValue();
      if (AlarmList==null) return null;

      for (int i=0; i<AlarmList.size();i++){
         AlarmItem item = AlarmList.get(i);
         if (((int)item.getCreateTime()) == id)
            return item;
      }
      return null;
   }

   public LiveData<List<AlarmItem>> getAlarmList() {

      return LiveAlarmList;
   }

   public void toggleSelection(long id){
      int index = selectedItems.indexOf((int)id);
      if (index >=0)
         selectedItems.remove(index);
      else
         selectedItems.add((int)id);

      LiveSelectedItems.setValue(selectedItems);
   }

   public boolean clearSelection(long id){
      int index = selectedItems.indexOf((int)id);
      if (index >=0) {
         selectedItems.remove(index);
         LiveSelectedItems.setValue(selectedItems);
         return true;
      }
      return false;
   }

   public boolean isDuplicate(int hour, int minute, long createTime) {
      // Get List
      AlarmList = LiveAlarmList.getValue();
      if (AlarmList==null) return false;

      for (int i=0; i<AlarmList.size();i++){
         AlarmItem item = AlarmList.get(i);
         if ((item.getHour()) == hour && (item.getMinute()==minute) && (item.getCreateTime()!=createTime))
            return true;
      }
      return false;
   }
   public int getNofSelectedItems(){
      return selectedItems.size();
   }
   public MutableLiveData<ArrayList<Integer>> getSelectedItems(){
      Log.d("THE_TIME_MACHINE", "getSelectedItems()");
      return LiveSelectedItems;
   }
   // This Class holds the values of the alarm that is being added/modified
   public class SetUpAlarmValues {
      final MutableLiveData<Integer> hour, minute;
      final MutableLiveData<String> label;
      final MutableLiveData<Boolean> active;
      final MutableLiveData<Long>  createTime;
      final MutableLiveData<Integer> weekDays;
      final MutableLiveData<Boolean> oneOff;

      final MutableLiveData<Integer> dayOfMonth, month, year;
      final MutableLiveData<Boolean>  futureDate;

      final MutableLiveData<List<Date>> exceptionDates;

      // Preferences
      final MutableLiveData<String> ringDuration;
      final MutableLiveData<String> ringRepeat;
      MutableLiveData<String> snoozeDuration;
      final MutableLiveData<String> vibrationPattern;
      final MutableLiveData<String> alarmSound;
      final MutableLiveData<String> gradualVolume;


      // Default constructor
      public SetUpAlarmValues() {
         this.hour = new MutableLiveData<>();
         this.minute = new MutableLiveData<>();
         this.label = new MutableLiveData<>();
         this.active = new MutableLiveData<>();
         this.createTime = new MutableLiveData<>();
         this.weekDays = new MutableLiveData<>();
         this.oneOff = new MutableLiveData<>();
         this.dayOfMonth = new MutableLiveData<>();
         this.month= new MutableLiveData<>();
         this.year= new MutableLiveData<>();
         this.futureDate = new MutableLiveData<>();
         this.exceptionDates = new MutableLiveData<>();

         // Preferences
         snoozeDuration = new MutableLiveData<>();
         ringDuration = new MutableLiveData<>();
         ringRepeat = new MutableLiveData<>();
         snoozeDuration = new MutableLiveData<>();
         vibrationPattern = new MutableLiveData<>();
         alarmSound = new MutableLiveData<>();
         gradualVolume = new MutableLiveData<>();

         ResetValues();
      }



      // Reset Setup Values: Time is the current time. Label is empty
      public void ResetValues(){
         Calendar calendar = Calendar.getInstance();
         calendar.setTimeInMillis(System.currentTimeMillis());

         hour.setValue(calendar.get(Calendar.HOUR_OF_DAY));
         minute.setValue(calendar.get(Calendar.MINUTE));
         label.setValue("");

         weekDays.setValue(0);
         oneOff.setValue(true);

         dayOfMonth.setValue(0);
         month.setValue(0);
         year.setValue(0);
         futureDate.setValue(false);
         exceptionDates.setValue(null);

         // Preferences
         // Copy default preferences to this item's preferences
         Context context = TheTimeMachineApp.appContext;
         SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

         ringDuration.setValue(preferences.getString(context.getString(R.string.key_ring_duration), ""));
         ringRepeat.setValue(preferences.getString(context.getString(R.string.key_ring_repeat), ""));
         snoozeDuration.setValue(preferences.getString(context.getString(R.string.key_snooze_duration), ""));
         vibrationPattern.setValue(preferences.getString(context.getString(R.string.key_vibration_pattern), ""));
         alarmSound.setValue(preferences.getString(context.getString(R.string.key_alarm_sound), ""));
         gradualVolume.setValue(preferences.getString(context.getString(R.string.key_gradual_volume), ""));


         //weekDays.setValue(ONEOFF);
      }

      public void GetValuesFromList(int position){
         // Sanity check
         if (position<0)
            return;
         AlarmList = LiveAlarmList.getValue();
         assert AlarmList != null;
         hour.setValue(AlarmList.get(position).getHour());
         minute.setValue(AlarmList.get(position).getMinute());
         label.setValue(AlarmList.get(position).getLabel());
         active.setValue(AlarmList.get(position).isActive());

         AlarmItem ai = AlarmList.get(position);
         long c = ai.getCreateTime();
         createTime.setValue(c);

         weekDays.setValue(AlarmList.get(position).getWeekDays());
         oneOff.setValue((AlarmList.get(position).isOneOff()));

         dayOfMonth.setValue(AlarmList.get(position).getDayOfMonth());
         month.setValue(AlarmList.get(position).getMonth());
         year.setValue(AlarmList.get(position).getYear());
         futureDate.setValue(AlarmList.get(position).isFutureDate());
         // TODO: exceptionDates.setValue()

         // Preferences ???
      }

      public void GetValuesFromList(AlarmItem item, boolean edit){
         // Sanity check
         if (item == null)
            return;

         hour.setValue(item.getHour());
         minute.setValue(item.getMinute());
         label.setValue(item.getLabel());
         active.setValue(item.isActive());


         long c;
         if (edit)
            c = item.getCreateTime();
         else
            c=0;
         createTime.setValue(c);

         weekDays.setValue(item.getWeekDays());
         oneOff.setValue((item.isOneOff()));

         dayOfMonth.setValue(item.getDayOfMonth());
         month.setValue(item.getMonth());
         year.setValue(item.getYear());
         futureDate.setValue(item.isFutureDate());
         // TODO: exceptionDates.setValue()

         // Preferences
         ringDuration.setValue(item.getRingDuration());
         ringRepeat.setValue(item.getRingRepeat());
         snoozeDuration.setValue(item.getSnoozeDuration());
         vibrationPattern.setValue(item.getVibrationPattern());
         alarmSound.setValue(item.getAlarmSound());
         gradualVolume.setValue(item.getGradualVolume());
      }
      public void GetValuesFromList(AlarmItem item){
         GetValuesFromList( item, true);
      }


      /* Getter Methods */
      public MutableLiveData<Integer> getHour() {
         return hour;
      }

      // Reset Alarm
      /* Setter Methods*/
      public void setHour(int hour) {
         this.hour.setValue(hour);
      }

      public MutableLiveData<Integer> getMinute() {
         return minute;
      }

      public MutableLiveData<Integer> getWeekDays(){return  weekDays;}


      public void setMinute(int minute) {
         this.minute.setValue(minute);
      }

      public void setWeekDays(int weekDays){this.weekDays.setValue(weekDays);}

      public void setOneOff(boolean oneOff){this.oneOff.setValue(oneOff);}

      public void setDayOfMonth(int dayOfMonth) {this.dayOfMonth.setValue(dayOfMonth) ;}

      public void setMonth(int month) {this.month.setValue(month);}

      public void setYear(int year) {this.year.setValue(year);}

      public void setFutureDate(boolean futureDate) {this.futureDate.setValue(futureDate) ;}
      public void  setExceptionDates(List<Date> dates){this.exceptionDates.setValue(dates);}

      public void setRingduration(String val) {this.ringDuration.setValue(val);}
      public void setRingRepeat(String val) {this.ringRepeat.setValue(val);}
      public void setSnoozeDuration(String val) {this.snoozeDuration.setValue(val);}
      public void setVibrationPattern(String val) {this.vibrationPattern.setValue(val);}
      public void setAlarmSound(String val) {this.alarmSound.setValue(val);}
      public void setGradualVolume(String val) {this.gradualVolume.setValue(val);}

      public MutableLiveData<String> getLabel() {
         return label;
      }

      public MutableLiveData<Boolean> isActive() {return active;}
      public MutableLiveData<Boolean> isOneOff() {return oneOff;}

      public MutableLiveData<Integer> getDayOfMonth() {return dayOfMonth;}

      public MutableLiveData<Integer> getMonth() {return month;}

      public MutableLiveData<Integer> getYear() {return year;}

      public MutableLiveData<Boolean> isFutureDate() {return futureDate;}
      public MutableLiveData<List<Date>> getExceptionDates(){return exceptionDates;}

      public MutableLiveData<String> getRingDuration() {return  ringDuration;}
      public MutableLiveData<String> getRingRepeat() {return  ringRepeat;}
      public MutableLiveData<String> getSnoozeDuration() {return snoozeDuration;}
      public MutableLiveData<String> getVibrationPattern() {return vibrationPattern;}
      public MutableLiveData<String> getAlarmSound() {return alarmSound;}
      public MutableLiveData<String> getGradualVolume() {return gradualVolume;}

      public void setLabel(String label) {
         String t = this.label.getValue();
         assert t != null;
         if (!(t.equals(label)))
            this.label.setValue(label);
      }

   }


}
