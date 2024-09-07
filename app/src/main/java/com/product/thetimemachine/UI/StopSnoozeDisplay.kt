@file:JvmName("StopSnoozeDisplay")

package com.product.thetimemachine.UI

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView

@Composable
fun StopSnoozeDisplay() {
    Surface {
        Text("Hello Compose")
    }
}

fun setContent(composeView: ComposeView) {
    composeView.setContent { StopSnoozeDisplay() }
}

