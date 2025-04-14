package com.product.thetimemachine.ui


import android.content.Context
import android.preference.PreferenceManager.getDefaultSharedPreferencesName
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.product.thetimemachine.Application.TheTimeMachineApp.appContext
import com.product.thetimemachine.Application.TheTimeMachineApp.mainActivity
import com.product.thetimemachine.LanguageManager.restartApp
import com.product.thetimemachine.R
import com.product.thetimemachine.Settings
import com.product.thetimemachine.ui.theme.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.util.Locale


const val USER_PREFERENCES_NAME = "the_time_machine_preferences_datastore"

// TODO: Use R.string instead of hardcoded strings
object PreferencesKeys {
    val KEY_H12_24 = stringPreferencesKey("KEY_H12_24")
    val KEY_CLOCK_TYPE = stringPreferencesKey("KEY_CLOCK_TYPE")
    val KEY_RING_DURATION = stringPreferencesKey("KEY_RING_DURATION")
    val KEY_FIRST_DAY = stringPreferencesKey("KEY_FIRST_DAY")
    val KEY_RING_REPEAT = stringPreferencesKey("KEY_RING_REPEAT")
    val KEY_VIBRATION_PATTERN = stringPreferencesKey("KEY_VIBRATION_PATTERN")
    val KEY_ALARM_SOUND = stringPreferencesKey("KEY_ALARM_SOUND")
    val KEY_SORT_TYPE = stringPreferencesKey("KEY_SORT_TYPE")
    val KEY_GRADUAL_VOLUME = stringPreferencesKey("KEY_GRADUAL_VOLUME")
    val KEY_SNOOZE_DURATION = stringPreferencesKey("KEY_SNOOZE_DURATION")
    val KEY_SORT_SEPARATE = stringPreferencesKey("KEY_SORT_SEPARATE")
    val KEY_LANGUAGE = stringPreferencesKey("KEY_LANGUAGE")
    val KEY_THEME = stringPreferencesKey("KEY_THEME")
    val KEY_THEME_TYPE = stringPreferencesKey("KEY_THEME_TYPE")
}


data class UserPreferences(
    val ringRepeat: String,
    val ringDuration: String,
    val snoozeDuration: String,
    val hour12Or24: String,
    val clockType: String,
    val firstDayWeek: String,
    val vibrationPattern: String,
    val alarmSound: String,
    val sortType: String,
    val sortSeparate: String,
    val gradualVolume: String,
    val language: String,
    val theme: String,
    val themeType: String,
)

val Context.timeMachineDataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME,
    produceMigrations = { context ->
        listOf(SharedPreferencesMigration(context, getDefaultSharedPreferencesName(context)))
    }
)

private const val isDynamicColor = false

fun mapUserPreferences(parent: Context, preferences: Preferences): UserPreferences {

    val ringRepeat =
        preferences[PreferencesKeys.KEY_RING_REPEAT] ?: parent.getString(R.string.ring_repeat_def)
    val ringDuration = preferences[PreferencesKeys.KEY_RING_DURATION]
        ?: parent.getString(R.string.ring_duration_def)
    val snoozeDuration = preferences[PreferencesKeys.KEY_SNOOZE_DURATION]
        ?: parent.getString(R.string.snooze_duration_def)
    val hour12Or24 = preferences[PreferencesKeys.KEY_H12_24] ?: getSystemTimeFormat(parent)
    val clockType =
        preferences[PreferencesKeys.KEY_CLOCK_TYPE] ?: parent.getString(R.string.clock_type_def)
    val firstDayWeek =
        preferences[PreferencesKeys.KEY_FIRST_DAY] ?: parent.getString(R.string.first_day_week_def)
    val vibratePattern = preferences[PreferencesKeys.KEY_VIBRATION_PATTERN]
        ?: parent.getString(R.string.vibration_pattern_def)
    val alarmSound =
        preferences[PreferencesKeys.KEY_ALARM_SOUND] ?: parent.getString(R.string.alarm_sound_def)
    val sortType =
        preferences[PreferencesKeys.KEY_SORT_TYPE] ?: parent.getString(R.string.sort_type_def)
    val sortSeparate = preferences[PreferencesKeys.KEY_SORT_SEPARATE]
        ?: parent.getString(R.string.sort_separate_def)
    val gradualVolume = preferences[PreferencesKeys.KEY_GRADUAL_VOLUME]
        ?: parent.getString(R.string.gradual_volume_def)
    val language = preferences[PreferencesKeys.KEY_LANGUAGE] ?: Locale.getDefault().language
    val theme = preferences[PreferencesKeys.KEY_THEME] ?: "OLIVEGREEN"
    val themeType =
        preferences[PreferencesKeys.KEY_THEME_TYPE] ?: parent.getString(R.string.theme_type_auto)


    return UserPreferences(
        ringRepeat, ringDuration, snoozeDuration,
        hour12Or24, clockType, firstDayWeek,
        vibratePattern, alarmSound, sortType,
        sortSeparate, gradualVolume, language,
        theme, themeType,
    )
}

suspend fun fetchInitialPreferences(parent: Context) =
    mapUserPreferences(parent, parent.timeMachineDataStore.data.first().toPreferences())

fun updatePref(parent: Context, key: Preferences.Key<String>?, value: String?) {
    if (key == null || value == null) return
    runBlocking {
        parent.timeMachineDataStore.edit { preferences ->
            preferences[key] = value
        }
    }
}

fun getPrefs(parent: Context): UserPreferences {
    return runBlocking { fetchInitialPreferences(parent) }
}

fun isClockTypeAnalog(parent: Context?): Boolean {
    if (parent == null) return false
    return getPrefs(parent).clockType == appContext.getString(R.string.analog_clock)
}

fun isPref24h(parent: Context?): Boolean {
    if (parent == null) return false
    return getPrefs(parent).hour12Or24 == appContext.getString(R.string.h24_clock)
}

fun isPrefSortSeparate(parent: Context?): Boolean {
    if (parent == null) return false
    return getPrefs(parent).sortSeparate.toBoolean()
}

fun getPrefFirstDayOfWeek(parent: Context?): String {
    if (parent == null) return ""
    return getPrefs(parent).firstDayWeek
}

fun getPrefLanguage(parent: Context?): String {
    if (parent == null) return ""
    return getPrefs(parent).language
}

fun getPrefTheme(parent: Context?): String {
    if (parent == null) return ""
    Log.d("THE_TIME_MACHINE", "getPrefTheme(): getPrefs(parent).theme = ${getPrefs(parent).theme} ")
    return getPrefs(parent).theme
}

fun getPrefThemeType(parent: Context?): String {
    if (parent == null) return ""
    return getPrefs(parent).themeType
}

fun getPrefSortType(parent: Context?): String {
    if (parent == null) return ""
    return getPrefs(parent).sortType
}

fun getPrefRingDuration(parent: Context?): String {
    if (parent == null) return ""
    return getPrefs(parent).ringDuration
}

fun getPrefRingRepeat(parent: Context?): String {
    if (parent == null) return ""
    return getPrefs(parent).ringRepeat
}

fun getPrefSnoozeDuration(parent: Context?): String {
    if (parent == null) return ""
    return getPrefs(parent).snoozeDuration
}

fun getPrefVibrationPattern(parent: Context?): String {
    if (parent == null) return ""
    return getPrefs(parent).vibrationPattern
}

fun getPrefAlarmSound(parent: Context?): String {
    if (parent == null) return ""
    return getPrefs(parent).alarmSound
}

fun getPrefGradualVolume(parent: Context?): String {
    if (parent == null) return ""
    return getPrefs(parent).gradualVolume
}

// Return "h24"/"h12"
fun getSystemTimeFormat(context: Context): String {
    return if (is24HourFormat(context)) "h24" else "h12"
}

@Composable
private fun SettingsActions(onActionClick: (String) -> Unit) {

    // Alarm List Action
    IconButton(onClick = {
        onActionClick(alarmListDesc)
    }) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.List,
            contentDescription = alarmListDesc
        )
    }
}


////////////////////////////////////////////////////////////////////////////////
class SettingsScreen( private val navToAlarmList: () -> Unit, private val navBack: () -> Unit, ) {

    lateinit var parent: Context
    private lateinit var userPreferencesFlow: Flow<UserPreferences>
    private lateinit var setUpAlarmValues: UserPreferences

    private fun actionClicked(action: String) {
        when (action) {
            alarmListDesc -> navToAlarmList()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SettingsDisplayTop() {

        parent = appContext
        /*
                //          --- DEBUG Section ---                //
                // Remove all preferences - for Debug use only
                // Testing reset values
                runBlocking {
                    parent.timeMachineDataStore.edit { preferences ->
                        preferences.clear()
                    }
                }
         */

        userPreferencesFlow = parent.timeMachineDataStore.data.map { preferences ->
            mapUserPreferences(parent, preferences)
        }

        // List of all entries
        setUpAlarmValues = getPrefs(appContext)
        val listOfPrefsEx = getListOfGeneralPreferences(setUpAlarmValues)
        val listOfPrefs = remember { mutableStateListOf<PrefData>() }
        listOfPrefs.clear()
        listOfPrefs.addAll(listOfPrefsEx)


        // val prefs = getPrefs(parent)
        //var sortSeparate by remember { mutableStateOf(prefs.sortSeparate) }


        var currentTheme by rememberSaveable { mutableStateOf(getPrefTheme(parent)) }
        var currentThemeType by rememberSaveable { mutableStateOf(getPrefThemeType(parent)) }

        // Call when preferences dialog OK button pressed
        val onPrefDialogOK = { index: Int, value: String? ->
            Log.d("THE_TIME_MACHINE", "onPrefDialogOK(): index=$index ; value=$value ")

            // Stop displaying the dialog box
            listOfPrefs[index].showDialog?.value = false

            // Changes the origValue to the currently selected Preference value
            val hasChanged = listOfPrefs[index].origValue?.value != value
            listOfPrefs[index].origValue?.value = value

            // Update the Preferences db for this Preference
            updatePref(parent, listOfPrefs[index].prefKey, value)

            // Special case: If language changed - restart app
            if (listOfPrefs[index].prefKey == PreferencesKeys.KEY_LANGUAGE && hasChanged)
                restartApp(mainActivity)
        }


        // Call when entry (radiobutton) selected in preferences dialog
        fun onPrefDialogSelected(entry: PrefData, value: String?) {
            entry.currentValue!!.value = value

            // Special cases
            when (entry.prefKey) {
                // Theme selected
                PreferencesKeys.KEY_THEME -> currentTheme = entry.currentValue.value.toString()

                // Theme Type
                PreferencesKeys.KEY_THEME_TYPE -> {
                    currentThemeType = entry.currentValue.value.toString()
                    Log.d("THE_TIME_MACHINE", "onPrefDialogSelected():  currentThemeType = $currentThemeType ")
                }

                // Sound/Vibration
                PreferencesKeys.KEY_VIBRATION_PATTERN,
                PreferencesKeys.KEY_ALARM_SOUND -> playVibOrSound(value, entry)
            }


        }

        // Call when preferences dialog Cancel button pressed
        // or dialog dismissed
        fun onPrefDialogCancel(entry: PrefData) {
            entry.showDialog?.value = false
            entry.currentValue!!.value = entry.origValue!!.value

            // Special cases
            when (entry.prefKey) {
                // Theme selected
                PreferencesKeys.KEY_THEME -> currentTheme = entry.origValue.value.toString()

                // Theme Type
                PreferencesKeys.KEY_THEME_TYPE -> currentThemeType = entry.origValue.value.toString()

                // Sound/Vibration
                PreferencesKeys.KEY_VIBRATION_PATTERN,
                PreferencesKeys.KEY_ALARM_SOUND -> playVibOrSound(null, entry)
            }

        }

        mainActivity.NavigationBarBgColor(currentTheme,  currentThemeType)

        AppTheme(dynamicColor = isDynamicColor, theme = currentTheme, darkTheme = currentThemeType) {
            Surface {
                MaterialTheme {
                    Scaffold(
                        topBar = @Composable {
                            TopAppBar(
                                title = {
                                    Text(
                                        mainActivity.getString(Settings.label),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = { navBack() }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Localized description" // TODO: Replace
                                        )
                                    }
                                },
                                actions = {
                                    SettingsActions() { actionClicked(it) }
                                },

                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                                ),
                            )
                        }
                    )
                    {
                        // This Box contains the Settings main screen
                        // The box contains only ShowPreferences()
                        Box(
                            //horizontalAlignment = CenterHorizontally, //of children
                            modifier = Modifier
                                .padding(it)
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            /* Preferences - listOfPrefs is the list of top entries */
                            /* The trailing lambda: Called when user clicks OK after
                            *  changing a property value                            */
                            ShowPreferences(
                                listOfPrefs = listOfPrefs,
                                onSelected = { e, v -> onPrefDialogSelected(e, v) },
                                onCancel = { _, e -> onPrefDialogCancel( e) }
                            ) { i, v -> onPrefDialogOK(i, v) } // onOK
                        }
                    }
                }
            }
        }
    }
}