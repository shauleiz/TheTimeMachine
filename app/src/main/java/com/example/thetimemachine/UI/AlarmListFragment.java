package com.example.thetimemachine.UI;

import static android.Manifest.permission.POST_NOTIFICATIONS;
import static androidx.core.content.PermissionChecker.PERMISSION_DENIED;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thetimemachine.AlarmReceiver;
import com.example.thetimemachine.AlarmService;
import com.example.thetimemachine.Data.AlarmItem;
import com.example.thetimemachine.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class AlarmListFragment extends Fragment {


    private FloatingActionButton AddAlarm_Button;
    private RecyclerView rvAlarms;
    private MainActivity parent;
    private AlarmAdapter alarmAdapter;

    private List<AlarmItem> alarmList;

    private View fragmentView;

    private ArrayList<Integer> selectedItems;

    // Required empty public constructor
    public AlarmListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parent = (MainActivity)getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create Adapter for the Recycler View
        alarmList = parent.alarmViewModel.getAlarmList().getValue();
        alarmAdapter = new AlarmAdapter(alarmList);

        // Get the list of selected alarm items
        if (savedInstanceState!=null) {
            selectedItems = savedInstanceState.getIntegerArrayList("ARRAY_SI");
        }
        else {
            selectedItems = new ArrayList<>();
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alarm_list, container, false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList("ARRAY_SI", selectedItems);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentView = view;

        // Toolbar: Title + Menu
        Toolbar AppToolbar = requireActivity().findViewById(R.id.app_toolbar);
        AppToolbar.setTitle(R.string.alarmlist_title);
        ((AppCompatActivity)getActivity()).setSupportActionBar(AppToolbar);
        parent.UpdateOptionMenu();

        // Add Alarm button (FAB)
        AddAlarm_Button = view.findViewById(R.id.Add_Alarm_fab);
        AddAlarm_Button.setOnClickListener(v -> AddAlarmClicked());

        // Recyclerview widget - List of alarms
        rvAlarms = view.findViewById(R.id.alarmListRecyclerView);
        // Set layout manager to position the items
        rvAlarms.setLayoutManager(new LinearLayoutManager(getContext()));
        // Attach the adapter to the recyclerview to populate items
        // (The Adapter was created previously in onCreateView())
        rvAlarms.setAdapter(alarmAdapter);

        // Create observer to inform recycler view that there was a change in the list
        parent.alarmViewModel.getAlarmList().observe(getViewLifecycleOwner(), new Observer<List<AlarmItem>>() {
            @Override
            public void onChanged(List<AlarmItem> m) {
                if (m != null) {
                    alarmList = m;
                    alarmAdapter.UpdateAlarmAdapter(m);
                }
            }
        });

        parent.alarmViewModel.getSelectedItems().observe(getViewLifecycleOwner(), new Observer<ArrayList<Integer>>() {
            @Override
            public void onChanged(ArrayList<Integer> m) {
                if (m != null) {
                    // Modify Alarm List
                    selectedItems = m;
                    alarmAdapter.UpdateAlarmAdapter(m);
                    // Modify toolbar according to number of selected items
                    parent.UpdateOptionMenu();
                }
            }
        });



        // Define the Item Click listener - What to do when user clicks on an alarm item
        // Test what was clicked: Compare ID of view to known item elements
        // If none found then it means that the item itself was clicked
        alarmAdapter.setOnItemClickListener(new AlarmAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                int id = view.getId();
                if (id == R.id.AlarmActive) {// Alarm Active checkbox has been clicked
                    ActiveCheckboxChanged( view,  position);
                }
                else{ // The Alarm item itself has been clicked
                    String label = alarmList.get(position).getLabel();
                    String alarmTime = String.format( Locale.US,"%d:%02d", alarmList.get(position).getHour(), alarmList.get(position).getMinute());
                    Toast.makeText(getContext(), "Alarm Clicked: " + label + ": Time: " + alarmTime, Toast.LENGTH_SHORT).show();
                    AlarmItemEdit(alarmList.get(position));
                }
            }
        });

        alarmAdapter.setOnItemLongClickListener(new AlarmAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position) {
                int id = view.getId();
                if (id == R.id.AlarmActive) {// Alarm Active checkbox has been clicked
                    ActiveCheckboxChanged( view,  position);
                }
                else{ // The Alarm item itself has been clicked
                    String label = alarmList.get(position).getLabel();
                    String alarmTime = String.format( Locale.US,"%d:%02d", alarmList.get(position).getHour(), alarmList.get(position).getMinute());
                    Toast.makeText(getContext(), "Alarm Long Clicked: " + label + ": Time: " + alarmTime, Toast.LENGTH_SHORT).show();
                    AlarmItemLongClicked(alarmList.get(position).getCreateTime());
                }
            }
        });
        // Decoration
        RecyclerView.ItemDecoration itemDecoration = new  DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        rvAlarms.addItemDecoration(itemDecoration);
    }


    // Called while initializing the activity.
    // Checks if Notification is enabled
    // If not enabled - launches request for permission that defines the callback to run
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void CheckPermissions(){
        int permission = ContextCompat.checkSelfPermission(parent, POST_NOTIFICATIONS);
        if (permission == PERMISSION_GRANTED) {
            Log.i("THE_TIME_MACHINE", "POST_NOTIFICATIONS Permission Granted");
            //AddAlarm_Button.setEnabled(true);;
        }
        if (permission == PERMISSION_DENIED) {
            Log.i("THE_TIME_MACHINE", "POST_NOTIFICATIONS Permission Denied");
            boolean shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(parent, POST_NOTIFICATIONS);
            Log.i("THE_TIME_MACHINE", "POST_NOTIFICATIONS Should Show Request Permission - " + shouldShow);
            if (shouldShow) {
                // Need to show a pop-up window that explains why it is important to grant permissions
                // Display the pop-up window
                PopupWindow popupWindow =  displayPopUpNotifPemis();

                // Define action to do when pop-up window is dismissed -
                // Request permission to show notifications
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        Log.i("THE_TIME_MACHINE", "onDismiss called - Requesting permission");
                        requestPermissionLauncher.launch(POST_NOTIFICATIONS);
                    }
                });
            }
           else {
               // Request permission to show notifications (when pop-up window is not shown)
               Log.i("THE_TIME_MACHINE", "Requesting permission");
               requestPermissionLauncher.launch(POST_NOTIFICATIONS);
            }
        }
    }


    // Defines the Request Permission Launcher
    // Launches the permission request dialog box for POST_NOTIFICATIONS
    // Gets the result Granted (true/false) and sets variable  notificationPermission
    private final ActivityResultLauncher<String> requestPermissionLauncher =
          registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission is granted - Add Alarm button is enabled
                        Log.i("THE_TIME_MACHINE", "Activity Result - Granted");
                        //AddAlarm_Button.setEnabled(true);

                    } else {
                        // Permission was NOT granted - Add Alarm button becomes disabled
                        Log.i("THE_TIME_MACHINE", "Activity Result - NOT Granted");
                        //AddAlarm_Button.setEnabled(false);

                        // Explain to the user that the feature is unavailable because the
                        // feature requires a permission that the user has denied. At the
                        // same time, respect the user's decision. Don't link to system
                        // settings in an effort to convince the user to change their
                        // decision.
                    }

                });

    private PopupWindow displayPopUpNotifPemis(){
        LayoutInflater inflater = (LayoutInflater) parent.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_notification_permission, (ViewGroup) fragmentView);

        // Create the PopupWindow object and set its content view to the inflated layout
        PopupWindow popupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.WRAP_CONTENT,
              RelativeLayout.LayoutParams.WRAP_CONTENT);

        ((ViewGroup) fragmentView.getParent()).removeView(popupView);
        popupWindow.setFocusable(true);

        popupWindow.showAtLocation(fragmentView, Gravity.CENTER, 0, 0);

        Button btnClose = popupView.findViewById(R.id.button_ok_notif);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        return popupWindow;
    }

    // Get Item's info from Alarm list
    // Get new state of checkbox
    // Send update to this item down to the ViewModel
    // Schedule/cancel alarm
    void ActiveCheckboxChanged(View view, int position) {

        // Get alarm item data from list
        Bundle b = alarmList.get(position).getBundle();

        // Create a new alarm, set active
        AlarmItem item = new AlarmItem(b);
        // Get 'active' checkbox state and insert it to the Alarm
        boolean active = ((CheckBox)view).isChecked();
        item.setActive(active);
        // Update View Model
        parent.alarmViewModel.UpdateAlarm(item);

        // Schedule/Cancel Alarm
        item.Exec();

        // Stop ringing if unchecked
        if (!active){
            Context context = requireContext();
            Intent stopIntent = new Intent(context, AlarmService.class);
            stopIntent.putExtras(b);
            AlarmReceiver.stopping(context, stopIntent );
        }


    }
    /*
     *   Called when user clicks on Alarm item in recycler or toolbar Edit action
     *   Copy values from the selected item to be used by the setup fragment
     *   Create a bundle with data to be passed to the setup fragment
     *   Replace this fragment by setup fragment
     */
    void AlarmItemEdit(AlarmItem item){

        if (item == null) return;

        // Copy values from the selected item to be used by the setup fragment
        parent.alarmViewModel.setUpAlarmValues.GetValuesFromList(item);

        // Passing parameters to setup fragment
        Bundle b = new Bundle();
        b.putBoolean("INIT_NEWALARM",false);
        b.putInt("INIT_POSITION", 0);
        List<AlarmItem> alarmItems = parent.alarmViewModel.getAlarmList().getValue();
        if (alarmItems==null)return;
        b.putLong("INIT_CREATE_TIME", item.getCreateTime());

        // Replace current fragment with the Setup Alarm fragment
        parent = (MainActivity) getActivity();
        if (parent != null)
            parent.getSupportFragmentManager().
                  beginTransaction().
                  replace(R.id.fragment_container_view, SetUpAlarmFragment.class, b).
                  addToBackStack("tag2").
                  commit();
    }

    /* When an item is Long-Clicked:
      - The item is selected/deselected (Visually)
      - The App bar may take one of the following modes:
      -- Basic: 0 items selected
      -- Single: 1 item selected
      -- Multi: 2 or more items selected
     */
    void AlarmItemLongClicked(long id) {

        // get the list of alarms
        //List<AlarmItem> alarmItems = parent.alarmViewModel.getAlarmList().getValue();
        //if (alarmItems==null) return;

        // Update the list of the selected items - simply toggle
        parent.alarmViewModel.toggleSelection(id);

        // Modify toolbar according to number of selected items
        parent.UpdateOptionMenu();
    }


    /* *
    * Called when user clicks on Add Alarm button
    * Resets values to be used by the setup fragment
    * Create a bundle with data to be passed to the setup fragment
    * Replace this fragment by setup fragment
    * */
    void AddAlarmClicked()
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            CheckPermissions();
        }

        // Reset the setup alarm values
        parent.alarmViewModel.setUpAlarmValues.ResetValues();

        // Passing parameters to setup fragment
        Bundle b = new Bundle();
        b.putBoolean("INIT_NEWALARM",true);

        // Replace current fragment with the Setup Alarm fragment
        parent = (MainActivity) getActivity();
        if (parent != null)
            parent.getSupportFragmentManager().
                beginTransaction().
                replace(R.id.fragment_container_view, SetUpAlarmFragment.class, b).
                    addToBackStack("tag2").
                    commit();
    }

    public void DeleteSelectedAlarms(){

        ArrayList<Integer> tempList;
        tempList = new ArrayList<>(selectedItems);

        for (int i=0 ; i<tempList.size();i++) {
            AlarmItem item = parent.alarmViewModel.getAlarmItemById(tempList.get(i));
            if (item == null) return;
            parent.alarmViewModel.DeleteAlarm(item);
        }
    }

    public void EditSelectedAlarm(){

        // Edit only is exactly one item selected
        if (selectedItems.size() !=1 ) return;
        AlarmItemEdit( parent.alarmViewModel.getAlarmItemById(selectedItems.get(0)));
    }

}