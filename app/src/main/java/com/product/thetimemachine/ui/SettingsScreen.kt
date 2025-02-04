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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.product.thetimemachine.Application.TheTimeMachineApp.appContext
import com.product.thetimemachine.Application.TheTimeMachineApp.mainActivity
import com.product.thetimemachine.R
import com.product.thetimemachine.Settings
import com.product.thetimemachine.ui.theme.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking


const val USER_PREFERENCES_NAME = "the_time_machine_preferences_datastore"

// TODO: Use R.string instead of hardcoded strings
object PreferencesKeys {
    val KEY_H12_24            = stringPreferencesKey("KEY_H12_24")
    val KEY_CLOCK_TYPE        = stringPreferencesKey("KEY_CLOCK_TYPE")
    val KEY_RING_DURATION     = stringPreferencesKey("KEY_RING_DURATION")
    val KEY_FIRST_DAY         = stringPreferencesKey("KEY_FIRST_DAY")
    val KEY_RING_REPEAT       = stringPreferencesKey("KEY_RING_REPEAT")
    val KEY_VIBRATION_PATTERN = stringPreferencesKey("KEY_VIBRATION_PATTERN")
    val KEY_ALARM_SOUND       = stringPreferencesKey("KEY_ALARM_SOUND")
    val KEY_SORT_TYPE         = stringPreferencesKey("KEY_SORT_TYPE")
    val KEY_GRADUAL_VOLUME    = stringPreferencesKey("KEY_GRADUAL_VOLUME")
    val KEY_SNOOZE_DURATION   = stringPreferencesKey("KEY_SNOOZE_DURATION")
    val KEY_SORT_SEPARATE     = stringPreferencesKey("KEY_SORT_SEPARATE")
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
)

val Context.timeMachineDataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME,
    produceMigrations = { context ->
        listOf(SharedPreferencesMigration(context, getDefaultSharedPreferencesName(context)))
    }
)

private const val isDynamicColor = false

fun mapUserPreferences(parent : Context, preferences: Preferences): UserPreferences {

    val ringRepeat      = preferences[PreferencesKeys.KEY_RING_REPEAT] ?: parent.getString(R.string.ring_repeat_def)
    val ringDuration    = preferences[PreferencesKeys.KEY_RING_DURATION] ?: parent.getString(R.string.ring_duration_def)
    val snoozeDuration  = preferences[PreferencesKeys.KEY_SNOOZE_DURATION] ?: parent.getString(R.string.snooze_duration_def)
    val hour12Or24      = preferences[PreferencesKeys.KEY_H12_24] ?: getSystemTimeFormat(parent)
    val clockType       = preferences[PreferencesKeys.KEY_CLOCK_TYPE] ?: parent.getString(R.string.clock_type_def)
    val firstDayWeek    = preferences[PreferencesKeys.KEY_FIRST_DAY] ?: parent.getString(R.string.first_day_week_def)
    val vibratePattern  = preferences[PreferencesKeys.KEY_VIBRATION_PATTERN] ?: parent.getString(R.string.vibration_pattern_def)
    val alarmSound      = preferences[PreferencesKeys.KEY_ALARM_SOUND] ?: parent.getString(R.string.alarm_sound_def)
    val sortType        = preferences[PreferencesKeys.KEY_SORT_TYPE] ?: parent.getString(R.string.sort_type_def)
    val sortSeparate    = preferences[PreferencesKeys.KEY_SORT_SEPARATE] ?: parent.getString(R.string.sort_separate_def)
    val gradualVolume   = preferences[PreferencesKeys.KEY_GRADUAL_VOLUME] ?: parent.getString(R.string.gradual_volume_def)


    return UserPreferences(
        ringRepeat, ringDuration, snoozeDuration,
        hour12Or24, clockType, firstDayWeek,
        vibratePattern, alarmSound, sortType,
        sortSeparate, gradualVolume
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
    return getPrefs(parent).clockType == mainActivity.getString(R.string.analog_clock)
}


fun isPref24h(parent: Context?): Boolean {
    if (parent == null) return false
    return getPrefs(parent).hour12Or24 == mainActivity.getString(R.string.h24_clock)
}

fun isPrefSortSeparate(parent: Context?): Boolean {
    if (parent == null) return false
    return getPrefs(parent).sortSeparate.toBoolean()
}

fun getPrefFirstDayOfWeek(parent: Context?): String {
    if (parent == null) return ""
    return getPrefs(parent).firstDayWeek
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
fun getSystemTimeFormat(context: Context) : String{
    return if (is24HourFormat(context)) "h24" else "h12"
}


////////////////////////////////////////////////////////////////////////////////
class SettingsScreen(private val navBack: () -> Unit) {

    lateinit var parent: Context
    private lateinit var userPreferencesFlow: Flow<UserPreferences>
    private lateinit var setUpAlarmValues: UserPreferences


    /*

        override fun onCreate(savedInstanceState: Bundle?) {
            parent = activity as MainActivity
            userPreferencesFlow  = parent.timeMachinedataStore.data.map { preferences ->
                mapUserPreferences(preferences)
            }

            super.onCreate(savedInstanceState)
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            setUpAlarmValues = getPrefs(parent)
            // Start the compose display
            return ComposeView(requireContext()).apply { setContent { SettingsFragDisplayTop() } }
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            // Display the Up arrow
                */
    /*
            val actionBar = checkNotNull(Objects.requireNonNull<MainActivity>(parent).supportActionBar)
            actionBar.setHomeAsUpIndicator(R.drawable.arrow_back_fill0_wght400_grad0_opsz24)
            actionBar.setHomeActionContentDescription(R.string.description_up_arrow_back)
            actionBar.setDisplayHomeAsUpEnabled(true)
                 *//*

    }

    override fun onDestroy() {
        // Remove the Up arrow
*/
    /*
            val actionBar = checkNotNull(parent.supportActionBar)
            actionBar.setDisplayHomeAsUpEnabled(false)


     *//*

        super.onDestroy()
    }
*/

/*
    private fun updateSortSeparate(sortSeparate: Boolean) {
        runBlocking {
            parent.timeMachinedataStore.edit { preferences ->
                preferences[KEY_SORT_SEPARATE] = sortSeparate.toString()
            }
        }
    }

 */

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SettingsDisplayTop() {

        parent = appContext
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


        // Execute when preferences dialog OK button pressed
        val onPrefDialogOK = { index: Int, value: String? ->
            Log.d("THE_TIME_MACHINE", "onPrefDialogOK(): index=$index ; value=$value ")
            listOfPrefs[index].showDialog?.value = false
            listOfPrefs[index].origValue?.value = value
            updatePref(parent, listOfPrefs[index].prefKey, value)
        }


        AppTheme(dynamicColor = isDynamicColor) {
            Surface {
                MaterialTheme {
                    Scaffold(
                        topBar = @Composable {
                            TopAppBar(
                                title = {
                                    Text(
                                        Settings.label,
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
                                    //AlarmEditActions() { actionClicked(it) }
                                },

                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                                ),
                            )
                        }
                    )
                    {
                        Box(
                            //horizontalAlignment = CenterHorizontally, //of children
                            modifier = Modifier
                                .padding(it)
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            /* Preferences */
                            Log.d("THE_TIME_MACHINE", "Box: list size: ${listOfPrefs.size}")
                            ShowPreferences(listOfPrefs) { i, v -> onPrefDialogOK(i, v) }
                        }
                    }
                }
            }
        }
    }
}