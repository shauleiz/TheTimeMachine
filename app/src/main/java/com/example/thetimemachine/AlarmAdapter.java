package com.example.thetimemachine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// Adapter for RecyclerView of the list of Alarms
// Contains the ViewHolder for the Alarm item
public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private List<AlarmViewModel.AlarmItem> alarmList;
    public AlarmAdapter(List<AlarmViewModel.AlarmItem> _alarmList){
        if (_alarmList == null)
            alarmList = new ArrayList<AlarmViewModel.AlarmItem>();
        else
            alarmList = new ArrayList<AlarmViewModel.AlarmItem>(_alarmList);
    }

    // Called when the RecyclerView needs to create a new entry
    // Here it is provided with the item's layout
    @Override
    public AlarmAdapter.AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Item Layout
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.recycler_view_item, parent, false);

        // Return a new holder instance
        AlarmViewHolder viewHolder = new AlarmViewHolder(contactView);
        return viewHolder;
    }

    // Populating data into the item through holder
    @Override
    public void onBindViewHolder(AlarmAdapter.AlarmViewHolder holder, int position) {
        // Get the data model based on position
        AlarmViewModel.AlarmItem alarmItem = alarmList.get(position);

        // Set item views from data in the ViewModel

        // Label
        holder.AlarmLabel.setText(alarmItem.getLabel());

        // Time
        String alarmTime = alarmItem.getHour() +":" + alarmItem.getMinute();
        holder.AlarmTime.setText(alarmTime);

        // Active
        holder.AlarmActive.setChecked(alarmItem.isActive());

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return alarmList.size();
    }
    public class AlarmViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView AlarmLabel, WeekDays, AlarmTime;
        public CheckBox AlarmActive;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public AlarmViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            AlarmLabel = (TextView) itemView.findViewById(R.id.AlarmLabel);
            WeekDays = (TextView) itemView.findViewById(R.id.WeekDays);
            AlarmTime = (TextView) itemView.findViewById(R.id.AlarmTime);
            AlarmActive = (CheckBox) itemView.findViewById(R.id.AlarmActive);
        }
    }
}