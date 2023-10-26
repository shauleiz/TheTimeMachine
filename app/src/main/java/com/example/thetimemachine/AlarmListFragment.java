package com.example.thetimemachine;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class AlarmListFragment extends Fragment {

    FloatingActionButton AddAlarm_Button;

    public AlarmListFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
 /*   public static AlarmListFragment newInstance(String param1, String param2) {
        AlarmListFragment fragment = new AlarmListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
*/
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

        AddAlarm_Button = view.findViewById(R.id.Add_Alarm_fab);
        AddAlarm_Button.setOnClickListener(v -> AddAlarmClicked());
    }


    void AddAlarmClicked()
    {

    }
}