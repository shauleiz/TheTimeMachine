package com.product.thetimemachine.UI;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.Touch;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.product.thetimemachine.R;
import com.product.thetimemachine.databinding.FragmentCalendarBinding;
//import com.wisnu.datetimerangepickerandroid.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class CalendarFragment extends DialogFragment {
   // The system calls this to get the DialogFragment's layout, regardless of
   // whether it's being displayed as a dialog or an embedded fragment.

   CalendarPickerView.FluentInitializer fluentInitializer;
   CalendarPickerView.SelectionMode mode = CalendarPickerView.SelectionMode.SINGLE;

   CalendarPickerView datePicker;



   public void setSelectionMode(CalendarPickerView.SelectionMode mode){
      this.mode = mode;
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
            .setPositiveButton("positive", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int id) {
                  // Sign in the user.
               }
            })
            .setNegativeButton("negative", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                  //LoginDialogFragment.this.getDialog().cancel();
               }
            });



      Dialog dialog = builder.create();


      // Define the boundary dates
      Calendar nextYear = Calendar.getInstance();
      nextYear.add(Calendar.YEAR, 1); // Next year
      Date todayDate = new Date(); // Today
      /**/
      // Get the Date Picker
      datePicker = (CalendarPickerView) v.findViewById(R.id.calendar_view);


      // Initialize Date picker to support a year from today with today selected
      fluentInitializer = datePicker.init(todayDate, nextYear.getTime());

      // Select a date (today)
      fluentInitializer.withSelectedDate(todayDate);

      // Set Date Picker to support selection  multiple/single/range of dates
      fluentInitializer.inMode(mode);

      return dialog;
   }

}