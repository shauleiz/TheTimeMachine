package com.product.thetimemachine.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.product.thetimemachine.AlarmViewModel;
import com.product.thetimemachine.R;


public class MainActivity extends AppCompatActivity {


    static final String action_delete = "ACTION_DELETE";
    static final String action_edit = "ACTION_EDIT";
    static final String action_settings = "ACTION_SETTINGS";
    static final String action_duplicate = "ACTION_DUPLICATE";
    static final String action_checkmark = "ACTION_CHECKMARK";


    // ViewModel object of class MyViewModel
    // Holds all UI variables related to this activity
    public AlarmViewModel alarmViewModel;
    private boolean deleteAction = false;
    private boolean editAction = false;
    private boolean duplicateAction = false;
    private boolean checkmarkAction = false;
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
        outState.putBoolean(action_duplicate, duplicateAction);
        outState.putBoolean(action_checkmark, checkmarkAction);

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
            duplicateAction = savedInstanceState.getBoolean(action_duplicate,false);
            checkmarkAction = savedInstanceState.getBoolean(action_checkmark,false);
            //Toast.makeText(getApplicationContext(), "MainActivity::onCreate Called - deleteAction=" + deleteAction, Toast.LENGTH_SHORT).show();
        }

        // Set the default volume control as ALARM volume control
        setVolumeControlStream(AudioManager.STREAM_ALARM);

       // Display Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, AlarmListFrag.class, null)
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
        setCheckmarkAction(false);
        int len = alarmViewModel.getNofSelectedItems();
        if (len == 0){
            setDeleteAction(false);
            setEditAction( false);
            setDuplicateAction(false);
        } else if (len == 1) {
            setDeleteAction(true);
            setEditAction( true);
            setDuplicateAction(true);
        } else {
            setDeleteAction(true);
            setEditAction( false);
            setDuplicateAction(false);
        }
        invalidateOptionsMenu();
    }

    public void setDeleteAction(boolean d){deleteAction = d;}

    public void setEditAction(boolean editAction) {this.editAction = editAction;}
    public void setDuplicateAction(boolean duplicateAction) {this.duplicateAction = duplicateAction;}
    public void setCheckmarkAction(boolean checkmarkAction) {this.checkmarkAction = checkmarkAction;}

    public void setSettingsAction(boolean settingsAction){this.settingsAction = settingsAction;}

    public boolean isDeleteAction() {return deleteAction;}

    public boolean isEditAction() {return editAction;}
    public boolean isDuplicateAction() {return duplicateAction;}

    public boolean isSettingsAction() {return settingsAction;}

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem deleteItem = menu.findItem(R.id.delete);
        deleteItem.setVisible(deleteAction);
        MenuItem editItem = menu.findItem(R.id.edit);
        editItem.setVisible(editAction);
        MenuItem duplicateItem = menu.findItem(R.id.duplicate);
        duplicateItem.setVisible(duplicateAction);
        MenuItem settingsItem =  menu.findItem(R.id.settings);
        settingsItem.setVisible(settingsAction);
        MenuItem checkmarkItem = menu.findItem(R.id.checkmark);
        checkmarkItem.setVisible(checkmarkAction);
        return super.onPrepareOptionsMenu(menu);
    }
    /**/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fragment_container_view);

        /**/
        // Setup Fragment - Only Setting is supported
        if (/*frag instanceof SetUpAlarmFragment ||*/ frag instanceof AlarmEditFrag) {
            Log.d("THE_TIME_MACHINE", "SetUpAlarmFragment");
            if (itemId == R.id.settings) {
                Settings();
                return true;
            } else if (itemId == android.R.id.home) {
                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                return true;
            } else if (itemId == R.id.checkmark) {
               assert frag instanceof AlarmEditFrag;
               ((AlarmEditFrag) frag).checkmarkClicked();
                return true;
            }
        }


        // Alarm List Fragment - Act according to selected item
        else if (frag instanceof AlarmListFrag) {
            Log.v("THE_TIME_MACHINE", "AlarmListFrag");
            if (itemId == R.id.delete) {
                ((AlarmListFrag) frag).deleteSelectedAlarms();
                return true;
            } else if (itemId == R.id.edit) {
                ((AlarmListFrag) frag).editSelectedAlarm();
                return true;
            } else if (itemId == R.id.duplicate) {
                ((AlarmListFrag) frag).duplicateSelectedAlarm();
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
        setDuplicateAction(false);
        setCheckmarkAction(false);

        getSupportFragmentManager()
              .beginTransaction()
              .replace(R.id.fragment_container_view, new SettingsFrag())
              //.replace(R.id.fragment_container_view, new SettingsFragment())
              .addToBackStack("tag_settings")
              .commit();
    }



}