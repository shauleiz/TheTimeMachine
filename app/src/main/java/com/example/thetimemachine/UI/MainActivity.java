package com.example.thetimemachine.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.thetimemachine.AlarmViewModel;

import com.example.thetimemachine.R;


public class MainActivity extends AppCompatActivity {


    static final String action_delete = "ACTION_DELETE";
    static final String action_edit = "ACTION_EDIT";

    // ViewModel object of class MyViewModel
    // Holds all UI variables related to this activity
    public AlarmViewModel alarmViewModel;
    private boolean deleteAction = false;
    private boolean editAction = false;

    public MainActivity() {
        super(R.layout.activity_main);
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(action_delete, deleteAction);
        outState.putBoolean(action_edit, editAction);

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

    public boolean isDeleteAction() {return deleteAction;}

    public boolean isEditAction() {return editAction;}

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem deleteItem = menu.findItem(R.id.delete);
        deleteItem.setVisible(deleteAction);
        MenuItem editItem = menu.findItem(R.id.edit);
        editItem.setVisible(editAction);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.fragment_container_view);

        // Setup Fragment - Only Setting is supported
        if (frag instanceof SetUpAlarmFragment) {
            Log.v("THE_TIME_MACHINE", "SetUpAlarmFragment");
            if (itemId == R.id.settings) {
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
                return true;
            }
        }
        else
            Log.v("THE_TIME_MACHINE", "??? Fragment");


        // The user's action isn't recognized.
        // Invoke the superclass to handle it.
        Log.d("THE_TIME_MACHINE", "??? Pressed");
        return super.onOptionsItemSelected(item);
    }

}