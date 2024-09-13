@file:JvmName("StopSnoozeDisplay")

package com.product.thetimemachine.ui


import android.graphics.drawable.shapes.Shape
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.FlowColumnScopeInstance.align
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.material.resources.TextAppearance
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

        // Box (Snooze) that serves as stop-button
        Spacer(modifier = Modifier.size(20.dp))
        SnoozeButton(activity)
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
                .clickable { activity?.onClickStop() }
        )
        Text(
            text = stringResource(id = R.string.stop_button),
            fontSize =  14.sp,
            fontFamily = FontFamily.SansSerif,
            //style = androidx.appcompat.R.style.Base_TextAppearance_AppCompat_Body2,
            modifier = Modifier.align(alignment = Alignment.Center),
            )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SnoozeButton(activity: StopSnoozeActivity? = null) {
    Box(
        modifier = Modifier
            .size((LocalConfiguration.current.screenHeightDp * 0.2f).dp)
            .clip(CircleShape)
            .background(color = colorResource(android.R.color.holo_orange_dark))
            .combinedClickable (onClick = {activity?.onClickStop()})
    ){
        Text(
            stringResource(id = R.string.snooze_button) ,
            fontSize =  14.sp,
            fontFamily = FontFamily.SansSerif,
            //style = androidx.appcompat.R.style.Base_TextAppearance_AppCompat_Body2,
            modifier = Modifier.align(alignment = Alignment.Center),
        )
    }
}

