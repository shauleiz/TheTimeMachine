package com.example.thetimemachine;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.thetimemachine.Data.AlarmRepository;

import java.util.Calendar;
import java.util.ArrayList;
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
   public MutableLiveData<List<AlarmItem>> LiveAlarmList;
   private List<AlarmItem> AlarmList;
   private AlarmRepository repo;
  Observer<List<AlarmRepository.RawAlarmItem>> ObsFrevr;

   // Constructor
   public AlarmViewModel(Application application) {
      super(application);

      setUpAlarmValues = new SetUpAlarmValues();
      LiveAlarmList = new MutableLiveData<List<AlarmItem>>();
      repo = new AlarmRepository(application);


      ObsFrevr = new Observer<List<AlarmRepository.RawAlarmItem>>() {
         @Override
         public void onChanged(List<AlarmRepository.RawAlarmItem> rawAlarmItems) {
            // TODO: Read repo and reconstruct Alarm List
            AlarmList = convertRawList(rawAlarmItems);
            LiveAlarmList.setValue(AlarmList);
         }
      };
      repo.getAlarmList().observeForever(ObsFrevr);

   }

   private List<AlarmItem> convertRawList(List<AlarmRepository.RawAlarmItem> rawAlarmItems){
      List<AlarmItem> out = new ArrayList<>();

      for (int i = 0; i < rawAlarmItems.size(); i++) {
         AlarmRepository.RawAlarmItem rawItem =  rawAlarmItems.get(i);
         int h = rawItem.minutesSinceMidnight/60;
         int m = rawItem.minutesSinceMidnight%60;
         AlarmItem item = new AlarmItem(h, m, rawItem.label, rawItem.active, rawItem.id);
         out.add(item);
      }
      return out;
   }

   @Override
   protected void onCleared() {
      repo = null;
      // TODO: Fix this: LiveAlarmList.removeObserver((Observer<ArrayList<AlarmItem>>) ObsFrevr);
      super.onCleared();
   }

   public void AddAlarm(int _hour, int _minute, String _label, boolean _active) {
      AlarmItem item = new AlarmItem(_hour, _minute, _label, _active);
      //AlarmList.add(item);
      //LiveAlarmList.setValue(AlarmList);

      // Repository
      repo.AddAlarm( _hour,  _minute,  _label,  _active, item.getCreateTime());
   }

   public void DeleteAlarm(int _position){


      AlarmList = LiveAlarmList.getValue();
      if (AlarmList==null) return;

      // Repository
      repo.DeleteAlarm(AlarmList.get(_position).getCreateTime());

      //AlarmList.remove(_position);
      //LiveAlarmList.setValue(AlarmList);
   }

   public void UpdateAlarm(int _hour, int _minute, String _label, boolean _active, int _position) {
      AlarmList = LiveAlarmList.getValue();
      if (AlarmList==null) return;
      AlarmItem item = AlarmList.get(_position);
      //item.hour = _hour;
      //item.minute = _minute;
      //item.label = _label;
      //LiveAlarmList.setValue(AlarmList);

      // Repository
      repo.AddAlarm( _hour,  _minute,  _label,  _active, item.getCreateTime());
   }

   public MutableLiveData<List<AlarmItem>> getAlarmList() {

      return LiveAlarmList;
   }

   // This class hold an item in the Alarm List
   public class AlarmItem {
      long createTime;
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


      // TODO: Implement getters and setters

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

      public long getCreateTime(){return  createTime;}
   }

   // This Class holds the values of the alarm that is being added/modified
   public class SetUpAlarmValues {
      MutableLiveData<Integer> hour, minute;
      MutableLiveData<String> label;
      MutableLiveData<Boolean> active;

      // Default constructor
      public SetUpAlarmValues() {
         this.hour = new MutableLiveData<>();
         this.minute = new MutableLiveData<>();
         this.label = new MutableLiveData<>();
         this.active = new MutableLiveData<>();
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

      public void ResetValues(){
         // TODO: Reset to user defined values
         hour.setValue(0);
         minute.setValue(0);
         label.setValue("");
      }

      public void GetValuesFromList(int position){
         // Sanity check
         if (position<0)
            return;
         hour.setValue(AlarmList.get(position).getHour());
         minute.setValue(AlarmList.get(position).getMinute());
         label.setValue(AlarmList.get(position).getLabel());
         active.setValue(AlarmList.get(position).isActive());
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

      public void setMinute(int minute) {
         this.minute.setValue(minute);
      }

      public MutableLiveData<String> getLabel() {
         return label;
      }

      public MutableLiveData<Boolean> isActive() {return active;}

      public void setLabel(String label) {
         String t = this.label.getValue();
         assert t != null;
         if (!(t.equals(label)))
            this.label.setValue(label);
      }

   }


}
