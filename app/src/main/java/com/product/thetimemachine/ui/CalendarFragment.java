package com.product.thetimemachine.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.product.thetimemachine.R;
//import com.wisnu.datetimerangepickerandroid.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class CalendarFragment extends DialogFragment {

   final private String KEY_MODE = "keyMode";
   final private String KEY_LIST_DATES = "keyListOfDates";
   final private String KEY_MIN_DATE = "keyMinDate";
   final private String KEY_MAX_DATE = "keyMaxDate";
   final private String KEY_HIGHLIGHT = "keyHighlight";

   CalendarPickerView.SelectionMode mode = CalendarPickerView.SelectionMode.SINGLE;
   CalendarPickerView datePicker;
   CalendarDialogListener listener;
   List<Date> selectedDates = new ArrayList<>();
   Date minDate, maxDate;
   int days2Highlight;


   public interface CalendarDialogListener {
      public void onCalendarDialogPositiveClick(DialogFragment dialog);
      public void onCalendarDialogNegativeClick(DialogFragment dialog);
   }


   public void setSelectionMode(CalendarPickerView.SelectionMode mode){
      this.mode = mode;
   }

   private CalendarPickerView.SelectionMode int2mode(int i){
      switch (i){
         case 0:
            return CalendarPickerView.SelectionMode.SINGLE;
         case 1:
            return CalendarPickerView.SelectionMode.MULTIPLE;
         case 2:
            return CalendarPickerView.SelectionMode.RANGE;
         default:
            return CalendarPickerView.SelectionMode.SINGLE;
      }
   }

   private int mode2int(CalendarPickerView.SelectionMode m){
      switch (m){
         case SINGLE:
            return 0;
         case MULTIPLE:
            return 1;
         case RANGE:
            return 2;
         default:
            return 0;
      }
   }


   // Use this instance of the interface to deliver action events.

   @Override
   public void onAttach(@NonNull Context context) {
      super.onAttach(context);

      // Verify that the host activity implements the callback interface.
      try {
         // Instantiate the CalendarDialogListener so you can send events to
         // the host.
         listener = (CalendarDialogListener) getParentFragment();
      } catch (ClassCastException e) {
         // The activity doesn't implement the interface. Throw exception.
         throw new ClassCastException(listener.toString()
                                            + " must implement CalendarDialogListener");
      }

   }
   // The system calls this only when creating the layout in a dialog.
   @NonNull
   @Override

   //-------------------------

   public Dialog onCreateDialog(Bundle savedInstanceState) {

      // Get the layout inflater.
      LayoutInflater inflater = requireActivity().getLayoutInflater();

      // Inflate and set the layout for the dialog.
      // Pass null as the parent view because it's going in the dialog layout.
      View viewFragmentCalendar = inflater.inflate(R.layout.fragment_calendar, null);

      // Alert dialog builder
      Dialog dialog = new AlertDialog.Builder(getActivity())
      .setTitle(R.string.dates_to_exclude_calendar_title)
      .setView(viewFragmentCalendar)

            // Add action buttons
            .setPositiveButton(R.string.ok_calendar, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                  // Send the positive button event back to the host activity.
                  listener.onCalendarDialogPositiveClick(CalendarFragment.this);
               }
            })

            .setNegativeButton(R.string.cancel_calendar, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                  // Send the negative button event back to the host activity.
                  listener.onCalendarDialogNegativeClick(CalendarFragment.this);
               }
            })
      .create();



      ArrayList<Integer> list;

      // Get the Saved State
      if (savedInstanceState !=null) {
         minDate = int2Date(savedInstanceState.getInt(KEY_MIN_DATE));
         maxDate = int2Date(savedInstanceState.getInt(KEY_MAX_DATE));
         mode = int2mode(savedInstanceState.getInt(KEY_MODE));
         days2Highlight = savedInstanceState.getInt(KEY_HIGHLIGHT);
         list = savedInstanceState.getIntegerArrayList(KEY_LIST_DATES);
         setSelectedDatesAsInt(list);
      }

      Log.d("THE_TIME_MACHINE", "onCreateDialog():  dateList = " + selectedDates);

      // Get the Date Picker
      datePicker = (CalendarPickerView) viewFragmentCalendar.findViewById(R.id.calendar_view);

      // Intercepts click. Prevents selection/unselection of dates that are not in the
      // right weekdays or that are in the past
      datePicker.setCellClickInterceptor(new CalendarPickerView.CellClickInterceptor() {
         @Override
         public boolean onCellClicked(Date date) {
            // Returns TRUE (intercept) is cell is highlighted or in the past
            return (isHighlighted(date) || isInThePast(date));
         }
      });

      // Initialize Date picker to support a year from today with today selected
      datePicker.init(minDate, maxDate)
      // Set Date Picker to support selection  multiple/single/range of dates
      .inMode(mode)
      // Highlight the weekdays with alarms
      .withHighlightedDates(getPreselectedDates())
      .withSelectedDates(selectedDates);

      dialog.setOnShowListener(new DialogInterface.OnShowListener() {
         @Override public void onShow(DialogInterface dialogInterface) {
            datePicker.fixDialogDimens();
         }
      });


      return dialog;
   }

   private boolean isHighlighted(Date date) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      int theDay = calendar.get(Calendar.DAY_OF_WEEK)-1;
      int mask = 1;
      mask = mask<<theDay;
      return ((days2Highlight & mask) == 0);
   }

   private boolean isInThePast(Date date) {
      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.HOUR_OF_DAY,0);
      calendar.set(Calendar.MINUTE,0);
      calendar.set(Calendar.SECOND,0);
      calendar.set(Calendar.MILLISECOND,0);
      Date midnight = new Date();
      midnight.setTime(calendar.getTimeInMillis());
      return (midnight.after(date));
   }

   private boolean isSameDay(Date date1, Date date2) {
      LocalDate localDate1 = date1.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
      LocalDate localDate2 = date2.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();
      return localDate1.isEqual(localDate2);
   }

   //-------------------------




   /*public Dialog onCreateDialog(Bundle savedInstanceState) {



      // Get the layout inflater.
      LayoutInflater inflater = requireActivity().getLayoutInflater();

      // Inflate and set the layout for the dialog.
      // Pass null as the parent view because it's going in the dialog layout.
      View calendarView = inflater.inflate(R.layout.fragment_calendar, null);

      // Alert dialog builder
      Dialog dialog = new AlertDialog.Builder(getActivity())
      .setTitle(R.string.dates_to_exclude_calendar_title) // TODO: Replace String
      .setView(calendarView)
      // Add action buttons
      .setPositiveButton(R.string.ok_calendar, new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int id) {
                  // Send the positive button event back to the host activity.
                  listener.onCalendarDialogPositiveClick(CalendarFragment.this);
        }
      })
      .setNegativeButton(R.string.cancel_calendar, new DialogInterface.OnClickListener() { // TODO: Replace String
        public void onClick(DialogInterface dialog, int id) {
           // Send the negative button event back to the host activity.
           listener.onCalendarDialogNegativeClick(CalendarFragment.this);
        }
      })
      .create();


      // Get the Date Picker
      datePicker = (CalendarPickerView) calendarView.findViewById(R.id.calendar_view);

      // Initialize Date picker to support a year from today with today selected
      fluentInitializer = datePicker.init(minDate, maxDate);

      // Get the Saved State
      if (savedInstanceState !=null) {
         mode = int2mode(savedInstanceState.getInt(KEY_MODE)) ;

         ArrayList<Integer> list = savedInstanceState.getIntegerArrayList(KEY_LIST_DATES);
         if (list !=null) {setSelectedDatesAsInt(list);}

         minDate = int2Date(savedInstanceState.getInt(KEY_MIN_DATE));
         maxDate = int2Date(savedInstanceState.getInt(KEY_MAX_DATE));
      }

      Log.d("THE_TIME_MACHINE", "fluentInitializer.inMode(mode): mode = " + mode);

      // Highlight all Tuesdays and Saturdays in range
      List<Integer> weekdays = new ArrayList<>();
      weekdays.add(2); // Tue
      weekdays.add(6); // Sat
      List<Date> highlightDates = getPreselectedDates(weekdays, minDate, maxDate);

      // Initialize Date picker
      //
      // To support a rage of dates
      datePicker.init(minDate, maxDate)

      // Set Date Picker to support selection  multiple/single/range of dates
      .inMode(mode)
       // Highlight the weekdays with alarms
      .withHighlightedDates(highlightDates)

       // Set selected dates - the days to exclude
      .withSelectedDates(selectedDates);

      // Display the dates to exclude
      displaySelectedDates();
      return dialog;
   }
*/

   @Override
   public void onResume() {
      super.onResume();
      Log.d("THE_TIME_MACHINE", "onResume() " );
      modifyDialogSize();
   }

   @Override
   public void onSaveInstanceState(Bundle savedInstanceState) {
      super.onSaveInstanceState(savedInstanceState);
      ArrayList<Integer> list = getSelectedDatesAsInt();
      savedInstanceState.putIntegerArrayList(KEY_LIST_DATES, list);
      savedInstanceState.putInt(KEY_MODE, mode2int(mode));
      savedInstanceState.putInt(KEY_MAX_DATE, date2Int(maxDate));
      savedInstanceState.putInt(KEY_MIN_DATE, date2Int(minDate));
      savedInstanceState.putInt(KEY_HIGHLIGHT, days2Highlight);
   }

   public List<Date> getSelectedDates(){
      return datePicker.getSelectedDates();
   }

   private void  displaySelectedDates(){
      Log.d("THE_TIME_MACHINE", "displaySelectedDates()");
      if (selectedDates==null || selectedDates.isEmpty())
         return;
      if (datePicker!=null){
         for (Date date: selectedDates){
            datePicker.selectDate(date, false);
            Log.d("THE_TIME_MACHINE", "displaySelectedDates() date: " + date);
         }
      }
   }

   public void setSelectedDates(List<Date> dates) {
      if (dates==null || dates.isEmpty() )
         return;

      if (selectedDates==null)
         selectedDates = new ArrayList<>();

      for (Date date: dates){
         selectedDates.add(new Date(date.getTime()));
      }
   }

   public void setMaxDate(Date maxDate) {
      this.maxDate =  new Date(maxDate.getTime());

      // Modify if needed
      if (maxDate == null || selectedDates == null  || selectedDates.isEmpty())
         return;

      // If a selected date is set to a date later that maxDate then maxDate is modified.
      for (Date date: selectedDates) {
         if (date.after(maxDate))
            maxDate.setTime(date.getTime());
      }
   }

   public void setMinDate(Date minDate) {
      this.minDate = new Date(minDate.getTime());

      // Modify if needed
      if (minDate == null || selectedDates == null  || selectedDates.isEmpty())
         return;

      // If a selected date is set to a date before that minDate then minDate is modified.
      for (Date date: selectedDates) {
         if (date.before(minDate))
            minDate.setTime(date.getTime());
      }
   }

   public void setHighlightedDays(int mask){
      days2Highlight = mask;
   }

   private void setSelectedDatesAsInt(ArrayList<Integer> dates){

      if (dates==null || dates.isEmpty())
         return;

      int y,m,d;

      Calendar cal = Calendar.getInstance();
      if (selectedDates != null)
         selectedDates.clear();

      for (int date: dates) {
         d = date % 100;
         m = (date / 100) % 100 - 1;
         y = (date / 10000);
         cal.clear();
         cal.set(y, m, d);

         Date dd = cal.getTime();
         selectedDates.add(dd);

      }
   }

   public ArrayList<Integer> getSelectedDatesAsInt(){
      ArrayList<Integer> out = new ArrayList<>();
      List<Date> listOfDates = getSelectedDates();
      Calendar cal = Calendar.getInstance();
      int y,m,d, entry;

      for (Date date: listOfDates) {
         cal.setTime(date);
         y = cal.get(Calendar.YEAR);
         m = cal.get(Calendar.MONTH)+1;
         d = cal.get(Calendar.DATE);
         entry = d+100*m+10000*y;
         out.add(entry);
         Log.d("THE_TIME_MACHINE", "getSelectedDatesAsInt():  Entry = " + entry);

      }

      return out;
   }


   // Convert a Date to integer format (e.g. 20240731)
   private int date2Int(Date date){
      if (date == null)
         return  0;

      int y,m,d;
      Calendar cal = Calendar.getInstance();
      cal.setTime(date);
      y = cal.get(Calendar.YEAR);
      m = cal.get(Calendar.MONTH)+1;
      d = cal.get(Calendar.DATE);

      return d+100*m+10000*y;
   }

   // Convert an int to Date format
   private Date int2Date(int date){
      int d = date % 100;
      int m = (date / 100) % 10 - 1;
      int y = (date / 10000);

      Calendar cal = Calendar.getInstance();
      cal.clear();
      cal.set(y, m, d);

      return cal.getTime();
   }

   /*
    * Returns a list of dates.
    * The List consists of all dates (In  a range) of a given weekday (e.g. all Thursdays )
    * Weekdays are defined as 0 max 6 (Sunday max Saturday)
    *
    * Parameters:
    *   weekdays: List of given weekdays
    *   min: Beginning of range
    *   max: end of range
    *
    * */
   private List<Date> getPreselectedDates(int weekdays, Date min, Date max){
      List<Date> preselected = new ArrayList<>();

      if (weekdays == 0)
         return preselected;

      if (min.after(max))
         return preselected;

      long minMillies = min.getTime();
      long maxMillies = max.getTime();
      int mask;


      // Loop on all days: min->max
      Calendar now = Calendar.getInstance();
      now.setTime(min);

     // Log.d("THE_TIME_MACHINE", "getPreselectedDates():  weekdays = " + weekdays  );


      while (now.getTimeInMillis() <= (maxMillies-1000*60*60*24)) {
         mask = 1;
         int today = now.get(Calendar.DAY_OF_WEEK)-1;
         mask = mask<<today;
         //Log.d("THE_TIME_MACHINE", "getPreselectedDates():  today = " + today + " mask = " + mask  );
         if ((weekdays & mask) != 0) {
            preselected.add(new Date(now.getTimeInMillis()));
         }
         now.setTimeInMillis(now.getTimeInMillis()+1000*60*60*24);
      }


      return preselected;
   }

   private List<Date> getPreselectedDates(){
      return getPreselectedDates(days2Highlight, minDate, maxDate);
   }


   private void modifyDialogSize(){
      DisplayMetrics displayMetrics = new DisplayMetrics();
      assert getActivity() != null;
      Window window = Objects.requireNonNull(getDialog()).getWindow();
      if (window == null)
         return;


      Point p = new Point();
      window.getWindowManager().getDefaultDisplay().getSize(p);
      int h = p.y;
      int w = p.x;
      Log.d("THE_TIME_MACHINE", "dialogMetricsFix():  " + p.toString());

      if (w/h > 1.5 || h<800)
         window.setLayout((int) (h * 0.7), WindowManager.LayoutParams.WRAP_CONTENT);
      else
         window.setLayout((int) (w * 0.9), WindowManager.LayoutParams.WRAP_CONTENT);
      window.setGravity(Gravity.CENTER);
   }

}