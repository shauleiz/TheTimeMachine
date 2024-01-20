package com.example.thetimemachine.UI;

import static android.widget.TextView.BufferType.SPANNABLE;

import static androidx.core.content.ContextCompat.getColor;

import static com.example.thetimemachine.Data.AlarmItem.SUNDAY;
import static com.example.thetimemachine.Data.AlarmItem.MONDAY;
import static com.example.thetimemachine.Data.AlarmItem.TUESDAY;
import static com.example.thetimemachine.Data.AlarmItem.WEDNESDAY;
import static com.example.thetimemachine.Data.AlarmItem.THURSDAY;
import static com.example.thetimemachine.Data.AlarmItem.FRIDAY;
import static com.example.thetimemachine.Data.AlarmItem.SATURDAY;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
        return new AlarmViewHolder(contactView);
    }

    // Populating data into the item through holder
    @Override
    public void onBindViewHolder(AlarmAdapter.AlarmViewHolder holder, int position) {
        // Get the data model based on position
        AlarmItem alarmItem = alarmList.get(position);

        // Label
        holder.AlarmLabel.setText(alarmItem.getLabel());

        // Weekdays
        holder.WeekDays.setText(R.string.list_of_weekdays_SU);
        prepareWeekdaysSpan(alarmItem, holder.WeekDays);

        // Time
        String fmt = context.getResources().getString(R.string.alarm_format);
        String alarmTime = String.format(fmt,alarmItem.getHour(),alarmItem.getMinute());
        holder.AlarmTime.setText(alarmTime);

        // Active
        boolean isActive = alarmItem.isActive();
        holder.AlarmActive.setChecked(isActive);
        if (isActive) {
            holder.AlarmTime.setTextColor(getColor(context,
                  com.google.android.material.R.color.design_default_color_primary_variant));
            holder.AlarmLabel.setTextColor(getColor(context, R.color.black));
            holder.WeekDays.setTextColor(getColor(context, R.color.black));
        }
        else {
            holder.AlarmTime.setTextColor(getColor(context, R.color.faded_violet));
            holder.AlarmLabel.setTextColor(getColor(context, R.color.black_overlay));
            holder.WeekDays.setTextColor(getColor(context, R.color.black_overlay));

        }


        //

    }

    // Prepare the string to show Today/Tomorrow or the selected days of the week
    // The info is gotten from the Alarm item
    // The function returns a spanable string for display
    private void prepareWeekdaysSpan(AlarmItem alarmItem, TextView textView) {
        Spannable word = new SpannableString("");
        textView.setText(word, SPANNABLE);
        textView.setText(word);



        // Alarm activated? If not, return an empty string
        if (!alarmItem.isActive())
            return;

        // Is it a One-Off case? If so, is the alarm set for today or tomorrow?
        if (alarmItem.isOneOff()){
            if (alarmItem.isToday()) {
                word = new SpannableString("Today");
                word.setSpan(new ForegroundColorSpan(Color.RED),
                      0, word.length(),
                      Spannable.SPAN_INCLUSIVE_INCLUSIVE);}
            else {
                word = new SpannableString("Tomorrow");
                word.setSpan(new ForegroundColorSpan(getColor(context,
                            R.color.light_blue_600)),
                      0, word.length(),
                      Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
        // This is a repeating alarm - print the weekdays
        else{
            // By default - all days are grayed
            word = new SpannableString("Su Mo Tu We Th Fr Sa");
            word.setSpan(new ForegroundColorSpan(getColor(context, R.color.medium_gray)),
                  0, word.length(),
                  Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            int weekdays = alarmItem.getWeekDays();
            if ((weekdays&SUNDAY) >0)
                word.setSpan(new ForegroundColorSpan(Color.BLACK),0, 2,
                      Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if ((weekdays&MONDAY) >0)
                word.setSpan(new ForegroundColorSpan(Color.BLACK),3, 5,
                      Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if ((weekdays&TUESDAY) >0)
                word.setSpan(new ForegroundColorSpan(Color.BLACK),6, 8,
                      Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if ((weekdays&WEDNESDAY) >0)
                word.setSpan(new ForegroundColorSpan(Color.BLACK),9, 11,
                      Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if ((weekdays&THURSDAY) >0)
                word.setSpan(new ForegroundColorSpan(Color.BLACK),12, 14,
                      Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if ((weekdays&FRIDAY) >0)
                word.setSpan(new ForegroundColorSpan(Color.BLACK),15, 17,
                      Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if ((weekdays&SATURDAY) >0)
                word.setSpan(new ForegroundColorSpan(Color.BLACK),18, 20,
                      Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }

        textView.setText(word);
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