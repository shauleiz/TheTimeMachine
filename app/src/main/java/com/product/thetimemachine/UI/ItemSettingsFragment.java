package com.product.thetimemachine.UI;

import static com.product.thetimemachine.Application.TheTimeMachineApp.appContext;
import static com.product.thetimemachine.UI.SettingsFragment.sound;
import static com.product.thetimemachine.UI.SettingsFragment.vibrate;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

import com.product.thetimemachine.AlarmViewModel;
import com.product.thetimemachine.R;

import java.util.Objects;

public class ItemSettingsFragment extends PreferenceFragmentCompat
      implements SharedPreferences.OnSharedPreferenceChangeListener {

   MainActivity parent;
   Context context;
   SharedPreferences preferences;
   AlarmViewModel.SetUpAlarmValues setUpAlarmValues;
   AlarmViewModel alarmViewModel;

   String getItemPrefFileName(){
      return appContext.getPackageName()+"_"+getString(R.string.ITEM_PREF_FILENAME);
   }
   @Override
   public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
      // Init important global parameters
      context = appContext;



      // Create/acquire the ViewModel object of class AlarmViewModel
      alarmViewModel = new ViewModelProvider(requireActivity()).get(AlarmViewModel.class);
      setUpAlarmValues = alarmViewModel.setUpAlarmValues;

      // Set the current SharedPreferences file name
      getPreferenceManager().setSharedPreferencesName(getItemPrefFileName());

      // Get the SharedPreferences global parameter
      preferences = context.getSharedPreferences(getItemPrefFileName(), Context.MODE_PRIVATE);

      // Copy the preferences for this item from AlarmItem to Item Preferences
      SetItemPreferences();

      // Load the preferences XML (Global XML - irrelevant entries will be hidden in onCreateView()
      setPreferencesFromResource(R.xml.preferences, rootKey);

   }

   private void SetItemPreferences() {
      // Get the Item Preferences file
      Context context = getContext();
      SharedPreferences preferences = context.getSharedPreferences(getItemPrefFileName(), Context.MODE_PRIVATE);
      // Create an Editor
      SharedPreferences.Editor editor = preferences.edit();

      // Copy preferences
      editor.putString(context.getString(R.string.key_ring_duration), setUpAlarmValues.getRingDuration().getValue());
      editor.putString(context.getString(R.string.key_ring_repeat), setUpAlarmValues.getRingRepeat().getValue());
      editor.putString(context.getString(R.string.key_snooze_duration), setUpAlarmValues.getSnoozeDuration().getValue());
      editor.putString(context.getString(R.string.key_vibration_pattern), setUpAlarmValues.getVibrationPattern().getValue());
      editor.putString(context.getString(R.string.key_alarm_sound), setUpAlarmValues.getAlarmSound().getValue());
      editor.putString(context.getString(R.string.key_gradual_volume), setUpAlarmValues.getGradualVolume().getValue());

      // Now, apply changes
      editor.apply();
   }

   @Override
   public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
      // Copy preferences to SetupAlarmValues
      if (key.equals(context.getString(R.string.key_ring_duration)))
         setUpAlarmValues.setRingduration(preferences.getString(key, ""));
      else if (key.equals(context.getString(R.string.key_ring_repeat)))
         setUpAlarmValues.setRingRepeat(preferences.getString(key, ""));
      else if (key.equals(context.getString(R.string.key_snooze_duration)))
         setUpAlarmValues.setSnoozeDuration(preferences.getString(key, ""));
      else if (key.equals(context.getString(R.string.key_vibration_pattern))) {
         setUpAlarmValues.setVibrationPattern(preferences.getString(key, ""));
         vibrate(preferences.getString(key, ""));
      }
      else if (key.equals(context.getString(R.string.key_alarm_sound))) {
         setUpAlarmValues.setAlarmSound(preferences.getString(key, ""));
         sound(preferences.getString(key, ""));
      }
      else if (key.equals(context.getString(R.string.key_gradual_volume)))
         setUpAlarmValues.setGradualVolume(preferences.getString(key, ""));
   }
   @NonNull
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

      // Hide irrelevant categories
      PreferenceCategory mCategory;
      mCategory= findPreference(getString(R.string.key_time_format));
      mCategory.setVisible(false);
      mCategory = findPreference(getString(R.string.key_alarm_list));
      mCategory.setVisible(false);


      return super.onCreateView(inflater, container, savedInstanceState);
   }

   @Override
   public void onResume() {
      super.onResume();
      Objects.requireNonNull(getPreferenceManager().getSharedPreferences()).registerOnSharedPreferenceChangeListener(this);
   }

   @Override
   public void onStop() {

      // Copy preferences to SetupAlarmValues
      setUpAlarmValues.setRingduration(preferences.getString(context.getString(R.string.key_ring_duration), ""));
      setUpAlarmValues.setRingRepeat(preferences.getString(context.getString(R.string.key_ring_repeat), ""));
      setUpAlarmValues.setSnoozeDuration(preferences.getString(context.getString(R.string.key_snooze_duration), ""));
      setUpAlarmValues.setVibrationPattern(preferences.getString(context.getString(R.string.key_vibration_pattern), ""));
      setUpAlarmValues.setAlarmSound(preferences.getString(context.getString(R.string.key_alarm_sound), ""));
      setUpAlarmValues.setGradualVolume(preferences.getString(context.getString(R.string.key_gradual_volume), ""));

      // Clear the preferences  - no need to keep them
      preferences.edit().clear().apply();

      super.onStop();
   }

   @Override
   public void onPause() {
      super.onPause();
      Objects.requireNonNull(getPreferenceManager().getSharedPreferences()).unregisterOnSharedPreferenceChangeListener(this);
   }
}
