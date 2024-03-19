package com.example.thetimemachine;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.thetimemachine.Data.AlarmItem;
import com.example.thetimemachine.Data.AlarmRepository;

import java.util.ArrayList;
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
      MutableLiveData<Integer> hour, minute;
      MutableLiveData<String> label;
      MutableLiveData<Boolean> active;
      MutableLiveData<Long>  createTime;
      MutableLiveData<Integer> weekDays;
      MutableLiveData<Boolean> oneOff;

      MutableLiveData<Integer> dayOfMonth, month, year;
      MutableLiveData<Boolean>  futureDate;

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

         dayOfMonth.setValue(0);
         month.setValue(0);
         year.setValue(0);
         futureDate.setValue(false);

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
      }

      public void GetValuesFromList(AlarmItem item, boolean edit){
         // Sanity check
         if (item == null)
            return;

         hour.setValue(item.getHour());
         minute.setValue(item.getMinute());
         label.setValue(item.getLabel());
         active.setValue(item.isActive());


         AlarmItem ai = item;
         long c;
         if (edit)
            c = ai.getCreateTime();
         else
            c=0;
         createTime.setValue(c);

         weekDays.setValue(item.getWeekDays());
         oneOff.setValue((item.isOneOff()));

         dayOfMonth.setValue(item.getDayOfMonth());
         month.setValue(item.getMonth());
         year.setValue(item.getYear());
         futureDate.setValue(item.isFutureDate());
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

      public MutableLiveData<String> getLabel() {
         return label;
      }

      public MutableLiveData<Boolean> isActive() {return active;}
      public MutableLiveData<Boolean> isOneOff() {return oneOff;}

      public MutableLiveData<Integer> getDayOfMonth() {return dayOfMonth;}

      public MutableLiveData<Integer> getMonth() {return month;}

      public MutableLiveData<Integer> getYear() {return year;}

      public MutableLiveData<Boolean> isFutureDate() {return futureDate;}

      public void setLabel(String label) {
         String t = this.label.getValue();
         assert t != null;
         if (!(t.equals(label)))
            this.label.setValue(label);
      }

   }


}
