@file:JvmName("StopSnoozeDisplay")

package com.product.thetimemachine.ui


//import androidx.compose.foundation.layout.FlowColumnScopeInstance.align
import android.content.res.TypedArray
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.integerArrayResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.io.Resources
import com.product.thetimemachine.R


class StopSnoozeDisplay (val activity: StopSnoozeActivity) {


fun setContent(composeView: ComposeView) {
    composeView.setContent { StopSnoozeDisplayTop() }
}
@Composable
fun StopSnoozeDisplayTop() {

    Surface{
        MaterialTheme {
            ContentViewCompose(activity.DisplayScreenText())
        }
    }
}



@Composable
private fun ContentViewCompose(name: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(com.product.thetimemachine.R.color.light_blue_A400)),
        horizontalAlignment = Alignment.CenterHorizontally,

    ){
        // Label + Alarm time
    Text(
        text = name,
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier
            .background(color = colorResource(com.product.thetimemachine.R.color.light_blue_A400))
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
    )

        // Image (Stop) that serves as stop-button
        Spacer(modifier = Modifier.size(20.dp))
        StopButton()

        // Box (Snooze) that serves as stop-button
        Spacer(modifier = Modifier.size(20.dp))
        SnoozeButton()
    }
}



@Composable
private fun StopButton() {
    Box() {
        Image(
            painter = painterResource(id = com.product.thetimemachine.R.drawable.iconmonstr_octagon_1),
            contentDescription = "Stop Button",
            modifier = Modifier
                .align(alignment = Alignment.Center)
                .requiredSize((LocalConfiguration.current.screenHeightDp * 0.2f).dp)
                .clickable { activity.onClickStop() }
        )
        Text(
            text = stringResource(id = com.product.thetimemachine.R.string.stop_button),
            fontSize =  14.sp,
            fontFamily = FontFamily.SansSerif,
            //style = androidx.appcompat.R.style.Base_TextAppearance_AppCompat_Body2,
            modifier = Modifier.align(alignment = Alignment.Center),
            )


    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SnoozeButton() {

    // Keep the state of the menu
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size((LocalConfiguration.current.screenHeightDp * 0.2f).dp)
            .clip(CircleShape)
            .background(color = colorResource(android.R.color.holo_orange_dark))
            .combinedClickable(
                onClick = { activity.onClickSnooze() },
                onClickLabel = "Snooze", //TODO: Replace
                onLongClick = { expanded = true },
                onLongClickLabel = "Modify Snooze Duration", //TODO: Replace
            )
    ){

            Text(
                activity.snoozeButtonTextCompose(),
                fontSize =  14.sp,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.align(alignment = Alignment.Center),
            )

        SnoozePopupMenu (expanded,  onSnoozeMenuSelected = {expanded=it} )
    }
}

@Composable
private fun SnoozePopupMenu ( expanded:Boolean, onSnoozeMenuSelected : (Boolean)->Unit){

    // Arrays of Entries/Values
    val snoozeDurationEntries = stringArrayResource(R.array.snooze_duration_entries)



    // Create the menu
    DropdownMenu(
        expanded = expanded, // Temp
        onDismissRequest = { onSnoozeMenuSelected(false)  },
    ) {
        for (index in snoozeDurationEntries.indices) {
            DropdownMenuItem(
                text = { Text(snoozeDurationEntries[index] )},
                onClick = { onSnoozeMenuSelected(false) },
            )
        }
    }


}

}

