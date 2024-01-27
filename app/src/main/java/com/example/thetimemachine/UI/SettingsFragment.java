package com.example.thetimemachine.UI;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.preference.PreferenceFragmentCompat;

import com.example.thetimemachine.R;

public class SettingsFragment extends PreferenceFragmentCompat {

   MainActivity parent;
   @Override
   public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
      setPreferencesFromResource(R.xml.preferences, rootKey);

      // Display the Up arrow
      parent = (MainActivity)getActivity();
      ActionBar actionBar = parent.getSupportActionBar();
      actionBar.setHomeAsUpIndicator(R.drawable.arrow_back_fill0_wght400_grad0_opsz24);
      actionBar.setHomeActionContentDescription(R.string.description_up_arrow_back);
      actionBar.setDisplayHomeAsUpEnabled(true);
   }

   @Override
   public void onDestroy() {

      // Remove the Up arrow
      ActionBar actionBar = parent.getSupportActionBar();
      actionBar.setDisplayHomeAsUpEnabled(false);

      super.onDestroy();
   }
}
