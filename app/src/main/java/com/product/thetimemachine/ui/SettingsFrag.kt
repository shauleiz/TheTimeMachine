package com.product.thetimemachine.ui


import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager.getDefaultSharedPreferencesName
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.datastore.migrations.SharedPreferencesMigration
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import com.product.thetimemachine.R
import com.product.thetimemachine.ui.SettingsFrag.PreferencesKeys.KEY_H12_24
import com.product.thetimemachine.ui.theme.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.util.Objects


const val USER_PREFERENCES_NAME = "the_time_machine_preferences_datastore"



data class UserPreferences(
    val hour12Or24 : String,
    val sortSeparate: Boolean,
)
private val Context.timemachinedataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME,
    produceMigrations = { context ->
        listOf(SharedPreferencesMigration(context, getDefaultSharedPreferencesName ( context)))
    }
)

private const val isDynamicColor = false

class SettingsFrag : Fragment() {

    // TODO: Use R.string instead of hardcoded strings
    private object PreferencesKeys {
        val KEY_H12_24 = stringPreferencesKey("KEY_H12_24")
        val KEY_RING_DURATION = stringPreferencesKey("KEY_RING_DURATION")
        val KEY_FIRST_DAY = stringPreferencesKey("KEY_FIRST_DAY")
        val KEY_RING_REPEAT = stringPreferencesKey("KEY_RING_REPEAT")
        val KEY_VIBRATION_PATTERN = stringPreferencesKey("KEY_VIBRATION_PATTERN")
        val KEY_ALARM_SOUND = stringPreferencesKey("KEY_ALARM_SOUND")
        val KEY_SORT_TYPE = stringPreferencesKey("KEY_SORT_TYPE")
        val KEY_GRADUAL_VOLUME = stringPreferencesKey("KEY_GRADUAL_VOLUME")
        val KEY_TIME_FORMAT = stringPreferencesKey("KEY_TIME_FORMAT")
        val KEY_SORT_SEPARATE = booleanPreferencesKey("KEY_SORT_SEPARATE")
    }

    lateinit var parent: MainActivity
    lateinit var userPreferencesFlow: Flow<UserPreferences>



    override fun onCreate(savedInstanceState: Bundle?) {
        parent = activity as MainActivity
        userPreferencesFlow  = parent.timemachinedataStore.data.map { preferences ->
            val sortSeparate = preferences[PreferencesKeys.KEY_SORT_SEPARATE] ?: false
            val hour12Or24 =   preferences[PreferencesKeys.KEY_H12_24] ?: "Err"
            UserPreferences(hour12Or24, sortSeparate)
        }

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Start the compose display
        return ComposeView(requireContext()).apply { setContent { SettingsFragDisplayTop() } }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Display the Up arrow
        val actionBar = checkNotNull(Objects.requireNonNull<MainActivity>(parent).supportActionBar)
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_back_fill0_wght400_grad0_opsz24)
        actionBar.setHomeActionContentDescription(R.string.description_up_arrow_back)
        actionBar.setDisplayHomeAsUpEnabled(true)
    }

    override fun onDestroy() {
        // Remove the Up arrow

        val actionBar = checkNotNull(parent!!.supportActionBar)
        actionBar.setDisplayHomeAsUpEnabled(false)

        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        //Objects.requireNonNull<SharedPreferences>(getPreferenceManager().getSharedPreferences()).registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        //Objects.requireNonNull<SharedPreferences>(getPreferenceManager().getSharedPreferences()).unregisterOnSharedPreferenceChangeListener(this)
    }

    suspend fun fetchInitialPreferences() =
        mapUserPreferences(parent.timemachinedataStore.data.first().toPreferences())


    private fun mapUserPreferences(preferences: Preferences): UserPreferences {

        Log.d("THE_TIME_MACHINE", "mapUserPreferences():  preferences = $preferences")

        // Get our show completed value, defaulting to false if not set:
        val sortSeparate = preferences[PreferencesKeys.KEY_SORT_SEPARATE] ?: false
        val hour12Or24 =   preferences[KEY_H12_24] ?: "Error"

        Log.d("THE_TIME_MACHINE", "mapUserPreferences():  preferences[PreferencesKeys.KEY_H12_24] = ${preferences[PreferencesKeys.KEY_H12_24]}")
        Log.d("THE_TIME_MACHINE", "mapUserPreferences():  sortSeparate = $sortSeparate ; hour12Or24 = $hour12Or24")

        return UserPreferences(hour12Or24, sortSeparate)
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun SettingsFragDisplayTop() {
        AppTheme(dynamicColor = isDynamicColor) {
            Surface {
                MaterialTheme {
                    Column(
                        horizontalAlignment = CenterHorizontally, //of children
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {

                        val  prefs  = runBlocking { fetchInitialPreferences() }
                        Text (prefs.sortSeparate.toString())
                        Text (prefs.hour12Or24)
                    }
                }
            }
        }
    }
}