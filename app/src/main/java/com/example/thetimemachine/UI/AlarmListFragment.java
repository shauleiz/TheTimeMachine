package com.example.thetimemachine.UI;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thetimemachine.AlarmAdapter;
import com.example.thetimemachine.AlarmViewModel;
import com.example.thetimemachine.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class AlarmListFragment extends Fragment {


    private FloatingActionButton AddAlarm_Button;
    private RecyclerView rvAlarms;
    private MainActivity parent;
    private AlarmAdapter alarmAdapter;

    // Default constructor
    public AlarmListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parent = (MainActivity) getActivity();
        // Create Adapter for the Recycler View
        List<AlarmViewModel.AlarmItem> alarmList = parent.alarmViewModel.getAlarmList().getValue();
        alarmAdapter = new AlarmAdapter(alarmList);

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

        // Get the Recycle View
        rvAlarms = view.findViewById(R.id.alarmListRecyclerView);

        // Set layout manager to position the items
        rvAlarms.setLayoutManager(new LinearLayoutManager(getContext()));
        // Attach the adapter to the recyclerview to populate items
        rvAlarms.setAdapter(alarmAdapter);

        // Create observer to inform recycler view that there was a change in the list
        parent.alarmViewModel.getAlarmList().observe(getViewLifecycleOwner(), new Observer<ArrayList<AlarmViewModel.AlarmItem>>() {
            @Override
            public void onChanged(ArrayList<AlarmViewModel.AlarmItem> m) {
                if (m != null) {
                    alarmAdapter.notifyItemChanged(0, null);
                    //alarmAdapter.notifyDataSetChanged();
                }
            }
        });

    }


    void AddAlarmClicked()
    {
        // Replace current fragment with the Setup Alarm fragment
        parent = (MainActivity) getActivity();
        if (parent != null)
            parent.getSupportFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container_view, SetUpAlarmFragment.class, null).
                    addToBackStack("tag2").
                    commit();
    }



}