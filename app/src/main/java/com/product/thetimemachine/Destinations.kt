/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.product.thetimemachine

import com.product.thetimemachine.Application.TheTimeMachineApp

/**
 * Contract for information needed on every Rally navigation destination
 */

sealed interface Destination {
    val route: String
    val label: String
}

/**
 * Rally app navigation destinations
 */
data object AlarmEdit : Destination {
    override val route = "AlarmEdit"
    override val label = TheTimeMachineApp.appContext.getString(R.string.alarmsetup_title)
}

data object AlarmList :  Destination {
    override val route = "AlarmList"
    override val label = TheTimeMachineApp.appContext.getString(R.string.alarmlist_title)

}

data object Settings : Destination {
    override val route = "Settings"
    override val label = TheTimeMachineApp.appContext.getString(R.string.settings_title)
}


// Screens to be displayed
val alarmScreens = listOf(AlarmEdit, AlarmList, Settings)
