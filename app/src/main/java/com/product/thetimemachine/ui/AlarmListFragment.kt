package com.product.thetimemachine.ui

import android.Manifest.permission
import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.PopupWindow
import android.widget.RelativeLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.material3.Text
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.product.thetimemachine.AlarmReceiver
import com.product.thetimemachine.AlarmService
import com.product.thetimemachine.Data.AlarmItem
import com.product.thetimemachine.R

class AlarmListFragment : Fragment() {
    private var parent: MainActivity? = null
    private var alarmAdapter: AlarmAdapter? = null
    private var alarmList: List<AlarmItem>? = null
    private var fragmentView: View? = null
    private var selectedItems: ArrayList<Int>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parent = activity as MainActivity?
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = super.onCreateView(inflater, container, savedInstanceState)
        // Create Adapter for the Recycler View

        alarmList = parent!!.alarmViewModel.alarmList.value
        alarmAdapter = AlarmAdapter(alarmList)

        // Get the list of selected alarm items
        selectedItems = if (savedInstanceState != null) {
            savedInstanceState.getIntegerArrayList("ARRAY_SI")
        } else {
            ArrayList()
        }

        return ComposeView(requireContext()).apply {
            setContent {
                Text(text = "Hello world.")
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntegerArrayList("ARRAY_SI", selectedItems)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentView = view

        // Toolbar: Title + Menu
        val AppToolbar = requireActivity().findViewById<Toolbar>(R.id.app_toolbar)
        AppToolbar.setTitle(R.string.alarmlist_title)
        (requireActivity() as AppCompatActivity).setSupportActionBar(AppToolbar)
        parent!!.UpdateOptionMenu()

        /*// Add Alarm button (FAB)
        val addAlarm_Button = view.findViewById<FloatingActionButton>(R.id.Add_Alarm_fab)
        addAlarm_Button.setOnClickListener { v: View? -> AddAlarmClicked() }

        // Recyclerview widget - List of alarms
        val rvAlarms = view.findViewById<RecyclerView>(R.id.alarmListRecyclerView)
        // Set layout manager to position the items
        rvAlarms.layoutManager = LinearLayoutManager(context)
        // Attach the adapter to the recyclerview to populate items
        // (The Adapter was created previously in onCreateView())
        rvAlarms.adapter = alarmAdapter*/

        // Create observer to inform recycler view that there was a change in the list
        /*
        parent!!.alarmViewModel.alarmList.observe(
            viewLifecycleOwner,
            object : Observer<List<AlarmItem?>?> {
                override fun onChanged(m: List<AlarmItem>) {
                    if (m != null) {
                        alarmList = m
                        sortAlarmList(
                            SettingsFragment.pref_sort_type(),
                            SettingsFragment.pref_sort_separate()
                        )
                        alarmAdapter!!.UpdateAlarmAdapter(m)
                    }
                }
            })
        */

        /*
        parent!!.alarmViewModel.selectedItems.observe(
            viewLifecycleOwner,
            object : Observer<ArrayList<Int?>?> {
                override fun onChanged(m: ArrayList<Int>) {
                    if (m != null) {
                        // Modify Alarm List
                        selectedItems = m
                        alarmAdapter!!.UpdateAlarmAdapter(m)
                        // Modify toolbar according to number of selected items
                        parent!!.UpdateOptionMenu()
                    }
                }
            })
            */

        // Define the Item Click listener - What to do when user clicks on an alarm item
        // Test what was clicked: Compare ID of view to known item elements
        // If none found then it means that the item itself was clicked
        alarmAdapter!!.setOnItemClickListener { view, position ->
            val id = view.id
            if (id == R.id.AlarmActive) { // Alarm Active checkbox has been clicked
                ActiveCheckboxChanged(view, position)
            } else { // The Alarm item itself has been clicked
                //String label = alarmList.get(position).getLabel();
                //String alarmTime = String.format( Locale.US,"%d:%02d", alarmList.get(position).getHour(), alarmList.get(position).getMinute());
                //Toast.makeText(getContext(), "Alarm Clicked: " + label + ": Time: " + alarmTime, Toast.LENGTH_SHORT).show();
                if (!parent!!.alarmViewModel.clearSelection(alarmList!![position].getCreateTime())) AlarmItemEdit(
                    alarmList!![position], true
                )
            }
        }

        alarmAdapter!!.setOnItemLongClickListener { view, position ->
            val id = view.id
            if (id == R.id.AlarmActive) { // Alarm Active checkbox has been clicked
                ActiveCheckboxChanged(view, position)
            } else { // The Alarm item itself has been clicked
                AlarmItemLongClicked(alarmList!![position].getCreateTime())
            }
        }

        // Decoration
/*
        val itemDecoration: ItemDecoration =
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        rvAlarms.addItemDecoration(itemDecoration)
*/
    }


    // Called while initializing the activity.
    // Checks if Notification is enabled
    // If not enabled - launches request for permission that defines the callback to run
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private fun CheckPermissions() {
        val permission = ContextCompat.checkSelfPermission(parent!!, permission.POST_NOTIFICATIONS)
        if (permission == PermissionChecker.PERMISSION_GRANTED) {
            Log.i("THE_TIME_MACHINE", "POST_NOTIFICATIONS Permission Granted")
            //AddAlarm_Button.setEnabled(true);;
        }
        if (permission == PermissionChecker.PERMISSION_DENIED) {
            Log.i("THE_TIME_MACHINE", "POST_NOTIFICATIONS Permission Denied")
            val shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(
                    parent!!, POST_NOTIFICATIONS)
            Log.i(
                "THE_TIME_MACHINE",
                "POST_NOTIFICATIONS Should Show Request Permission - $shouldShow"
            )
            if (shouldShow) {
                // Need to show a pop-up window that explains why it is important to grant permissions
                // Display the pop-up window
                val popupWindow = displayPopUpNotifPemis()

                // Define action to do when pop-up window is dismissed -
                // Request permission to show notifications
                popupWindow.setOnDismissListener {
                    Log.i("THE_TIME_MACHINE", "onDismiss called - Requesting permission")
                    requestPermissionLauncher.launch(POST_NOTIFICATIONS)
                }
            } else {
                // Request permission to show notifications (when pop-up window is not shown)
                Log.i("THE_TIME_MACHINE", "Requesting permission")
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }
    }


    // Defines the Request Permission Launcher
    // Launches the permission request dialog box for POST_NOTIFICATIONS
    // Gets the result Granted (true/false) and sets variable  notificationPermission
    private val requestPermissionLauncher =
        registerForActivityResult<String, Boolean>(ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted - Add Alarm button is enabled
            Log.i("THE_TIME_MACHINE", "Activity Result - Granted")

            //AddAlarm_Button.setEnabled(true);
        } else {
            // Permission was NOT granted - Add Alarm button becomes disabled
            Log.i("THE_TIME_MACHINE", "Activity Result - NOT Granted")

            //AddAlarm_Button.setEnabled(false);

            // Explain to the user that the feature is unavailable because the
            // feature requires a permission that the user has denied. At the
            // same time, respect the user's decision. Don't link to system
            // settings in an effort to convince the user to change their
            // decision.
        }
    }

    private fun displayPopUpNotifPemis(): PopupWindow {
        val inflater = parent!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView =
            inflater.inflate(R.layout.popup_notification_permission, fragmentView as ViewGroup?)

        // Create the PopupWindow object and set its content view to the inflated layout
        val popupWindow = PopupWindow(
            popupView, RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        (fragmentView!!.parent as ViewGroup).removeView(popupView)
        popupWindow.isFocusable = true

        popupWindow.showAtLocation(fragmentView, Gravity.CENTER, 0, 0)

        val btnClose = popupView.findViewById<Button>(R.id.button_ok_notif)
        btnClose.setOnClickListener { popupWindow.dismiss() }

        return popupWindow
    }

    // Get Item's info from Alarm list
    // Get new state of checkbox
    // Send update to this item down to the ViewModel
    // Schedule/cancel alarm
    fun ActiveCheckboxChanged(view: View, position: Int) {
        // Get alarm item data from list

        var b = alarmList!![position].bundle

        // Create a new alarm, set active
        val item = AlarmItem(b!!)
        // Get 'active' checkbox state and insert it to the Alarm
        val active = (view as CheckBox).isChecked
        item.isActive = active
        item.resetSnoozeCounter()
        // Update View Model
        parent!!.alarmViewModel.UpdateAlarm(item)

        // Schedule/Cancel Alarm
        item.Exec()

        // Stop ringing if unchecked
        if (!active) {
            b = item.bundle
            val context = requireContext()
            val stopIntent = Intent(context, AlarmService::class.java)
            stopIntent.putExtras(b)
            AlarmReceiver.stopping(context, stopIntent)
        }
    }

    /*
     *   Called when user clicks on Alarm item in recycler or toolbar Edit action
     *   Copy values from the selected item to be used by the setup fragment
     *   Create a bundle with data to be passed to the setup fragment
     *   Replace this fragment by setup fragment
     *
     *   If parameter edit if false - this means that this item should be duplicated
     *   rather than edited
     */
    fun AlarmItemEdit(item: AlarmItem?, edit: Boolean) {
        if (item == null) return

        // Copy values from the selected item to be used by the setup fragment
        parent!!.alarmViewModel.setUpAlarmValues.GetValuesFromList(item, edit)

        // Passing parameters to setup fragment
        val b = Bundle()

        b.putInt("INIT_POSITION", 0)
        val alarmItems = parent!!.alarmViewModel.alarmList.value ?: return
        if (edit) {
            b.putLong("INIT_CREATE_TIME", item.getCreateTime())
            b.putBoolean("INIT_NEWALARM", false)
        } else {
            b.putLong("INIT_CREATE_TIME", 0)
            b.putBoolean("INIT_NEWALARM", true)
        }

        // Replace current fragment with the Setup Alarm fragment
        parent = activity as MainActivity?
        if (parent != null) parent!!.supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container_view, SetUpAlarmFragment::class.java, b)
            .addToBackStack("tag2")
            .commit()
    }


    /* When an item is Long-Clicked:
      - The item is selected/deselected (Visually)
      - The App bar may take one of the following modes:
      -- Basic: 0 items selected
      -- Single: 1 item selected
      -- Multi: 2 or more items selected
     */
    fun AlarmItemLongClicked(id: Long) {
        // get the list of alarms
        //List<AlarmItem> alarmItems = parent.alarmViewModel.getAlarmList().getValue();
        //if (alarmItems==null) return;

        // Update the list of the selected items - simply toggle

        parent!!.alarmViewModel.toggleSelection(id)

        // Modify toolbar according to number of selected items
        parent!!.UpdateOptionMenu()
    }


    /* *
    * Called when user clicks on Add Alarm button
    * Resets values to be used by the setup fragment
    * Create a bundle with data to be passed to the setup fragment
    * Replace this fragment by setup fragment
    * */
    fun AddAlarmClicked() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            CheckPermissions()
        }

        // Reset the setup alarm values
        parent!!.alarmViewModel.setUpAlarmValues.ResetValues()

        // Passing parameters to setup fragment
        val b = Bundle()
        b.putBoolean("INIT_NEWALARM", true)

        // Replace current fragment with the Setup Alarm fragment
        parent = activity as MainActivity?
        if (parent != null) parent!!.supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, SetUpAlarmFragment::class.java, b)
            .addToBackStack("tag2").commit()
    }

    fun DeleteSelectedAlarms() {
        val tempList = ArrayList(selectedItems)

        for (i in tempList.indices) {
            val item = parent!!.alarmViewModel.getAlarmItemById(tempList[i]) ?: return
            parent!!.alarmViewModel.DeleteAlarm(item)
        }
    }

    fun EditSelectedAlarm() {
        // Edit only is exactly one item selected

        if (selectedItems!!.size != 1) return
        AlarmItemEdit(parent!!.alarmViewModel.getAlarmItemById(selectedItems!![0]), true)
    }

    fun DuplicateSelectedAlarm() {
        // Duplicate only is exactly one item selected
        if (selectedItems!!.size != 1) return
        AlarmItemEdit(parent!!.alarmViewModel.getAlarmItemById(selectedItems!![0]), false)
    }

    private fun sortAlarmList(comparatorType: String, separate: Boolean) {

        when(comparatorType){
            "alphabetically" -> alarmList?.sortedWith(ascendSortByLabel(separate))
            "by_alarm_time"  -> alarmList?.sortedWith(ascendSortByAlarmTime(separate))
            else             -> alarmList?.sortedWith(descendSortByCreateTime(separate))
        }
    }

    internal inner class ascendSortByAlarmTime(separate: Boolean) : Comparator<AlarmItem> {
        // Used for sorting in ascending order of
        // Alarm Time
        var inactiveSeparate: Boolean = true

        init {
            inactiveSeparate = separate
        }

        override fun compare(a: AlarmItem, b: AlarmItem): Int {
            if (inactiveSeparate) {
                if (b.isActive && !a.isActive) return 1
                if (!b.isActive && a.isActive) return -1
            }

            return (a.nextAlarmTimeInMillis() - b.nextAlarmTimeInMillis()).toInt()
        }
    }

    internal inner class descendSortByAlarmTime : Comparator<AlarmItem> {
        // Used for sorting in descending order of
        // Alarm Time
        override fun compare(a: AlarmItem, b: AlarmItem): Int {
            return (b.nextAlarmTimeInMillis() - a.nextAlarmTimeInMillis()).toInt()
        }
    }

    internal inner class ascendSortByLabel(separate: Boolean) : Comparator<AlarmItem> {
        // Used for sorting in ascending order of Labels
        // If labels are identical - sort by alarm time
        var inactiveSeparate: Boolean = true

        init {
            inactiveSeparate = separate
        }


        override fun compare(a: AlarmItem, b: AlarmItem): Int {
            if (inactiveSeparate) {
                if (b.isActive && !a.isActive) return 1
                if (!b.isActive && a.isActive) return -1
            }

            val compLabel = a.getLabel().compareTo(b.getLabel())
            if (compLabel != 0) return compLabel


            return (b.nextAlarmTimeInMillis() - a.nextAlarmTimeInMillis()).toInt()
        }
    }

    internal inner class descendSortByLabel : Comparator<AlarmItem> {
        // Used for sorting in descending order of Labels
        // If labels are identical - sort by alarm time
        override fun compare(b: AlarmItem, a: AlarmItem): Int {
            val compLabel = a.getLabel().compareTo(b.getLabel())
            if (compLabel != 0) return compLabel

            return (b.nextAlarmTimeInMillis() - a.nextAlarmTimeInMillis()).toInt()
        }
    }

    internal inner class descendSortByCreateTime(separate: Boolean) : Comparator<AlarmItem> {
        // Used for sorting in descending order of Creation Time
        var inactiveSeparate: Boolean = true

        init {
            inactiveSeparate = separate
        }

        override fun compare(a: AlarmItem, b: AlarmItem): Int {
            if (inactiveSeparate) {
                if (b.isActive && !a.isActive) return 1
                if (!b.isActive && a.isActive) return -1
            }
            return (b.getCreateTime() - a.getCreateTime()).toInt()
        }
    }

    internal inner class ascendSortByCreateTime : Comparator<AlarmItem> {
        // Used for sorting in descending order of Creation Time
        override fun compare(b: AlarmItem, a: AlarmItem): Int {
            return (b.getCreateTime() - a.getCreateTime()).toInt()
        }
    }
}