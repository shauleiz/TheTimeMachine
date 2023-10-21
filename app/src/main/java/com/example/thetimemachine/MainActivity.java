package com.example.thetimemachine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    // ViewModel object of class MyViewModel
    // Holds all UI variables related to this activity
    AlarmViewModel alarmViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create/acquire the ViewModel object of class AlarmViewModel
        alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);

        // Display the main Activity window
        setContentView(R.layout.activity_main);

        // Add alarm floating button
        // Get it from the XML file then define what it does when clicked
        FloatingActionButton AddAlarm_Button = findViewById(R.id.Add_Alarm_fab);
        AddAlarm_Button.setOnClickListener(v -> alarmViewModel.AddAlarm());
    }
}