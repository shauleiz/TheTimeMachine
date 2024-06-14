package com.product.thetimemachine.UI;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.Dialog;
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
import com.wisnu.datetimerangepickerandroid.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;


public class CalendarFragment extends DialogFragment {
   // The system calls this to get the DialogFragment's layout, regardless of
   // whether it's being displayed as a dialog or an embedded fragment.

   CalendarPickerView.FluentInitializer fluentInitializer;
   CalendarPickerView.SelectionMode mode;

   CalendarFragment(CalendarPickerView.SelectionMode mode){
      super();
      this.mode = mode;
   }
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      // Inflate the layout to use as a dialog or embedded fragment.
      View v= inflater.inflate(R.layout.fragment_calendar, container, false);

      // Define the boundary dates
      Calendar nextYear = Calendar.getInstance();
      nextYear.add(Calendar.YEAR, 1); // Next year
      Date todayDate = new Date(); // Today

      // Get the Date Picker
      CalendarPickerView datePicker = (CalendarPickerView) v.findViewById(R.id.calendar_view);

      // Initialize Date picker to support a year from today with today selected
      fluentInitializer = datePicker.init(todayDate, nextYear.getTime()).withSelectedDate(todayDate);

      // Set Date Picker to support selection of multiple dates
      fluentInitializer.inMode(mode);

      return v;

   }

   public void setSelectionMode(CalendarPickerView.SelectionMode mode){
      this.mode = mode;
   }

   // The system calls this only when creating the layout in a dialog.
   @Override
   public Dialog onCreateDialog(Bundle savedInstanceState) {
      // The only reason you might override this method when using
      // onCreateView() is to modify the dialog characteristics. For example,
      // the dialog includes a title by default, but your custom layout might
      // not need it. Here, you can remove the dialog title, but you must
      // call the superclass to get the Dialog.
      Dialog dialog = super.onCreateDialog(savedInstanceState);
      //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
      return dialog;
   }

}