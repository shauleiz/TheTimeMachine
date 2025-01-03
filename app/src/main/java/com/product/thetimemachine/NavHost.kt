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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.product.thetimemachine.ui.AlarmEditScreen
import com.product.thetimemachine.ui.AlarmListScreen
import com.product.thetimemachine.ui.SettingsScreen

@Composable
fun AlarmNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    alarmViewModel: AlarmViewModel?,
    currentBackStack : NavBackStackEntry?,
) {
    NavHost(
        navController = navController,
        startDestination = AlarmList.route,
        //modifier = modifier
    ) {
        composable(route = AlarmList.route) {
            ShowAlarmListScreen(alarmViewModel, navController, currentBackStack)
        }
        composable(
            route = AlarmEdit.routeWithArgs,
            arguments = AlarmEdit.arguments,
        ) { navBackStackEntry ->
            val accountType =
                navBackStackEntry.arguments?.getLong(AlarmEdit.itemIdArg, 0)
            ShowAlarmEditScreen(navController, accountType, currentBackStack?.destination)
        }
        composable(route = Settings.route) {
            ShowSettingsScreen()
        }
    }
}


@Composable
fun ShowSettingsScreen(){
    SettingsScreen().SettingsFragDisplayTop()
}

@Composable
fun ShowAlarmEditScreen(navController: NavHostController,
                        itemId : Long?,
                        currentDestination: NavDestination?,)
{
    if (itemId != null) AlarmEditScreen(navController, currentDestination).AlarmEditFragDisplayTop(itemId)
}

@Composable
fun ShowAlarmListScreen(alarmViewModel: AlarmViewModel?,
                        navController: NavHostController,
                        currentBackStack : NavBackStackEntry?, )
{

    if (alarmViewModel != null && currentBackStack != null) {
        AlarmListScreen(navController). AlarmListFragDisplay(alarmViewModel)
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }

private fun NavHostController.navigateToAlarmEdit(accountType: String) {
    this.navigateSingleTopTo("${AlarmEdit.route}/$accountType")
}
