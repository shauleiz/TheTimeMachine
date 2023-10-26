package com.example.thetimemachine;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FragmentAlarmList extends Fragment {
    public FragmentAlarmList() {
        super(R.layout.fragment_alarm_list);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
       super.onViewCreated(view, savedInstanceState);

    }
}
