package com.example.thetimemachine;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

public class SetUpAlarmFragment extends Fragment {

    private SetUpAlarmViewModel mViewModel;
    private Button Ok_Button;
    private TimePicker timePicker;


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
    }

    // OK Button clicked
    private void OkClicked(){
        // Get the time from the time picker and pass it to the ViewModel
        int h = timePicker.getHour();
        int m = timePicker.getMinute();

        // Go back to the Alarm List Fragment
        final FragmentActivity parent = getActivity();
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
}