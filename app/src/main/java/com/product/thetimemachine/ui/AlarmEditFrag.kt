package com.product.thetimemachine.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.product.thetimemachine.AlarmViewModel
import com.product.thetimemachine.Data.AlarmItem
import com.product.thetimemachine.R
import com.product.thetimemachine.ui.theme.AppTheme
import java.util.Calendar


class AlarmEditFrag : Fragment() {
    private var parent: MainActivity? = null
    private lateinit var setUpAlarmValues: AlarmViewModel.SetUpAlarmValues
    private var initParams: Bundle? = null
    private var isNewAlarm: Boolean = true
    private var isOneOff = true
    private var weekdays = 0;


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


    @Composable
    private fun AlarmEditFragDisplayTop() {
        val weekdays = rememberSaveable { mutableStateOf(weekdays) }
        val oneOff = rememberSaveable { mutableStateOf(isOneOff) }

        Log.d("THE_TIME_MACHINE", "AlarmEditFragDisplayTop():  weekdays = $weekdays")

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

                        /* Time Picker */
                        TimePickerField()

                        /* Single/Weekly button */
                        AlarmTypeBox(weekdays, oneOff)
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
    private fun TimePickerField() {

        var hour: Int
        var minute: Int

        ///// Feed H:M data to Time Picker
        // Time from Alarm to edit
        if (!isNewAlarm) {
            hour = setUpAlarmValues.hour.value!!
            minute = setUpAlarmValues.minute.value!!
        } else {
            // Get Current time
            val currentTime = Calendar.getInstance()
            hour = currentTime.get(Calendar.HOUR_OF_DAY)
            minute = currentTime.get(Calendar.MINUTE)
        }

        // Set initial time
        val timePickerState = rememberTimePickerState(
            initialHour = hour,
            initialMinute = minute,
            is24Hour = true, // TODO: Get from preferences
        )

        // Display Time Picker
        TimePicker(
            state = timePickerState,
            layoutType = TimePickerDefaults.layoutType(),
            modifier = Modifier
                .wrapContentWidth(CenterHorizontally),
        )

        // Update H:M Values
        setUpAlarmValues.hour.value = timePickerState.hour
        setUpAlarmValues.minute.value = timePickerState.minute


    }

    @Composable
    private fun WeeklyOrOneOff(oneOff: Boolean, onClick:(Boolean) -> Unit) {

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
                    onClick = { selectedIndex = index ; onClick(index == 0)},
                    selected = index == selectedIndex
                ) {
                    Text(stringResource(id =label))
                }
            }

        }

        // Write back to ViewModel oneOff variable
        setUpAlarmValues.setOneOff(selectedIndex == 0)

    }

    @Composable
    private fun CalendarButton(selectedDays : Int , oneOff: Boolean) {

        // Type of button: 0=One Off ; 1=Weekly
        var buttonType = if (oneOff) 0 else 1


        Button(
            onClick = {}, // TODO: Launch the correct calendar
            enabled = (oneOff ||  selectedDays!=0)
        ) {
            Icon(
                painter = painterResource(id = if (buttonType==0)
                    R.drawable.calendar_month_fill0_wght400_grad0_opsz24
                else R.drawable.calendar_month_24dp_fill0_wght400_grad0_opsz24_x),

                contentDescription = stringResource(id = R.string.open_date_picker),
            )
        }
    }


    @Composable
    private fun DayButtons(
        selectedDaysExt: Int,
        onSel: (Int) -> Unit
    ) {

        // If first day in week is Sunday then 0 else 1
        val firstDayInWeek =  if (SettingsFragment.pref_first_day_of_week() == "Su") 0 else 1

        // List of labels on the weekdays buttons
        val weekdays =
            if (firstDayInWeek == 0)
        listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
        else
            listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")

        // Shift selected days to suit a Monday-week
        var selectedDays = selectedDaysExt
        if (firstDayInWeek == 1){
            val sunday = selectedDays and 1
            selectedDays = selectedDays shr 1
            selectedDays+=sunday*0x40
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
                    color = if  ((selectedDays and (1 shl index)) >0) {
                        MaterialTheme.colorScheme.background
                    } else {
                        MaterialTheme.colorScheme.onBackground
                    },
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(size = 12.dp))
                        .clickable { onSel(index) }
                        .background(
                            if ((selectedDays and (1 shl index)) > 0) {
                                MaterialTheme.colorScheme.onBackground
                            } else {
                                MaterialTheme.colorScheme.primaryContainer
                            }
                        )
                        .padding(
                            vertical = 12.dp,
                            horizontal = 16.dp,
                        ),
                )
            }
        }


        // If a Monday week - re-adjust selected days to norm (=Sunday week)
        if (firstDayInWeek == 1){
            selectedDays= selectedDays shl 1
            if ((selectedDays and 0x80) >0) selectedDays+=1
            selectedDays = selectedDays and 0x7F
        }
        // Write back to ViewModel oneOff variable
        setUpAlarmValues.setWeekDays(selectedDays)
    }

    @Composable
    private fun AlarmTypeBox(selectedDaysx : MutableState<Int>, oneOff:MutableState<Boolean>) {

        // Hoisted State: One Off
        var oneOff by rememberSaveable { mutableStateOf(oneOff.value) }
        var setOneOff =  {v : Boolean-> oneOff = v}



        var selectedDays by rememberSaveable { mutableStateOf(selectedDaysx.value) }
        var setSelectedDays = { index: Int ->
            var i:Int
            i = if (SettingsFragment.pref_first_day_of_week() == "Mo"){
                if (index==6) 0 else index+1
            } else index
            selectedDays = selectedDays xor (1 shl i)
        }

        Log.d("THE_TIME_MACHINE", "AlarmTypeBox():  selectedDaysx = $selectedDaysx")

        Column(modifier = Modifier.padding(8.dp)) {
            WeeklyOrOneOff(oneOff, setOneOff)
            CalendarButton(selectedDays, oneOff)
            DayButtons(selectedDays, setSelectedDays)
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
        item.isOneOff = setUpAlarmValues.isOneOff.value!! ||  (setUpAlarmValues.weekDays.value!! == 0)

        // Selected weekdays
        item.weekDays = setUpAlarmValues.weekDays.value!!



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




