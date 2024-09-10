@file:JvmName("StopSnoozeDisplay")

package com.product.thetimemachine.UI

import androidx.annotation.ContentView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.product.thetimemachine.R
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat.getColor

fun setContent(composeView: ComposeView) {
    composeView.setContent { StopSnoozeDisplay() }
}
@Composable
fun StopSnoozeDisplay() {
    Surface() {
        MaterialTheme {
            ContentViewCompose("Apple")
        }
    }
}



@Composable
private fun ContentViewCompose(name: String) {
    Text(
        text = name,
        style = MaterialTheme.typography.headlineLarge,
        color = colorResource(id = com.google.android.material.R.color.design_default_color_surface),
        modifier = Modifier
            .background(color = colorResource(R.color.light_blue_A400))
            .fillMaxWidth()
            .padding(horizontal = 5.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
    )
}

@Preview
@Composable
private fun StopSnoozeDisplayPrev() {
    StopSnoozeDisplay()
}


