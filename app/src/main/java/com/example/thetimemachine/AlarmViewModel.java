package com.example.thetimemachine;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import androidx.lifecycle.MutableLiveData;


import com.example.thetimemachine.Data.AlarmItem;
import com.example.thetimemachine.Data.AlarmRepository;

import java.util.Calendar;
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

   public SetUpAlarmValues setUpAlarmValues;
   public LiveData<List<AlarmItem>> LiveAlarmList;
   private List<AlarmItem> AlarmList;
   private AlarmRepository repo;
 // Observer<List<AlarmRepository.RawAlarmItem>> ObsFrevr;

   // Constructor
   public AlarmViewModel(Application application) {
      super(application);

      setUpAlarmValues = new SetUpAlarmValues();
      repo = new AlarmRepository(application);
      LiveAlarmList = repo.getAlarmList();




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

   public void DeleteAlarm(int _position){

      // Get List
      AlarmList = LiveAlarmList.getValue();
      if (AlarmList==null) return;

      // Get alarm item and cancel the alarm
      AlarmItem item = (AlarmList.get(_position));
      item.setActive(false);
      item.Exec();

      // Repository
      repo.DeleteAlarm(item);

   }

   public void UpdateAlarm(AlarmItem item) {
      // Repository
      repo.UpdateAlarm(item);
   }

   public LiveData<List<AlarmItem>> getAlarmList() {

      return LiveAlarmList;
   }

   // This Class holds the values of the alarm that is being added/modified
   public class SetUpAlarmValues {
      MutableLiveData<Integer> hour, minute;
      MutableLiveData<String> label;
      MutableLiveData<Boolean> active;
      MutableLiveData<Long>  createTime;
      MutableLiveData<Integer> weekDays;
      MutableLiveData<Boolean> oneOff;

      // Default constructor
      public SetUpAlarmValues() {
         this.hour = new MutableLiveData<>();
         this.minute = new MutableLiveData<>();
         this.label = new MutableLiveData<>();
         this.active = new MutableLiveData<>();
         this.createTime = new MutableLiveData<>();
         this.weekDays = new MutableLiveData<>();
         this.oneOff = new MutableLiveData<>();
         ResetValues();
      }

      public SetUpAlarmValues(int hour, int minute, String label, boolean newAlarm) {
         this.hour = new MutableLiveData<>();
         this.minute = new MutableLiveData<>();
         this.label = new MutableLiveData<>();
         //this.active = new MutableLiveData<>();

         if (newAlarm) { // Add new alarm
            // TODO: Get the default values and ignore the passed values
            this.hour.setValue(hour);
            this.minute.setValue(minute);
            this.label.setValue(label);
         }
         // TODO: Modify alarm
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


      public MutableLiveData<String> getLabel() {
         return label;
      }

      public MutableLiveData<Boolean> isActive() {return active;}
      public MutableLiveData<Boolean> isOneOff() {return oneOff;}

      public void setLabel(String label) {
         String t = this.label.getValue();
         assert t != null;
         if (!(t.equals(label)))
            this.label.setValue(label);
      }

   }


}
