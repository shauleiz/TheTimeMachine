package com.example.thetimemachine.UI;

import static android.icu.util.TimeZone.getTimeZone;
import static com.example.thetimemachine.Data.AlarmItem.FRIDAY;
import static com.example.thetimemachine.Data.AlarmItem.MONDAY;
import static com.example.thetimemachine.Data.AlarmItem.SATURDAY;
import static com.example.thetimemachine.Data.AlarmItem.SUNDAY;
import static com.example.thetimemachine.Data.AlarmItem.THURSDAY;
import static com.example.thetimemachine.Data.AlarmItem.TUESDAY;
import static com.example.thetimemachine.Data.AlarmItem.WEDNESDAY;
import static com.example.thetimemachine.UI.SettingsFragment.pref_first_day_of_week;
import static com.example.thetimemachine.UI.SettingsFragment.pref_is24HourClock;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.icu.util.ULocale;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.thetimemachine.AlarmViewModel;
import com.example.thetimemachine.Data.AlarmItem;
import com.example.thetimemachine.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class SetUpAlarmFragment extends Fragment {

    private TimePicker timePicker;
    private DatePickerDialog datePickerDialog;
    private SwitchCompat repeating;
    private ImageButton callDatePickerButton;

    private ToggleButton suToggleButton, moToggleButton, tuToggleButton,
          weToggleButton, thToggleButton,frToggleButton, saToggleButton;
    private EditText label;
    private TextView targetAlarmText;
    private AlarmViewModel.SetUpAlarmValues setUpAlarmValues;
    private AlarmViewModel alarmViewModel;
    private MainActivity parent;
    private Bundle initParams;


    // Default constructor
    public SetUpAlarmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStop() {

        // Get the selected values
        String t = label.getText().toString();
        int hourOfDay = timePicker.getHour();
        int minute = timePicker.getMinute();

        // Save the values on the ViewModel
        setUpAlarmValues.setLabel(t);
        setUpAlarmValues.setHour(hourOfDay);
        setUpAlarmValues.setMinute(minute);

        setUpAlarmValues.setWeekDays(getDaysValues());
        setUpAlarmValues.setOneOff(!repeating.isChecked());

        // Remove the Up arrow
        ActionBar actionBar = parent.getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(false);

        super.onStop();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParams = getArguments();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_set_up_alarm, container, false);
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


        // Get instance of SetUpAlarmValues sub-class
        setUpAlarmValues = parent.alarmViewModel.setUpAlarmValues;
        alarmViewModel = parent.alarmViewModel;

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
        repeating = view.findViewById(R.id.RepeatSw);
        Integer WeekDays = setUpAlarmValues.getWeekDays().getValue();
        if (WeekDays!=null)
            weekDays = WeekDays;
        else
            weekDays = 0;
        setDaysValues(weekDays,view);
        oneOff = setUpAlarmValues.isOneOff().getValue();
        if (oneOff == null)
            oneOff = false;
        repeating.setChecked(!oneOff);
        setDaysVisible(!oneOff, view);
        setDisplayAreaVisible(oneOff, view);

        // This button calls the Date Picker
        callDatePickerButton = view.findViewById(R.id.ShowDatePicker_Btn);
        callDatePickerButton.setOnClickListener(this::ShowDatePickerOnClick);

        // Listener to the Repeating button (set the weekdays button visibility)
        repeating.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setDaysVisible(isChecked, view);
                setDisplayAreaVisible(!isChecked,view);
            }
        });


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
                    repeating.setChecked(!t);
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

    // Set Display Area visible/gone
    private void setDisplayAreaVisible(boolean visible, View view){

        TextView textView = view.findViewById(R.id.target_time_date);
        if (visible) {
            textView.setText(displayTargetAlarm());
            textView.setVisibility(View.VISIBLE);
        }
        else
            textView.setVisibility(View.GONE);
    }

    @NonNull
    private String displayTargetAlarm(){
        String out = "";

        // Get time/date values
        int m = setUpAlarmValues.getMinute().getValue();
        int h = setUpAlarmValues.getHour().getValue();
        int dd = setUpAlarmValues.getDayOfMonth().getValue();
        int mm = setUpAlarmValues.getMonth().getValue();
        int yy = setUpAlarmValues.getYear().getValue();

        // Create a calendar object and modify it
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        if (dd>0 && mm>0 && yy>0)
            calendar.set(yy, mm, dd,h,m,0);
        else{
            calendar.set(Calendar.HOUR_OF_DAY, h);
            calendar.set(Calendar.MINUTE, m);
            //calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),h,m,0);
        }


        // Create a string
        SimpleDateFormat format;
        format = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' HH:mm", Locale.US);
        out = format.format(calendar.getTimeInMillis());
        return out;
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


    // Called when user clicks on the calendar button to call the Date Picker
    public void ShowDatePickerOnClick(View view){
        Calendar calendar;
        int d, m, y;

        Log.d("THE_TIME_MACHINE", "ShowDatePickerOnClick");
        datePickerDialog = new DatePickerDialog(requireContext());

        // Get the date stored in the VM.
        d = setUpAlarmValues.getDayOfMonth().getValue();
        m = setUpAlarmValues.getMonth().getValue();
        y = setUpAlarmValues.getYear().getValue();

        // If date not stored, set today's date
        if (d == 0 || m == 0 || y == 0) {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            d = calendar.get(Calendar.DAY_OF_MONTH);
            m = calendar.get(Calendar.MONTH);
            y = calendar.get(Calendar.YEAR);
        }

        // Set as cancelable and set a listener that will be called on OK
        datePickerDialog.setCancelable(true);
        datePickerDialog.setOnDateSetListener(this::DateWidgetListener);

        // Set the date and show the widget
        datePickerDialog.updateDate(y,m,d);
        datePickerDialog.show();

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

    }

    // OK Button clicked
    //@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void OkClicked(){
        final MainActivity parent = (MainActivity) getActivity();

        /// This part should be replaced by:
        /// 1. Put new/modified alarm entry in alarm list
        /// 2. Reset setUpAlarmValues
        // Get the time from the time picker and pass it to the ViewModel
        int h = timePicker.getHour();
        int m = timePicker.getMinute();
        String t = label.getText().toString();
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
        item.setOneOff(!repeating.isChecked() || (weekdays==0));

        // Future Date
        if (setUpAlarmValues.isFutureDate().getValue()  ){
            int yy = setUpAlarmValues.getYear().getValue();
            int mm = setUpAlarmValues.getMonth().getValue();
            int dd = setUpAlarmValues.getDayOfMonth().getValue();
            if (yy>0 && mm>0 && dd>0) {
                item.setYear(yy);
                item.setMonth(mm);
                item.setDayOfMonth(dd);
                item.setFutureDate(true);
            }
        }

        // Get the position and the status of the entry to be created/updated
        // And Add or Update the entry on the list
        //int position = initParams.getInt("INIT_POSITION");
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
        String t = label.getText().toString();
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
    }
}