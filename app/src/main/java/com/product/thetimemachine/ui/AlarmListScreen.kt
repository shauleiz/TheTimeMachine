package com.product.thetimemachine.ui

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.os.Build
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension.Companion.fillToConstraints
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import com.product.thetimemachine.AlarmEdit
import com.product.thetimemachine.AlarmList
import com.product.thetimemachine.AlarmReceiver
import com.product.thetimemachine.AlarmService
import com.product.thetimemachine.AlarmViewModel
import com.product.thetimemachine.Application.TheTimeMachineApp
import com.product.thetimemachine.Application.TheTimeMachineApp.appContext
import com.product.thetimemachine.Application.TheTimeMachineApp.mainActivity
import com.product.thetimemachine.Data.AlarmItem
import com.product.thetimemachine.R
import com.product.thetimemachine.Settings
import com.product.thetimemachine.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Locale


 class AlarmListScreen (private val navController: NavHostController, currentBackStack : NavBackStackEntry) {
     private var parent = appContext
     private val isDynamicColor = false
     private val currentDestination = currentBackStack.destination
     private val editDesc = appContext.getString(R.string.edit_action_bar)

     //private var selectedItems: ArrayList<Int>? = null
/*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parent = activity as MainActivity?
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Start the compose display
        return ComposeView(requireContext()).apply { setContent { AlarmListFragDisplayTop() } }
    }


 */

/*
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // Toolbar: Title + Menu
        val appToolbar = requireActivity().findViewById<Toolbar>(R.id.app_toolbar)
        appToolbar.setTitle(R.string.alarmlist_title)
        (requireActivity() as AppCompatActivity).setSupportActionBar(appToolbar)
        //parent!!.UpdateOptionMenu()

    }


 */


    // Called while initializing the activity.
    // Checks if Notification is enabled
    // If not enabled - launches request for permission that defines the callback to run
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    fun checkPermissions( showDialog: (Boolean) -> Unit) {
        val permission = appContext.checkSelfPermission(POST_NOTIFICATIONS)
        if (permission == PermissionChecker.PERMISSION_GRANTED) {
            Log.i("THE_TIME_MACHINE", "POST_NOTIFICATIONS Permission Granted")
            //AddAlarm_Button.setEnabled(true);;
        }
        if (permission == PermissionChecker.PERMISSION_DENIED) {
            Log.i("THE_TIME_MACHINE", "POST_NOTIFICATIONS Permission Denied")
            val shouldShow = checkSelfPermission(mainActivity, POST_NOTIFICATIONS) != PERMISSION_GRANTED
            Log.i(
                "THE_TIME_MACHINE",
                "POST_NOTIFICATIONS Should Show Request Permission - $shouldShow"
            )
            if (shouldShow) {
                // Need to show a pop-up window that explains why it is important to grant permissions
                // Display the pop-up window
                Log.i("THE_TIME_MACHINE", "Going to display a popup")
                showDialog(true)
                Log.i("THE_TIME_MACHINE", "Should have displayed a popup")

                // Define action to do when pop-up window is dismissed -
                // Request permission to show notifications

                //requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            } else {
                // Request permission to show notifications (when pop-up window is not shown)
                Log.i("THE_TIME_MACHINE", "Not going to display a popup")
                mainActivity.requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }
    }





/*
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
*/


    /*
     *   Called when user clicks on Alarm item in recycler or toolbar Edit action
     *   Copy values from the selected item to be used by the setup fragment
     *   Create a bundle with data to be passed to the setup fragment
     *   Replace this fragment by setup fragment
     *
     *   If parameter edit if false - this means that this item should be duplicated
     *   rather than edited
     */
    private fun alarmItemEdit(item: AlarmItem?, edit: Boolean) {
        if (item == null) return

        // Copy values from the selected item to be used by the setup fragment
        alarmViewModel?.setUpAlarmValues?.GetValuesFromList(item, edit)
        alarmViewModel?.alarmList?.value ?: return

        if (edit) {
            navigate2AlarmEdit(navController = navController, itemId = item.getCreateTime())
        } else {
            navigate2AlarmEdit(navController = navController, itemId = 0)
        }

        // Remove from list of selected alarms
        alarmViewModel?.clearSelection(item.createTime)
    }


    /* When an item is Long-Clicked:
      - The item is selected/deselected (Visually)
      - The App bar may take one of the following modes:
      -- Basic: 0 items selected
      -- Single: 1 item selected
      -- Multi: 2 or more items selected
     */
    private fun alarmItemLongClicked(id: Long) {
        // Update the list of the selected items - simply toggle
        alarmViewModel?.toggleSelection(id)



        // Modify toolbar according to number of selected items
        //parent!!.UpdateOptionMenu()
    }


    /* *
    * Called when user clicks on Add Alarm button
    * Resets values to be used by the setup fragment
    * Create a bundle with data to be passed to the setup fragment
    * Replace this fragment by setup fragment
    * */
    private fun addAlarmClicked( showDialog: (Boolean)->Unit) {

        Log.d("THE_TIME_MACHINE", "AddAlarmClicked()) " )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermissions(showDialog)
        }

        // Reset the setup alarm values
        alarmViewModel?.setUpAlarmValues?.ResetValues()


        //actionClicked(navController = navController, clicked = AlarmEdit.route)
        navigate2AlarmEdit(navController = navController, itemId = 0)
/*
        // Replace current fragment with the Setup Alarm fragment
        parent = activity as MainActivity?
        if (parent != null) parent!!.supportFragmentManager
            .beginTransaction()
            //.replace(R.id.fragment_container_view, SetUpAlarmFragment::class.java, b)
            .replace(R.id.fragment_container_view, AlarmEditScreen::class.java, b)
            .addToBackStack("tag2")
            .commit()

 */

        // Clear list of selected alarms
        alarmViewModel?.clearSelection()
    }



fun deleteSelectedAlarms() {
        //val tempList = ArrayList(selectedItems)
        val tempList = alarmViewModel?.liveSelectedItems?.value?.let { ArrayList(it) }
        if (tempList != null) {
            for (id in tempList) {
                val item = alarmViewModel?.getAlarmItemById(id)
                if (item!=null) {
                    alarmViewModel?.DeleteAlarm(item)
                    // Remove from list of selected alarms
                    alarmViewModel?.clearSelection(item.createTime)
                }
            }
        }
        // Modify toolbar according to number of selected items
        //parent!!.UpdateOptionMenu()

    }

     private fun actionClicked(action : String){
         when (action) {
             editDesc -> editSelectedAlarm()
         }
     }

    fun editSelectedAlarm() {
        val tempList = alarmViewModel?.liveSelectedItems?.value?.let { ArrayList(it) }
        // Edit only is exactly one item selected
        if (tempList==null || tempList.size != 1) return

        alarmItemEdit(alarmViewModel?.getAlarmItemById(tempList[0]), true)
    }

    fun duplicateSelectedAlarm() {
        // Duplicate only is exactly one item selected
        val tempList = alarmViewModel?.liveSelectedItems?.value?.let { ArrayList(it) }
        if (tempList==null || tempList.size != 1) return

        alarmItemEdit(alarmViewModel?.getAlarmItemById(tempList[0]), false)
    }



    /*** Composable Functions ***/
    //
    /*
    /* Top level Display - Call Portrait/Landscape Content View */
    @Composable
    fun AlarmListFragDisplayTop() {
        parent = activity as MainActivity?
        AppTheme(dynamicColor = isDynamicColor) {
            Surface {
                MaterialTheme {
                    alarmViewModel?.let { AlarmListFragDisplay(it) }
                }
            }
        }
    }

     */

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
     fun AlarmListFragDisplay(alarmViewModel : AlarmViewModel){
        // Observes values coming from the VM's LiveData<Plant> field
        val alarmList         by alarmViewModel.alarmList.observeAsState()
        var showPermissionDialog  by rememberSaveable { mutableStateOf(false) }
        var nSelectedItems   by rememberSaveable { mutableIntStateOf(0) }

        Scaffold (
            topBar = @Composable { MediumTopAppBar(
                    title = { Text(AlarmList.label) },
                    navigationIcon = { NavBack(currentDestination, navController) },
                    actions = {AlarmListActions(nSelectedItems) { route -> actionClicked(route)} },
                )
            },
            floatingActionButton =  {DisplayAddFloatButton { showPermissionDialog = it } },
            floatingActionButtonPosition = FabPosition.End,
        ) { DisplayAlarmList(alarmList, it) {n -> nSelectedItems=n } }

        ShowPopUpPermissionWarning(showPermissionDialog) { showPermissionDialog = it }
    }

     // Display Action icons on the Top App Bar - and react to click
     @Composable
     private fun AlarmListActions(nSelected: Int, onActionClick: (String)-> Unit) {

         // Edit Action: Display only if one items selected
         if (nSelected ==1 ) {
             IconButton(onClick = {
                 onActionClick(editDesc)
             }) {
                 Icon(
                     imageVector = Icons.Filled.Edit,
                     contentDescription = editDesc // TODO: Replace
                 )
             }
         }
     }



     // Show a Pop-up dialog box that warns that Notification Permission is required
     @OptIn(ExperimentalMaterial3Api::class)
     @Composable
     private fun ShowPopUpPermissionWarning(show : Boolean, onClick : (Boolean)->Unit){
         if (!show) return
         BasicAlertDialog(
             //title = R.string.title_error,
             //text = "LLL",
             onDismissRequest = { launchRequest4Permission(); onClick(false)}
             //confirmButton = null
         )
         {

             AppTheme(dynamicColor = isDynamicColor) {
                 Surface(
                     modifier = Modifier
                         .wrapContentWidth()
                         .wrapContentHeight(),
                     shape = MaterialTheme.shapes.large,
                     tonalElevation = AlertDialogDefaults.TonalElevation
                 )
                 {
                     MaterialTheme {
                         Column(modifier = Modifier.padding(16.dp)) {
                             Text(
                                 text = stringResource(R.string.title_warning),
                                 style = MaterialTheme.typography.titleLarge,
                                 color = MaterialTheme.colorScheme.error,
                             )
                             Spacer(modifier = Modifier.height(16.dp))
                             Text(text = stringResource(R.string.must_permit))
                             TextButton(
                                 onClick = { launchRequest4Permission(); onClick(false) },
                                 modifier = Modifier.align(Alignment.End)
                             ) {
                                 Text(stringResource(R.string.confirm))
                             }
                         }
                     }
                 }
             }
         }

     }

     private fun launchRequest4Permission() =
         mainActivity.requestPermissionLauncher.launch(POST_NOTIFICATIONS)

     @Composable
    fun DisplayAlarmList (list: MutableList<AlarmItem>?, pad : PaddingValues, nSel : (Int)->Unit) {
        if (list == null) return

        // Sorting
        val sortedList = sortAlarmList(list)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad),
            contentPadding = PaddingValues(16.dp),
        ) {
            items(
                count = sortedList.size,
                key = { sortedList[it].createTime }
            ) { DisplayAlarmItem(sortedList[it]){n -> nSel(n)} }
        }
    }


    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun DisplayAlarmItem(alarmItem: AlarmItem, nSel : (Int) -> Unit) {

            // Force this function to be called when list of selected changes
            // val selectToggle by parent!!.alarmViewModel.selectToggleObserve.observeAsState( )
            // val toggled = if (selectToggle!=null && selectToggle as Boolean) "A" else "B"

            // Get list of selected alarms and mark this item as selected(yes/no)
            val selectedAlarmList by alarmViewModel!!.liveSelectedItems.observeAsState(
                ArrayList()
            )
            val filterList = selectedAlarmList?.filter { it.equals(alarmItem.createTime.toInt()) }
            val selected = !filterList.isNullOrEmpty()

            val currentAlpha = if (alarmItem.isActive) 1.0f else 0.3f
            val snoozeIconColor =
                if (alarmItem.getSnoozeCounter() > 0) Color.Unspecified else Color.Transparent
            val vibrateIconColor =
                if (alarmItem.isVibrationActive) Color.Unspecified else Color.Transparent
            val muteIconColor = if (alarmItem.isAlarmMute) Color.Unspecified else Color.Transparent

            // Animation (From: https://developer.android.com/develop/ui/compose/animation/quick-guide#animate-text-scale)
            val infiniteTransition = rememberInfiniteTransition(label = "infinite transition")
            val scale by infiniteTransition.animateFloat(
                initialValue = if (alarmItem.isRinging) 1.2f else 1.0f,
                targetValue = if (alarmItem.isRinging) 0.8f else 1.0f,
                animationSpec = infiniteRepeatable(tween(200), RepeatMode.Reverse),
                label = "scale"
            )

        // Hoist number of selected items
        nSel(alarmViewModel!!.nofSelectedItems)


            Card(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth()
                    .combinedClickable(
                        onLongClick = { alarmItemLongClicked(alarmItem.getCreateTime()) },
                        onClickLabel = stringResource(id = R.string.edit_alarm)
                    )
                    {
                        if (selected) alarmItemLongClicked(alarmItem.getCreateTime())
                        else alarmItemEdit(alarmItem, true)
                    }
                    .background(MaterialTheme.colorScheme.surface)
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.small,
                elevation = CardDefaults.elevatedCardElevation(5.dp),
                colors = CardDefaults.cardColors(containerColor = if (selected) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface),
                // onClick = {}
            ) {
                ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                    val refLabel = createRef()
                    val refAlarmTime = createRef()
                    val refAlarmActive = createRef()
                    val refAmPm24h = createRef()
                    val refWeekdays = createRef()
                    val refSnoozeIcon = createRef()
                    val refVibrateIcon = createRef()
                    val refMuteIcon = createRef()

                    // Alarm Label
                    Text(
                        alarmItem.getLabel(),
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier
                            .alpha(currentAlpha)
                            .constrainAs(refLabel) {
                                width = fillToConstraints
                                centerVerticallyTo(refAlarmActive, bias = 0.7f)
                                linkTo(
                                    start = refAlarmActive.end,
                                    end = refAlarmTime.start,
                                    startMargin = 8.dp,
                                    endMargin = 8.dp,
                                    bias = 0.0f,
                                )
                            },
                        )



                    // Weekdays / Today / Tomorrow
                    Text(
                        text = getDisplayWeekdays(alarmItem),
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.onSurface,
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
                            .alpha(currentAlpha)
                            .height(16.dp)
                            .constrainAs(refSnoozeIcon) {
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
                            .alpha(currentAlpha)
                            .height(16.dp)
                            .constrainAs(refVibrateIcon) {
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
                            .alpha(currentAlpha)
                            .height(16.dp)
                            .constrainAs(refMuteIcon) {
                                top.linkTo(parent.top)
                                start.linkTo(refVibrateIcon.end)
                            }
                    )


                    // Alarm Active Checkbox
                    //val isChecked = remember { mutableStateOf(false) }
                    Checkbox(
                        checked = alarmItem.isActive,
                        onCheckedChange = { onActiveChange(alarmItem, it) },
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
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                transformOrigin = TransformOrigin.Center
                            }
                            .constrainAs(refAlarmTime) {
                                bottom.linkTo(parent.bottom)
                                end.linkTo(refAmPm24h.start)
                            }
                    )
                }
            }
        }


    @Composable
    private fun sortAlarmList(list: MutableList<AlarmItem>) : List<AlarmItem>{
        // Sorting
        val comparatorType = getPrefSortType(parent)
        val separate = isPrefSortSeparate(parent)
        val sortedList = remember(comparatorType, list) {
            if (separate) {
                when (comparatorType) {
                    "alphabetically" -> list.sortedWith(compareBy<AlarmItem> { !it.isActive }.thenBy { it.label })
                    "by_alarm_time" -> list.sortedWith(compareBy<AlarmItem> { !it.isActive }.thenBy { it.alarmTimeInMillis() })
                    else -> list.sortedWith(compareBy<AlarmItem> { !it.isActive }.thenBy { it.createTime })
                }
            } else {
                when (comparatorType) {
                    "alphabetically" -> list.sortedWith(compareBy { it.label })
                    "by_alarm_time" -> list.sortedWith(compareBy{ it.alarmTimeInMillis() })
                    else -> list.sortedWith(compareBy { it.createTime })
                }
            }
        }

        return sortedList
    }

        @Composable
        private fun getPrimaryTextColor(alarmItem: AlarmItem): Color {
            if (alarmItem.isActive) return MaterialTheme.colorScheme.primary
            return MaterialTheme.colorScheme.inversePrimary
        }

        @Composable
        private fun DisplayAddFloatButton( showDialog: (Boolean)->Unit) {
            Log.i("THE_TIME_MACHINE", "DisplayAddFloatButton()")
            FloatingActionButton(
                onClick =  { addAlarmClicked(showDialog) },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondary,
            )
            {
                Icon(
                    painter = painterResource(R.drawable.baseline_alarm_add_48),
                    contentDescription = parent?.applicationContext?.getString(R.string.alarm_add),
                    modifier = Modifier.size(24.dp),
                )
            }


        }


        private fun getAmPm24h(alarmItem: AlarmItem): Int {
            // Time
            val h = alarmItem.getHour()
            return if (isPref24h(parent))
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
        private fun getDisplayAlarmTime(alarmItem: AlarmItem): String {

            // Time
            var h = alarmItem.getHour()
            if (!isPref24h(parent))
            {
                if (h == 0) {
                    h = 12
                }
                else {
                    if (h > 12) h -= 12
                }
            }
            val fmt = stringResource(R.string.alarm_format)
            val alarmTime = java.lang.String.format(fmt, h, alarmItem.getMinute())
            return (alarmTime)
        }


        @Composable
        private fun getDisplayWeekdays(alarmItem: AlarmItem): AnnotatedString {

            /// Is it a One-Off case? If so, is the alarm set for today or tomorrow?
            if (alarmItem.isOneOff) {

                // Today - Active(Red) or inactive (pale Primary)
                if (alarmItem.isNotInThePast() && alarmItem.isToday()) {
                    //val todayColorId = if (alarmItem.isActive) colorResource(R.color.RealRed) else MaterialTheme.colorScheme.inversePrimary
                    return ((buildAnnotatedString {
                        withStyle(style = SpanStyle(color = colorResource(R.color.RealRed))) {
                            append(stringResource(R.string.day_today))
                        }
                    }))
                }

                // Tomorrow - Active(Blue) or inactive (pale Primary)
                if (alarmItem.isNotInThePast() && alarmItem.isTomorrow()) {
                    //val tomorrowColorId = if (alarmItem.isActive) colorResource(R.color.light_blue_600) else MaterialTheme.colorScheme.inversePrimary
                    return ((buildAnnotatedString {
                        withStyle(style = SpanStyle(color = colorResource(R.color.light_blue_600))) {
                            append(stringResource(R.string.day_tomorrow))
                        }
                    }))

                    // Future Date
                } /**/ else {
                    val format = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale("US"))
                    val alarmTime = alarmItem.alarmTimeInMillis()
                    val word = format.format(alarmTime)
                    //if (alarmItem.isActive)
                        return ((buildAnnotatedString {
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                append(word)
                            }
                        }))
                }
            }

            // Repeating
            return getAnnotatedWeekdays(alarmItem)

        }

        @Composable
        private fun getAnnotatedWeekdays(alarmItem: AlarmItem): AnnotatedString {

            val suArray = intArrayOf(0, 2, 3, 5, 6, 8, 9, 11, 12, 14, 15, 17, 18, 20)
            val moArray = intArrayOf(18, 20, 0, 2, 3, 5, 6, 8, 9, 11, 12, 14, 15, 17, 18, 20)
            val genArray: IntArray
            val weekdaysString: String

            // Get the array os 'skips' and the correct weekdays string
            when (getPrefFirstDayOfWeek(parent)) {
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
                MaterialTheme.colorScheme.onSurface
            val fadedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

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
            var currentColor: Color
            var underlined: TextDecoration

            Log.d("THE_TIME_MACHINE", "weekdaysString = $weekdaysString" )
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
        private fun onActiveChange(alarmItem: AlarmItem, checked: Boolean) {

            var b = alarmItem.bundle

            // Create a new alarm, set active
            val item = AlarmItem(b!!)
            item.active = checked

            // Reset snooze
            item.resetSnoozeCounter()

            if (checked)
                item.recalculateDate() // If is an explicit date and in the past - change date to the near future

            // Update View Model - this also causes recomposition
            alarmViewModel?.UpdateAlarm(item)

            // Schedule/Cancel Alarm
            item.Exec()

            // Stop ringing if unchecked
            if (!item.active) {
                b = item.bundle
                val context = appContext
                val stopIntent = Intent(context, AlarmService::class.java)
                stopIntent.putExtras(b)
                AlarmReceiver.stopping(context, stopIntent)
            }
        }
    }
