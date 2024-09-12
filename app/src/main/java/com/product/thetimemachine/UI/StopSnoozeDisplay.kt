@file:JvmName("StopSnoozeDisplay")

package com.product.thetimemachine.UI


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.FlowColumnScopeInstance.align
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.product.thetimemachine.R


fun setContent(composeView: ComposeView, label: String, activity: StopSnoozeActivity) {
    composeView.setContent { StopSnoozeDisplay(label, activity) }
}
@Composable
fun StopSnoozeDisplay(label: String, activity: StopSnoozeActivity? = null) {

    Surface{
        MaterialTheme {
            if (activity!=null)
                ContentViewCompose(activity.DisplayScreenText(), activity)
            else
                ContentViewCompose(label)
        }
    }
}



@Composable
private fun ContentViewCompose(name: String, activity: StopSnoozeActivity? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(R.color.light_blue_A400)),
        horizontalAlignment = Alignment.CenterHorizontally,

    ){
        // Label + Alarm time
    Text(
        text = name,
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier
            .background(color = colorResource(R.color.light_blue_A400))
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
    )

        // Image (Stop) that serves as stop-button
        Spacer(modifier = Modifier.size(20.dp))
        StopButton(activity)
    }
}

@Preview
@Composable
private fun StopSnoozeDisplayPrev() {
    StopSnoozeDisplay("Test 123")
}

@Composable
private fun StopButton(activity: StopSnoozeActivity? = null) {
    Box() {
        Image(
            painter = painterResource(id = R.drawable.iconmonstr_octagon_1),
            contentDescription = "Stop Button",
            modifier = Modifier
                .align(alignment = Alignment.Center)
                .requiredSize((LocalConfiguration.current.screenHeightDp * 0.2f).dp)
                .clickable { onClickStop(activity) }
        )
        Text(
            text = "Stop",
            modifier = Modifier.align(alignment = Alignment.Center),
            )
    }
}

// Stop Button clicked
private fun onClickStop(activity: StopSnoozeActivity? = null)
{
    activity?.onClickStop()
}


