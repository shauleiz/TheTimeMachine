@file:JvmName("StopSnoozeDisplay")

package com.product.thetimemachine.UI


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.FlowColumnScopeInstance.align
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.product.thetimemachine.R

fun setContent(composeView: ComposeView, label: String) {
    composeView.setContent { StopSnoozeDisplay(label) }
}
@Composable
fun StopSnoozeDisplay(label: String) {
    Surface() {
        MaterialTheme {
            ContentViewCompose(label)
        }
    }
}



@Composable
private fun ContentViewCompose(name: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(R.color.light_blue_A400)),
        horizontalAlignment = Alignment.CenterHorizontally,

    ){
    Text(
        text = name,
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier
            .background(color = colorResource(R.color.light_blue_A400))
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
    )
        StopButton()
    }
}

@Preview
@Composable
private fun StopSnoozeDisplayPrev() {
    StopSnoozeDisplay("Test 123")
}

@Composable
private fun StopButton() {
    Box(Modifier
        .background(Color.Green)
        ) {
        Image(
            modifier = Modifier.align(alignment = Alignment.Center),
            painter = painterResource(id = R.drawable.iconmonstr_octagon_1),
            contentDescription = "Stop Button",
        )
        Text(text = "Stop")
    }

}


