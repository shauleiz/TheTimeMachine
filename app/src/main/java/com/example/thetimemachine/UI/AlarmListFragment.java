package com.example.thetimemachine.UI;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.thetimemachine.Data.AlarmItem;
import com.example.thetimemachine.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class AlarmListFragment extends Fragment {


    private FloatingActionButton AddAlarm_Button;
    private RecyclerView rvAlarms;
    private MainActivity parent;
    private AlarmAdapter alarmAdapter;

    private List<AlarmItem> alarmList;

    // Required empty public constructor
    public AlarmListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parent = (MainActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create Adapter for the Recycler View
        alarmList = parent.alarmViewModel.getAlarmList().getValue();
        alarmAdapter = new AlarmAdapter(alarmList);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alarm_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Add Alarm button (FAB)
        AddAlarm_Button = view.findViewById(R.id.Add_Alarm_fab);
        AddAlarm_Button.setOnClickListener(v -> AddAlarmClicked());

        // Recyclerview widget - List of alarms
        rvAlarms = view.findViewById(R.id.alarmListRecyclerView);
        // Set layout manager to position the items
        rvAlarms.setLayoutManager(new LinearLayoutManager(getContext()));
        // Attach the adapter to the recyclerview to populate items
        // (The Adapter was created previously in onCreateView())
        rvAlarms.setAdapter(alarmAdapter);

        // Create observer to inform recycler view that there was a change in the list
        parent.alarmViewModel.getAlarmList().observe(getViewLifecycleOwner(), new Observer<List<AlarmItem>>() {
            @Override
            public void onChanged(List<AlarmItem> m) {
                if (m != null) {
                    alarmList = m;
                    alarmAdapter.UpdateAlarmAdapter(m);
                }
            }
        });


        // Define the Item Click listener - What to do when user clicks on an alarm item
        // Test what was clicked: Compare ID of view to known item elements
        // If none found then it means that the item itself was clicked
        alarmAdapter.setOnItemClickListener(new AlarmAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int id = view.getId();
                if (id == R.id.AlarmActive) {// Alarm Active checkbox has been clicked
                    ActiveCheckboxChanged( view,  position);
                }
                else{ // The Alarm item itself has been clicked
                    String label = alarmList.get(position).getLabel();
                    String alarmTime = String.format("%d:%02d", alarmList.get(position).getHour(), alarmList.get(position).getMinute());
                    Toast.makeText(getContext(), "Alarm Clicked: " + label + ": Time: " + alarmTime, Toast.LENGTH_SHORT).show();
                    AlarmItemClicked(position);
                }
            }
        });

        // Decoration
        RecyclerView.ItemDecoration itemDecoration = new  DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvAlarms.addItemDecoration(itemDecoration);
    }


    // Get Item's info from Alarm list
    // Get new state of checkbox
    // Send update to this item down to the ViewModel
    // Schedule/cancel alarm
    void ActiveCheckboxChanged(View view, int position) {

        // Get alarm item data from list
        int h = alarmList.get(position).getHour();
        int m = alarmList.get(position).getMinute();
        String l = alarmList.get(position).getLabel();
        long c = alarmList.get(position).getCreateTime();

        // Get 'active' checkbox state
        boolean active = ((CheckBox)view).isChecked();
        // Create a new alarm, update View Model and schedule/cancel alarm
        AlarmItem item = new AlarmItem(h, m, l, active, c);
        parent.alarmViewModel.UpdateAlarm(item);
        if (active==true)
            item.Schedule();
        else
            item.cancelAlarm();
    }
    /*
     *   Called when user clicks on Alarm item in recycler
     *   Copy values from the selected item to be used by the setup fragment
     *   Create a bundle with data to be passed to the setup fragment
     *   Replace this fragment by setup fragment
     */
    void AlarmItemClicked(int position){

        // Copy values from the selected item to be used by the setup fragment
        parent.alarmViewModel.setUpAlarmValues.GetValuesFromList(position);

        // Passing parameters to setup fragment
        Bundle b = new Bundle();
        b.putBoolean("INIT_NEWALARM",false);
        b.putInt("INIT_POSITION", position);
        b.putLong("INIT_CREATE_TIME", parent.alarmViewModel.getAlarmList().getValue().get(position).getCreateTime());

        // Replace current fragment with the Setup Alarm fragment
        parent = (MainActivity) getActivity();
        if (parent != null)
            parent.getSupportFragmentManager().
                    beginTransaction().
                    replace(R.id.fragment_container_view, SetUpAlarmFragment.class, b).
                    addToBackStack("tag2").
                    commit();
    }

    /* *
    * Called when user clicks on Add Alarm button
    * Resets values to be used by the setup fragment
    * Create a bundle with data to be passed to the setup fragment
    * Replace this fragment by setup fragment
    * */
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