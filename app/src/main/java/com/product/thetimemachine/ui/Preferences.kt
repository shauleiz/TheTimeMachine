package com.product.thetimemachine.ui

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.CountDownTimer
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.MutableLiveData
import com.product.thetimemachine.AlarmService
import com.product.thetimemachine.AlarmViewModel
import com.product.thetimemachine.Application.TheTimeMachineApp
import com.product.thetimemachine.R


/***********************************************************************************************/
/* *
*  *  General Preference Section
* */

data class PrefData(
    val title: Int = 0,
    val currentValue: MutableState<String?>? = null,
    val origValue: MutableState<String?>? = null,
    val list: List<Pair<String, String>>? = null,
    val iconId: Int = 0,
    val showDialog: MutableState<Boolean>? = null,
    val isDialog: Boolean = true,
    val prefKey: Preferences.Key<String>? = null
)

// TODO: Replace Strings
// Menu Items
val ringDurationList = listOf(
    Pair("15 Seconds", "15Seconds"), Pair("30 Seconds", "30Seconds"),
    Pair("45 Seconds", "45Seconds"), Pair("1 Minute", "60Seconds"),
    Pair("2 Minutes", "120Seconds"), Pair("5 Minutes", "300Seconds"),
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
val firstDayList = listOf(Pair("Sunday", "Su"), Pair("Monday", "Mo"))

val timeFormatList = listOf(
    Pair("12h Clock", "h12"),
    Pair("24h Clock", "h24")
)
val sortTypeList = listOf(
    Pair("By time set", "by_time_set"), Pair("By alarm time", "by_alarm_time"),
    Pair("Alphabetically", "alphabetically"))

val languageList = listOf (
    Pair("English","en"), Pair("Hebrew", "iw"))

object SoundObj {

    @Volatile private var timer : CountDownTimer? = null
    const val TICK : Long = 1000


    fun playSound(pattern: String?, duration : Long){

        // Start by killing the sound and killing the timer
        killSound()

        Log.d("THE_TIME_MACHINE", "playSound()[1]:  pattern = $pattern ; duration = $duration")
        // Null or Empty pattern OR non-positive sound duration -> return
        if (pattern.isNullOrEmpty() || duration <= 0) {
            return
        }


        // Create a count down timer that stops the sound
        timer  = object: CountDownTimer(duration, TICK) {

            override fun onTick(p0: Long) {
                Log.d("THE_TIME_MACHINE", "playSound()::onTick[4]:     pattern = $pattern ; duration = $duration ; p0 = $p0")
            }

            override fun onFinish() {
                Log.d("THE_TIME_MACHINE", "playSound()::onFinish[2]:  pattern = $pattern ; duration = $duration")
                killSound() }
        }

        // Start timer and play pattern
        timer?.start()
        AlarmService.sound(TheTimeMachineApp.appContext, pattern)
    }

    fun killSound(){
        timer?.cancel()
        Log.d("THE_TIME_MACHINE", "playSound()::Kill[3]")
        AlarmService.sound(TheTimeMachineApp.appContext, null)}
}

object VibrateObj {

    @Volatile private var timer : CountDownTimer? = null
    const val TICK : Long = 1000


    fun playVibrate(pattern: String?, duration : Long){


        if (pattern.isNullOrEmpty() || duration <= 0) {
            killVibrate()
            return
        }

        killVibrate()

        timer = object: CountDownTimer(duration, TICK) {

            override fun onTick(p0: Long) {
                Log.d("THE_TIME_MACHINE", "onTick(): pattern = $pattern ; duration = $duration")
            }

            override fun onFinish() {
                killVibrate()
            }
        }

        timer?.start()
        Log.d("THE_TIME_MACHINE", "playVibrate(): pattern = $pattern ; duration = $duration")
        AlarmService.VibrateEffect(TheTimeMachineApp.appContext, pattern)
    }

    private fun killVibrate(){
        timer?.cancel()

        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                TheTimeMachineApp.appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            TheTimeMachineApp.appContext.getSystemService(VIBRATOR_SERVICE) as Vibrator
        }


        if (vibrator.hasAmplitudeControl()) Log.d("THE_TIME_MACHINE", "vibrate(): hasAmplitudeControl")
        else Log.d("THE_TIME_MACHINE", "vibrate(): No AmplitudeControl")
        vibrator.cancel()
    }
}

@Composable
fun getListOfItemPreferences(setUpAlarmValues: AlarmViewModel.SetUpAlarmValues) : List<PrefData>{
    return listOf(
        PrefData(
            title = R.string.ring_and_snooze
        ),

        PrefData(
            title = R.string.ring_duration,
            currentValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.ringDuration.value) }),
            origValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.ringDuration.value) }),
            list = ringDurationList,
            iconId = R.drawable.music_note_fill0_wght400_grad0_opsz24,
            showDialog = rememberSaveable { mutableStateOf(false) }),

        PrefData(
            title = R.string.times_to_keep_on_ringing,
            currentValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.ringRepeat.value) }),
            origValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.ringRepeat.value) }),
            list = ringRepeatList,
            iconId = R.drawable.music_note_fill0_wght400_grad0_opsz24,
            showDialog = rememberSaveable { mutableStateOf(false) }
        ),


        PrefData(
            title = R.string.snooze_for,
            currentValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.snoozeDuration.value) }),
            origValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.snoozeDuration.value) }),
            list = snoozeDurationList,
            iconId = R.drawable.snooze_fill0_wght400_grad0_opsz24,
            showDialog = rememberSaveable { mutableStateOf(false) }
        ),

        PrefData(
            title = R.string.sound_and_vibration
        ),

        PrefData(
            title = R.string.vibration_pattern,
            currentValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.vibrationPattern.value) }),
            origValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.vibrationPattern.value) }),
            list = vibrationPatternList,
            iconId = R.drawable.vibration_opsz24,
            showDialog = rememberSaveable { mutableStateOf(false) }
        ),

        PrefData(
            title = R.string.alarm_sounds,
            currentValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.alarmSound.value) }),
            origValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.alarmSound.value) }),
            list = alarmSoundList,
            iconId = R.drawable.library_music_fill0_wght400_grad0_opsz24,
            showDialog = rememberSaveable { mutableStateOf(false) }
        ),

        PrefData(
            title = R.string.gradual_volume_increase,
            currentValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.gradualVolume.value) }),
            origValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.gradualVolume.value) }),
            list = gradualVolumeList,
            iconId = R.drawable.gradual_increase_opsz24,
            showDialog = rememberSaveable { mutableStateOf(false) }
        ),
    )
}

@Composable
fun getListOfGeneralPreferences(setUpAlarmValues: UserPreferences): List<PrefData> {

    val timeFormatList = listOf(
        PrefData(
            title = R.string.general
        ),

        PrefData(
            title = R.string.clock_format,
            prefKey = PreferencesKeys.KEY_H12_24,
            currentValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.hour12Or24) }),
            origValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.hour12Or24) }),
            list = timeFormatList,
            iconId = R.drawable.mdi__wrench_clock_outline,
            showDialog = rememberSaveable { mutableStateOf(false) }),

        PrefData(
            title = R.string.first_day_of_week,
            prefKey = PreferencesKeys.KEY_FIRST_DAY,
            currentValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.firstDayWeek) }),
            origValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.firstDayWeek) }),
            list = firstDayList,
            iconId = R.drawable.today_fill0_wght400_grad0_opsz24,
            showDialog = rememberSaveable { mutableStateOf(false) }),

        PrefData(
            title = R.string.language_list,
            prefKey = PreferencesKeys.KEY_LANGUAGE,
            currentValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.language) }),
            origValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.language) }),
            list = languageList,
            iconId = R.drawable.language_opsz24,
            showDialog = rememberSaveable { mutableStateOf(false) }
        ),

        PrefData(
            title = R.string.ring_and_snooze
        ),

        PrefData(
            title = R.string.ring_duration,
            prefKey = PreferencesKeys.KEY_RING_DURATION,
            currentValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.ringDuration) }),
            origValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.ringDuration) }),
            list = ringDurationList,
            iconId = R.drawable.music_note_fill0_wght400_grad0_opsz24,
            showDialog = rememberSaveable { mutableStateOf(false) }),

        PrefData(
            title = R.string.times_to_keep_on_ringing,
            prefKey = PreferencesKeys.KEY_RING_REPEAT,
            currentValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.ringRepeat) }),
            origValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.ringRepeat) }),
            list = ringRepeatList,
            iconId = R.drawable.music_note_fill0_wght400_grad0_opsz24,
            showDialog = rememberSaveable { mutableStateOf(false) }
        ),


        PrefData(
            title = R.string.snooze_for,
            prefKey = PreferencesKeys.KEY_SNOOZE_DURATION,
            currentValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.snoozeDuration) }),
            origValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.snoozeDuration) }),
            list = snoozeDurationList,
            iconId = R.drawable.snooze_fill0_wght400_grad0_opsz24,
            showDialog = rememberSaveable { mutableStateOf(false) }
        ),



        PrefData(
            title = R.string.vibration_pattern,
            prefKey = PreferencesKeys.KEY_VIBRATION_PATTERN,
            currentValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.vibrationPattern) }),
            origValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.vibrationPattern) }),
            list = vibrationPatternList,
            iconId = R.drawable.vibration_opsz24,
            showDialog = rememberSaveable { mutableStateOf(false) }
        ),

        PrefData(
            title = R.string.alarm_sounds,
            prefKey = PreferencesKeys.KEY_ALARM_SOUND,
            currentValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.alarmSound) }),
            origValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.alarmSound) }),
            list = alarmSoundList,
            iconId = R.drawable.library_music_fill0_wght400_grad0_opsz24,
            showDialog = rememberSaveable { mutableStateOf(false) }
        ),

        PrefData(
            title = R.string.gradual_volume_increase,
            prefKey = PreferencesKeys.KEY_GRADUAL_VOLUME,
            currentValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.gradualVolume) }),
            origValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.gradualVolume) }),
            list = gradualVolumeList,
            iconId = R.drawable.gradual_increase_opsz24,
            showDialog = rememberSaveable { mutableStateOf(false) }
        ),

        PrefData(
            title = R.string.alarm_list_conf
        ),

        PrefData(
            title = R.string.sort_alarms_list,
            prefKey = PreferencesKeys.KEY_SORT_TYPE,
            currentValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.sortType) }),
            origValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.sortType) }),
            list = sortTypeList,
            iconId = R.drawable.sort_fill0_wght400_grad0_opsz24,
            showDialog = rememberSaveable { mutableStateOf(false) }
        ),

        PrefData(
            title = R.string.inactive_alarms_at_the_bottom,
            prefKey = PreferencesKeys.KEY_SORT_SEPARATE,
            currentValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.sortSeparate) }),
            origValue = (rememberSaveable { mutableStateOf(setUpAlarmValues.sortSeparate) }),
            list = sortTypeList,
            iconId = R.drawable.sort_fill0_wght400_grad0_opsz24, // TODO: Replace icon
            showDialog = rememberSaveable { mutableStateOf(false) },
            isDialog = false
        ),


        )

    return timeFormatList
}

fun getListOfPrefLiveData (setUpAlarmValues: AlarmViewModel.SetUpAlarmValues) : List< MutableLiveData<String>?>{
    return listOf (
        null,
        setUpAlarmValues.ringDuration, setUpAlarmValues.ringRepeat, setUpAlarmValues.snoozeDuration,
        null,
        setUpAlarmValues.vibrationPattern, setUpAlarmValues.alarmSound, setUpAlarmValues.gradualVolume
        )
}

/*
        Show the list of preferences
        listOfPrefs: List of all preferences
        onOK:   Called when user changes a value of a preference and clicks OK
                index: The index of the preference
                value: The new value of the preference

       The visual structure is of a single column of PrefRow's
       Each PrefRow is a structure representing a Preference
 */
@Composable
fun ShowPreferences(listOfPrefs: List<PrefData>, onOK : (index: Int, value : String?)->Unit) {
    val typography = MaterialTheme.typography
    val styledOverLineText = typography.labelSmall
    val styledTrailing = typography.bodySmall

    // TODO: Write a callback function to give a sample of vibration & Sound pattern
    // Use SettingsFragment::vibrate and SettingsFragment::sound
    // Call from Row/Radio button OnClick
    fun playVibOrSound(index : Int, pattern : String?) {
        Log.d("THE_TIME_MACHINE", "playVibOrSound():  index = $index ; pattern = $pattern")
        if (listOfPrefs[index].title == R.string.vibration_pattern) VibrateObj.playVibrate(pattern, 5000)
        else if (listOfPrefs[index].title == R.string.alarm_sounds) SoundObj.playSound(pattern, 4000)
    }

    // Loop on all Preferences to find the one that needs to display its dialog:
    // 1. Variable showDialog of this Preference is TRUE
    // 2. AND this row corresponds with a dialog (as opposed to switch)
    listOfPrefs.forEachIndexed { index, entry ->
        if (entry.showDialog != null && entry.showDialog.value && entry.isDialog) {

            // Display Preference Dialog when a Preference row is clicked (index of Preference)
            Dialog(onDismissRequest = {
                playVibOrSound(index, null) // Mute
                entry.showDialog.value = false
                entry.currentValue!!.value = entry.origValue!!.value }) {

                // Column: Text (Dialog title) then all radio buttons then OK/Cancel buttons
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
                    // Dialog Title: e.g. "Clock Format"
                    Text(
                        text = stringResource(id = entry.title),
                        style = styledTrailing,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Left,
                        modifier = Modifier
                            .padding(top = 32.dp, bottom = 16.dp, start = 16.dp)
                            .fillMaxWidth()
                    )

                    // Loop on all entries in the list of possible values of Preference
                    // Every entry is a row with a radio button and text
                    // The row is clickable
                    entry.list!!.forEachIndexed { _, pair ->
                        Row(horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(bottom = 16.dp, start = 16.dp)
                                .fillMaxWidth()
                                .clickable {
                                    // The radio button was selected - currentValue changes
                                    entry.currentValue!!.value = pair.second
                                    playVibOrSound(index, pair.second)
                                }
                        ) {
                            // Radio button: Selected if equal to currentValue
                            RadioButton(
                                selected = entry.currentValue!!.value == pair.second,
                                onClick = null
                            )
                            // Text associated with the radio button
                            Text(pair.first)
                        }
                    }

                    // Cancel/OK Buttons
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()){

                        // Cancel: Stops sound/vibration and resets CurrentValue to origValue
                        TextButton({
                            playVibOrSound(index, null) // Mute
                            entry.showDialog.value = false
                            entry.currentValue!!.value = entry.origValue!!.value})
                        {
                            Text(stringResource(id = R.string.cancel_general)
                            )
                        }

                        // OK: Stops sound/vibration and calls onOK callback:
                        //      onOK:
                        //          index: index of Preference in Preference List
                        //          currentValue: Value of selected preference
                        TextButton({
                            playVibOrSound(index, null) // Mute
                            onOK(index, entry.currentValue!!.value) })
                        {
                            Text(stringResource(id = R.string.ok_general))
                        }
                    }
                }
            }


        }
    }

    /// Composable parts of the Preferences display as Internal Functions

    // Icon to display
    // Id imageId==0 then do nothing
    @Composable
    fun PrefIcon(imageId: Int, title: Int) {
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
    fun PrefTitle(titleID: Int){
        Text(
            text = stringResource(id = titleID),
            style = styledTrailing,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        )
    }

    // Get the string that corresponds with the display-value of a selected value of a Preference
    fun getEntryValueStr(value: String, list: List<Pair<String, String>>): String {
        list.forEach { if (it.second == value) return it.first }
        return ""
    }

    //  Display the current value (e.g. 24h Clock) under the title
    //  Pass the current selected preference value and the list of possible values
    //  Get the text by entering the current selected value and the list of values
    @Composable
    fun PrefSelValue(value: String, list: List<Pair<String, String>>) {
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
    fun PrefSwitch(index: Int, onOK : (index: Int, value : String?)->Unit){
        val on = rememberSaveable { mutableStateOf( listOfPrefs[index].origValue!!.value.toBoolean())}
        val toggle = listOfPrefs[index].showDialog?.value ?: false
        if (toggle){
            on.value = !on.value
            onOK(index, on.value.toString())
            listOfPrefs[index].showDialog?.value = false
        }
        Switch(checked = on.value, onCheckedChange = null /*{ on.value = it}*/)
    }


    /*
        PrefRow: Display one Preference in a single row
            index: The index of the preference in the list of preferences
            onOK: Called when user clicks OK in the preference dialog
    */
    @Composable
    fun PrefRow(index: Int, onOK : (index: Int, value : String?)->Unit){

        if (listOfPrefs[index].list != null) { // Normal row (Not group label)
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    // The row is clickable. A click sets showDialog=true which causes the dialog box to pop
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
                    // Title of the row (e.g. "Clock Format")
                    PrefTitle(listOfPrefs[index].title)

                    // Type of row: Dialog or Switch?
                    if (listOfPrefs[index].isDialog)
                        // Dialog:  Pass the current selected preference value
                        //          and the list of possible values
                        PrefSelValue(
                            value = listOfPrefs[index].origValue!!.value!!,
                            list = listOfPrefs[index].list!!,
                            )
                    else
                        // Switch: Pass the index of the preference an the onOK callback
                        PrefSwitch(index, onOK)
                }
            }
        } else { // Header
            Text(
                text = stringResource(listOfPrefs[index].title),
                style = styledOverLineText,//MaterialTheme.typography.bodyMedium,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp, top = 40.dp)
            )
            HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))
        }
    }


    // List of all preferences and section titles
    // The visual structure is of a single column of PrefRow's
    // Each PrefRow is a structure representing a Preference
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(start = 8.dp)
    ) {
        // Loop on all Preferences. For each, display a PrefRow
        // index: Index of Preference in listOfPrefs
        listOfPrefs.forEachIndexed{ index, _ ->  key(index){PrefRow(index, onOK)}  }
    }
}
/***********************************************************************************************/
