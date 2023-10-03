package com.gogolook.wheresmoney.ui.navigation

import androidx.navigation.NavController

class NavigationActions(private val navController: NavController) {

    val back: () -> Unit = { navController.popBackStack() }

    val navigateTo: (navItem: NavItem) -> Unit = {
        navController.navigate(it.destination) {
            launchSingleTop = true
            restoreState = true
        }
    }
}