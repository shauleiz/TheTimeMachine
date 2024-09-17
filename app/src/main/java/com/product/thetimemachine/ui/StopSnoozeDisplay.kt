@file:JvmName("StopSnoozeDisplay")

package com.product.thetimemachine.ui


import android.content.res.Configuration
import android.util.Log
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.product.thetimemachine.R


class StopSnoozeDisplay (private val activity: StopSnoozeActivity) {


    fun setContent(composeView: ComposeView) {
        composeView.setContent { StopSnoozeDisplayTop() }
    }
    @Composable
    fun StopSnoozeDisplayTop() {

        Surface{
            MaterialTheme {
                if (isPortrait())
                    ContentViewComposePortrait(activity.DisplayScreenText())
                else
                    ContentViewComposeLand(activity.DisplayScreenText() )
            }
        }
    }



    @Composable
    private fun ContentViewComposePortrait(name: String) {
        Log.d(
            "THE_TIME_MACHINE",
            "called: ContentViewComposePortrait"
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = colorResource(R.color.light_blue_A400)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween

            ){
            // Label + Alarm time
            HeaderText(name)

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

    @Composable
    private fun ContentViewComposeLand(name: String) {
        Log.d(
            "THE_TIME_MACHINE",
            "called: ContentViewComposeLand"
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = colorResource(R.color.light_blue_A400)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ){
            // Label + Alarm time
            HeaderText(name)

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
    private fun HeaderText(name :String){
        Text(
            text = name,
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
    }

    @Composable
    // Footer: App name
    private fun FooterText(){
        Box ()
        {
            Text(
                text =  activity.footerText,
                style = MaterialTheme.typography.headlineLarge,
                color =  colorResource(com.google.android.material.R.color.design_default_color_surface),
                fontSize = 16.sp,
                modifier = Modifier
                    .background(color = colorResource(R.color.light_blue_A400))
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .padding(horizontal = 5.dp, vertical = 24.dp)
                    .align(alignment = Alignment.BottomCenter),
            )
        }
    }

    @Composable
    // Stop "Button"
    // Box containing an image (Red octagon) and Text ("Stop")
    private fun StopButton() {
        Box {
            Image(
                painter = painterResource(id = R.drawable.iconmonstr_octagon_1),
                contentDescription = "Stop Button",
                modifier = Modifier
                    .align(alignment = Alignment.Center)
                    .requiredSize((LocalConfiguration.current.smallestScreenWidthDp * 0.5f).dp)
                    .clickable { activity.onClickStop() }
            )
            Text(
                text = stringResource(id = R.string.stop_button),
                fontSize =  30.sp,
                fontFamily = FontFamily.SansSerif,
                color =  colorResource(com.google.android.material.R.color.design_default_color_surface),
                modifier = Modifier.align(alignment = Alignment.Center),
            )


        }
    }


    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    // Snooze "Button"
    // Box clipped to a circle+Text+menu(Collapsed)
    private fun SnoozeButton() {

        // Keep the state of the menu
        var expanded by rememberSaveable { mutableStateOf(false) }

        // Circle
        Box(
            modifier = Modifier
                .requiredSize((LocalConfiguration.current.smallestScreenWidthDp * 0.5f).dp)
                .clip(CircleShape)
                .background(color = colorResource(android.R.color.holo_orange_dark))
                .combinedClickable(
                    onClick = { activity.onClickSnooze() },
                    onClickLabel = stringResource(id = R.string.snooze_button),
                    onLongClick = { expanded = true },
                    onLongClickLabel = stringResource(id = R.string.snooze_duration_label),
                )
        ){

            // "Snooze for XXX
            Text(
                activity.snoozeButtonTextCompose(),
                color =  colorResource(com.google.android.material.R.color.design_default_color_surface),
                fontSize =  30.sp,
                fontFamily = FontFamily.SansSerif,
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
                        activity.onSnoozeMenuItemSelected(index)}
                )
            }
        }


    }


    @Composable
    private fun isPortrait():Boolean{
        return LocalConfiguration.current.orientation.equals(Configuration.ORIENTATION_PORTRAIT)
        //return (LocalConfiguration.current.screenWidthDp<LocalConfiguration.current.screenHeightDp)
    }
}

