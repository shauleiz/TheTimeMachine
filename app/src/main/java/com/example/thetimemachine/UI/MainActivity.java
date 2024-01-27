package com.example.thetimemachine.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.thetimemachine.AlarmViewModel;

import com.example.thetimemachine.Application.TheTimeMachineApp;
import com.example.thetimemachine.R;


public class MainActivity extends AppCompatActivity {


    static final String action_delete = "ACTION_DELETE";
    static final String action_edit = "ACTION_EDIT";
    static final String action_settings = "ACTION_SETTINGS";


    // ViewModel object of class MyViewModel
    // Holds all UI variables related to this activity
    public AlarmViewModel alarmViewModel;
    private boolean deleteAction = false;
    private boolean editAction = false;
    private boolean settingsAction = true;

    public MainActivity() {
        super(R.layout.activity_main);
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(action_delete, deleteAction);
        outState.putBoolean(action_edit, editAction);
        outState.putBoolean(action_settings, settingsAction);

        //Toast.makeText(getApplicationContext(), "MainActivity::onSaveInstanceState Called", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Brute Force removal of Room database
        // getApplication().getApplicationContext().deleteDatabase("raw_alarm_database");

        //Toast.makeText(getApplicationContext(), "MainActivity::onCreate Called", Toast.LENGTH_SHORT).show();


        // Create/acquire the ViewModel object of class AlarmViewModel
        alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);

        // Toolbar: App name + Icons
        Toolbar AppToolbar =  findViewById(R.id.app_toolbar);
        AppToolbar.setTitle("Alarm Clock"); // TODO: Change later - Each fragment should modify this text
        setSupportActionBar(AppToolbar);

        if (savedInstanceState!=null) {
            deleteAction = savedInstanceState.getBoolean(action_delete, false);
            editAction = savedInstanceState.getBoolean(action_edit, false);
            settingsAction =  savedInstanceState.getBoolean(action_settings,false);
            //Toast.makeText(getApplicationContext(), "MainActivity::onCreate Called - deleteAction=" + deleteAction, Toast.LENGTH_SHORT).show();
        }

       // Display Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, AlarmListFragment.class, null)
                    //.addToBackStack("tag1")
                    .commit();
      }
/*
        getSupportFragmentManager()
              .beginTransaction()
              .replace(R.id.fragment_container_view, new SettingsFragment())
              .commit();
*/

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    //// Option Menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_single, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void UpdateOptionMenu(){
        // Modify toolbar according to number of selected items
        setSettingsAction(true);
        int len = alarmViewModel.getNofSelectedItems();
        if (len == 0){
            setDeleteAction(false);
            setEditAction( false);
        } else if (len == 1) {
            setDeleteAction(true);
            setEditAction( true);
        } else {
            setDeleteAction(true);
            setEditAction( false);
        }
        invalidateOptionsMenu();
    }

    public void setDeleteAction(boolean d){deleteAction = d;}

    public void setEditAction(boolean editAction) {this.editAction = editAction;}

    public void setSettingsAction(boolean settingsAction){this.settingsAction = settingsAction;}

    public boolean isDeleteAction() {return deleteAction;}

    public boolean isEditAction() {return editAction;}

    public boolean isSettingsAction() {return settingsAction;}

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem deleteItem = menu.findItem(R.id.delete);
        deleteItem.setVisible(deleteAction);
        MenuItem editItem = menu.findItem(R.id.edit);
        editItem.setVisible(editAction);
        MenuItem settingsItem =  menu.findItem(R.id.settings);
        settingsItem.setVisible(settingsAction);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fragment_container_view);

        // Setup Fragment - Only Setting is supported
        if (frag instanceof SetUpAlarmFragment) {
            Log.d("THE_TIME_MACHINE", "SetUpAlarmFragment");
            if (itemId == R.id.settings) {
                Settings();
                return true;
            }
        }

        // Alarm List Fragment - Act according to selected item
        else if (frag instanceof AlarmListFragment) {
            Log.v("THE_TIME_MACHINE", "AlarmListFragment");
            if (itemId == R.id.delete) {
                ((AlarmListFragment) frag).DeleteSelectedAlarms();
                return true;
            } else if (itemId == R.id.edit) {
                ((AlarmListFragment) frag).EditSelectedAlarm();
                return true;
            } else if (itemId == R.id.settings) {
                Settings();
                return true;
            }
        }
        else if (itemId == android.R.id.home){
            Log.d("THE_TIME_MACHINE", "UP was selected");
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        } else
            Log.v("THE_TIME_MACHINE", "??? Fragment");


        // The user's action isn't recognized.
        // Invoke the superclass to handle it.
        Log.d("THE_TIME_MACHINE", "??? Pressed");
        return super.onOptionsItemSelected(item);
    }

    private void Settings(){

        // Toolbar: Title
        Toolbar AppToolbar = findViewById(R.id.app_toolbar);
        AppToolbar.setTitle(R.string.settings_title);
        setSupportActionBar(AppToolbar);

        setDeleteAction(false);
        setEditAction( false);
        setSettingsAction(false);

        getSupportFragmentManager()
              .beginTransaction()
              .replace(R.id.fragment_container_view, new SettingsFragment())
              .addToBackStack("tag_settings")
              .commit();
    }

    public static boolean is24HourClock(){
        Context context = TheTimeMachineApp.appContext;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String s = preferences.getString(context.getString(R.string.key_h12_24), "");
        if (s.equals("h24"))
            return (true);
        else
            return (false);
    }

}