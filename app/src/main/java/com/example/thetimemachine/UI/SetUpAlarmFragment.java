package com.example.thetimemachine.UI;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.thetimemachine.Alarm;
import com.example.thetimemachine.AlarmViewModel;
import com.example.thetimemachine.R;

public class SetUpAlarmFragment extends Fragment {

    private Button Ok_Button;
    private TimePicker timePicker;
    private EditText label;
    private Alarm alarm;
    private AlarmViewModel.SetUpAlarmValues setUpAlarmValues;
    private AlarmViewModel alarmViewModel;
    private MainActivity parent;


    // Default constructor
    public SetUpAlarmFragment() {
        // Required empty public constructor
    }


    @Override
    public void onDestroy(){

        // Get the selected values
        String t = label.getText().toString();
        int hourOfDay = timePicker.getHour();
        int minute = timePicker.getMinute();

        // Save the values on the ViewModel
        setUpAlarmValues.setLabel(t);
        setUpAlarmValues.setHour(hourOfDay);
        setUpAlarmValues.setMinute(minute);


        super.onDestroy();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_set_up_alarm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get instance of SetUpAlarmValues sub-class
        parent = (MainActivity) getActivity();
        setUpAlarmValues = parent.alarmViewModel.setUpAlarmValues;
        alarmViewModel = parent.alarmViewModel;

        // Get the handle to the Add Alarm button (FAB)
        // Then define what to do when clicked
        Ok_Button = view.findViewById(R.id.OK);
        Ok_Button.setOnClickListener(v -> OkClicked());

        // Get Time Picker and modify it
        timePicker = view.findViewById(R.id.time_picker);
        InitTimePicker(timePicker, true); // newalarm=true for default values

        // The Label edit field
        label = (EditText) view.findViewById((R.id.alarm_label));
        label.setText(setUpAlarmValues.getLabel().getValue());


        // Set observers on the time picker fragment
        // SetTimePickerObserver();
        // TitleObserver();

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




        setUpAlarmValues.getLabel().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String t) {
                if (t != null) {
                    label.setText(t);
                }
            }
        });

    }

    // OK Button clicked
    private void OkClicked(){
        final MainActivity parent = (MainActivity) getActivity();

        /// This part should be replaced by:
        /// 1. Put new/modified alarm entry in alarm list
        /// 2. Reset setUpAlarmValues
        // Get the time from the time picker and pass it to the ViewModel
        int h = timePicker.getHour();
        int m = timePicker.getMinute();
        String t = label.getText().toString();
        //alarm = new Alarm(h, m, t);
        //parent.alarmViewModel.AddAlarm(alarm);
        setUpAlarmValues.setHour(h);
        setUpAlarmValues.setMinute(m);
        setUpAlarmValues.setLabel(t);

        //TODO: Put new/modified alarm entry in alarm list
        alarmViewModel.AddAlarm(h,m,t,true);


        //TODO: Reset setUpAlarmValues in the View Model

        // Display the Alarm List Fragment
        if (parent != null)
            parent.getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.fragment_container_view, AlarmListFragment.class, null).
                    //addToBackStack("tag3").
                    commit();

    }


    // Initialize the time picker to default setup
    // TODO Initialize time picker to time values when it is called to modify values
    private void InitTimePicker(TimePicker timePicker, boolean newalarm){
        // TODO: Replace hardcoded default values by values defined by the user
        timePicker.setIs24HourView(true);

        if (newalarm)
        { // This is a new alarm - set default values
            timePicker.setHour(setUpAlarmValues.getHour().getValue());
            timePicker.setMinute(setUpAlarmValues.getMinute().getValue());
            timePicker.animate();
        }
        // TODO: newalarm == false: Modify values of existing alarm.
    }

    // Observe changes to the time picker introduced by the user
    // If the time has changed then update the ViewModel
    private  void SetTimePickerObserver(){
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                setUpAlarmValues.setHour(hourOfDay);
                setUpAlarmValues.setMinute(minute);

            }
        });
    }

    // Observe changes in the label
    private void LabelObserver(){
        label.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()>0)
                { String t = s.toString();
                //String t = label.getText().toString();
               setUpAlarmValues.setLabel(t);
                }
            }
        });
    }
}