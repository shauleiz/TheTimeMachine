package com.example.thetimemachine.UI;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;

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
    private EditText title;
    private Alarm alarm;
    private AlarmViewModel.SetUpAlarmValues setUpAlarmValues;
    private MainActivity parent;


    // Default constructor
    public SetUpAlarmFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_set_up_alarm, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the handle to the Add Alarm button (FAB)
        // Then define what to do when clicked
        Ok_Button = view.findViewById(R.id.OK);
        Ok_Button.setOnClickListener(v -> OkClicked());

        // Get Time Picker and modify it
        timePicker = view.findViewById(R.id.time_picker);
        InitTimePicker(timePicker, true); // newalarm=true for default values

        // The Title edit field
        title = view.findViewById((R.id.alarm_title));

        // Get instance of SetUpAlarmValues sub-class
        parent = (MainActivity) getActivity();
        setUpAlarmValues = parent.alarmViewModel.setUpAlarmValues;

        // Set observers on the time picker
        SetTimePickerObserver();

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


        setUpAlarmValues.getTitle().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String t) {
                if (t != null) {
                    title.setText(t);
                }
            }
        });

    }

    // OK Button clicked
    private void OkClicked(){
        final MainActivity parent = (MainActivity) getActivity();

        // Get the time from the time picker and pass it to the ViewModel
        int h = timePicker.getHour();
        int m = timePicker.getMinute();
        String t = title.getText().toString();
        //alarm = new Alarm(h, m, t);
        //parent.alarmViewModel.AddAlarm(alarm);
        setUpAlarmValues.setHour(h);
        setUpAlarmValues.setMinute(m);
        setUpAlarmValues.setTitle(t);



        // Go back to the Alarm List Fragment
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
            timePicker.setHour(12);
            timePicker.setMinute(0);
            timePicker.animate();
        }
        // TODO: newalarm == false: Modify values of existing alarm.
    }

    private  void SetTimePickerObserver(){
        //title.setOnT
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                setUpAlarmValues.setHour(hourOfDay);
                setUpAlarmValues.setMinute(minute);

            }
        });
    }
}