package com.product.thetimemachine.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.product.thetimemachine.R;
//import com.wisnu.datetimerangepickerandroid.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;


public class CalendarFragment extends DialogFragment {
   // The system calls this to get the DialogFragment's layout, regardless of
   // whether it's being displayed as a dialog or an embedded fragment.

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

   // The system calls this only when creating the layout in a dialog.
   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {

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

}