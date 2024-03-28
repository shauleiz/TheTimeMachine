package com.example.thetimemachine.UI;

import static androidx.core.content.ContextCompat.getSystemService;

import static com.example.thetimemachine.Application.TheTimeMachineApp.appContext;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.thetimemachine.AlarmService;
import com.example.thetimemachine.Application.TheTimeMachineApp;
import com.example.thetimemachine.R;

import java.util.Objects;


public class SettingsFragment extends PreferenceFragmentCompat
    implements SharedPreferences.OnSharedPreferenceChangeListener {

   MainActivity parent;
   Context context;

   static String ringRepeat;
   static String ringDuration;

   static String snoozeDuration;
   static String nHoursClock;
   static String firstDayWeek;
   static String vibratePattern;


   @Override
   public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {

      parent = (MainActivity)getActivity();
      setPreferencesFromResource(R.xml.preferences, rootKey);
      context = appContext;

     /* Preference  vibrationPreference = findPreference(context.getString(R.string.key_vibration_pattern));
      vibrationPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
         @Override
         public boolean onPreferenceClick(Preference p) {
            String key = p.getKey();
            Log.d("THE_TIME_MACHINE", "onPreferenceClick() : KEY= "+ key);
            return true;
         }
      });*/

     /* Context context = TheTimeMachineApp.appContext;
      findPreference(context.getString(R.string.key_h12_24)).setOnPreferenceClickListener(
            reference -> {
               Log.d("THE_TIME_MACHINE", "H12/H24 Preference: " + reference.getKey());
         return true;
      });*/

   }

   @Override
   public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
      super.onViewCreated(view, savedInstanceState);
      // Display the Up arrow
      ActionBar actionBar = Objects.requireNonNull(parent).getSupportActionBar();
      assert actionBar != null;
      actionBar.setHomeAsUpIndicator(R.drawable.arrow_back_fill0_wght400_grad0_opsz24);
      actionBar.setHomeActionContentDescription(R.string.description_up_arrow_back);
      actionBar.setDisplayHomeAsUpEnabled(true);
   }

   @Override
   public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
      // Called everytime one of the Shared Preferences (Setting) has changed
      // Get the shared preferences and using the KEY find its new value
      SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
      String newPref = preferences.getString(key, "");
      Log.d("THE_TIME_MACHINE", "onSharedPreferenceChanged() Called: KEY=" + key +" Value="+ newPref);

      if (key.equals(context.getString(R.string.key_ring_repeat))) ringRepeat = newPref;
      else
      if (key.equals(context.getString(R.string.key_ring_duration))) ringDuration = newPref;
      else
      if (key.equals(context.getString(R.string.key_snooze_duration))) snoozeDuration = newPref;
      else
      if (key.equals(context.getString(R.string.key_h12_24)))  nHoursClock = newPref;
      else
      if (key.equals(context.getString(R.string.key_first_day)))  firstDayWeek = newPref;
      else
      if (key.equals(context.getString(R.string.key_vibration_pattern))) {
         vibratePattern = newPref;
         vibrate(vibratePattern);
      }

      Log.d("THE_TIME_MACHINE", "onSharedPreferenceChanged() Called: KEY=" + key +" Value="+ newPref);

   }

   @Override
   public void onDestroy() {

      // Remove the Up arrow
      ActionBar actionBar = parent.getSupportActionBar();
      assert actionBar != null;
      actionBar.setDisplayHomeAsUpEnabled(false);

      super.onDestroy();
   }





   @Override
   public void onResume() {
      super.onResume();
      Objects.requireNonNull(getPreferenceManager().getSharedPreferences()).registerOnSharedPreferenceChangeListener(this);
   }

   @Override
   public void onPause() {
      super.onPause();
      Objects.requireNonNull(getPreferenceManager().getSharedPreferences()).unregisterOnSharedPreferenceChangeListener(this);
   }

   void vibrate(String pattern) {
      Vibrator vibrator =  (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
      if (vibrator.hasAmplitudeControl())
         Log.d("THE_TIME_MACHINE", "vibrate(): hasAmplitudeControl");
      else
         Log.d("THE_TIME_MACHINE", "vibrate(): No AmplitudeControl");


      final Runnable r = new Runnable() {
         public void run() {
            Log.d("THE_TIME_MACHINE", "vibrate(): Cancelling Vibrator");
            vibrator.cancel();
         }
      };

      // Vibrate
      AlarmService.VibrateEffect(getContext(), pattern);



      Log.d("THE_TIME_MACHINE", "vibrate(): Start Handler");
      // Call delayed stopping of the vibrator
      Handler handler = new Handler();
      handler.postDelayed(r, 5000);
   }

   // Preference: 24h/12h Clock
   // Returns:
   // True: 24h Clock (00:00 - 23:59)
   // False: 12h Clock (Am/Pm)
   public static boolean pref_is24HourClock(){
     // Log.d("THE_TIME_MACHINE", "pref_is24HourClock() Called[1]: KEY= key_h12_24" + " Value="+ nHoursClock);
      if (nHoursClock == null || nHoursClock.isEmpty()) {
         Context context = TheTimeMachineApp.appContext;
         SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
         nHoursClock = preferences.getString(context.getString(R.string.key_h12_24), "");
       //  Log.d("THE_TIME_MACHINE", "pref_is24HourClock() Called[1.5]: KEY= key_h12_24" + " Value="+ nHoursClock);
      }
    //  Log.d("THE_TIME_MACHINE", "pref_is24HourClock() Called[2]: KEY= key_h12_24" + " Value="+ nHoursClock);

      return nHoursClock.equals("h24");
   }

   // Preference: Duration of ringing until it stops
   // Returns:
   // Duration in milliseconds (Default is 30000)
   public static int pref_ring_duration(){
      //Log.d("THE_TIME_MACHINE", "pref_ring_duration() Called[1]: KEY= key_ring_duration" + " Value="+ ringDuration);

      if (ringDuration == null || ringDuration.isEmpty()) {
         Context context = TheTimeMachineApp.appContext;
         SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
         ringDuration = preferences.getString(context.getString(R.string.key_ring_duration), "");
        // Log.d("THE_TIME_MACHINE", "pref_ring_duration() Called[1.5]: KEY= key_ring_duration" + " Value="+ ringDuration);

         // String is of type 15000Seconds
         if (ringDuration.length() < 4)
            return 30000;
      }
      //Log.d("THE_TIME_MACHINE", "pref_ring_duration() Called[2]: KEY= key_ring_duration" + " Value="+ ringDuration);
      // Extract the numeral part and convert from seconds to milliseconds
      String intValue = ringDuration.replaceAll("[^0-9]", "");
      return Integer.parseInt(intValue)*1000;
   }

   // Preference: Duration of snooze
   // Returns:
   // Duration in milliseconds - Default is 60000 (1 Minute)
   public static int pref_snooze_duration() {
      Log.d("THE_TIME_MACHINE", "pref_snooze_duration() Called[1]: KEY= key_snooze_duration" + " Value="+ snoozeDuration);
      if (snoozeDuration == null || snoozeDuration.isEmpty()) {
         Context context = TheTimeMachineApp.appContext;
         SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
         snoozeDuration = preferences.getString(context.getString(R.string.key_snooze_duration), "");
         //Log.d("THE_TIME_MACHINE", "pref_snooze_duration() Called[1.5]: KEY= key_snooze_duration" + " Value="+ snoozeDuration);

         // String is of type 15000Seconds
         if (snoozeDuration.length() < 4)
            return 60000;
      }
      //Log.d("THE_TIME_MACHINE", "pref_snooze_duration() Called[2]: KEY= key_snooze_duration" + " Value="+ snoozeDuration);

      // Extract the numeral part and convert from seconds to milliseconds
      String intValue = snoozeDuration.replaceAll("[^0-9]", "");
      return Integer.parseInt(intValue)*1000;
   }

   // Preference: Number of times the alarm rings before it stops
   // Returns:
   // Number of times (Default is 5) - 100 means forever
   public static int pref_ring_repeat(){
      //Log.d("THE_TIME_MACHINE", "pref_ring_repeat() Called[1]: KEY= key_ring_repeat" + " Value="+ ringRepeat);
      if (ringRepeat == null || ringRepeat.isEmpty()) {
         Context context = TheTimeMachineApp.appContext;
         SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
         ringRepeat = preferences.getString(context.getString(R.string.key_ring_repeat), "");
         //Log.d("THE_TIME_MACHINE", "pref_ring_repeat() Called[1.5]: KEY= key_ring_repeat" + " Value="+ ringRepeat);
      }
    //  Log.d("THE_TIME_MACHINE", "pref_ring_repeat() Called[2]: KEY= key_ring_repeat" + " Value="+ ringRepeat);


      // String is of type 15000Seconds
      if (ringRepeat.length()<2)
         return 5;

      // Extract the numeral part
      String intValue = ringRepeat.replaceAll("[^0-9]", "");
      return Integer.parseInt(intValue);
   }

   // Preference: First Day of the week
   // Returns:
   // Su or Mo (Default is Su)
   public static String pref_first_day_of_week(){
     // Log.d("THE_TIME_MACHINE", "pref_first_day_of_week() Called[1]: KEY= key_first_day_week" + " Value="+ firstDayWeek);
      if (firstDayWeek == null || firstDayWeek.isEmpty()) {
         Context context = TheTimeMachineApp.appContext;
         SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
         firstDayWeek = preferences.getString(context.getString(R.string.key_first_day), "");
         //Log.d("THE_TIME_MACHINE", "pref_first_day_of_week() Called[1.5]: KEY= key_first_day_week" + " Value="+ firstDayWeek);
      }
     // Log.d("THE_TIME_MACHINE", "pref_first_day_of_week() Called[2]: KEY= key_first_day_week" + " Value="+ firstDayWeek);

      // String is Su or Mo
      if (firstDayWeek.length() != 2)
         return "Su";

      // Return 2 Char day
      return firstDayWeek;
   }

   public static  String pref_vibration_pattern(){
      if (vibratePattern == null || vibratePattern.isEmpty()){
         Context context = TheTimeMachineApp.appContext;
         SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
         vibratePattern = preferences.getString(context.getString(R.string.key_vibration_pattern), "");
      }



      if (vibratePattern.isEmpty())
            return "none";
         else
            return vibratePattern;
   }

}
