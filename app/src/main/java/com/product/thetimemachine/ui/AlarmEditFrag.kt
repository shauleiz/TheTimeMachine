package com.product.thetimemachine.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.product.thetimemachine.R
import com.product.thetimemachine.ui.theme.AppTheme
import java.util.Calendar
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource


class AlarmEditFrag : Fragment() {
    private var parent: MainActivity? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the MainActivity as Parent
        parent = activity as MainActivity?
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
    }


    @Composable
    private fun AlarmEditFragDisplayTop() {
        var selectedList =  rememberSaveable { mutableStateOf<List<Boolean>> (mutableListOf(false, false,false, false,false, false, false,) )}
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
                        AlarmTypeBox(selectedList)
                    }
                }
            }
        }
    }

    @Composable
    private fun LabelField() {
        var value by rememberSaveable { mutableStateOf("") }
        var isFocused by rememberSaveable { mutableStateOf(false) }

        OutlinedTextField(
            value = value,
            onValueChange = { value = it },
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

        // Get Current time
        val currentTime = Calendar.getInstance()

        // Set initial time to current time
        val timePickerState = rememberTimePickerState(
            initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
            initialMinute = currentTime.get(Calendar.MINUTE),
            is24Hour = true,
        )
        TimePicker(
            state = timePickerState,
            layoutType = TimePickerDefaults.layoutType(),
            modifier = Modifier
                .wrapContentWidth(CenterHorizontally),
        )
    }

    @Composable
    private fun WeeklyOrOneOff () {
        var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
        val options = listOf(R.string.single, R.string.weekly)

        SingleChoiceSegmentedButtonRow(modifier = Modifier
            .fillMaxWidth()
            //.padding(8.dp)
        ) {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    onClick = { selectedIndex = index },
                    selected = index == selectedIndex
                ) {
                    Text(stringResource(id = options[index]))
                }
            }

        }
    }

    @Composable
    private fun CalendarButton(){
        Button(onClick = {}) {
            Icon(
                painter = painterResource(id = R.drawable.calendar_month_fill0_wght400_grad0_opsz24),
                contentDescription = stringResource(id = R.string.open_date_picker),
                )
        }
    }


    private fun onSelectionChange(index:Int,selectedList: MutableState<List<Boolean>>) {
        var selectedListLocal  = mutableListOf(false, false,false, false,false, false, false,)
        selectedListLocal = selectedList.value.toMutableList()
        selectedListLocal[index] = !selectedListLocal[index]
        selectedList.value = selectedListLocal
    }

    @Composable
    private fun DayButtons(selectedList: MutableState<List<Boolean>>) {
        val su_Weekdays = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa", )
        val mo_Weekdays = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su", )


        // TODO: Put selector here
        val weekdays = su_Weekdays

        var selectedOption by remember {
            mutableStateOf("")
        }


        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ){
            weekdays.forEachIndexed {   index, s ->
                Text(
                    text = s,
                    color = if ( selectedList.value[index]) {
                        MaterialTheme.colorScheme.background
                    } else {
                        MaterialTheme.colorScheme.onBackground
                    },
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(size = 12.dp,),)
                        .clickable { onSelectionChange(index, selectedList) }
                        .background(
                            if (selectedList.value[index]) {
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
    }

    @Composable
    private fun AlarmTypeBox(selectedList: MutableState<List<Boolean>>){
        Column(modifier = Modifier.padding(8.dp)){
            WeeklyOrOneOff()
            CalendarButton()
            DayButtons(selectedList)
        }
    }
}




