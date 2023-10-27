package com.example.thetimemachine;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class AlarmListFragment extends Fragment {


    private FloatingActionButton AddAlarm_Button;

    // Default constructor
    public AlarmListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

      //AddAlarm_Button.setOnClickListener(v -> alarmViewModel.AddAlarm());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alarm_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the handle to the Add Alarm button (FAB)
        // Then define what to do when clicked
        AddAlarm_Button = view.findViewById(R.id.Add_Alarm_fab);
        AddAlarm_Button.setOnClickListener(v -> AddAlarmClicked());



    }

    /**
     * Call the fragment
     * where a new entry is added to the list of alarms
     *
     */
    void AddAlarmClicked()
    {
        // Replace current fragment with the Setup Alarm fragment
        final FragmentActivity parent = getActivity();
        if (parent != null)
            parent.getSupportFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container_view, SetUpAlarmFragment.class, null).
                    addToBackStack("tag2").
                    commit();
    }



}