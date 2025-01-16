package com.product.thetimemachine.ui

import android.content.res.Configuration
import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.product.thetimemachine.AlarmEdit
import com.product.thetimemachine.AlarmViewModel
import com.product.thetimemachine.Application.TheTimeMachineApp.mainActivity
import com.product.thetimemachine.Data.AlarmItem
import com.product.thetimemachine.R
import com.product.thetimemachine.ui.theme.AppTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


/* TODO:
        - Create Text-Styles of each type of Calendar part.
*/


class AlarmEditScreen(
    private val navToSettings: () -> Unit,
    private val navyToAlarmList: () -> Unit,
    private val navBack: () -> Unit,
    )  {


    private var parent: MainActivity? = mainActivity
    private  var  setUpAlarmValues : AlarmViewModel.SetUpAlarmValues
    private  var itemId : Long = 0L
    //private var initParams: Bundle? = null
    private var isNewAlarm: Boolean = true
    private var isDuplicate : Boolean = false
    private var isOneOff = true
    private var weekdays = 0
    private val isDynamicColor = false

    init {

        // Get the initial setup values from the ViewModel
        setUpAlarmValues = alarmViewModel!!.setUpAlarmValues

        // Is it a new Alarm or Alarm to be edited
        isOneOff = setUpAlarmValues.isOneOff.value!!
        weekdays = setUpAlarmValues.weekDays.value!!

        Log.d("THE_TIME_MACHINE", "onViewCreated():  weekdays = $weekdays")

    }




    private fun getInitialHour(): Int {
        if (!isNewAlarm) {
            return setUpAlarmValues.hour.value!!
        } else {
            // Get Current time
            val currentTime = Calendar.getInstance()
            return currentTime.get(Calendar.HOUR_OF_DAY)
        }
    }

    private fun getInitialMinute(): Int {
        if (!isNewAlarm) {
            return setUpAlarmValues.minute.value!!
        } else {
            // Get Current time
            val currentTime = Calendar.getInstance()
            return currentTime.get(Calendar.MINUTE)
        }
    }



    // Converts selected Dates in JSON string format to List<Date>
    // Each entry in format "YYYYMMYY" (e.g. "20240630")
    private fun exceptionDates2String(dates: String?): String {
        Log.d("THE_TIME_MACHINE", "exceptionDates2String(): dates = $dates")
        if (dates.isNullOrEmpty()) return ""

        // From JSON to Array of strings
        val listOfMyClassObject = object : TypeToken<ArrayList<String?>?>() {}.type
        val gson = Gson()
        val dateListStr = gson.fromJson<List<String>>(dates, listOfMyClassObject)

        // Test list of strings
        if (dateListStr == null || dateListStr.isEmpty()) return ""

        // Target
        var yyyymmdd = ""

        // Convert each string to Date
        for (dateStr in dateListStr) {
            yyyymmdd = "$yyyymmdd $dateStr"
        }

        return yyyymmdd
    }

    private fun string2ExceptionDates(input: String): String {

        if (input.isEmpty()) return ""

        Log.d("THE_TIME_MACHINE", "string2ExceptionDates(): input = $input")

        val trimmed = input.trim()
        val noNumber = "\\D+".toRegex()
        val list = trimmed.split(noNumber)

        Log.d("THE_TIME_MACHINE", "string2ExceptionDates(): list = $list")

        // Convert list of strings to a single JSON string
        val gson = Gson()
        return gson.toJson(list)
    }


   // @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AlarmEditDisplayTop(itemId : Long) {

        Log.d("THE_TIME_MACHINE", "AlarmEditDisplayTop(): itemId=$itemId")

        this.itemId = itemId
        // Get the initial setup values from the ViewModel
        setUpAlarmValues = alarmViewModel!!.setUpAlarmValues
        isNewAlarm = (itemId == 0L )
       isDuplicate = setUpAlarmValues.isDuplicate!!.value == true


        Log.d("THE_TIME_MACHINE", "AlarmEditDisplayTop(): setUpAlarmValues.label=${setUpAlarmValues.label.value}")

        // List of all entries
        val listOfPrefsEx = getListOfItemPreferences(setUpAlarmValues)
        val listOfPrefs = remember { mutableStateListOf<PrefData>()}
       listOfPrefs.clear()
        listOfPrefs.addAll(listOfPrefsEx)

        // State Variables
        var showCalendarDialog  by rememberSaveable { mutableStateOf(false) }
        var showErrorDialog by rememberSaveable { mutableStateOf(false) }
        var calendarSelType : CalendarSelection by rememberSaveable { mutableStateOf(CalendarSelection.Single) }
        val weekDays = rememberSaveable { mutableIntStateOf(weekdays) }
        val oneOff = rememberSaveable { mutableStateOf(isOneOff) }
        // Set initial time
        val timePickerState = rememberTimePickerState(
            initialHour = getInitialHour(),
            initialMinute = getInitialMinute(),
            is24Hour = isPref24h(mainActivity),
        )

        // Event Handlers
        val setSelectedDays = { index: Int ->
            val i = if (getPrefFirstDayOfWeek(mainActivity) == "Mo") {
                if (index == 6) 0 else index + 1
            } else index
            weekDays.intValue = weekDays.intValue xor (1 shl i)
        }

            // Get date from alarm
            val yy = setUpAlarmValues.year.value!!
            val mm = setUpAlarmValues.month.value!!
            val dd = setUpAlarmValues.dayOfMonth.value!!
            val ld = if (yy != 0  && dd != 0) LocalDate.of(yy, mm + 1, dd) else null

        // Create a string of dates that are exception to the rule (weekdays)
        val exceptions = exceptionDates2String(setUpAlarmValues.exceptionDates.getValue())

        // Exit DisplayCalendar (Single selection) with OK pressed
        val singleSelectionCalOkPressed = {value : LocalDate? ->
            Log.d("THE_TIME_MACHINE", "DisplayCalendar()[SINGLE]: value=$value ; now=${LocalDate.now()}")

            if (value==null) {
                setUpAlarmValues.year.value = 0
                setUpAlarmValues.month.value = 0
                setUpAlarmValues.dayOfMonth.value = 0
                showCalendarDialog = false
            }
            else if (value.isBefore(LocalDate.now())) {
                // Error
                Log.d("THE_TIME_MACHINE", "DisplayCalendar(): ERROR")
                showErrorDialog = true
            }
            else {
                setUpAlarmValues.year.value = value.year
                setUpAlarmValues.month.value = value.monthValue-1
                setUpAlarmValues.dayOfMonth.value = value.dayOfMonth
                setUpAlarmValues.setFutureDate(true)
                setUpAlarmValues.setOneOff(true)
                showCalendarDialog = false
            }
        }

        // Exit DisplayCalendar (Single selection) with OK pressed
        val multiSelectionCalOkPressed = { value: String ->
            Log.d("THE_TIME_MACHINE", "DisplayCalendar()[MULTI]: value=$value ; now=${LocalDate.now()}")
            setUpAlarmValues.exceptionDates.value = string2ExceptionDates(value)
            showCalendarDialog = false
            Log.d("THE_TIME_MACHINE", "DisplayCalendar()[MULTI]: setUpAlarmValues.exceptionDates.value=${setUpAlarmValues.exceptionDates.value} ")
        }


        // Display Calendar Dialog
        DisplayCalendar(
            showDialog =  showCalendarDialog,
            onDismiss = {showCalendarDialog=false },
            selectionType = calendarSelType,
            weekdays = weekDays.intValue,
            origSelectedDate = ld,
            originalExceptions =  exceptions)
        {
            when (calendarSelType) {
                CalendarSelection.Single -> singleSelectionCalOkPressed(it as LocalDate?)
                CalendarSelection.Multiple -> multiSelectionCalOkPressed(it as String)
            }
        }

        // Display error dialog - user selected a date in the past
        DisplayWrongDateDialog(showErrorDialog){showErrorDialog = false}

        // Execute when preferences dialog OK button pressed
        val onPrefDialogOK = {index : Int, value : String? ->
            Log.d("THE_TIME_MACHINE", "onPrefDialogOK(): index=$index ; value=$value ")
            getListOfPrefLiveData(setUpAlarmValues)[index]?.value = value
            listOfPrefs[index].showDialog?.value =  false
            listOfPrefs[index].origValue?.value = value
        }

        // Updates calendar trigger and type when Calendar button is clicked
        val onCalendarButtonClicked = {type: CalendarSelection ->
            showCalendarDialog = true
            calendarSelType = type
        }

        AppTheme(dynamicColor = isDynamicColor) {
            Surface {
                MaterialTheme {
                    Scaffold(
                        topBar = @Composable {
                            TopAppBar(
                                title = {
                                    Text(
                                        AlarmEdit.label,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = {navBack()}) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Localized description" // TODO: Replace
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                                ),
                                //scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState()),
                                actions = {
                                    AlarmEditActions() { actionClicked(it) }
                                },
                            )
                        },

                        //bottomBar = @Composable {
                           // BottomAppBar(containerColor = MaterialTheme.colorScheme.surfaceContainer, ) {} },

                    ) {
                        AlarmEditBody(
                        it,
                        timePickerState,
                        //showCalendarDialog,
                        //calendarSelType,
                        oneOff,
                        weekDays,
                        setSelectedDays,
                        listOfPrefs,
                        onCalendarButtonClicked,
                        onPrefDialogOK
                    )}

                }
            }
        }



    }


    private fun actionClicked(action : String){
        when (action) {
            checkDesc -> checkmarkClicked()
            settingsDesc -> navToSettings()//navigate2Settings(navController = navController)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AlarmEditBody(
        pad: PaddingValues,
        timePickerState: TimePickerState,
        oneOff: MutableState<Boolean>,
        weekDays: MutableIntState,
        setSelectedDays: (Int) -> Unit,
        listOfPrefs: SnapshotStateList<PrefData>,
        onCalendarButtonClick : (CalendarSelection) -> Unit,
        onPrefDialogOK: (Int, String?) -> Unit
    ) {
        var showCalendarDialog1 = false
        var calendarSelType1 = CalendarSelection.Single
     //   MaterialTheme {
            Column(
                horizontalAlignment = CenterHorizontally, //of children
                modifier = Modifier
                    .padding(pad)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                /* Alarm Label */
                LabelField()

                if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    // Portrait
                    /* Time Picker */
                    TimePickerField(timePickerState)

                    /* Single/Weekly button */
                    AlarmTypeBox(
                        {
                            onCalendarButtonClick(if (it) CalendarSelection.Single else CalendarSelection.Multiple)
                        },
                        oneOff,
                        { oneOff.value = it },
                        weekDays,
                        setSelectedDays,
                        timePickerState,
                    )

                    /* Preferences */
                    ShowPreferences(listOfPrefs) { i, v -> onPrefDialogOK(i, v) }

                } else {
                    // Landscape
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        /* Time Picker */
                        TimePickerField(timePickerState)

                        VerticalDivider(thickness = 4.dp)

                        Column {
                            /* Single/Weekly button */
                            AlarmTypeBox(
                                {
                                    onCalendarButtonClick(if (it) CalendarSelection.Single else CalendarSelection.Multiple)
                                },
                                oneOff,
                                { oneOff.value = it },
                                weekDays,
                                setSelectedDays,
                                timePickerState,
                            )

                            /* Preferences */
                            ShowPreferences(listOfPrefs) { i, v -> onPrefDialogOK(i, v) }
                        }
                    }
                }
            }
  //      }
    }

    // Display Action icons on the Top App Bar - and react to click
    @Composable
    private fun AlarmEditActions(onActionClick: (String)-> Unit) {

        // Check (OK) Action
            IconButton(onClick = {
                onActionClick(checkDesc)
            }) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = checkDesc
                )
            }


        // Setting action
        IconButton(onClick = {
            onActionClick(settingsDesc)
        }) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = settingsDesc
            )
        }
    }


    @Composable
    private fun LabelField() {
        // If editing alarm then get the label
        val label =
            if (!setUpAlarmValues.label.value.isNullOrEmpty()) setUpAlarmValues.label.value
            else ""

        var value by rememberSaveable { mutableStateOf(label!!) }
        var isFocused by rememberSaveable { mutableStateOf(false) }



        OutlinedTextField(
            value = value,
            onValueChange = { value = it; setUpAlarmValues.setLabel(it) },
            label = {
                Text(
                    text = stringResource(id = R.string.label_hint),
                    color = if (!isFocused && value.isEmpty()) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.onSurface
                )
            },
            singleLine = true,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.label_hint),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState -> isFocused = focusState.isFocused }
                .heightIn(1.dp, Dp.Infinity)
                .padding(8.dp)
        )
    }

    // TODO: Add icon button to toggle between Dial and InputTimePicker
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TimePickerField(timePickerState: TimePickerState) {

        // Update mode
        timePickerState.is24hour = isPref24h(parent)

        // Display Time Picker
        TimePicker(
            state = timePickerState,
            layoutType = TimePickerLayoutType.Vertical,
            modifier = Modifier.padding(16.dp)
        )

        // Update H:M Values
        setUpAlarmValues.hour.value = timePickerState.hour
        setUpAlarmValues.minute.value = timePickerState.minute


    }

    @Composable
    private fun WeeklyOrOneOff(oneOff: Boolean, onClick: (Boolean) -> Unit) {

        // Text to put on buttons
        val options = listOf(R.string.single, R.string.weekly)

        // Index of selected button: 0=One Off ; 1=Weekly
        var selectedIndex by rememberSaveable { mutableIntStateOf(if (oneOff) 0 else 1) }


        // Segmented Buttons: Single/Weekly
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
            //.padding(8.dp)
        ) {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    onClick = { selectedIndex = index; onClick(index == 0) },
                    selected = index == selectedIndex
                ) {
                    Text(stringResource(id = label))
                }
            }

        }

        // Write back to ViewModel oneOff variable
        setUpAlarmValues.setOneOff(selectedIndex == 0)

    }

    @Composable
    private fun CalendarButton(onCalendarClicked: (Boolean) -> Unit , selectedDays: Int, oneOff: Boolean) {

        // Type of button: 0=One Off ; 1=Weekly
        val buttonType = if (oneOff) 0 else 1


        Button(
            onClick = { onCalendarClicked(buttonType == 0) },
            enabled = (oneOff || selectedDays != 0)
        ) {
            Icon(
                painter = painterResource(
                    id = if (buttonType == 0)
                        R.drawable.calendar_month_fill0_wght400_grad0_opsz24
                    else R.drawable.calendar_month_24dp_fill0_wght400_grad0_opsz24_x
                ),

                contentDescription = stringResource(id = R.string.open_date_picker),
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun NextAlarmText(timePickerState: TimePickerState) {

        Text(
            text = displayTargetAlarm(timePickerState.hour, timePickerState.minute),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                vertical = 12.dp,
                horizontal = 16.dp,
            ),
        )
    }

    @Composable
    private fun DayButtons(
        selectedDaysExt: Int,
        onSel: (Int) -> Unit
    ) {

        // If first day in week is Sunday then 0 else 1
        val firstDayInWeek = if (getPrefFirstDayOfWeek(parent) == "Su") 0 else 1

        // List of labels on the weekdays buttons
        val weekdays =
            if (firstDayInWeek == 0)
                listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
            else
                listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")

        // Shift selected days to suit a Monday-week
        var selectedDays = selectedDaysExt
        if (firstDayInWeek == 1) {
            val sunday = selectedDays and 1
            selectedDays = selectedDays shr 1
            selectedDays += sunday * 0x40
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            weekdays.forEachIndexed { index, s ->
                Text(
                    text = s,
                    textAlign = TextAlign.Center,
                    color = if ((selectedDays and (1 shl index)) > 0) {
                        MaterialTheme.colorScheme.background
                    } else {
                        MaterialTheme.colorScheme.onBackground
                    },
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .weight(1f)
                        .clip(shape = RoundedCornerShape(size = 12.dp))
                        .clickable { onSel(index) }
                        .background(
                            if ((selectedDays and (1 shl index)) > 0) {
                                MaterialTheme.colorScheme.onBackground
                            } else {
                                MaterialTheme.colorScheme.primaryContainer
                            }
                        )
                        .padding(top = 8.dp, bottom = 8.dp),
                )
            }
        }


        // If a Monday week - re-adjust selected days to norm (=Sunday week)
        if (firstDayInWeek == 1) {
            selectedDays = selectedDays shl 1
            if ((selectedDays and 0x80) > 0) selectedDays += 1
            selectedDays = selectedDays and 0x7F
        }
        // Write back to ViewModel oneOff variable
        setUpAlarmValues.setWeekDays(selectedDays)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AlarmTypeBox(
        onCalendarClicked: (Boolean)->Unit,
        oneOff: MutableState<Boolean>,
        setOneOff: (Boolean)->Unit,
        selectedDays: MutableState<Int>,
        setSelectedDays: (Int)->Unit,
        timePickerState: TimePickerState,
    ) {

        Column(modifier = Modifier
            .padding(8.dp)
        ) {
            WeeklyOrOneOff(oneOff.value, setOneOff)
            CalendarButton(onCalendarClicked, selectedDays.value, oneOff.value)
            if (oneOff.value)
                NextAlarmText(timePickerState)
            else
                DayButtons(selectedDays.value, setSelectedDays)
        }
    }

    @Composable
    private fun displayTargetAlarm(hour: Int, minute: Int): String {
        var out : String

        /// Helper Functions
        fun isInThePast(alarmInMillis: Long): Boolean {
            val now = System.currentTimeMillis()
            return (alarmInMillis < now)
        }

        fun isToday(alarmInMillis: Long): Boolean {
            val now = System.currentTimeMillis()
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = now

            // Get time of today's midnight (minus a second)
            calendar[Calendar.HOUR_OF_DAY] = 23
            calendar[Calendar.MINUTE] = 59
            calendar[Calendar.SECOND] = 59
            val lastSecondOfDay = calendar.timeInMillis


            return (alarmInMillis in now..lastSecondOfDay)
        }

        fun isTomorrow(alarmInMillis: Long): Boolean {
            val now = System.currentTimeMillis()
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = now + 24 * 60 * 60 * 1000

            // Get time of tomorrow's midnight (minus a second)
            calendar[Calendar.HOUR_OF_DAY] = 23
            calendar[Calendar.MINUTE] = 59
            calendar[Calendar.SECOND] = 59
            val lastSecondOfDay = calendar.timeInMillis

            // Get time of tomorrow's first second
            calendar[Calendar.HOUR_OF_DAY] = 0
            calendar[Calendar.MINUTE] = 0
            calendar[Calendar.SECOND] = 0
            val firstSecondOfDay = calendar.timeInMillis

            return (alarmInMillis in firstSecondOfDay..lastSecondOfDay)
        }


        // Get date values
        val dd = if (setUpAlarmValues.dayOfMonth.value != null) setUpAlarmValues.dayOfMonth.value!!
        else 0
        val mm = if (setUpAlarmValues.month.value != null) setUpAlarmValues.month.value!!
        else 0
        val yy = if (setUpAlarmValues.year.value != null) setUpAlarmValues.year.value!!
        else 0

        // Create a calendar object and modify it
        val nowInMillis = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = nowInMillis

        Log.d("THE_TIME_MACHINE", "displayTargetAlarm()[1]: dd=$dd ; mm=$mm ; yy=$yy")
        if (dd > 0  && yy > 0) calendar[yy, mm, dd, hour, minute] = 0
        else {
            calendar[Calendar.HOUR_OF_DAY] = hour
            calendar[Calendar.MINUTE] = minute
        }

        Log.d("THE_TIME_MACHINE", "displayTargetAlarm()[2]:" +
                "Year=${calendar.get(Calendar.YEAR)}  " +
                "Month=${calendar.get(Calendar.MONTH)}  " +
                "Day=${calendar.get(Calendar.DAY_OF_MONTH)}  " +
                "Hour=${calendar.get(Calendar.HOUR)}  " +
                "Minute=${calendar.get(Calendar.MINUTE)}  " +
                "")

            if (isInThePast(calendar.timeInMillis)) {
                Log.d("THE_TIME_MACHINE", "displayTargetAlarm()[3]: Is in the Past")
                calendar.timeInMillis = nowInMillis
                calendar[Calendar.HOUR_OF_DAY] = hour
                calendar[Calendar.MINUTE] = minute
                if (isInThePast(calendar.timeInMillis))
                    calendar.timeInMillis += 24 * 60 * 60 * 1000
            }
            //calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),h,m,0);



        // Create an output string
        val format =
            SimpleDateFormat( mainActivity.applicationContext.getString(R.string.time_format_display), Locale.US)
        out = format.format(calendar.timeInMillis)



        // Determine Today/Tomorrow
        if (isToday(calendar.timeInMillis)) out += mainActivity.applicationContext.getString(R.string.today_in_brackets)
        else if (isTomorrow(calendar.timeInMillis)) {
            out += mainActivity.applicationContext.getString(R.string.tomorrow_in_brackets)
        }

        //Log.d("THE_TIME_MACHINE", "displayTargetAlarm(): " + out);
        return out
    }




    enum class CalendarSelection{
        Single,
        Multiple,
        //Range,
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun DisplayWrongDateDialog( showDialog : Boolean, onDismiss : ()->Unit) {
        if (!showDialog) return
        BasicAlertDialog(
            //title = R.string.title_error,
            //text = "LLL",
            onDismissRequest = { onDismiss() }
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
                                text = stringResource(R.string.title_error),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.error,
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = stringResource(R.string.alarm_in_the_past))
                            TextButton(
                                onClick = { onDismiss() },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Confirm")
                            }
                        }
                    }
                }
            }
        }
    }



    @Composable
    private fun DisplayCalendar(
        showDialog : Boolean,
        onDismiss : ()->Unit,
        selectionType : CalendarSelection = CalendarSelection.Single,
        weekdays : Int = 0,
        origSelectedDate : LocalDate? = null,
        originalExceptions : String = "",
        onOkClicked: (Any?) -> Unit,
    ) {

        // Convert alarm date to LocalDate
        // If no need to show dialog box then reset selectedDate to its original value
        var selectedDate by rememberSaveable { mutableStateOf(origSelectedDate) }
        if (!showDialog) {
            selectedDate = origSelectedDate
            return
        }

        // Use date from alarm if exists else calculate current date
        val ym = if (setUpAlarmValues.year.value!! != 0)
            YearMonth.of(setUpAlarmValues.year.value!!, setUpAlarmValues.month.value!! + 1)
        else
            YearMonth.now()
        val currentMonth = remember { ym }

        val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
        val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
        val firstDayOfWeek =
            if (getPrefFirstDayOfWeek(parent) == "Su") DayOfWeek.SUNDAY
            else DayOfWeek.MONDAY

        // Get the dates that are an exception
        var selectedDates by rememberSaveable  {mutableStateOf(originalExceptions)}

        val state = rememberCalendarState(
            startMonth = startMonth,
            endMonth = endMonth,
            firstVisibleMonth = currentMonth,
            firstDayOfWeek = firstDayOfWeek,
        )

        // Square that displays a single day on the calendar
        @Composable
        fun Day(
            day: CalendarDay,
            today: LocalDate,
            isSelected: Boolean,
            onClick: (CalendarDay) -> Unit
        ) {

            val interactionSource = remember { MutableInteractionSource() }


            @Composable
            fun backgroundColor(): Color {

                // Selected weekdays are marked with dim background
                val bgAlpha = when {
                    selectionType == CalendarSelection.Single -> 1.0f
                    else -> 0.5f
                }
                val  bgColor = MaterialTheme.colorScheme.secondary.copy(alpha = bgAlpha)

                // Selected dates (that are visible) are marked with solid color background
                // Other date do not have a background color
                return when {
                    isSelected && day.position == DayPosition.MonthDate -> bgColor
                    else -> Color.Transparent
                }
            }

            @Composable
            fun backgroundShape() : Shape {
                return when {
                    selectionType == CalendarSelection.Single -> CircleShape
                    else -> RectangleShape
                }
            }

            @Composable
            fun textColor(): Color {

                var alph = 1.0f
                if (day.date.isBefore(today)) alph = 0.3f
                val txtSel = MaterialTheme.colorScheme.onSecondary.copy(alpha = alph)
                val txtNorm = MaterialTheme.colorScheme.onSurface.copy(alpha = alph)


                return when {
                    isSelected && day.position == DayPosition.MonthDate -> txtSel
                    day.position == DayPosition.MonthDate -> txtNorm
                    else -> Color.Transparent
                }
            }

            Box(
                modifier = Modifier
                    //.background(MaterialTheme.colorScheme.surfaceBright)

                    .indication(interactionSource = interactionSource, indication = null)
                    .aspectRatio(1f)
                    .background(color = backgroundColor(), shape = backgroundShape()),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.date.dayOfMonth.toString(),
                    color = textColor(),
                    fontWeight = if (day.date == today) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .indication(interactionSource = interactionSource, indication = null)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            enabled = day.position == DayPosition.MonthDate,
                            onClick = { onClick(day) }
                        )
                )
            }
        }

        // Strip of names of days above the calendar main area
        @Composable
        fun DaysOfWeekTitle() {
            // List of labels on the weekdays buttons
            val daysOfWeek =
                if (getPrefFirstDayOfWeek(parent) == "Su")
                    listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
                else
                    listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")

            Row(modifier = Modifier.fillMaxWidth()) {
                for (dayOfWeek in daysOfWeek) {
                    Text(
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        text = dayOfWeek,
                    )
                }
            }
        }

        @Composable
        fun MonthTitle(calendarMonth: CalendarMonth) {
            Column {
                Text(
                    text = calendarMonth.yearMonth.month.name.lowercase()
                        .replaceFirstChar { it.uppercaseChar() }
                            + "  " + calendarMonth.yearMonth.year,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                )
                DaysOfWeekTitle()
            }
        }

        @Composable
        fun HeaderText(date: LocalDate?): String {

            if (selectionType == CalendarSelection.Single) {
                if (date == null) return  stringResource(R.string.select_date)

                val dom = date.dayOfMonth
                val year = date.year
                val month = date.month.name.lowercase().replaceFirstChar{  it.titlecase(Locale.getDefault()) }
                val dow = date.dayOfWeek.name.lowercase().replaceFirstChar{  it.titlecase(Locale.getDefault()) }

                return String.format(Locale.ROOT, "%.3s, %d %.3s %d", dow, dom, month, year)
            }

            if (selectionType == CalendarSelection.Multiple) {
                return stringResource(R.string.dates_to_exclude)
            }

            return ""
        }



        // Multi-selection calendar: Is day selected?
        // It is if day is one of the weekDays (Days selected for repeating alarms) AND
        // it is not mentioned in the list of exception days
        @Composable
        fun isDaySelected (day: CalendarDay,
                           weekDays: Int=0,
                           selectedDates : String="",
                           selectedDate: LocalDate? = null) : Boolean {

            if (selectionType == CalendarSelection.Multiple) {
                // Convert the calendarDay to String of type yyyymmdd
                val formatters = DateTimeFormatter.ofPattern("uuuuMMdd")
                val dayString: String = day.date.format(formatters)

                // Search for date in list of exceptions
                // If found - then NOT selected
                if (selectedDates.contains(dayString)) return false

                val mask = 1 shl day.date.dayOfWeek.value % 7
                return (mask and weekDays) > 0
            }

            if (selectionType == CalendarSelection.Single){
                return selectedDate == day.date
            }

            return false
        }


        fun toggleDaySelection(day: CalendarDay, weekDays: Int, selectedDates : String) : String {

            Log.d("THE_TIME_MACHINE", "toggleDaySelection():  day: $day")
           var modifiedDates = selectedDates

            // If in the past - ignore
            if (day.date.isBefore(LocalDate.now()))
                return selectedDates

            // If not a preselected weekday - ignore
            val mask = 1 shl day.date.dayOfWeek.value%7
            if ((mask and weekDays) == 0)
                return selectedDates

            // Convert the calendarDay to String of type yyyymmdd
            val formatters = DateTimeFormatter.ofPattern("uuuuMMdd")
            val dayString: String = day.date.format(formatters)


            // Search for date in list of exceptions
            // If found - remove it from list
            modifiedDates = if (modifiedDates.contains(dayString))
                modifiedDates.replace(dayString, "")
            else
                "$modifiedDates $dayString"


            return modifiedDates
        }


        // Calendar Dialog - Single day selection
        if (showDialog ) {
            state.firstDayOfWeek =
                if (getPrefFirstDayOfWeek(parent) == "Su") DayOfWeek.SUNDAY
                else DayOfWeek.MONDAY
            val today = LocalDate.now()

            Dialog(onDismissRequest = { onDismiss() }) {
                AppTheme(dynamicColor = isDynamicColor) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(color = MaterialTheme.colorScheme.surfaceBright),
                    ) {
                        MaterialTheme {
                            Column(
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Top,
                                ) {/**/
                                Text(
                                    HeaderText(selectedDate),
                                    fontSize = 24.sp,
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(CenterHorizontally)
                                        .background(color = MaterialTheme.colorScheme.secondary)
                                        .padding(16.dp)
                                )

                                HorizontalCalendar(
                                    state = state,
                                    monthHeader = { MonthTitle(it) },
                                    dayContent = {
                                        Day(day = it,
                                            today = today,
                                            isSelected = isDaySelected(it, weekdays, selectedDates, selectedDate)
                                        ) { calendarDay ->
                                            if (selectionType == CalendarSelection.Multiple)
                                                selectedDates = toggleDaySelection(calendarDay, weekdays, selectedDates)
                                            if (selectionType == CalendarSelection.Single)
                                                selectedDate = if (selectedDate == calendarDay.date) null else calendarDay.date
                                        }
                                    }
                                )
                                // Cancel/OK Buttons
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    TextButton({ onDismiss() }) {
                                        Text(
                                            stringResource(id = R.string.cancel_general)
                                        )
                                    }
                                    TextButton({
                                        if (selectionType == CalendarSelection.Multiple)
                                            onOkClicked(selectedDates)
                                        if (selectionType == CalendarSelection.Single)
                                            onOkClicked(selectedDate)
                                    }) {
                                        Text(stringResource(id = R.string.ok_general))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private fun checkmarkClicked() {

        // Null checks
        if (setUpAlarmValues.hour == null || setUpAlarmValues.hour.value == null) return
        if (setUpAlarmValues.minute == null || setUpAlarmValues.minute.value == null) return
        if (setUpAlarmValues.label == null || setUpAlarmValues.label.value == null) return

        //val c = initParams!!.getLong("INIT_CREATE_TIME")


        // If modified alarm then use its old Create Time (id)
        // If new alarm then create it using a new Create Time (id)
        val item = if (isNewAlarm ||  isDuplicate) AlarmItem(
            setUpAlarmValues.hour.value!!,
            setUpAlarmValues.minute.value!!,
            setUpAlarmValues.label.value,
            true,
        )
        else AlarmItem(
            setUpAlarmValues.hour.value!!,
            setUpAlarmValues.minute.value!!,
            setUpAlarmValues.label.value,
            true,
            itemId
        )

        // Update OneOff Value in ViewModel
        item.isOneOff =
            setUpAlarmValues.isOneOff.value!! || (setUpAlarmValues.weekDays.value!! == 0)

        // Is it a specified date in the future
        item.futureDate = setUpAlarmValues.isFutureDate.value!!
        if (item.futureDate) {
            item.dayOfMonth = setUpAlarmValues.dayOfMonth.value!!
            item.month = setUpAlarmValues.month.value!!
            item.year = setUpAlarmValues.year.value!!
        }
        else {
            item.dayOfMonth = 0
            item.month = 0
            item.year = 0
        }

        Log.d("THE_TIME_MACHINE", "checkmarkClicked():  item.isOneOff = ${item.isOneOff} ; item.futureDate=${item.futureDate}")

        item.recalculateDate() // If is an explicit date and in the past - change date to the near future

        // Selected weekdays and dates that are exceptions
        item.weekDays = setUpAlarmValues.weekDays.value!!
        item.exceptionDatesStr = setUpAlarmValues.exceptionDates.value!!

        // Update Preferences
        item.ringDuration = setUpAlarmValues.ringDuration.value!!
        item.ringRepeat = setUpAlarmValues.ringRepeat.value!!
        item.snoozeDuration = setUpAlarmValues.snoozeDuration.value!!
        item.vibrationPattern = setUpAlarmValues.vibrationPattern.value!!
        item.alarmSound = setUpAlarmValues.alarmSound.value!!
        item.gradualVolume = setUpAlarmValues.gradualVolume.value!!


        // And finally:
        // Add or Update the entry on the list
        if (isNewAlarm ||  isDuplicate)
            alarmViewModel?.AddAlarm(item)
        else
            alarmViewModel?.UpdateAlarm(item)


        // Schedule this new/modified alarm
        item.Exec()

        // Display the Alarm List Fragment
        navyToAlarmList()

        /*
        if (parent != null) parent!!.supportFragmentManager
            .beginTransaction()
            //.replace(R.id.fragment_container_view, SetUpAlarmFragment::class.java, b)
            .replace(R.id.fragment_container_view, AlarmListScreen::class.java, null)
            .addToBackStack("tag5")
            .commit()

         */

        // Remove from list of selected alarms
        // parent!!.alarmViewModel.clearSelection(item.createTime)

    }
}





