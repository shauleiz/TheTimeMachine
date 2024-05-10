package com.product.thetimemachine.UI;

import static com.product.thetimemachine.Application.TheTimeMachineApp.appContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.product.thetimemachine.AlarmViewModel;
import com.product.thetimemachine.R;

import java.util.Objects;

public class ItemSettingsFragment extends PreferenceFragmentCompat
      implements SharedPreferences.OnSharedPreferenceChangeListener {

   MainActivity parent;
   Context context;
   SharedPreferences preferences;
   AlarmViewModel.SetUpAlarmValues setUpAlarmValues;

   @Override
   public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
      // Init important global parameters
      context = appContext;
      parent = (MainActivity)getActivity();
      setUpAlarmValues = parent.alarmViewModel.setUpAlarmValues;

      // Set the current SharedPreferences file name
      getPreferenceManager().setSharedPreferencesName(getString(R.string.ITEM_PREF_FILENAME));

      // Load the preferences XML (Global XML - irrelevant entries will be hidden in onCreateView()
      setPreferencesFromResource(R.xml.preferences, rootKey);

      // Get the SharedPreferences global parameter
      preferences = context.getSharedPreferences(getString(R.string.ITEM_PREF_FILENAME), Context.MODE_PRIVATE);
      // Populate Item Preferences - data is passed through setUpAlarmValues
      //preferences.edit().putString(context.getString(R.string.key_snooze_duration),setUpAlarmValues.getSnoozeDuration().getValue());
      //preferences.edit().commit();


   }

   @Override
   public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
      // Copy preferences to SetupAlarmValues
      setUpAlarmValues.setSnoozeDuration(preferences.getString(context.getString(R.string.key_snooze_duration), ""));

   }
   @NonNull
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

      // Hide irrelevant categories
      PreferenceCategory mCategory;
      mCategory= (PreferenceCategory) findPreference(getString(R.string.key_time_format));
      mCategory.setVisible(false);
      mCategory = (PreferenceCategory) findPreference(getString(R.string.key_alarm_list));
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
      setUpAlarmValues.setSnoozeDuration(preferences.getString(context.getString(R.string.key_snooze_duration), ""));

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
