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

import androidx.navigation.NavType
import androidx.navigation.navArgument

/**
 * Contract for information needed on every Rally navigation destination
 */

sealed interface Destination {
    val route: String
    val label: Int
}

/**
 * Rally app navigation destinations
 */
data object AlarmEdit : Destination {
    override val route = "AlarmEdit"
    override val label = R.string.alarmsetup_title
    const val ITEM_ID_ARG = "item_id"
    val routeWithArgs = "$route/{$ITEM_ID_ARG}"
    val arguments = listOf(navArgument(ITEM_ID_ARG) { type = NavType.LongType })
}

data object AlarmEditEntry : Destination {
    override val route = "AlarmEditEntry"
    override val label = R.string.alarmsetup_title
}

data object AlarmList :  Destination {
    override val route = "AlarmList"
    override val label = R.string.alarmlist_title
}

data object Settings : Destination {
    override val route = "Settings"
    override val label = R.string.settings_title
}


// Screens to be displayed
//val alarmScreens = listOf(AlarmEdit, AlarmList, Settings)
