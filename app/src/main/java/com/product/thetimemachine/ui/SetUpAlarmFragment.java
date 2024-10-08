package com.product.thetimemachine.ui;

import static com.product.thetimemachine.Data.AlarmItem.FRIDAY;
import static com.product.thetimemachine.Data.AlarmItem.MONDAY;
import static com.product.thetimemachine.Data.AlarmItem.SATURDAY;
import static com.product.thetimemachine.Data.AlarmItem.SUNDAY;
import static com.product.thetimemachine.Data.AlarmItem.THURSDAY;
import static com.product.thetimemachine.Data.AlarmItem.TUESDAY;
import static com.product.thetimemachine.Data.AlarmItem.WEDNESDAY;
import static com.product.thetimemachine.ui.SettingsFragment.pref_first_day_of_week;
import static com.product.thetimemachine.ui.SettingsFragment.pref_is24HourClock;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.product.thetimemachine.AlarmViewModel;
import com.product.thetimemachine.Data.AlarmItem;
import com.product.thetimemachine.R;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
//import com.wisnu.datetimerangepickerandroid.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SetUpAlarmFragment extends Fragment implements CalendarFragment.CalendarDialogListener {

    private MyTimePicker timePicker;
   //private SwitchCompat repeating;
    private MaterialButtonToggleGroup repeating;

   ImageButton callDatePickerButtonX, callDatePickerButton;

   private ToggleButton suToggleButton, moToggleButton, tuToggleButton,
          weToggleButton, thToggleButton,frToggleButton, saToggleButton;
    //private EditText label;
    private TextInputEditText label;
    private TextView targetAlarmText;
    private AlarmViewModel.SetUpAlarmValues setUpAlarmValues;
    private AlarmViewModel alarmViewModel;
    private MainActivity parent;
    private Bundle initParams;
    private DatePickerDialog datePickerDialog;
    private List<Date> selectedDates;
    private boolean keepDatePickerDialog;

   final private String KEY_KEEPDATEPICKER_DAY = "key_keep_date_picker_day";
   final private String KEY_DATEPICKER_DAY = "key_date_picker_day";
   final private String KEY_DATEPICKER_MONTH = "key_date_picker_month";
   final private String KEY_DATEPICKER_YEAR = "key_date_picker_year";




    // Default constructor
    public SetUpAlarmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStop() {

        // Get the selected values
        String t;
        Editable txt = label.getText();
        if (txt != null) {
            t = txt.toString();
        }
        else
            t= "";
        int hourOfDay = timePicker.getHour();
        int minute = timePicker.getMinute();

        // Save the values on the ViewModel
        setUpAlarmValues.setLabel(t);
        setUpAlarmValues.setHour(hourOfDay);
        setUpAlarmValues.setMinute(minute);

        setUpAlarmValues.setWeekDays(getDaysValues());
       setUpAlarmValues.setOneOff(repeating.getCheckedButtonId() == R.id.oneoff_btn);



        // Remove the Up arrow
        ActionBar actionBar = parent.getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(false);

        super.onStop();
    }

   @Override
   public void onPause() {
      super.onPause();
      if(datePickerDialog!=null) {
         keepDatePickerDialog = true;
         datePickerDialog.dismiss();
      }
   }

   @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParams = getArguments();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_set_up_alarm, container, false);

        // Insert the preference fragment
        Bundle b = new Bundle();
        b.putInt("Type", 1);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
              .replace(R.id.fragment_pref, ItemSettingsFragment.class, b)
              //.setReorderingAllowed(true)
              .addToBackStack("ItemSettingsFragment")
              .commit();

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int weekDays;
        Boolean oneOff;

        // Get the parent Activity
        parent = (MainActivity) getActivity();
        if (parent == null) return;

        // Toolbar: Title + Menu + leave only "settings" icon
        Toolbar AppToolbar = getActivity().findViewById(R.id.app_toolbar);
        AppToolbar.setTitle(R.string.alarmsetup_title);
        ((AppCompatActivity)getActivity()).setSupportActionBar(AppToolbar);
        parent.setDeleteAction(false);
        parent.setSettingsAction(true);
        parent.setEditAction(false);
        parent.setDuplicateAction(false);
        parent.setCheckmarkAction(true);
        parent.invalidateOptionsMenu();

        ActionBar actionBar = Objects.requireNonNull(parent).getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_back_fill0_wght400_grad0_opsz24);
        actionBar.setHomeActionContentDescription(R.string.description_up_arrow_back);
        actionBar.setDisplayHomeAsUpEnabled(true);


        // Create/acquire the ViewModel object of class AlarmViewModel
        alarmViewModel = new ViewModelProvider(requireActivity()).get(AlarmViewModel.class);
        setUpAlarmValues = alarmViewModel.setUpAlarmValues;

        // DEBUG
        if (alarmViewModel != null)
            Log.d("THE_TIME_MACHINE", "SetUpAlarmFragment.onViewCreated():  alarmViewModel is OK ");
        else
            Log.d("THE_TIME_MACHINE", "SetUpAlarmFragment.onViewCreated(): alarmViewModel is NULL ");



        // Get the handle to the Add Alarm button
        // Then define what to do when clicked
        Button ok_Button = view.findViewById(R.id.OK_Btn);
        ok_Button.setOnClickListener(v -> OkClicked());

        // Get the handle to the Delete Alarm button
        // Then define what to do when clicked
        Button delete_Button = view.findViewById(R.id.Delete_Btn);

        boolean isNewAlarm = initParams.getBoolean("INIT_NEWALARM", false);


        // No buttons needed
        delete_Button.setVisibility(View.GONE);
        ok_Button.setVisibility(View.GONE);
      /*  if (!isNewAlarm)
            delete_Button.setOnClickListener(v -> DeleteClicked());
        else
            delete_Button.setVisibility(View.GONE);*/


        // Get Time Picker and modify it
        timePicker = view.findViewById(R.id.time_picker);
        InitTimePicker(timePicker, isNewAlarm);

        // The Label edit field
        label = view.findViewById((R.id.alarm_label));
        label.setText(setUpAlarmValues.getLabel().getValue());

        // The target time/date text view
        targetAlarmText = view.findViewById((R.id.target_time_date));

        // Get the Repeating button and set the weekdays button visibility
        repeating = view.findViewById(R.id.single_weekly_btngrp);
        Integer WeekDays = setUpAlarmValues.getWeekDays().getValue();
        if (WeekDays!=null)
            weekDays = WeekDays;
        else
            weekDays = 0;
        setDaysValues(weekDays,view);
        oneOff = setUpAlarmValues.isOneOff().getValue();
        if (oneOff == null)
            oneOff = false;
        if (oneOff)
            repeating.check(R.id.oneoff_btn);
        else
            repeating.check(R.id.repeate_btn);
        setDaysVisible(!oneOff, view);
        setDisplayAreaVisible(oneOff);
        setCalendarButton(oneOff, view);

        selectedDates = ExceptionDates2Date(setUpAlarmValues.getExceptionDates().getValue());

        // Restore dialog date picker dialog box
       if (savedInstanceState != null && savedInstanceState.getBoolean(KEY_KEEPDATEPICKER_DAY, false))
          restoreDatePicker(savedInstanceState);

       // Date picker button (weekly) clickable only if one day at least is selected
       setDatePickerButtonXClickable();

        // Listener to the Repeating button (set the weekdays button visibility)
        repeating.addOnButtonCheckedListener((materialButtonToggleGroup, id, checked) -> {
            if (id == R.id.oneoff_btn) {
                setDaysVisible(!checked, view);
                setDisplayAreaVisible(checked);
               setCalendarButton(checked, view);
            }
            else {
                setDaysVisible(checked, view);
                setDisplayAreaVisible(!checked);
               setCalendarButton(!checked, view);
            }
        });

       suToggleButton.setOnCheckedChangeListener((compoundButton, b) -> setDatePickerButtonXClickable());
       moToggleButton.setOnCheckedChangeListener((compoundButton, b) -> setDatePickerButtonXClickable());
       tuToggleButton.setOnCheckedChangeListener((compoundButton, b) -> setDatePickerButtonXClickable());
       weToggleButton.setOnCheckedChangeListener((compoundButton, b) -> setDatePickerButtonXClickable());
       thToggleButton.setOnCheckedChangeListener((compoundButton, b) -> setDatePickerButtonXClickable());
       frToggleButton.setOnCheckedChangeListener((compoundButton, b) -> setDatePickerButtonXClickable());
       saToggleButton.setOnCheckedChangeListener((compoundButton, b) -> setDatePickerButtonXClickable());

        // Observers to change in the setup values:
        setUpAlarmValues.getHour().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer h) {
                if (h != null) {
                    timePicker.setHour(h);
                }
            }
        });

        setUpAlarmValues.getMinute().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer m) {
                if (m != null) {
                    timePicker.setMinute(m);
                }
            }
        });

        setUpAlarmValues.getWeekDays().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer m) {
                if (m != null) {
                    setDaysValues(m, view);
                }
            }
        });

        setUpAlarmValues.isOneOff().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean t) {
                if (t != null) {
                    if (t)
                        repeating.check(R.id.oneoff_btn);
                    else
                        repeating.check(R.id.repeate_btn);
                }
            }
        });

        setUpAlarmValues.getLabel().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String t) {
                if (t != null) {
                    label.setText(t);
                }
            }
        });

    }

   @Override
   public void onSaveInstanceState(@NonNull Bundle outState) {
      super.onSaveInstanceState(outState);
      if (datePickerDialog != null) {
         outState.putBoolean(KEY_KEEPDATEPICKER_DAY,keepDatePickerDialog);
         outState.putInt(KEY_DATEPICKER_DAY,datePickerDialog.getDatePicker().getDayOfMonth());
         outState.putInt(KEY_DATEPICKER_MONTH,datePickerDialog.getDatePicker().getMonth());
         outState.putInt(KEY_DATEPICKER_YEAR,datePickerDialog.getDatePicker().getYear());
      }
   }


   // Set the Date Picker (in weekly alarm mode) to clickable (on/off)
   // Off: If none of the weekdays selected
   // On:  If at least one of the weekdays selected
   private void setDatePickerButtonXClickable(){
      float alpha;
      boolean b =
            suToggleButton.isChecked() || moToggleButton.isChecked() ||
                  tuToggleButton.isChecked() || weToggleButton.isChecked() ||
                  thToggleButton.isChecked() || frToggleButton.isChecked() ||
                  saToggleButton.isChecked();
      if (b)
         alpha=1;
      else
         alpha = 0.5f;
      callDatePickerButtonX.setAlpha(alpha);
      callDatePickerButtonX.setEnabled(b);
   }

   // Weekly alarm mode:
   // Set the row of weekday buttons visible/gone
    private void setDaysVisible(boolean visible, View view){

        String firstDay = pref_first_day_of_week();

        if (visible)
            if (firstDay.equals("Su")) { // TODO: Replace
                view.findViewById(R.id.day_buttons_layout).setVisibility(View.VISIBLE);
                view.findViewById(R.id.day_buttons_layout_mo).setVisibility(View.GONE);
            }
            else {
                view.findViewById(R.id.day_buttons_layout).setVisibility(View.GONE);
                view.findViewById(R.id.day_buttons_layout_mo).setVisibility(View.VISIBLE);
            }
        else {
            view.findViewById(R.id.day_buttons_layout).setVisibility(View.GONE);
            view.findViewById(R.id.day_buttons_layout_mo).setVisibility(View.GONE);
        }
    }

    private void setCalendarButton(boolean oneOff, View view){
       // This button calls the Date Picker
       callDatePickerButton = view.findViewById(R.id.ShowDatePicker_Btn);
       callDatePickerButtonX = view.findViewById(R.id.ShowDatePicker_Btn_X);
       if (oneOff) {
          callDatePickerButton.setVisibility(View.VISIBLE);
          callDatePickerButtonX.setVisibility(View.INVISIBLE);
          callDatePickerButton.setOnClickListener(this::ShowDatePickerOnClick);
       }
       else {
          callDatePickerButton.setVisibility(View.INVISIBLE);
          callDatePickerButtonX.setVisibility(View.VISIBLE);
          callDatePickerButtonX.setOnClickListener(this::ShowExcludeDatePickerOnClick);
       }
    }

    public boolean isToday(long alarmInMillis){

        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now);

        // Get time of today's midnight (minus a second)
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        long lastSecondOfDay = calendar.getTimeInMillis();


        return  (lastSecondOfDay >= alarmInMillis && now<=alarmInMillis);

    }

    public boolean isTomorrow(long alarmInMillis){
        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(now+24*60*60*1000);

        // Get time of tomorrow's midnight (minus a second)
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        long lastSecondOfDay = calendar.getTimeInMillis();

        // Get time of tomorrow's first second
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long firstSecondOfDay = calendar.getTimeInMillis();

        return (alarmInMillis>=firstSecondOfDay && alarmInMillis<=lastSecondOfDay);

    }
public boolean isInThePast(long alarmInMillis){
    long now = System.currentTimeMillis();
    return (alarmInMillis<now);
}

    // Set Display Area visible/gone
    private void setDisplayAreaVisible(boolean visible){

        //TextView textView = view.findViewById(R.id.target_time_date);
        if (visible) {
            targetAlarmText.setText(displayTargetAlarm());
            targetAlarmText.setVisibility(View.VISIBLE);
        }
        else
            targetAlarmText.setVisibility(View.GONE);
    }

    @NonNull
    private String displayTargetAlarm(){
        String out = "";
        long nowInMillis;
       int dd, mm, yy;

        // Get time/date values
        int m = timePicker.getMinute();
        int h = timePicker.getHour();
        if ( setUpAlarmValues.getDayOfMonth().getValue()!=null)
            dd = setUpAlarmValues.getDayOfMonth().getValue();
        else
            dd = 0;
        if (setUpAlarmValues.getMonth().getValue()!=null)
            mm = setUpAlarmValues.getMonth().getValue();
        else
            mm = 0;
        if (setUpAlarmValues.getYear().getValue()!=null)
            yy = setUpAlarmValues.getYear().getValue();
        else
            yy = 0;

        // Create a calendar object and modify it
        nowInMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(nowInMillis);

        if (dd>0 && mm>0 && yy>0)
            calendar.set(yy, mm, dd,h,m,0);
        else{
            calendar.set(Calendar.HOUR_OF_DAY, h);
            calendar.set(Calendar.MINUTE, m);
            if (isInThePast(calendar.getTimeInMillis()))
                calendar.setTimeInMillis(calendar.getTimeInMillis()+24*60*60*1000);
            //calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),h,m,0);
        }


        // Create an output string
        SimpleDateFormat format;
        format = new SimpleDateFormat(getString(R.string.time_format_display), Locale.US);
        out = format.format(calendar.getTimeInMillis());

        // Determine Today/Tomorrow
        if (isToday(calendar.getTimeInMillis()))
            out+=getString(R.string.today_in_brackets);
        else if (isTomorrow(calendar.getTimeInMillis())) {
            out+=getString(R.string.tomorrow_in_brackets);
        }

        //Log.d("THE_TIME_MACHINE", "displayTargetAlarm(): " + out);
        return out;
    }


    private void setDatePickerButtonXActive(int weekDays){
       callDatePickerButtonX.setActivated(weekDays != 0);
    }

    // Set/reset the individual day-buttons
    private void setDaysValues(int weekDays, View view){
        boolean isSunday = (weekDays&SUNDAY) >0;
        boolean isMonday = (weekDays&MONDAY) >0;
        boolean isTuesday = (weekDays&TUESDAY) >0;
        boolean isWednesday = (weekDays&WEDNESDAY) >0;
        boolean isThursday = (weekDays&THURSDAY) >0;
        boolean isFriday = (weekDays&FRIDAY) >0;
        boolean isSaturday = (weekDays&SATURDAY) >0;

        if (pref_first_day_of_week().equals("Su")) {
            suToggleButton = view.findViewById(R.id.SundayButton);
            suToggleButton.setChecked(isSunday);
            moToggleButton = view.findViewById(R.id.MondayButton);
            moToggleButton.setChecked(isMonday);
            tuToggleButton = view.findViewById(R.id.TuesdayButton);
            tuToggleButton.setChecked(isTuesday);
            weToggleButton = view.findViewById(R.id.WednesdayButton);
            weToggleButton.setChecked(isWednesday);
            thToggleButton = view.findViewById(R.id.ThursdayButton);
            thToggleButton.setChecked(isThursday);
            frToggleButton = view.findViewById(R.id.FridayButton);
            frToggleButton.setChecked(isFriday);
            saToggleButton = view.findViewById(R.id.SaturdayButton);
            saToggleButton.setChecked(isSaturday);
        }
        else {
            suToggleButton = view.findViewById(R.id.SundayButton_2);
            suToggleButton.setChecked(isSunday);
            moToggleButton = view.findViewById(R.id.MondayButton_2);
            moToggleButton.setChecked(isMonday);
            tuToggleButton = view.findViewById(R.id.TuesdayButton_2);
            tuToggleButton.setChecked(isTuesday);
            weToggleButton = view.findViewById(R.id.WednesdayButton_2);
            weToggleButton.setChecked(isWednesday);
            thToggleButton = view.findViewById(R.id.ThursdayButton_2);
            thToggleButton.setChecked(isThursday);
            frToggleButton = view.findViewById(R.id.FridayButton_2);
            frToggleButton.setChecked(isFriday);
            saToggleButton = view.findViewById(R.id.SaturdayButton_2);
            saToggleButton.setChecked(isSaturday);
        }


    }


   private int getDaysValues(){

        int mask = 0;

        if (suToggleButton.isChecked())  mask|=SUNDAY;
        if (moToggleButton.isChecked())  mask|=MONDAY;
        if (tuToggleButton.isChecked())  mask|=TUESDAY;
        if (weToggleButton.isChecked())  mask|=WEDNESDAY;
        if (thToggleButton.isChecked())  mask|=THURSDAY;
        if (frToggleButton.isChecked())  mask|=FRIDAY;
        if (saToggleButton.isChecked())  mask|=SATURDAY;

       return mask;
    }


   // Converts selected Dates in JSON string format to List<Date>
   // Each entry in format "YYYYMMYY" (e.g. "20240630")
    private List<Date> ExceptionDates2Date(String dates){
       List<Date> dateList = new ArrayList<>();

       if (dates==null || dates.isEmpty())
          return dateList;


       // From JSON to Array of strings
       Type listOfMyClassObject = new TypeToken<ArrayList<String>>() {}.getType();
       Gson gson = new Gson();
       List<String > dateListStr = gson.fromJson(dates, listOfMyClassObject);

       // Test list of strings
       if (dateListStr==null || dateListStr.isEmpty())
          return dateList;

       // Convert each string to Date
       int d,m,y, yyyymmdd;
       Calendar cal = Calendar.getInstance();
       for (String dateStr: dateListStr) {
          yyyymmdd = Integer.valueOf(dateStr);
          d = yyyymmdd%100;
          m = (yyyymmdd/100)%100 -1;
          y = (yyyymmdd/10000);
          cal.clear();
          cal.set(y,m,d);
          dateList.add(cal.getTime());
       }

       Log.d("THE_TIME_MACHINE", "ExceptionDates2Date():  dateList = " + dateList);
       return dateList;
    }


    // Converts selected Dates to the corresponding JSON string
    // Each date is converted into format "YYYYMMYY" (e.g. "20240630")
    // before inserted to JSON string
    private String ExceptionDates2Str(List<Date> dates){
       // Check input
       if (dates == null || dates.isEmpty())
          return "";

       // For each Date, convert it to string and add it to a list of strings
       List<String> datesStr = new ArrayList<>();
       int y, m, d, entry;
       String dateStr;
       Calendar cal = Calendar.getInstance();
       for (Date date: dates) {
          cal.setTime(date);
          y = cal.get(Calendar.YEAR);
          m = cal.get(Calendar.MONTH)+1;
          d = cal.get(Calendar.DATE);
          entry = d+100*m+10000*y;
          dateStr = Integer.toString(entry);
          datesStr.add(dateStr);
       }

       // Convert list of strings to a single JSON string
       Gson gson = new Gson();
       return gson.toJson(datesStr);
    }

    private Date computeMaxDate() {
       // Define the boundary dates
       Calendar c = Calendar.getInstance();
       c.add(Calendar.YEAR, 2); // 2 years
       return c.getTime();
    }

   private Date computeMinDate() {
      // Define the boundary dates
      Calendar c = Calendar.getInstance();
      c.add(Calendar.MONTH, -2); // 2 years
      return c.getTime();
   }

    public void ShowExcludeDatePickerOnClick(View view) {

       // Get the dates that are an exception
       selectedDates = ExceptionDates2Date(setUpAlarmValues.getExceptionDates().getValue());


       // Get the Calendar Dialog Fragment
       CalendarFragment calendarFragment = new CalendarFragment();

       // Set Calendar parameters
       calendarFragment.setSelectionMode(CalendarPickerView.SelectionMode.MULTIPLE);
       calendarFragment.setSelectedDates(selectedDates);
       calendarFragment.setHighlightedDays(getDaysValues());
       calendarFragment.setMaxDate(computeMaxDate());
       calendarFragment.setMinDate(computeMinDate());

       // Show dialog
       calendarFragment.show(getChildFragmentManager(), "dialog");
    }

   @Override
   public void onCalendarDialogPositiveClick(DialogFragment dialog) {
      // User taps the dialog's positive button.
      selectedDates = ((CalendarFragment)dialog).getSelectedDates();
      setUpAlarmValues.setExceptionDates(ExceptionDates2Str(selectedDates));

/*
      // DEBUG
      long time0=0;
      if (!selectedDates.isEmpty()) {
         for (Date d : selectedDates) {
            Log.d("THE_TIME_MACHINE", "onCalendarDialogPositiveClick() - Time = " + d);
         }
      }
      else {
         Log.d("THE_TIME_MACHINE", "onCalendarDialogPositiveClick() Empty");
      }

      Log.d("THE_TIME_MACHINE", "onCalendarDialogPositiveClick() - JSON = " + ExceptionDates2Str(selectedDates));
*/
    }

   @Override
   public void onCalendarDialogNegativeClick(DialogFragment dialog) {
      Log.d("THE_TIME_MACHINE", "onCalendarDialogNegativeClick()");
   }


   // Called when user clicks on the calendar button to call the Date Picker
    public void ShowDatePickerOnClick(View view){
        Calendar calendar;
        int d, m, y;

        Log.d("THE_TIME_MACHINE", "ShowDatePickerOnClick");

        // Get the date stored in the VM.
        d = setUpAlarmValues.getDayOfMonth().getValue()==null ? 0: setUpAlarmValues.getDayOfMonth().getValue();
        m = setUpAlarmValues.getMonth().getValue()==null ? 0: setUpAlarmValues.getMonth().getValue();
        y = setUpAlarmValues.getYear().getValue()==null ? 0: setUpAlarmValues.getYear().getValue();

        // If date not stored, set today's date
        if (d == 0 || m == 0 || y == 0) {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            d = calendar.get(Calendar.DAY_OF_MONTH);
            m = calendar.get(Calendar.MONTH);
            y = calendar.get(Calendar.YEAR);
        }

       invokeDatePickerDialog(d, m, y);
    }

    private void restoreDatePicker(Bundle b){

       int d = b.getInt(KEY_DATEPICKER_DAY,0);
       int m = b.getInt(KEY_DATEPICKER_MONTH, 0);
       int y = b.getInt(KEY_DATEPICKER_YEAR,0);

       if (d==0 || m==0 || y==0){
          Log.e("THE_TIME_MACHINE", "restoreDatePicker() bad values: d/y/m: " +d+"/"+m+"/"+y);
          return;
       }
       invokeDatePickerDialog(d, m, y);
    }

   private void invokeDatePickerDialog(int d, int m, int y) {
      datePickerDialog = new DatePickerDialog(requireContext());

      // Set as cancelable and set a listener that will be called on OK
      datePickerDialog.setCancelable(true);
      datePickerDialog.setOnDateSetListener(this::DateWidgetListener);
      datePickerDialog.setOnDismissListener(this::DateWidgetDismissListener);

      // Set the date and show the widget
      datePickerDialog.updateDate(y, m, d);
      datePickerDialog.show();
   }

   private void DateWidgetDismissListener(DialogInterface dialogInterface) {
      Log.d("THE_TIME_MACHINE", "DateWidgetDismissListener");
      datePickerDialog= null;
   }



    // Called when user exits the Date Picker with 'OK'
    // Gets the selected date (
    public void DateWidgetListener(View v, int year, int month, int day){
        Calendar calendar;
        long currentTimeMillis, targetTimeMillis;

        Log.d("THE_TIME_MACHINE", "ShowDatePickerOnClick Selected:" + day+"/"+month+1+"/"+year);

        // Analyze the date/time:
        //  Is the time in the past? => Error message + Date not changed
        //  Is the time soon (within 24h) => Set as Today/Tomorrow, leave d/m/y = 0. Clear F Flag
        //  Is the time in the Future (Later than the next 24h) => set d/m/y Set F Flag
        int h = timePicker.getHour();
        int m = timePicker.getMinute();
        calendar = Calendar.getInstance();
        currentTimeMillis = System.currentTimeMillis();
        // Debug:
        //SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a", Locale.US);
        //Log.d("THE_TIME_MACHINE", "Current time: " + format.format(currentTimeMillis) );


        calendar.set(year,  month,  day,h,m);
        targetTimeMillis = calendar.getTimeInMillis();
        //Log.d("THE_TIME_MACHINE", "Target time: " + format.format(targetTimeMillis) );

        if (targetTimeMillis<=currentTimeMillis) // Past or now
        {
            new MaterialAlertDialogBuilder(requireContext())
                  .setMessage(R.string.alarm_in_the_past)
                  .setTitle(R.string.title_error)
                  .setNegativeButton(R.string.cancel_as_negative, null)
                  .show();
        }
        else if (targetTimeMillis-currentTimeMillis <= 24*60*60*1000) // Soon
        {
            setUpAlarmValues.setFutureDate(false);
            setUpAlarmValues.setOneOff(true);
            setUpAlarmValues.setDayOfMonth(0);
            setUpAlarmValues.setMonth(0);
            setUpAlarmValues.setYear(0);
            Log.d("THE_TIME_MACHINE", "Target time: " );
        }
        else  {
            setUpAlarmValues.setFutureDate(true);
            setUpAlarmValues.setOneOff(true);
            setUpAlarmValues.setDayOfMonth(day);
            setUpAlarmValues.setMonth(month);
            setUpAlarmValues.setYear(year);
        }

        targetAlarmText.setText(displayTargetAlarm());
    }

    // OK Button clicked
    //@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void OkClicked(){
        final MainActivity parent = (MainActivity) getActivity();
        String t;

        /// This part should be replaced by:
        /// 1. Put new/modified alarm entry in alarm list
        /// 2. Reset setUpAlarmValues
        // Get the time from the time picker and pass it to the ViewModel
        int h = timePicker.getHour();
        int m = timePicker.getMinute();
       if (label.getText() != null)
         t = label.getText().toString();
       else
          t= "";
        boolean active = true; // A new/modified alarm is always active
        long c = initParams.getLong("INIT_CREATE_TIME");
        boolean newAlarm = initParams.getBoolean("INIT_NEWALARM", true);
        AlarmItem item;

        /*// Prevent creation of a duplicate alarm item
        if (alarmViewModel.isDuplicate(h,m,c)){
            String txt = "Duplicate: " + h+":"+m;
            Log.d("THE_TIME_MACHINE", txt);

            new MaterialAlertDialogBuilder(requireContext())
                  .setMessage(R.string.duplicate_alarm_message)
                  .setTitle(R.string.duplicate_alarm_title)
                  .show();
            return;
        }*/

        // If modified alarm then use its old Create Time (id)
        // If new alarm then create it using a new Create Time (id)
        if (!newAlarm)
            item = new AlarmItem(h, m,t, active, c);
        else
            item = new AlarmItem(h, m, t, active);


        // Weekdays
        int weekdays = getDaysValues();
        item.setWeekDays(weekdays);
        int btnChecked = repeating.getCheckedButtonId();
        item.setOneOff(btnChecked == R.id.oneoff_btn || (weekdays==0));

        // Future Date is true only if Repeating is off and the date picker was previously used
        if (!item.isOneOff())
            item.setFutureDate(false);
        else
            item.setFutureDate(Boolean.TRUE.equals(setUpAlarmValues.isFutureDate().getValue()));

        // Set Exception dates if this is a weekly (repetative) alarm item
       item.setExceptionDatesStr(ExceptionDates2Str(selectedDates));


        // Future Date
        if (item.isFutureDate() /* && (weekdays==0)*/){
           int yy, mm, dd;
           if (setUpAlarmValues.getYear().getValue() != null)
              yy = setUpAlarmValues.getYear().getValue();
           else
              yy=0;
           if (setUpAlarmValues.getMonth().getValue() != null)
              mm = setUpAlarmValues.getMonth().getValue();
           else
              mm=0;
           if (setUpAlarmValues.getDayOfMonth().getValue()!=null)
              dd = setUpAlarmValues.getDayOfMonth().getValue();
           else
              dd= 0;

            if (yy>0 && mm>0 && dd>0) {
                item.setYear(yy);
                item.setMonth(mm);
                item.setDayOfMonth(dd);
            }
        }

        // Preferences
        item.setRingDuration(setUpAlarmValues.getRingDuration().getValue());
        item.setRingRepeat(setUpAlarmValues.getRingRepeat().getValue());
        item.setSnoozeDuration(setUpAlarmValues.getSnoozeDuration().getValue());
        item.setVibrationPattern(setUpAlarmValues.getVibrationPattern().getValue());
        item.setAlarmSound(setUpAlarmValues.getAlarmSound().getValue());
        item.setGradualVolume(setUpAlarmValues.getGradualVolume().getValue());


        // Add or Update the entry on the list
       if (newAlarm /*|| position<0*/)
            alarmViewModel.AddAlarm(item);
        else
            alarmViewModel.UpdateAlarm(item);

        // Schedule this new/modified alarm
        item.Exec();


        // Display the Alarm List Fragment
        if (parent != null){
            FragmentManager lm = parent.getSupportFragmentManager();
                    lm. beginTransaction().
                    replace(R.id.fragment_container_view, AlarmListFragment.class, null).
                    //addToBackStack("tag3").
                    commit();
        }

    }




    // Delete Button Clicked
    private void DeleteClicked() {

        // Create alarm item to be removed
        int h = timePicker.getHour();
        int m = timePicker.getMinute();
        String t;
        if (label.getText()!=null )
           t = label.getText().toString();
        else t= "";
        boolean active = true; // A new/modified alarm is always active
        long c = initParams.getLong("INIT_CREATE_TIME");
        AlarmItem item = new AlarmItem(h, m,t, active, c);


        // Get position of alarm to delete and check that it exists
        if (alarmViewModel == null)
            return;

        // Delete Alarm from list in ViewModel
        alarmViewModel.DeleteAlarm(item);

        // Display the Alarm List Fragment
        if (parent != null)
            parent.getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.fragment_container_view, AlarmListFragment.class, null).
                    //addToBackStack("tag3").
                            commit();

    }

    // Initialize the time picker to default setup
    private void InitTimePicker(TimePicker timePicker, boolean newAlarm) {

       timePicker.setIs24HourView(pref_is24HourClock());

        if (newAlarm)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int h =calendar.get(Calendar.HOUR_OF_DAY);
            int m = calendar.get(Calendar.MINUTE);
            timePicker.setHour(h);
            timePicker.setMinute(m);
        }
        else {
            if (setUpAlarmValues == null) return;
            MutableLiveData<Integer> h = setUpAlarmValues.getHour();
            MutableLiveData<Integer> m = setUpAlarmValues.getMinute();
            if (h == null || m == null || h.getValue() == null || m.getValue() == null) return;

            timePicker.setHour(h.getValue());//
            timePicker.setMinute(m.getValue());//
        }

        timePicker.animate();

        // Set a listener to events in the Time Picker
        timePicker.setOnTimeChangedListener(this::onTimeChangedListener);
    }

    // Called when Time Picker values change
    private void onTimeChangedListener(TimePicker timePicker, int hourOfDay, int minute) {
        targetAlarmText.setText(displayTargetAlarm());
    }

}