package com.example.thetimemachine.UI;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

    private List<AlarmViewModel.AlarmItem> alarmList;
    // Default constructor
    public AlarmListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parent = (MainActivity) getActivity();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create Adapter for the Recycler View
        AlarmViewModel avm = parent.alarmViewModel;
        MutableLiveData<List<AlarmViewModel.AlarmItem>> al= avm.getAlarmList();
        alarmList = al.getValue();
        alarmList = parent.alarmViewModel.getAlarmList().getValue();
        alarmAdapter = new AlarmAdapter(alarmList);
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
        parent.alarmViewModel.getAlarmList().observe(getViewLifecycleOwner(), new Observer<List<AlarmViewModel.AlarmItem>>() {
            @Override
            public void onChanged(List<AlarmViewModel.AlarmItem> m) {
                if (m != null) {
                    //alarmAdapter.notifyItemChanged(0, null);
                    alarmAdapter.notifyDataSetChanged();
                }
            }
        });


        // Define the Item Click listener - What to do when user clicks on an alarm
        alarmAdapter.setOnItemClickListener(new AlarmAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String label = alarmList.get(position).getLabel();
                String alarmTime = String.format("%d:%02d",alarmList.get(position).getHour(),alarmList.get(position).getMinute());
                Toast.makeText(getContext(), "Alarm Clicked: " + label + ": Time: " + alarmTime, Toast.LENGTH_SHORT).show();
                AlarmItemClicked(position);
            }
        });

        // Decoration
        RecyclerView.ItemDecoration itemDecoration = new  DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvAlarms.addItemDecoration(itemDecoration);
    }


    void AlarmItemClicked(int position){
        // Copy values from the item to the setup values
        parent.alarmViewModel.setUpAlarmValues.GetValuesFromList(position);

        // Passing parameters to setup fragment
        Bundle b = new Bundle();
        b.putBoolean("INIT_NEWALARM",false);
        b.putInt("INIT_POSITION", position);

        // Replace current fragment with the Setup Alarm fragment
        parent = (MainActivity) getActivity();
        if (parent != null)
            parent.getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.fragment_container_view, SetUpAlarmFragment.class, b).
                    addToBackStack("tag2").
                    commit();
    }
    void AddAlarmClicked()
    {

        // Reset the setup alarm values
        parent.alarmViewModel.setUpAlarmValues.ResetValues();

        // Passing parameters to setup fragment
        Bundle b = new Bundle();
        b.putBoolean("INIT_NEWALARM",true);

        // Replace current fragment with the Setup Alarm fragment
        parent = (MainActivity) getActivity();
        if (parent != null)
            parent.getSupportFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container_view, SetUpAlarmFragment.class, b).
                    addToBackStack("tag2").
                    commit();
    }



}