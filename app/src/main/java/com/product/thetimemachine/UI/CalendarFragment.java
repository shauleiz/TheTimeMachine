package com.product.thetimemachine.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.product.thetimemachine.R;
//import com.wisnu.datetimerangepickerandroid.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class CalendarFragment extends DialogFragment {
   // The system calls this to get the DialogFragment's layout, regardless of
   // whether it's being displayed as a dialog or an embedded fragment.

   final private String KEY_MODE = "keyMode";
   final private String KEY_LIST_DATES = "keyListOfDates";

   CalendarPickerView.FluentInitializer fluentInitializer;
   CalendarPickerView.SelectionMode mode = CalendarPickerView.SelectionMode.SINGLE;

   CalendarPickerView datePicker;

   public interface CalendarDialogListener { // Change to CalendarDialogListener
      public void onDialogPositiveClick(DialogFragment dialog);
      public void onDialogNegativeClick(DialogFragment dialog);
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
   CalendarDialogListener listener;

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

   public List<Date> getSelectedDates(){
      return datePicker.getSelectedDates();
   }

   // The system calls this only when creating the layout in a dialog.
   @NonNull
   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {

      // Get the Saved State
      if (savedInstanceState !=null) {
         mode = int2mode(savedInstanceState.getInt(KEY_MODE)) ;
         List<Integer> list = savedInstanceState.getIntegerArrayList(KEY_LIST_DATES);
         if (list !=null) {
            for (int a : list) {
               Log.d("THE_TIME_MACHINE", "Item: " + a);
            }
         }
      }


      // Get the layout inflater.
      LayoutInflater inflater = requireActivity().getLayoutInflater();

      // Inflate and set the layout for the dialog.
      // Pass null as the parent view because it's going in the dialog layout.
      View v = inflater.inflate(R.layout.fragment_calendar, null);

      // Alert dialog builder
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setTitle("Title");
      builder.setView(v)
            // Add action buttons
            .setPositiveButton("Pos", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                  // Send the positive button event back to the host activity.
                  listener.onDialogPositiveClick(CalendarFragment.this);
               }
            })

            .setNegativeButton("negative", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                  // Send the negative button event back to the host activity.
                  listener.onDialogNegativeClick(CalendarFragment.this);
               }
            });



      Dialog dialog = builder.create();


      // Define the boundary dates
      Calendar nextYear = Calendar.getInstance();
      nextYear.add(Calendar.YEAR, 1); // Next year
      Date todayDate = new Date(); // Today
      Calendar tomorrow = Calendar.getInstance();
      tomorrow.add(Calendar.DAY_OF_MONTH,1);
      /**/
      // Get the Date Picker
      datePicker = (CalendarPickerView) v.findViewById(R.id.calendar_view);


      // Initialize Date picker to support a year from today with today selected
      fluentInitializer = datePicker.init(todayDate, nextYear.getTime());

      // Select a date (tomorrow)
      fluentInitializer.withSelectedDate(tomorrow.getTime());

      // Set Date Picker to support selection  multiple/single/range of dates
      fluentInitializer.inMode(mode);

      return dialog;
   }

   @Override
   public void onSaveInstanceState(Bundle savedInstanceState) {
      super.onSaveInstanceState(savedInstanceState);
      ArrayList<Integer> list = new ArrayList<>();
      list.add(20240516);
      list.add(20230330);

      savedInstanceState.putIntegerArrayList(KEY_LIST_DATES, list);
      savedInstanceState.putInt(KEY_MODE, mode2int(mode));
   }

}