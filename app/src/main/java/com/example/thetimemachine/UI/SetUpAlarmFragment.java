package com.example.thetimemachine.UI;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.loader.app.LoaderManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import com.example.thetimemachine.AlarmViewModel;
import com.example.thetimemachine.R;

public class SetUpAlarmFragment extends Fragment {

    private Button Ok_Button, Delete_Button;
    private TimePicker timePicker;
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

        // Get instance of SetUpAlarmValues sub-class
        parent = (MainActivity) getActivity();
        if (parent == null) return;
        setUpAlarmValues = parent.alarmViewModel.setUpAlarmValues;
        alarmViewModel = parent.alarmViewModel;

        // Get the handle to the Add Alarm button
        // Then define what to do when clicked
        Ok_Button = view.findViewById(R.id.OK);
        Ok_Button.setOnClickListener(v -> OkClicked());

        // Get the handle to the Delete Alarm button
        // Then define what to do when clicked
        Delete_Button = view.findViewById(R.id.Delete);

        if (!initParams.getBoolean("INIT_NEWALARM", false))
            Delete_Button.setOnClickListener(v -> DeleteClicked());
        else
            Delete_Button.setVisibility(View.GONE);

        // Get Time Picker and modify it
        timePicker = view.findViewById(R.id.time_picker);
        InitTimePicker(timePicker);

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

        // Get the position and the status of the entry to be created/updated
        // And Add or Update the entry on the list
        int position = initParams.getInt("INIT_POSITION");
        if (initParams.getBoolean("INIT_NEWALARM", true) || position<0)
            alarmViewModel.AddAlarm(h,m,t,true);
        else
            alarmViewModel.UpdateAlarm(h,m,t,true, position);


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

        // Get position of alarm to delete and check that it exists
        int position = initParams.getInt("INIT_POSITION");
        if (position<0 || alarmViewModel == null)
            return;

        alarmViewModel.DeleteAlarm(position);

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
    private void InitTimePicker(TimePicker timePicker) {
        // TODO: Replace hardcoded default values by values defined by the user
        timePicker.setIs24HourView(true);

        if (setUpAlarmValues == null) return;
        MutableLiveData<Integer>  h = setUpAlarmValues.getHour();
        MutableLiveData<Integer>  m = setUpAlarmValues.getMinute();
        if (h==null || m==null || h.getValue()==null || m.getValue()==null) return;

        timePicker.setHour(h.getValue());
        timePicker.setMinute(m.getValue());
        timePicker.animate();
    }

 /*   // Observe changes to the time picker introduced by the user
    // If the time has changed then update the ViewModel
    private  void SetTimePickerObserver(){
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                setUpAlarmValues.setHour(hourOfDay);
                setUpAlarmValues.setMinute(minute);

            }
        });
    }*/

    /*// Observe changes in the label
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
    }*/
}