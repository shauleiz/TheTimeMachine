package com.product.thetimemachine.ui

import android.R
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.product.thetimemachine.AlarmList
import com.product.thetimemachine.AlarmNavHost
import com.product.thetimemachine.AlarmViewModel
import com.product.thetimemachine.alarmScreens
import com.product.thetimemachine.ui.theme.AppTheme


private const val isDynamicColor = false

class MainActivity : ComponentActivity() {
    // ViewModel object of class MyViewModel
    // Holds all UI variables related to this activity
    var alarmViewModel: AlarmViewModel? = null
    private var deleteAction = false
    private var editAction = false
    private var duplicateAction = false
    private var checkmarkAction = false
    private var settingsAction = true

    fun setDeleteAction(d: Boolean) {
        deleteAction = d
    }

    fun setEditAction(editAction: Boolean) {
        this.editAction = editAction
    }

    fun setDuplicateAction(duplicateAction: Boolean) {
        this.duplicateAction = duplicateAction
    }

    fun setCheckmarkAction(checkmarkAction: Boolean) {
        this.checkmarkAction = checkmarkAction
    }

    fun setSettingsAction(settingsAction: Boolean) {
        this.settingsAction = settingsAction
    }

    fun isDeleteAction(): Boolean {
        return deleteAction
    }

    fun isEditAction(): Boolean {
        return editAction
    }

    fun isDuplicateAction(): Boolean {
        return duplicateAction
    }

    fun isSettingsAction(): Boolean {
        return settingsAction
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
                 val navController = rememberNavController()
                 val currentBackStack by navController.currentBackStackEntryAsState()
                 val currentDestination = currentBackStack?.destination
                 val currentScreen =
                     alarmScreens.find { it.route == currentDestination?.route } ?: AlarmList

                 Scaffold(
                     topBar = {}
                 ) { innerPadding ->
                     AlarmNavHost(
                         navController = navController,
                         modifier = Modifier.padding(innerPadding)
                     )
                 }
             }
         }
     }
 }
