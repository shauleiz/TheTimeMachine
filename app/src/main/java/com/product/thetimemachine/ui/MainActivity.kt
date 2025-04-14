package com.product.thetimemachine.ui

import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModelProvider
import com.product.thetimemachine.AlarmNavHost
import com.product.thetimemachine.AlarmViewModel
import com.product.thetimemachine.Application.TheTimeMachineApp.appContext
import com.product.thetimemachine.Application.TheTimeMachineApp.mainActivity
import com.product.thetimemachine.BaseActivity
import com.product.thetimemachine.R
import com.product.thetimemachine.ui.theme.AppTheme
import com.product.thetimemachine.ui.theme.getCurrentColorScheme


private const val isDynamicColor = false
var alarmViewModel: AlarmViewModel? = null

val editDesc = appContext.getString(R.string.edit_action_bar)
val duplicateDesc = appContext.getString(R.string.duplicate_action_bar)
val deleteDesc = appContext.getString(R.string.delete_action_bar)
val settingsDesc = appContext.getString(R.string.settings_action_bar)
val alarmListDesc = appContext.getString(R.string.alarmlist_action_bar)
val checkDesc = appContext.getString(R.string.checkmark_action_bar)


class MainActivity : BaseActivity() {
    // ViewModel object of class MyViewModel
    // Holds all UI variables related to this activity


    // Defines the Request Permission Launcher
    // Launches the permission request dialog box for POST_NOTIFICATIONS
    // Gets the result Granted (true/false) and sets variable  notificationPermission
    val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
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




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create/acquire the ViewModel object of class AlarmViewModel
        alarmViewModel = ViewModelProvider(this)[AlarmViewModel::class.java]

        mainActivity = this



        setContent {
            enableEdgeToEdge()
            //statusBarBgColor()
            TopComposable()
            }
        }

    @Composable
    fun NavigationBarBgColor(theme: String = "", themeType: String = "") {

        val window = this.window
        //window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        //window.navigationBarColor = colorScheme.surfaceContainer.toArgb()
        window.navigationBarColor = getCurrentColorScheme(color = theme, darkTheme = themeType).surfaceContainer.toArgb()
    }

    override fun onResume() {
        // Set the default volume control as ALARM volume control
        volumeControlStream = AudioManager.STREAM_ALARM
        super.onResume()
    }

}



 @Composable
 fun TopComposable() {
     AppTheme(dynamicColor = isDynamicColor, theme = getPrefTheme(appContext)) {
         Surface {
             MaterialTheme {
                 AlarmNavHost(alarmViewModel = alarmViewModel)
             }
         }
     }
 }


