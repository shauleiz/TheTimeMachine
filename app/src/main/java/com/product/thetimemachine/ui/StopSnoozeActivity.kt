package com.product.thetimemachine.ui


import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.product.thetimemachine.AlarmReceiver
import com.product.thetimemachine.AlarmService
import com.product.thetimemachine.Application.TheTimeMachineApp
import com.product.thetimemachine.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class StopSnoozeActivity : AppCompatActivity() {
    private var extras: Bundle? = null
    private var strCurrentTime = "99:99 am"
    private var broadcastReceiver: BroadcastReceiver? = null
    private var dateFormat: SimpleDateFormat? = null

    val triggerTimeDisplay = mutableStateOf(false)



   private fun onClickSnooze() {

       // Stop the Alarm by stopping the Alarm Service
       val context = applicationContext
       val snoozeIntent = Intent(context, AlarmService::class.java)
       snoozeIntent.putExtras(extras!!)
       AlarmReceiver.snoozing(context, snoozeIntent)

       // Kill this activity
       finish()
   }

   private fun onClickStop() {

       // Stop the Alarm by stopping the Alarm Service
       val context = applicationContext

       val stopIntent = Intent(context, AlarmService::class.java)
       stopIntent.putExtras(extras!!)
       AlarmReceiver.stopping(context, stopIntent)

       /*
              // Leave this activity to main activity
              Intent intent = new Intent(context, MainActivity.class);
              startActivity(intent);
               */

       // Kill this activity
       finish()
   }

   private fun onSnoozeMenuItemSelected(iItem: Int) {
       val snoozeDurationValues = resources.obtainTypedArray(R.array.snooze_duration_values)
       // Update the bundle
       extras!!.putString(
           TheTimeMachineApp.appContext.getString(R.string.key_snooze_duration),
           snoozeDurationValues.getString(iItem)
       )

       Log.d(
           "THE_TIME_MACHINE",
           "You Clicked " + iItem + " - " + snoozeDurationValues.getString(iItem)
       )

       // Stop the Alarm by stopping the Alarm Service
       val context = applicationContext
       val snoozeIntent = Intent(context, AlarmService::class.java)
       snoozeIntent.putExtras(extras!!)
       AlarmReceiver.snoozing(context, snoozeIntent)

       snoozeDurationValues.recycle()


       // Done
       finish()
   }



   override fun onCreateView(
       parent: View?,
       name: String,
       context: Context,
       attrs: AttributeSet
   ): View? {
       val view = super.onCreateView(parent, name, context, attrs)

/*
       // Hide Navigation Bar
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
           (window.insetsController)?.hide(WindowInsets.Type.navigationBars())
       } else window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

       */
       return view
   }

   @SuppressLint("ClickableViewAccessibility")
   override fun onCreate(savedInstanceState: Bundle?) {
       enableEdgeToEdge()
       super.onCreate(savedInstanceState)

       // Allow this activity on a locked screen
       allowOnLockScreen()

       // Set the default volume control as ALARM volume control
       volumeControlStream = AudioManager.STREAM_ALARM

       // Set The time format
       dateFormat = if (SettingsFragment.pref_is24HourClock()) SimpleDateFormat("H:mm", Locale.US)
       else SimpleDateFormat("h:mm a", Locale.US)

       // Hide system bars
       val windowInsetsController =  WindowCompat.getInsetsController(window, window.decorView)
       windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

       // Compose Content
       setContent { StopSnoozeDisplayTop() }
   }


   override fun onStart() {
       super.onStart()

       // Definition of the receiver that gets an ACTION_TIME_TICK every minute
       broadcastReceiver = object : BroadcastReceiver() {
           override fun onReceive(ctx: Context, intent: Intent) {
               strCurrentTime = (dateFormat!!.format(Date()))

               // Refresh Text
               displayScreenText()

               triggerTimeDisplay.value = true
           }
       }

       // Register the receiver
       registerReceiver(broadcastReceiver, IntentFilter(Intent.ACTION_TIME_TICK))

   }

   override fun onStop() {
       unregisterReceiver(broadcastReceiver)
       super.onStop()
   }

   override fun onPostCreate(savedInstanceState: Bundle?) {
       super.onPostCreate(savedInstanceState)


       // Get the data passed from calling service
       extras = intent.extras

       //  Display the data on the screen
       strCurrentTime = (dateFormat!!.format(Date()))

       Log.d("THE_TIME_MACHINE", "onPostCreate")

   }


   override fun onResume() {
       super.onResume()
       allowOnLockScreen()
   }

   override fun onDestroy() {
       super.onDestroy()
       setShowWhenLocked(false)
       setTurnScreenOn(false)
   }


   private fun snoozeButtonTextCompose(): String {
       return AlarmService.snoozeButtonText(
           extras!!.getString(
               TheTimeMachineApp.appContext.getString(
                   R.string.key_snooze_duration
               ), ""
           )
       )
   }



   /* Based on: https://stackoverflow.com/questions/55913495/open-activity-above-lockscreen
  *  Allow this activity to be displayed on a locked machine and to accept user input.
  */
   private fun allowOnLockScreen() {
       setShowWhenLocked(true)
       setTurnScreenOn(true)
       if (Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
           val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
           keyguardManager.requestDismissKeyguard(this, null) // API 28 Only
       }
   }

   fun displayScreenText(): String {
       var label = extras!!.getString("LABEL", "")
       if (label.isNotEmpty()) label += " - "

       val displayText = String.format(Locale.US, "%s%s", label, strCurrentTime)

       // ((TextView)mContentView).setText(displayText);

       //footer.setText(appName);
       return displayText
   }

   private val footerText: String
       get() = extras!!.getString("APP_NAME", "")




   /* Top level Display - Call Portrait/Landscape Content View */
   @Composable
   fun StopSnoozeDisplayTop() {
       Surface{
           MaterialTheme {
               if (isPortrait())
                   ContentViewComposePortrait()
               else
                   ContentViewComposeLand( )
           }
       }
   }


   // Tests Portrait/Landscape orientation of the device
   @Composable
   fun isPortrait():Boolean{
       return LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
   }

   // Top lever display - Portrait mode
   @Composable
   fun ContentViewComposePortrait() {
       Log.d(
           "THE_TIME_MACHINE",
           "called: ContentViewComposePortrait"
       )
       Column(
           modifier = Modifier
               .fillMaxWidth()
               .fillMaxHeight()
               .background(color = colorResource(R.color.light_blue_A400)),
           horizontalAlignment = Alignment.CenterHorizontally,
           verticalArrangement = Arrangement.SpaceBetween

       ){
           // Label + Alarm time
           HeaderText(triggerTimeDisplay.value)

           // Image (Stop) that serves as stop-button
           Spacer(modifier = Modifier.size(20.dp))
           StopButton()

           // Box (Snooze) that serves as stop-button
           Spacer(modifier = Modifier.size(20.dp))
           SnoozeButton()

           // Footer: App name
           Spacer(modifier = Modifier.size(20.dp))
           FooterText()

       }
   }

   // Top lever display - Landscape mode
   @Composable
   fun ContentViewComposeLand() {
       Log.d(
           "THE_TIME_MACHINE",
           "called: ContentViewComposeLand"
       )

       Column(
           modifier = Modifier
               .fillMaxWidth()
               .fillMaxHeight()
               .background(color = colorResource(R.color.light_blue_A400)), // TODO: Replace
           horizontalAlignment = Alignment.CenterHorizontally,
           verticalArrangement = Arrangement.SpaceBetween
       ){
           // Label + Alarm time
           HeaderText(triggerTimeDisplay.value)

           Row (
               modifier =
               Modifier
                   .fillMaxWidth(),
               //.background(color = colorResource(R.color.custom_header_text)),
               verticalAlignment = Alignment.CenterVertically,
               horizontalArrangement = Arrangement.SpaceEvenly,
           ){
               // Image (Stop) that serves as stop-button
               Spacer(modifier = Modifier.size(20.dp, 20.dp))
               StopButton()

               // Box (Snooze) that serves as stop-button
               Spacer(modifier = Modifier.size(20.dp, 20.dp))
               SnoozeButton()

               Spacer(modifier = Modifier.size(20.dp, 20.dp))
           }
           // Footer: App name
           Spacer(modifier = Modifier.size(20.dp, 20.dp))
           FooterText()

       }
   }

   @Composable
   // Label + Alarm time
   fun HeaderText(trigger: Boolean){

       Text(
           text = displayScreenText(),
           style = MaterialTheme.typography.headlineLarge,
           fontSize = 30.sp,
           fontWeight = FontWeight.Bold,
           color =  colorResource(com.google.android.material.R.color.design_default_color_surface),
           maxLines = 2,
           modifier = Modifier
               .background(color = colorResource(R.color.light_blue_A400))
               .fillMaxWidth()
               .padding(horizontal = 5.dp, vertical = 24.dp)
               .wrapContentWidth(Alignment.CenterHorizontally)
       )

       if (trigger) triggerTimeDisplay.value = false
   }

   @Composable
   // Footer: App name
   fun FooterText(){
       Box ()
       {
           Text(
               text =  footerText ,
               style = MaterialTheme.typography.headlineLarge,
               color =  colorResource(com.google.android.material.R.color.design_default_color_surface),
               fontSize = 16.sp,
               modifier = Modifier
                   .background(color = colorResource(R.color.light_blue_A400))
                   .fillMaxWidth()
                   .wrapContentWidth(Alignment.CenterHorizontally)
                   .padding(horizontal = 5.dp, vertical = 24.dp)
                   .align(alignment = Alignment.BottomCenter),
           )
       }
   }




   @Composable
   // Stop "Button"
   // Box containing an image (Red octagon) and Text ("Stop")
   fun StopButton() {
       Box {
           Image(
               painter = painterResource(id = R.drawable.iconmonstr_octagon_1),
               contentDescription = "Stop Button",
               modifier = Modifier
                   .align(alignment = Alignment.Center)
                   .requiredSize((LocalConfiguration.current.smallestScreenWidthDp * 0.5f).dp)
                   .clickable { onClickStop() }
           )
           Text(
               text = stringResource(id = R.string.stop_button),
               fontSize =  30.sp,
               fontFamily = FontFamily.SansSerif,
               color =  colorResource(com.google.android.material.R.color.design_default_color_surface),
               textAlign = TextAlign.Center,
               modifier = Modifier.align(alignment = Alignment.Center),
           )


       }
   }


   @OptIn(ExperimentalFoundationApi::class)
   @Composable
   // Snooze "Button"
   // Box clipped to a circle+Text+menu(Collapsed)
   fun SnoozeButton() {

       // Keep the state of the menu
       var expanded by rememberSaveable { mutableStateOf(false) }

       // Circle
       Box(
           modifier = Modifier
               .requiredSize((LocalConfiguration.current.smallestScreenWidthDp * 0.5f).dp)
               .clip(CircleShape)
               .background(color = colorResource(android.R.color.holo_orange_dark))
               .combinedClickable(
                   onClick = { onClickSnooze() },
                   onClickLabel = stringResource(id = R.string.snooze_button),
                   onLongClick = { expanded = true },
                   onLongClickLabel = stringResource(id = R.string.snooze_duration_label),
               )
       ){

           // "Snooze for XXX
           Text(
               snoozeButtonTextCompose(),
               color =  colorResource(com.google.android.material.R.color.design_default_color_surface),
               fontSize =  30.sp,
               fontFamily = FontFamily.SansSerif,
               textAlign = TextAlign.Center,
               modifier = Modifier.align(alignment = Alignment.Center),
           )

           // Menu of optional snooze times (Collapsed)
           SnoozePopupMenu (expanded,  onSnoozeMenuSelected = {expanded=it} )
       }
   }

   @Composable
   private fun SnoozePopupMenu ( expanded:Boolean, onSnoozeMenuSelected : (Boolean)->Unit){

       // Arrays of Entries
       val snoozeDurationEntries = stringArrayResource(R.array.snooze_duration_entries)

       // Create the menu
       DropdownMenu(
           expanded = expanded, // Temp
           onDismissRequest = { onSnoozeMenuSelected(false)  },
       )

       // Populate the menu:
       // For every item:
       //  Text from the Arrays of Entries
       //  When clicked: Call a callback routine that changes its status and call an activity
       //  routine that replace snooze duration an start snoozing
       {
           for (index in snoozeDurationEntries.indices) {
               DropdownMenuItem(
                   text = { Text(snoozeDurationEntries[index] )},
                   onClick = {
                       onSnoozeMenuSelected(false)
                       onSnoozeMenuItemSelected(index)}
               )
           }
       }


   }


}