package com.product.thetimemachine.ui

import android.content.res.Configuration
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.product.thetimemachine.AlarmViewModel
import com.product.thetimemachine.Data.AlarmItem
import com.product.thetimemachine.R
import com.product.thetimemachine.ui.SettingsFragment.sound
import com.product.thetimemachine.ui.SettingsFragment.vibrate
import com.product.thetimemachine.ui.theme.AppTheme
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.Calendar
import java.util.Locale


class AlarmEditFrag : Fragment() {
    private var parent: MainActivity? = null
    private lateinit var setUpAlarmValues: AlarmViewModel.SetUpAlarmValues
    private var initParams: Bundle? = null
    private var isNewAlarm: Boolean = true
    private var isOneOff = true
    private var weekdays = 0;
    data class PrefData(
        val title: Int = 0,
        val currentValue: MutableState<String?>? = null,
        val origValue: MutableLiveData<String>? = null,
        val list: List<Pair<String, String>>? = null,
        val iconId: Int = 0,
        val showDialog: MutableState<Boolean>? = null,
    )



    // Menu Items
    val ringDurationList = listOf(
        Pair("15 Seconds", "15Seconds"), Pair("30 Seconds", "30Seconds"),
        Pair("45 Seconds", "45Seconds"), Pair("1 Minute", "60Seconds"),
        Pair("2 Minute", "120Seconds"), Pair("5 Minute", "300Seconds"),
    )
    val ringRepeatList = listOf(
        Pair("Never", "0T"), Pair("1 Time", "1T"),
        Pair("2 Times", "2T"), Pair("5 Times", "5T"),
        Pair("10 Times", "10T"), Pair("Forever", "100T"),
    )
    val snoozeDurationList = listOf(
        Pair("30 Seconds", "30Seconds"), Pair("1 Minute", "60Seconds"),
        Pair("2 Minute", "120Seconds"), Pair("3 Minute", "180Seconds"),
        Pair("5 Minutes", "300Seconds"), Pair("6 Minutes", "360Seconds"),
        Pair("7 Minutes", "420Seconds"), Pair("10 Minutes", "600Seconds"),
    )
    val vibrationPatternList = listOf(
        Pair("None", "none"), Pair("Single short beat", "ssb"),
        Pair("Three short beats", "tsb"), Pair("Single long beat", "slb"),
        Pair("Repeating short beats", "rsb"), Pair("Repeating long beats", "rlb"),
        Pair("Continuous", "cont"),
    )
    val alarmSoundList = listOf(
        Pair("No Sound (mute)", "silent"),
        Pair("Standard Digital", "a30_seconds_alarm_72117"),
        Pair("Clock Alarm", "clock_alarm_8761"),
        Pair("Digital Alarm", "digital_alarm_2_151919"),
        Pair("Digital Beep-Beep", "digital_alarm_clock_151920"),
        Pair("Electronic Alarm Clock", "electronic_alarm_clock_151927"),
        Pair("Old Mechanic Alarm Clock", "old_mechanic_alarm_clock_140410"),
        Pair("Oversimplified Alarm Clock", "oversimplified_alarm_clock_113180"),
        Pair("Rooster", "rooster"),
    )
    val gradualVolumeList = listOf(
        Pair("None", "00Seconds"), Pair("30 Seconds", "30Seconds"),
        Pair("1 Minute", "60Seconds"), Pair("2 Minute", "120Seconds"),
        Pair("3 Minute", "180Seconds"),
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the MainActivity as Parent
        parent = activity as MainActivity?
        initParams = arguments
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Start the compose display
        return ComposeView(requireContext()).apply { setContent { AlarmEditFragDisplayTop() } }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appToolbar =
            requireActivity().findViewById<Toolbar>(R.id.app_toolbar)
        appToolbar.setTitle(R.string.alarmsetup_title)
        (activity as AppCompatActivity?)!!.setSupportActionBar(appToolbar)
        parent!!.isDeleteAction = false
        parent!!.isSettingsAction = true
        parent!!.isEditAction = false
        parent!!.isDuplicateAction = false
        parent!!.setCheckmarkAction(true)
        parent!!.invalidateOptionsMenu()

        // Get the initial setup values from the ViewModel
        setUpAlarmValues = parent!!.alarmViewModel.setUpAlarmValues

        // Is it a new Alarm or Alarm to be edited
        isNewAlarm = initParams!!.getBoolean("INIT_NEWALARM", false)
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AlarmEditFragDisplayTop() {


        // List of all entries
        val listOfPrefs_ = listOf(
            PrefData(
                title = R.string.ring_and_snooze
            ),

            PrefData(
                title = R.string.ring_duration,
                currentValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.ringDuration.value) }),
                origValue = setUpAlarmValues.ringDuration,
                list = ringDurationList,
                iconId = R.drawable.music_note_fill0_wght400_grad0_opsz24,
                showDialog = remember { mutableStateOf(false) }),

            PrefData(
                title = R.string.times_to_keep_on_ringing,
                currentValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.ringRepeat.value) }),
                origValue = setUpAlarmValues.ringRepeat,
                list = ringRepeatList,
                iconId = R.drawable.music_note_fill0_wght400_grad0_opsz24,
                showDialog = remember { mutableStateOf(false) }
            ),


            PrefData(
                title = R.string.snooze_for,
                currentValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.snoozeDuration.value) }),
                origValue = setUpAlarmValues.snoozeDuration,
                list = snoozeDurationList,
                iconId = R.drawable.snooze_fill0_wght400_grad0_opsz24,
                showDialog = remember { mutableStateOf(false) }
            ),

            PrefData(
                title = R.string.sound_and_vibration
            ),

            PrefData(
                title = R.string.vibration_pattern,
                currentValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.vibrationPattern.value) }),
                origValue = setUpAlarmValues.vibrationPattern,
                list = vibrationPatternList,
                iconId = R.drawable.vibration_opsz24,
                showDialog = remember { mutableStateOf(false) }
            ),

            PrefData(
                title = R.string.alarm_sounds,
                currentValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.alarmSound.value) }),
                origValue = setUpAlarmValues.alarmSound,
                list = alarmSoundList,
                iconId = R.drawable.library_music_fill0_wght400_grad0_opsz24,
                showDialog = remember { mutableStateOf(false) }
            ),

            PrefData(
                title = R.string.gradual_volume_increase,
                currentValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.gradualVolume.value) }),
                origValue = setUpAlarmValues.gradualVolume,
                list = gradualVolumeList,
                iconId = R.drawable.gradual_increase_opsz24,
                showDialog = remember { mutableStateOf(false) }
            ),
        )
        val listOfPrefs = remember { mutableStateListOf<PrefData>()}
        listOfPrefs.addAll(listOfPrefs_)

        // State Variables
        var showCalendarDialog  by remember { mutableStateOf(false) }
        var calendarSelType : CalendarSelection by remember { mutableStateOf(CalendarSelection.Single) }
        val weekdays = rememberSaveable { mutableStateOf(weekdays) }
        val oneOff = rememberSaveable { mutableStateOf(isOneOff) }
        // Set initial time
        val timePickerState = rememberTimePickerState(
            initialHour = getInitialHour(),
            initialMinute = getInitialMinute(),
            is24Hour = SettingsFragment.pref_is24HourClock(),
        )

        // Event Handlers
        val setSelectedDays = { index: Int ->
            val i = if (SettingsFragment.pref_first_day_of_week() == "Mo") {
                if (index == 6) 0 else index + 1
            } else index
            weekdays.value = weekdays.value xor (1 shl i)
        }

        // Display Calendar Dialog
        DisplayCalendar(showCalendarDialog, {showCalendarDialog=false ; }, calendarSelType)

        AppTheme(dynamicColor = true) {
            Surface {
                MaterialTheme {
                    Column(
                        horizontalAlignment = CenterHorizontally, //of children
                        modifier = Modifier
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
                                    showCalendarDialog= true
                                    calendarSelType = if (it) CalendarSelection.Single else CalendarSelection.Multiple
                                    Log.d("THE_TIME_MACHINE", "AlarmTypeBox(): showCalendarDialog=$showCalendarDialog ; calendarSelType=$calendarSelType");

                                },
                                oneOff,
                                {oneOff.value = it},
                                weekdays,
                                setSelectedDays,
                                timePickerState,)

                            /* Preferences */
                            ItemPreferences(listOfPrefs)

                        } else {
                            // Landscape
                            Row(verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.Start,
                                modifier = Modifier
                                .fillMaxWidth()
                            ){
                                /* Time Picker */
                                TimePickerField(timePickerState)

                                VerticalDivider(thickness = 4.dp,)

                                Column() {
                                    /* Single/Weekly button */
                                    AlarmTypeBox(
                                        {
                                            calendarSelType = if (it) CalendarSelection.Single else CalendarSelection.Multiple
                                            showCalendarDialog= true},
                                        oneOff,
                                        {oneOff.value = it},
                                        weekdays,
                                        setSelectedDays,
                                        timePickerState,)

                                    /* Preferences */
                                    ItemPreferences(listOfPrefs)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun LabelField() {
        // If editing alarm then get the label
        val label =
            if (!isNewAlarm && !setUpAlarmValues.label.value.isNullOrEmpty()) setUpAlarmValues.label.value
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
        timePickerState.is24hour = SettingsFragment.pref_is24HourClock()

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
        var buttonType = if (oneOff) 0 else 1


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
        val firstDayInWeek = if (SettingsFragment.pref_first_day_of_week() == "Su") 0 else 1

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
                        .padding(horizontal = 4.dp,)
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
        var out = ""

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

        if (dd > 0 && mm > 0 && yy > 0) calendar[yy, mm, dd, hour, minute] = 0
        else {
            calendar[Calendar.HOUR_OF_DAY] = hour
            calendar[Calendar.MINUTE] = minute
            if (isInThePast(calendar.timeInMillis)) calendar.timeInMillis =
                calendar.timeInMillis + 24 * 60 * 60 * 1000
            //calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),h,m,0);
        }


        // Create an output string
        val format =
            SimpleDateFormat(getString(R.string.time_format_display), Locale.US)
        out = format.format(calendar.timeInMillis)

        // Determine Today/Tomorrow
        if (isToday(calendar.timeInMillis)) out += getString(R.string.today_in_brackets)
        else if (isTomorrow(calendar.timeInMillis)) {
            out += getString(R.string.tomorrow_in_brackets)
        }

        //Log.d("THE_TIME_MACHINE", "displayTargetAlarm(): " + out);
        return out
    }



    @Composable
    private fun ItemPreferences(listOfPrefs: List<PrefData>) {

        // Preference related constants
        val typography = MaterialTheme.typography
        val styledText = typography.titleMedium
        val styledSecondaryText = typography.bodyMedium // Alpha=0.5f
        val styledOverlineText = typography.labelSmall
        val styledTrailing = typography.bodySmall


        // TODO: Write a callback function to give a sample of vibration & Sound pattern
        // Use SettingsFragment::vibrate and SettingsFragment::sound
        // Call from Row/Radio button OnClick
        fun playVibOrSound(index : Int, pattern : String) : Unit{
            Log.d("THE_TIME_MACHINE", "playVibOrSound():  index = $index ; pattern = $pattern")
            if (listOfPrefs[index].title == R.string.vibration_pattern) vibrate(pattern)
            else if (listOfPrefs[index].title == R.string.alarm_sounds) sound(pattern)
        }

        // Display Dialog
        listOfPrefs.forEachIndexed() { index, entry ->
            if (entry.showDialog != null && entry.showDialog.value) {

                var selected by rememberSaveable { mutableStateOf( entry.currentValue!!.value)}

                Dialog(onDismissRequest = { entry.showDialog.value = false }) {
                    Column(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceBright,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start,
                    ) {
                        // Title
                        Text(
                            text = stringResource(id = entry.title),
                            style = styledTrailing,//MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Left,
                            modifier = Modifier
                                .padding(top = 32.dp, bottom = 16.dp, start = 16.dp)
                                .fillMaxWidth()
                        )

                        // Loop on all entries in the list
                        // Every entry is a row with a radio button and text
                        // The row is clickable
                        entry.list!!.forEachIndexed { _, pair ->
                            Row(horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(bottom = 16.dp, start = 16.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        selected = pair.second
                                        playVibOrSound(index, pair.second)
                                    }
                            ) {
                                RadioButton(
                                    selected = selected == pair.second,
                                    onClick = null)

                                Text(pair.first)
                            }
                        }

                        // Cancel/OK Buttons
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()){
                            TextButton({ entry.showDialog.value = false}){Text(stringResource(id = R.string.cancel_general))}
                            TextButton({
                                entry.currentValue!!.value = selected
                                entry.origValue!!.value = selected
                                entry.showDialog.value = false
                            }) {
                                Text(stringResource(id = R.string.ok_general))}
                        }
                    }
                }


            }
        }

        /// Composable parts of the Preferences display as Internal Functions

        // Icon to display
        // Id imageId==0 then do nothing
        @Composable
        fun PrefIcon(imageId: Int, title: Int): Unit {
            if (imageId == 0) return

            Icon(
                painter = painterResource(id = imageId),
                contentDescription = stringResource(id = title),
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer,
                        shape = CircleShape
                    )
                    .padding(16.dp)
                    //.align(Alignment.CenterVertically)
                    .size(30.dp)
            )
        }

        // Title of preference
        @Composable
        fun PrefTitle(titleID: Int): Unit {
            Text(
                text = stringResource(id = titleID),
                style = styledTrailing,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
        }

        fun getEntryValueStr(value: String, list: List<Pair<String, String>>): String {
            list.forEach() { if (it.second == value) return it.first }
            return ""
        }

        @Composable
        fun PrefSelValue(value: String, list: List<Pair<String, String>>): Unit {
            Text(
                text = getEntryValueStr(value, list),
                style = styledTrailing,
                fontSize = 16.sp,
                modifier = Modifier
                    .alpha(0.5f)
                    .padding(top = 6.dp)
            )
        }

        @Composable
        fun PrefRow(index: Int): Unit {
            if (listOfPrefs[index].list != null) { // Normal row
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClickLabel = stringResource(id = listOfPrefs[index].title)) {
                            listOfPrefs[index].showDialog!!.value = true
                        }
                        .padding(bottom = 16.dp, top = 16.dp),
                ) {
                    PrefIcon(listOfPrefs[index].iconId, listOfPrefs[index].title)
                    Column(
                        modifier = Modifier.padding(start = 24.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        PrefTitle(listOfPrefs[index].title)
                        PrefSelValue(
                            value = listOfPrefs[index].currentValue!!.value!!,
                            list = listOfPrefs[index].list!!
                        )
                    }
                }
            } else { // Header
                Text(
                    text = stringResource(listOfPrefs[index].title),
                    style = styledOverlineText,//MaterialTheme.typography.bodyMedium,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp, top = 40.dp)
                )
                HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
            }
        }


        // List of all preferences and section titles
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            listOfPrefs.forEachIndexed() { index, _ ->  key(index){PrefRow(index = index)}  }
        }
    }


    enum class CalendarOrient {
        Vertical,
        Horizontal
    }

    enum class CalendarSelection{
        Single,
        Multiple,
        Range,
    }

    @Composable
    private fun DisplayCalendar(showDialog : Boolean,  onDismiss : ()->Unit, selectionType : CalendarSelection){


        val currentMonth = remember { YearMonth.now() }
        val startMonth = remember { currentMonth.minusMonths(100) } // Adjust as needed
        val endMonth = remember { currentMonth.plusMonths(100) } // Adjust as needed
        val firstDayOfWeek =
            if (SettingsFragment.pref_first_day_of_week() == "Su") DayOfWeek.SUNDAY
            else DayOfWeek.MONDAY
        var selectedDate by rememberSaveable { mutableStateOf<LocalDate?>(null) }

        val state = rememberCalendarState(
            startMonth = startMonth,
            endMonth = endMonth,
            firstVisibleMonth = currentMonth,
            firstDayOfWeek = firstDayOfWeek,
        )

        // Square that displays a single day on the calendar
        @Composable
        fun Day(day: CalendarDay, isSelected: Boolean, onClick: (CalendarDay) -> Unit) {
            Box(
                modifier = Modifier
                    //.background(MaterialTheme.colorScheme.surfaceBright)
                    .aspectRatio(1f)
                    .background(color = if (isSelected) Color.Green else Color.Transparent)
                    .clickable(
                        enabled = day.position == DayPosition.MonthDate,
                        onClick = { onClick(day) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.date.dayOfMonth.toString(),
                    color = if (day.position == DayPosition.MonthDate) MaterialTheme.colorScheme.onSurface else Color.Gray
                )
            }
        }

        // Strip of names of days above the calendar main area
        @Composable
        fun DaysOfWeekTitle() {
            // List of labels on the weekdays buttons
            val daysOfWeek =
                if (SettingsFragment.pref_first_day_of_week() == "Su")
                    listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
                else
                    listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")

            Row(modifier = Modifier.fillMaxWidth()) {
                for (dayOfWeek in daysOfWeek) {
                    Text(
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        text = dayOfWeek,)
                }
            }
        }

        if (showDialog) {
            state.firstDayOfWeek = if (SettingsFragment.pref_first_day_of_week() == "Su") DayOfWeek.SUNDAY
            else DayOfWeek.MONDAY
            Dialog(onDismissRequest = { onDismiss() ; selectedDate=null }) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceBright,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .fillMaxWidth(),
                ) {
                    Text("Calendar", //TODO: Selected date
                        fontSize = 30.sp,
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                            .background(MaterialTheme.colorScheme.secondary)
                            .padding(16.dp)
                    )
                    DaysOfWeekTitle()
                    HorizontalCalendar(
                        state = state,
                        dayContent  = { day ->
                            Day(day, isSelected = selectedDate == day.date) { day ->
                                selectedDate = if (selectedDate == day.date) null else day.date
                            }
                        }
                    )
                }
            }
        }
    }

    fun checkmarkClicked() {

        // Null checks
        if (setUpAlarmValues == null || setUpAlarmValues.hour == null || setUpAlarmValues.hour.value == null) return
        if (setUpAlarmValues.minute == null || setUpAlarmValues.minute.value == null) return
        if (setUpAlarmValues.label == null || setUpAlarmValues.label.value == null) return

        val c = initParams!!.getLong("INIT_CREATE_TIME")


        // If modified alarm then use its old Create Time (id)
        // If new alarm then create it using a new Create Time (id)
        val item = if (!isNewAlarm) AlarmItem(
            setUpAlarmValues.hour.value!!,
            setUpAlarmValues.minute.value!!,
            setUpAlarmValues.label.value,
            true,
            initParams!!.getLong("INIT_CREATE_TIME")
        )
        else AlarmItem(
            setUpAlarmValues.hour.value!!,
            setUpAlarmValues.minute.value!!,
            setUpAlarmValues.label.value,
            true
        )

        // Update OneOff Value in ViewModel
        item.isOneOff =
            setUpAlarmValues.isOneOff.value!! || (setUpAlarmValues.weekDays.value!! == 0)

        // Selected weekdays
        item.weekDays = setUpAlarmValues.weekDays.value!!

        // Update Preferences
        item.ringDuration = setUpAlarmValues.ringDuration.value!!
        item.ringRepeat = setUpAlarmValues.ringRepeat.value!!
        item.snoozeDuration = setUpAlarmValues.snoozeDuration.value!!
        item.vibrationPattern = setUpAlarmValues.vibrationPattern.value!!
        item.alarmSound = setUpAlarmValues.alarmSound.value!!
        item.gradualVolume = setUpAlarmValues.gradualVolume.value!!


        // And finally:
        // Add or Update the entry on the list
        if (isNewAlarm)
            parent!!.alarmViewModel.AddAlarm(item)
        else
            parent!!.alarmViewModel.UpdateAlarm(item)


        // Schedule this new/modified alarm
        item.Exec()

        // Display the Alarm List Fragment
        if (parent != null) parent!!.supportFragmentManager
            .beginTransaction()
            //.replace(R.id.fragment_container_view, SetUpAlarmFragment::class.java, b)
            .replace(R.id.fragment_container_view, AlarmListFragment::class.java, null)
            .addToBackStack("tag5")
            .commit()

        // Remove from list of selected alarms
        // parent!!.alarmViewModel.clearSelection(item.createTime)

    }
}





