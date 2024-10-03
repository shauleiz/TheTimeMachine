package com.product.thetimemachine.ui

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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import com.product.thetimemachine.AlarmReceiver
import com.product.thetimemachine.AlarmService
import com.product.thetimemachine.AlarmViewModel
import com.product.thetimemachine.Data.AlarmItem
import com.product.thetimemachine.R
import com.product.thetimemachine.ui.SettingsFragment.pref_first_day_of_week
import com.product.thetimemachine.ui.SettingsFragment.pref_is24HourClock
import java.text.SimpleDateFormat
import java.util.Locale


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

        // Create Adapter for the Recycler View

        alarmList = parent!!.alarmViewModel.alarmList.value
        alarmAdapter = AlarmAdapter(alarmList)

        // Get the list of selected alarm items
        selectedItems = if (savedInstanceState != null) {
            savedInstanceState.getIntegerArrayList("ARRAY_SI")
        } else {
            ArrayList()
        }

        // Start the compose display
        return ComposeView(requireContext()).apply { setContent { AlarmListFragDisplayTop() } }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntegerArrayList("ARRAY_SI", selectedItems)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentView = view

        // Toolbar: Title + Menu
        val AppToolbar = requireActivity().findViewById<Toolbar>(com.product.thetimemachine.R.id.app_toolbar)
        AppToolbar.setTitle(com.product.thetimemachine.R.string.alarmlist_title)
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
        val permission = ContextCompat.checkSelfPermission(parent!!, POST_NOTIFICATIONS)
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
    private fun ActiveCheckboxChanged(view: View, position: Int) {
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
    private fun AlarmItemEdit(item: AlarmItem?, edit: Boolean) {
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
    private fun AlarmItemLongClicked(id: Long) {
        // get the list of alarms
        //List<AlarmItem> alarmItems = parent.alarmViewModel.getAlarmList().getValue();
        //if (alarmItems==null) return;

        // Update the list of the selected items - simply toggle
        parent!!.alarmViewModel.toggleSelection(id)

        // Modify toolbar according to number of selected items
        parent!!.UpdateOptionMenu()

        // Inform the ViewModel that the selection List was changed
        parent?.alarmViewModel?.selectToggleObserve()
    }


    /* *
    * Called when user clicks on Add Alarm button
    * Resets values to be used by the setup fragment
    * Create a bundle with data to be passed to the setup fragment
    * Replace this fragment by setup fragment
    * */
    private fun AddAlarmClicked() {

        Log.d("THE_TIME_MACHINE", "AddAlarmClicked()) " )

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

    fun deleteSelectedAlarms() {
        //val tempList = ArrayList(selectedItems)
        val tempList = ArrayList(parent!!.alarmViewModel.selectedItems.value)
        if (tempList != null) {
            for (id in tempList) {
                val item = parent!!.alarmViewModel.getAlarmItemById(id)
                if (item!=null)
                    parent!!.alarmViewModel.DeleteAlarm(item)
            }
        }
    }

    fun editSelectedAlarm() {
        val tempList = ArrayList(parent!!.alarmViewModel.selectedItems.value)
        // Edit only is exactly one item selected
        Log.d("THE_TIME_MACHINE", "editSelectedAlarm()) :: selectedItems=$tempList" )
        if (tempList==null || tempList.size != 1) return

        AlarmItemEdit(parent!!.alarmViewModel.getAlarmItemById(tempList[0]), true)
    }

    fun duplicateSelectedAlarm() {
        // Duplicate only is exactly one item selected
        val tempList = ArrayList(parent!!.alarmViewModel.selectedItems.value)
        Log.d("THE_TIME_MACHINE", "editSelectedAlarm()) :: selectedItems=$tempList" )
        if (tempList==null || tempList.size != 1) return

        AlarmItemEdit(parent!!.alarmViewModel.getAlarmItemById(tempList[0]), false)
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



    /*** Composable Functions ***/
    //
    /* Top level Display - Call Portrait/Landscape Content View */
    @Composable
    fun AlarmListFragDisplayTop() {
        Surface{
            MaterialTheme{/**/
                //if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT)
                    AlarmListFragDisplayPortrait(parent!!.alarmViewModel)
               // else
                   // AlarmListFragDisplayLand(parent!!.alarmViewModel )
            }
        }
    }

    @Composable
    fun AlarmListFragDisplayPortrait(alarmViewModel : AlarmViewModel){
        // Observes values coming from the VM's LiveData<Plant> field
        val alarmList         by alarmViewModel.alarmList.observeAsState()

        Scaffold (
            // Display "Add" floating button
            floatingActionButton =  {DisplayAddFloatButton()},
            floatingActionButtonPosition = FabPosition.End,
        ) {it
            DisplayAlarmList(alarmList)
        }

    }


    @Composable
    fun AlarmListFragDisplayLand(alarmViewModel : AlarmViewModel){}

    @Composable
    fun DisplayAlarmList (list: MutableList<AlarmItem>?) {



        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
        ) {
            if (list != null) {
                items(list.size,
                    key = {list[it].createTime}
                ) {DisplayAlarmItem(list[it]) }
            }
        }

    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    @Composable
    private fun DisplayAlarmItem(alarmItem: AlarmItem) {

        // Force this function to be called when list of selected changes
        val selectToggle by parent!!.alarmViewModel.selectToggleObserve.observeAsState( )
        val toggled = if (selectToggle!=null && selectToggle as Boolean) "A" else "B"

        // Get list of selected alarms and mark this item as selected(yes/no)
        val selectedAlarmList by parent!!.alarmViewModel.selectedItems.observeAsState()
        val filterList = selectedAlarmList?.filter {  it.equals(alarmItem.createTime.toInt()) }
        var selected= (filterList != null && filterList.isNotEmpty())
        val backgroundColor = if (selected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface


        val currentAlpha = if (alarmItem.isActive) 1.0f else 0.3f
        val snoozeIconColor = if (alarmItem.getSnoozeCounter() >0)  Color.Unspecified else Color.Transparent
        val vibrateIconColor = if (alarmItem.isVibrationActive()) Color.Unspecified else Color.Transparent
        val muteIconColor = if (alarmItem.isAlarmMute()) Color.Unspecified else Color.Transparent

        Card(
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth()
                .combinedClickable(
                    onLongClick = { AlarmItemLongClicked(alarmItem.getCreateTime()) },
                    onClickLabel = "Edit Alarm"
                )
                { if (selected) AlarmItemLongClicked(alarmItem.getCreateTime()) else AlarmItemEdit(alarmItem, true) }
                .background(MaterialTheme.colorScheme.surface)
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.small,
            elevation = CardDefaults.elevatedCardElevation(5.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            // onClick = {}
            ) {
                ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                    val refLabel = createRef()
                    val refBell = createRef()
                    val refAlarmTime = createRef()
                    val refAlarmActive = createRef()
                    val refAmPm24h = createRef()
                    val refWeekdays = createRef()
                    val refSnoozeIcon = createRef()
                    val refVibrateIcon = createRef()
                    val refMuteIcon = createRef()


                    // Bell Icon
                    /*
                    Image(
                        painter = painterResource(id = R.drawable.baseline_alarm_off_24),
                        contentDescription = stringResource(R.string.temp_icon),
                        //painter = painterResource(id = android.R.drawable.ic_lock_idle_alarm),
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                            .padding(start = 8.dp)
                            .constrainAs(refBell) {
                                bottom.linkTo(parent.bottom)
                                start.linkTo(parent.start)
                                top.linkTo(parent.top)
                            }
                        )*/

                    // Alarm Label
                    Text(
                        alarmItem.getLabel(),
                        textAlign = TextAlign.Start,
                        color = colorResource(com.google.android.material.R.color.m3_default_color_primary_text),
                        fontSize = 20.sp,
                        modifier = Modifier
                            .alpha(currentAlpha)
                            .constrainAs(refLabel) {
                                centerVerticallyTo(refAlarmActive, bias = 0.7f)
                                start.linkTo(refAlarmActive.end, margin = 8.dp) //
                            },
                        )

                    // Weekdays / Today / Tomorrow
                    Text(
                        getDisplayWeekdays(alarmItem),
                        textAlign = TextAlign.End,
                        color = colorResource(com.google.android.material.R.color.m3_default_color_primary_text),
                        fontSize = 14.sp,
                        modifier = Modifier
                            //.height(30.dp)
                            //.padding(bottom = 8.dp)
                            .alpha(currentAlpha)
                            .constrainAs(refWeekdays) {
                                end.linkTo(refAlarmTime.end)
                                bottom.linkTo(refAlarmTime.top) //
                            },
                    )

                    // Snooze Icon
                    Icon(
                        painter = painterResource(id = R.drawable.snooze_fill0_wght400_grad0_opsz24),
                        contentDescription = stringResource(R.string.per_item_zoom_icon),
                        tint = snoozeIconColor,
                        modifier = Modifier
                            .height(16.dp)
                            .constrainAs(refSnoozeIcon){
                                top.linkTo(parent.top)
                                start.linkTo(refAlarmActive.end)
                            }
                    )

                    // Vibrate Icon
                    Icon(
                        painter = painterResource(id = R.drawable.vibration_opsz24),
                        contentDescription = stringResource(R.string.per_item_vibrate_icon),
                        tint = vibrateIconColor,
                        modifier = Modifier
                            .height(16.dp)
                            .constrainAs(refVibrateIcon){
                                top.linkTo(parent.top)
                                start.linkTo(refSnoozeIcon.end)
                            }
                    )

                    // Mute Icon
                    Icon(
                        painter = painterResource(id = R.drawable.notifications_off_24dp_fill0_wght400_grad0_opsz24),
                        contentDescription = stringResource(R.string.per_item_mute_icon),
                        tint = muteIconColor,
                        modifier = Modifier
                            .height(16.dp)
                            .constrainAs(refMuteIcon){
                                top.linkTo(parent.top)
                                start.linkTo(refVibrateIcon.end)
                            }
                    )


                    // Alarm Active Checkbox
                    val isChecked = remember { mutableStateOf(false) }
                    Checkbox(
                        checked = alarmItem.isActive,
                        onCheckedChange = {  onActiveChange(alarmItem, it) },
                        enabled = true,
                        //colors = CheckboxDefaults.colors(custom),
                        modifier = Modifier
                            .constrainAs(refAlarmActive) {
                                start.linkTo(parent.start, margin = 8.dp)
                                linkTo(top = parent.top, bottom = parent.bottom, bias = 0.9f)
                            },
                    )

                    // Am/Pm/24h annotation
                    Text(
                        stringResource(id = getAmPm24h(alarmItem)),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .constrainAs(refAmPm24h) {
                                bottom.linkTo(parent.bottom, margin = 2.dp)
                                end.linkTo(parent.end, margin = 8.dp)
                            }
                        )

                    // Alarm Time
                    Text(
                        getDisplayAlarmTime(alarmItem),
                        fontSize = 34.sp,
                        color = getPrimaryTextColor(alarmItem),
                        modifier = Modifier
                            .constrainAs(refAlarmTime){
                                bottom.linkTo(parent.bottom)
                                end.linkTo(refAmPm24h.start)
                            }
                    )
                }
            }
    }

    @Composable
    private fun getPrimaryTextColor(alarmItem: AlarmItem) : androidx.compose.ui.graphics.Color {
         if (alarmItem.isActive) return MaterialTheme.colorScheme.primary
        else return MaterialTheme.colorScheme.inversePrimary
    }

    @Composable
    private fun DisplayAddFloatButton(){
       FloatingActionButton(
            onClick = onAddFloatButtonClick ,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.secondary,
        )
        { Icon(
            painter = painterResource(R.drawable.baseline_alarm_add_48),
            contentDescription = requireContext().resources.getString(R.string.alarm_add),
            modifier = Modifier.size(24.dp),
        )
       }


    }

    private val onAddFloatButtonClick = { AddAlarmClicked()}

    private fun getAmPm24h(alarmItem: AlarmItem ) : Int {
        // Time
        val h = alarmItem.getHour();
        return if (pref_is24HourClock())
            (R.string.format_24h)
        else {
            if (h == 0) {
                (R.string.format_am)
            } else if (h < 12)
                (R.string.format_am)
            else {
                (R.string.format_pm)
            }
        }
    }

    @Composable
    private fun  getDisplayAlarmTime(alarmItem: AlarmItem ) : String {

        // Time
        var h = alarmItem.getHour()
        if (pref_is24HourClock())
        else {
            if (h == 0) {
                h = 12
            } else if (h < 12)
            else {
                if (h != 12) h -= 12
            }
        }
        val fmt = stringResource(R.string.alarm_format)
        val alarmTime = java.lang.String.format(fmt, h, alarmItem.getMinute())
        return(alarmTime)
    }


    @Composable
    private fun getDisplayWeekdays(alarmItem: AlarmItem) : AnnotatedString {

        val defaultColor = colorResource(com.google.android.material.R.color.m3_default_color_primary_text)

        /// Is it a One-Off case? If so, is the alarm set for today or tomorrow?
        if (alarmItem.isOneOff) {

            // Today - Active(Red) or inactive (pale Primary)
            if (alarmItem.isToday()) {
                //val todayColorId = if (alarmItem.isActive) colorResource(R.color.RealRed) else MaterialTheme.colorScheme.inversePrimary
                return ((buildAnnotatedString {
                    withStyle(style = SpanStyle(color = colorResource(R.color.RealRed))) {
                        append(stringResource(R.string.day_today))
                    }
                }))
            }

            // Tomorrow - Active(Blue) or inactive (pale Primary)
            if (alarmItem.isTomorrow()) {
                //val tomorrowColorId = if (alarmItem.isActive) colorResource(R.color.light_blue_600) else MaterialTheme.colorScheme.inversePrimary
                return ((buildAnnotatedString {
                    withStyle(style = SpanStyle(color = colorResource(R.color.light_blue_600))) {
                        append(stringResource(R.string.day_tomorrow))
                    }
                }))

                // Future Date
            } /**/else {
                val format  =   SimpleDateFormat("EEEE, MMMM d, yyyy", Locale("US"))
                val alarmTime = alarmItem.alarmTimeInMillis();
                val word =  format.format(alarmTime);
                if (alarmItem.isActive)
                    return ((buildAnnotatedString {
                        withStyle(style = SpanStyle(color = defaultColor)) {
                            append(word)
                        }
                    }))
            }
        }

        // Repeating
        return getAnnotatedWeekdays(alarmItem)

         /*
        // This is a repeating alarm - print the weekdays
        else {
            // By default - all days are grayed
            if (pref_first_day_of_week().equals("Su")) {
                return ((buildAnnotatedString {
                    withStyle(style = SpanStyle(color = defaultColor)) {
                        append(stringResource(R.string.su_mo_tu_we_th_fr_sa))
                    }
                }))
            } else {
                return ((buildAnnotatedString {
                    withStyle(style = SpanStyle(color = defaultColor)) {
                        append(stringResource(R.string.mo_tu_we_th_fr_sa_su))
                    }
                }))
            }
        }
        return ((buildAnnotatedString {
            withStyle(style = SpanStyle(color = defaultColor)) {
                append ("Error")
            }
        }))*/
    }

    @Composable
    private fun getAnnotatedWeekdays(alarmItem: AlarmItem): AnnotatedString {

        val suArray = intArrayOf(0, 2, 3, 5, 6, 8, 9, 11, 12, 14, 15, 17, 18, 20)
        val moArray = intArrayOf(18, 20, 0, 2, 3, 5, 6, 8, 9, 11, 12, 14, 15, 17, 18, 20)
        val genArray: IntArray
        val weekdaysString: String

        // Get the array os 'skips' and the correct weekdays string
        when (pref_first_day_of_week()) {
            "Su" -> {
                weekdaysString = stringResource(R.string.su_mo_tu_we_th_fr_sa)
                genArray = suArray
            }

            else -> {
                weekdaysString = stringResource(R.string.mo_tu_we_th_fr_sa_su)
                genArray = moArray
            }
        }

        val defaultColor =
            colorResource(com.google.android.material.R.color.m3_default_color_primary_text)
        val fadedColor = colorResource(id = R.color.light_gray)

        // If inactive - just print string in default color
        if (!alarmItem.isActive)
            return buildAnnotatedString {
                withStyle(style = SpanStyle(color = defaultColor)) {
                    append(weekdaysString)
                }
            }

        // If Active - Set color to selected/unselected days and mark the next day with underline
        val indexOfNextDay = alarmItem.weekdayOfNextAlarm
        val weekdays = alarmItem.getWeekDays()
        var substr: String
        var currentColor: Color
        var underlined: TextDecoration

        Log.d("THE_TIME_MACHINE", "weekdaysString = " + weekdaysString)
        return buildAnnotatedString {
            append(weekdaysString)
            for (day in 0..6) {
                currentColor = if ((weekdays and (1 shl day)) > 0) defaultColor else fadedColor
                underlined =
                    if (day == indexOfNextDay) TextDecoration.Underline else TextDecoration.None
                addStyle(
                    style = SpanStyle(
                        color = currentColor,
                        textDecoration = underlined
                    ),
                    start = genArray[day * 2],
                    end = genArray[day * 2 + 1]
                )
            }
        }
    }

    // Get Item's info from Alarm list
    // Get new state of checkbox
    // Send update to this item down to the ViewModel
    // Schedule/cancel alarm
    private fun onActiveChange(alarmItem:AlarmItem, checked:Boolean){

        var b = alarmItem.bundle

        // Create a new alarm, set active
        val item = AlarmItem(b!!)
        item.active = checked

        // Reset snooze
        item.resetSnoozeCounter()

        // Update View Model - this also causes recomposition
        parent!!.alarmViewModel.UpdateAlarm(item)

        // Schedule/Cancel Alarm
        item.Exec()

        // Stop ringing if unchecked
        if (!item.active) {
            b = item.bundle
            val context = requireContext()
            val stopIntent = Intent(context, AlarmService::class.java)
            stopIntent.putExtras(b)
            AlarmReceiver.stopping(context, stopIntent)
        }
    }

}