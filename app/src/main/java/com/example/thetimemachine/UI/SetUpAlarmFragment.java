package com.example.thetimemachine.UI;

import static com.example.thetimemachine.Data.AlarmItem.FRIDAY;
import static com.example.thetimemachine.Data.AlarmItem.MONDAY;
import static com.example.thetimemachine.Data.AlarmItem.SATURDAY;
import static com.example.thetimemachine.Data.AlarmItem.SUNDAY;
import static com.example.thetimemachine.Data.AlarmItem.THURSDAY;
import static com.example.thetimemachine.Data.AlarmItem.TUESDAY;
import static com.example.thetimemachine.Data.AlarmItem.WEDNESDAY;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.example.thetimemachine.AlarmViewModel;
import com.example.thetimemachine.Data.AlarmItem;
import com.example.thetimemachine.R;

import java.util.Calendar;

public class SetUpAlarmFragment extends Fragment {

    private TimePicker timePicker;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch repeating;

    private ToggleButton suToggleButton, moToggleButton, tuToggleButton,
          weToggleButton, thToggleButton,frToggleButton, saToggleButton;
    private EditText label;
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
        Toolbar AppToolbar = (Toolbar) ((AppCompatActivity)getActivity()).findViewById(R.id.app_toolbar);
        AppToolbar.setTitle(R.string.alarmsetup_title);
        ((AppCompatActivity)getActivity()).setSupportActionBar(AppToolbar);
        parent.setDeleteAction(false);
        parent.setEditAction(false);
        parent.invalidateOptionsMenu();

        // Get instance of SetUpAlarmValues sub-class
        setUpAlarmValues = parent.alarmViewModel.setUpAlarmValues;
        alarmViewModel = parent.alarmViewModel;

        // Get the handle to the Add Alarm button
        // Then define what to do when clicked
        Button ok_Button = view.findViewById(R.id.OK);
        ok_Button.setOnClickListener(v -> OkClicked());

        // Get the handle to the Delete Alarm button
        // Then define what to do when clicked
        Button delete_Button = view.findViewById(R.id.Delete);

        boolean isNewAlarm = initParams.getBoolean("INIT_NEWALARM", false);
        if (!isNewAlarm)
            delete_Button.setOnClickListener(v -> DeleteClicked());
        else
            delete_Button.setVisibility(View.GONE);

        // Get Time Picker and modify it
        timePicker = view.findViewById(R.id.time_picker);
        InitTimePicker(timePicker, isNewAlarm);

        // The Label edit field
        label = view.findViewById((R.id.alarm_label));
        label.setText(setUpAlarmValues.getLabel().getValue());

        // Get the Repeating button and set the weekdays button visibility
        repeating = view.findViewById(R.id.RepeateSwitch);
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


        // Listener to the Repeating button (set the weekdays button visibility)
        repeating.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setDaysVisible(isChecked, view);
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
        int visibility;
        if (visible)
            visibility = View.VISIBLE;
        else
            visibility = View.GONE;

        view.findViewById(R.id.SundayButton).setVisibility(visibility);
        view.findViewById(R.id.MondayButton).setVisibility(visibility);
        view.findViewById(R.id.TuesdayButton).setVisibility(visibility);
        view.findViewById(R.id.WednesdayButton).setVisibility(visibility);
        view.findViewById(R.id.ThursdayButton).setVisibility(visibility);
        view.findViewById(R.id.FridayButton).setVisibility(visibility);
        view.findViewById(R.id.SaturdayButton).setVisibility(visibility);

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

        suToggleButton = ((ToggleButton) view.findViewById(R.id.SundayButton));
        suToggleButton.setChecked(isSunday);
        moToggleButton = ((ToggleButton) view.findViewById(R.id.MondayButton));
        moToggleButton.setChecked(isMonday);
        tuToggleButton = ((ToggleButton) view.findViewById(R.id.TuesdayButton));
        tuToggleButton.setChecked(isTuesday);
        weToggleButton =  ((ToggleButton) view.findViewById(R.id.WednesdayButton));
        weToggleButton.setChecked(isWednesday);
        thToggleButton = ((ToggleButton) view.findViewById(R.id.ThursdayButton));
        thToggleButton.setChecked(isThursday);
        frToggleButton = ((ToggleButton) view.findViewById(R.id.FridayButton));
        frToggleButton.setChecked(isFriday);
        saToggleButton = ((ToggleButton) view.findViewById(R.id.SaturdayButton));
        saToggleButton.setChecked(isSaturday);
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


    // OK Button clicked
    //@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void OkClicked(){
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

        // If modified alarm then use its old Create Time (id)
        // If new alarm then create it using a new Create Time (id)
        if (!newAlarm)
            item = new AlarmItem(h, m,t, active, c);
        else
            item = new AlarmItem(h, m,t, active);

        // Weekdays
        int weekdays = getDaysValues();
        item.setWeekDays(weekdays);
        item.setOneOff(!repeating.isChecked() || (weekdays==0));

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
        // TODO: Replace hardcoded default values by values defined by the user
        timePicker.setIs24HourView(true);

        if (newAlarm)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int h =calendar.get(Calendar.HOUR);
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