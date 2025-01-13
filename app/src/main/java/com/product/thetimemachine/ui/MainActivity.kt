package com.product.thetimemachine.ui

import android.R
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.product.thetimemachine.AlarmEdit
import com.product.thetimemachine.AlarmList
import com.product.thetimemachine.AlarmNavHost
import com.product.thetimemachine.AlarmViewModel
import com.product.thetimemachine.Application.TheTimeMachineApp.appContext
import com.product.thetimemachine.Application.TheTimeMachineApp.mainActivity
import com.product.thetimemachine.Settings
import com.product.thetimemachine.alarmScreens
import com.product.thetimemachine.ui.theme.AppTheme


private const val isDynamicColor = false
var alarmViewModel: AlarmViewModel? = null

val editDesc = appContext.getString(com.product.thetimemachine.R.string.edit_action_bar)
val duplicateDesc = appContext.getString(com.product.thetimemachine.R.string.duplicate_action_bar)
val deleteDesc = appContext.getString(com.product.thetimemachine.R.string.delete_action_bar)
val settingsDesc = appContext.getString(com.product.thetimemachine.R.string.settings_action_bar)
val checkDesc = appContext.getString(com.product.thetimemachine.R.string.checkmark_action_bar)


class MainActivity : ComponentActivity() {
    // ViewModel object of class MyViewModel
    // Holds all UI variables related to this activity
    private var deleteAction = false
    private var editAction = false
    private var duplicateAction = false
    private var checkmarkAction = false
    private var settingsAction = true


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

/**/


        @Composable
         fun statusBarBgColor() {
            val window = this.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor =
                ContextCompat.getColor(this, R.color.transparent) // here is your color
            //window.statusBarColor = colorScheme.background.toArgb() // here is your color
        }


        // Create/acquire the ViewModel object of class AlarmViewModel
        alarmViewModel = ViewModelProvider(this).get(AlarmViewModel::class.java)

        mainActivity = this

        setContent {
            enableEdgeToEdge()
            //statusBarBgColor()
            TopComposable()
            }
        }

}

 @Composable
 fun TopComposable() {
     AppTheme(dynamicColor = isDynamicColor) {
         Surface {
             MaterialTheme {
                 AlarmNavHost(
                     alarmViewModel = alarmViewModel,
                 )
             }
         }
     }
 }


// Display go-back arrow icon on the Top App Bar - and react to click
@Composable
fun NavBack(currentDestination: NavDestination?, navController: NavHostController) {
    if (currentDestination?.route != AlarmList.route) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Localized description" // TODO: Replace
            )
        }
    }
}


// Display Action icons on the Top App Bar - and react to click
@Composable
private fun Actions(
    currentDestination: NavDestination?,
    navController: NavHostController,
    onActionClick: (NavHostController, String)-> Unit
) {

    if (currentDestination?.route == AlarmEdit.routeWithArgs) {

        IconButton(onClick = {
            onActionClick(navController, AlarmList.route)
        }) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Localized description" // TODO: Replace
            )
        }
    }

    // Settings Icon
    if (currentDestination?.route != Settings.route) {
        IconButton(onClick = { onActionClick(navController, Settings.route)
        }) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Localized description" // TODO: Replace
            )
        }
    }


}

