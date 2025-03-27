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

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.product.thetimemachine.ui.AlarmEditScreen
import com.product.thetimemachine.ui.AlarmListScreen
import com.product.thetimemachine.ui.SettingsScreen

@Composable
fun AlarmNavHost(
    alarmViewModel: AlarmViewModel?,
) {


    val navController = rememberNavController()


    NavHost(
        navController = navController,
        startDestination = AlarmList.route,
    ) {
        composable(route = AlarmList.route) {
            ShowAlarmListScreen(alarmViewModel, navController)
        }
        composable(
            route = AlarmEdit.routeWithArgs,
            arguments = AlarmEdit.arguments,
        ) { navBackStackEntry ->
            val itemId =
                navBackStackEntry.arguments?.getLong(AlarmEdit.ITEM_ID_ARG, 0)
            ShowAlarmEditScreen(navController, itemId)
        }
        composable(route = Settings.route) {
            //mainActivity.NavigationBarBgColor()
            ShowSettingsScreen(navController)
        }
    }
}


@Composable
fun ShowSettingsScreen(navController: NavHostController){
    SettingsScreen(navBack = {navController.popBackStack()}).SettingsDisplayTop()
}

@Composable
fun ShowAlarmEditScreen(navController: NavHostController,
                        itemId : Long?, )
{
    if (itemId != null) AlarmEditScreen(
        navToSettings = { navigate2Settings(navController) },
        navyToAlarmList = {navigate2AlarmList(navController)},
        navBack = {navController.popBackStack()},
    ).AlarmEditDisplayTop(itemId)
}

@Composable
fun ShowAlarmListScreen(alarmViewModel: AlarmViewModel?,
                        navController: NavHostController,
                        )
{

    if (alarmViewModel != null) {
        AlarmListScreen(
            alarmViewModel = alarmViewModel,
            navToSettings = { navigate2Settings(navController) },
            navToAlarmEdit = {navigate2AlarmEdit(navController,it)}
        ).AlarmListDisplay()
    }
}

private fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        Log.d("THE_TIME_MACHINE", "+++ navigateSingleTopTo(): route=${route}")

        // popUp from AlarmList is outside the app
        if (route == AlarmList.route) {
            popUpTo(
                this@navigateSingleTopTo.graph.findStartDestination().id
            ) {
                saveState = true
            }
        }

        // Avoid multiple copies of the same destination when
        // re-selecting the same item
        launchSingleTop = true
        // Restore state when re-selecting a previously selected item
        restoreState = false


    }


private fun navigate2Target(navController: NavHostController, route : String){
    navController.navigateSingleTopTo(route)
}

fun navigate2AlarmEdit(navController: NavHostController, itemId: Long) {
    Log.d("THE_TIME_MACHINE", "+++ navigate2AlarmEdit(): itemId=${itemId}")
    navigate2Target(navController, "${AlarmEdit.route}/$itemId")
}

fun navigate2AlarmList(navController: NavHostController) {
    navigate2Target(navController, AlarmList.route)
}

fun navigate2Settings(navController: NavHostController) {
    navigate2Target(navController, Settings.route)
}


