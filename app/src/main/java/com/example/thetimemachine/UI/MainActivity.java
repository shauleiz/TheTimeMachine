package com.example.thetimemachine.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.example.thetimemachine.AlarmViewModel;

import com.example.thetimemachine.R;


public class MainActivity extends AppCompatActivity {

    public MainActivity() {
        super(R.layout.activity_main);
    }

    // ViewModel object of class MyViewModel
    // Holds all UI variables related to this activity
    public AlarmViewModel alarmViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create/acquire the ViewModel object of class AlarmViewModel
        alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);

        // Display Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, AlarmListFragment.class, null)
                    //.addToBackStack("tag1")
                    .commit();
      }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}