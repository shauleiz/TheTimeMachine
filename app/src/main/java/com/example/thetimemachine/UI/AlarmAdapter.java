package com.example.thetimemachine.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thetimemachine.Data.AlarmItem;
import com.example.thetimemachine.R;

import java.util.ArrayList;
import java.util.List;

// Adapter for RecyclerView of the list of Alarms
// Contains the ViewHolder for the Alarm item
public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {


    private List<AlarmItem> alarmList;

    // Define clickListener member variable
    private OnItemClickListener clickListener;

    private Context context;

    // Define the clickListener interface
    // The clickListener itself is defined in the fragment
    // and will be passed by onViewCreated()
    public interface OnItemClickListener { void onItemClick(View itemView, int position);}

    // Define the method that allows the parent fragment to define the clickListener
    public void setOnItemClickListener(OnItemClickListener _listener) {clickListener = _listener;}

    // Constructor: Gets the list of alarms (if exists)
    public AlarmAdapter(List<AlarmItem> _alarmList){
        if (_alarmList == null)
            alarmList = new ArrayList<>();
        else
            alarmList = _alarmList;
    }

    // Replace list of alarms and notify
    public void UpdateAlarmAdapter(List<AlarmItem> _alarmList){
         if (_alarmList != null) {
             alarmList = _alarmList;
             notifyDataSetChanged();
         }
     }

    // Called when the RecyclerView needs to create a new entry
    // Here it is provided with the item's layout
    @Override
    @NonNull
    public AlarmAdapter.AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Item Layout
        context = parent.getContext();
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
        AlarmItem alarmItem = alarmList.get(position);

        // Label
        holder.AlarmLabel.setText(alarmItem.getLabel());

        // Time
        String fmt = context.getResources().getString(R.string.alarm_format);
        String alarmTime = String.format(fmt,alarmItem.getHour(),alarmItem.getMinute());
        holder.AlarmTime.setText(alarmTime);

        // Active
        holder.AlarmActive.setChecked(alarmItem.isActive());

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    // ViewHolder Class - Holds one recycler line (One Alarm)
    public class AlarmViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView AlarmLabel, WeekDays, AlarmTime;
        public ImageView BellView;
        public CheckBox AlarmActive;
        public View itemView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public AlarmViewHolder(View _itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(_itemView);

            itemView = _itemView;

            BellView = (ImageView) itemView.findViewById(R.id.BellView);
            AlarmLabel = (TextView) itemView.findViewById(R.id.AlarmLabel);
            WeekDays = (TextView) itemView.findViewById(R.id.WeekDays);
            AlarmTime = (TextView) itemView.findViewById(R.id.AlarmTime);
            AlarmActive = (CheckBox) itemView.findViewById(R.id.AlarmActive);



            // Setup the click listener to the item (not specific control)
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (clickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            clickListener.onItemClick(itemView, position);
                        }
                    }
                }
            });

            // Setup the click listener to the Active checkbox of an item
            AlarmActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (clickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            clickListener.onItemClick(AlarmActive, position);
                        }
                    }
                }
            });
        }
    }
}