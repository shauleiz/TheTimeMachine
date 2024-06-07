package com.product.thetimemachine.UI;

import static android.widget.TextView.BufferType.SPANNABLE;
import static androidx.core.content.ContextCompat.getColor;
import static com.product.thetimemachine.UI.SettingsFragment.pref_first_day_of_week;
import static com.product.thetimemachine.UI.SettingsFragment.pref_is24HourClock;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.product.thetimemachine.Data.AlarmItem;
import com.product.thetimemachine.R;
import com.google.android.material.color.MaterialColors;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Adapter for RecyclerView of the list of Alarms
// Contains the ViewHolder for the Alarm item
public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {


    private List<AlarmItem> alarmList;
    private ArrayList<Integer> selectedItems;


    // Define clickListener member variable
    private OnItemClickListener clickListener;
    private OnItemLongClickListener clickLongListener;

    private Context context;

    // Define the clickListener interface
    // The clickListener itself is defined in the fragment
    // and will be passed by onViewCreated()
    public interface OnItemClickListener { void onItemClick(View itemView, int position);  }
    public interface OnItemLongClickListener { void onItemLongClick(View itemView, int position);}

    // Define the method that allows the parent fragment to define the clickListener
    public void setOnItemClickListener(OnItemClickListener _listener) {clickListener = _listener;}
    public void setOnItemLongClickListener(OnItemLongClickListener _listener) {clickLongListener = _listener;}

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

    public void UpdateAlarmAdapter(ArrayList<Integer> _selectedItems){
        if (_selectedItems != null) {
            selectedItems = _selectedItems;
            Log.d("THE_TIME_MACHINE", "UpdateAlarmAdapter()");
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

        // Snooze Counter
        int snoozeCounter = alarmItem.getSnoozeCounter();
        String s = String.format(Locale.US,"Adapter.onBindViewHolder(): Label=%s - Hour=%d - Minute=%d - Snooze Counter=%d",
              alarmItem.getLabel(), alarmItem.getHour(), alarmItem.getMinute(), snoozeCounter);
        Log.d("THE_TIME_MACHINE", s);

       if (snoozeCounter>0)
           holder.SnoozeIcon.setVisibility(View.VISIBLE);
       else
           holder.SnoozeIcon.setVisibility(View.INVISIBLE);

       // Vibration? If so set vibration icon
        if (alarmItem.isVibrationActive())
            holder.VibrationIcon.setVisibility(View.VISIBLE);
        else
            holder.VibrationIcon.setVisibility(View.INVISIBLE);

//        AudioManager am = (AudioManager) context.getApplicationContext().getSystemService( Context.AUDIO_SERVICE);
//        int volumeLevel= am.getStreamVolume(AudioManager.STREAM_ALARM);
//        Log.d("THE_TIME_MACHINE", "Alarm Volume: " + volumeLevel );

        // Warn if alarm muted
        if (alarmItem.isAlarmMute() /*|| volumeLevel<=1*/)
            holder.MuteIcon.setVisibility(View.VISIBLE);
        else
            holder.MuteIcon.setVisibility(View.INVISIBLE);


        // Weekdays
        if (pref_first_day_of_week().equals("Su"))
            holder.WeekDays.setText(R.string.list_of_weekdays_SU);
        else
            holder.WeekDays.setText(R.string.list_of_weekdays_Mo);

        prepareWeekdaysSpan(alarmItem, holder.WeekDays);

        // Time
        int h =  alarmItem.getHour();
        if (pref_is24HourClock())
            holder.amPm24h.setText(R.string.format_24h);
        else{
            if (h==0) {
                holder.amPm24h.setText(R.string.format_am);
                h=12;
            }
            else if (h<12)
                holder.amPm24h.setText(R.string.format_am);
            else {
                holder.amPm24h.setText(R.string.format_pm);
                if (h!=12)
                    h-=12;
            }
        }



        String fmt = context.getResources().getString(R.string.alarm_format);
        String alarmTime = String.format(fmt,h,alarmItem.getMinute());
        holder.AlarmTime.setText(alarmTime);


        // Marking item as selected (yes/no)
        int id = (int)alarmItem.getCreateTime();
        if (selectedItems != null) {
            int index = selectedItems.indexOf(id);
            if (index >= 0) {
                holder.itemView.setBackgroundColor(getColor(context, R.color.faded_violet_1));
            }
            else
                holder.itemView.setBackgroundColor(getColor(context, R.color.transparent));
        }

        // Active
        int AlarmTimeCheckedTextColor = MaterialColors.getColor(context, com.google.android.material.R.attr.colorPrimary, Color.BLACK);
        int AlarmTimeUncheckedTextColor = MaterialColors.getColor(context, com.google.android.material.R.attr.colorPrimaryInverse, Color.BLACK);
        boolean isActive = alarmItem.isActive();
        holder.AlarmActive.setChecked(isActive);
        if (isActive) {
            holder.AlarmTime.setTextColor(AlarmTimeCheckedTextColor);
        }
        else {
            holder.AlarmTime.setTextColor(AlarmTimeUncheckedTextColor);
        }

        // Ringing: Blink AlarmTime
        boolean isBlinking = alarmItem.isRinging();
        Animation anim = new AlphaAnimation(0.2f, 1.0f);
        if (isBlinking) {
            anim.setDuration(200); //You can manage the blinking time with this parameter
            anim.setStartOffset(20);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);
        }
        else
            anim.reset();
        holder.AlarmTime.startAnimation(anim);
    }

    // Prepare the string to show Today/Tomorrow or the selected days of the week
    // The info is gotten from the Alarm item
    // The function returns a span-able string for display
    private void prepareWeekdaysSpan(AlarmItem alarmItem, TextView textView) {

        final int[] su_array = {0, 2, 3, 5, 6, 8, 9, 11, 12, 14, 15, 17, 18, 20};
        final int[] mo_array = {18, 20, 0, 2, 3, 5, 6, 8, 9, 11, 12, 14, 15, 17, 18, 20};
        int[] gen_array;

        Spannable word = new SpannableString("");
        textView.setText(word, SPANNABLE);
        textView.setText(word);



        // Alarm activated? If not, return an empty string
        //if (!alarmItem.isActive())
        //    return;

        // Is it a One-Off case? If so, is the alarm set for today or tomorrow?
        if (alarmItem.isOneOff()){
            if (alarmItem.isToday()) {
                word = new SpannableString(context.getString(R.string.day_today));
                if (alarmItem.isActive())
                    word.setSpan(new ForegroundColorSpan(Color.RED),
                      0, word.length(),
                      Spannable.SPAN_INCLUSIVE_INCLUSIVE);}
            else if (alarmItem.isTomorrow()) {
                word = new SpannableString(context.getString(R.string.day_tomorrow));
                if (alarmItem.isActive())
                    word.setSpan(new ForegroundColorSpan(getColor(context,
                            R.color.light_blue_600)),
                      0, word.length(),
                      Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            } else {
                SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.US);
                long alarmTime = alarmItem.alarmTimeInMillis();
                word = new SpannableString(format.format(alarmTime));
                if (alarmItem.isActive())
                    word.setSpan(new ForegroundColorSpan(
                                MaterialColors.getColor(context, R.attr.primary_text, Color.BLACK)),
                          0, word.length(),
                          Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
        // This is a repeating alarm - print the weekdays
        else{
            // By default - all days are grayed
            if (pref_first_day_of_week().equals("Su")) {
                word = new SpannableString(context.getString(R.string.su_mo_tu_we_th_fr_sa));
                gen_array = su_array;
            }
            else {
                word = new SpannableString(context.getString(R.string.mo_tu_we_th_fr_sa_su));
                gen_array = mo_array;
            }

            // Color of days that are selected / not selected
            int notSelectedTextColor = MaterialColors.getColor(context, R.attr.faded_text, Color.BLACK);
            int selectedTextColor = MaterialColors.getColor(context, R.attr.primary_text, Color.BLACK);

                  word.setSpan(new ForegroundColorSpan(notSelectedTextColor),
                  0, word.length(),
                  Spannable.SPAN_INCLUSIVE_INCLUSIVE);/* */


            int indexOfNextDay = alarmItem.getWeekdayOfNextAlarm();
            int weekdays = alarmItem.getWeekDays();
            for (int day=0; day<7 ; day++){
                if ((weekdays&(1<<day)) >0){
                    word.setSpan(new ForegroundColorSpan(selectedTextColor),gen_array[day*2], gen_array[day*2+1],
                          Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    // Mark the day of the next alarm
                    if (day==indexOfNextDay){
                        word.setSpan(new UnderlineSpan(), gen_array[day*2], gen_array[day*2+1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }

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
        public final TextView AlarmLabel, WeekDays, AlarmTime, amPm24h;
        public final ImageView BellView, SnoozeIcon, VibrationIcon, MuteIcon;
        public final CheckBox AlarmActive;
        public final View itemView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public AlarmViewHolder(View _itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(_itemView);

            itemView = _itemView;

            BellView = itemView.findViewById(R.id.BellView);
            AlarmLabel = itemView.findViewById(R.id.AlarmLabel);
            WeekDays = itemView.findViewById(R.id.WeekDays);
            AlarmTime = itemView.findViewById(R.id.AlarmTime);
            AlarmActive = itemView.findViewById(R.id.AlarmActive);
            amPm24h = itemView.findViewById(R.id.am_pm_24h);
            SnoozeIcon = itemView.findViewById(R.id.Snooze_icon);
            VibrationIcon = itemView.findViewById(R.id.Vibrate_icon);
            MuteIcon = itemView.findViewById(R.id.Mute_icon);



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

            itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v) {
                    // Triggers click upwards to the adapter on click
                    if (clickLongListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            clickLongListener.onItemLongClick(itemView, position);
                        }
                    }
                    return true;
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