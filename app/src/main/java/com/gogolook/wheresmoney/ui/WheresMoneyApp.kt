package com.gogolook.wheresmoney.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gogolook.wheresmoney.ui.category.CategoryScreen
import com.gogolook.wheresmoney.ui.expense.ExpenseScreen
import com.gogolook.wheresmoney.ui.main.MainScreen
import com.gogolook.wheresmoney.ui.navigation.NavRoute
import com.gogolook.wheresmoney.ui.navigation.NavigationActions
import com.gogolook.wheresmoney.ui.navigation.categoryId
import com.gogolook.wheresmoney.ui.navigation.expenseId
import com.gogolook.wheresmoney.ui.setting.SettingScreen
import com.gogolook.wheresmoney.ui.theme.WheresMoneyTheme

@Composable
fun WheresMoneyApp(finishActivity: () -> Unit) {
    WheresMoneyTheme {
        val navController = rememberNavController()
        val navActions = remember(navController) {
            NavigationActions(navController)
        }
        NavGraph(
            modifier = Modifier.fillMaxSize(),
            finishActivity = finishActivity,
            navController = navController,
            navActions = navActions,
        )
    }
}

@Composable
fun NavGraph(
    modifier: Modifier,
    finishActivity: () -> Unit = {},
    navController: NavHostController,
    navActions: NavigationActions
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = NavRoute.Home.route
    ) {
        composable(NavRoute.Home.route) {
            BackHandler { finishActivity() }
            MainScreen(
                viewModel = hiltViewModel(),
                gotoSetting = { navActions.navigateTo(NavRoute.CategorySetting) },
                createExpense = { navActions.navigateTo(NavRoute.Expense(0)) },
                updateExpense = { expenseId ->
                    navActions.navigateTo(NavRoute.Expense(expenseId))
                },
            )
        }

        composable(NavRoute.CategorySetting.route) {
            SettingScreen(
                viewModel = hiltViewModel(),
                back = navActions.back,
                createCategory = { navActions.navigateTo(NavRoute.Category(0)) },
                updateCategory = { categoryId ->
                    navActions.navigateTo(NavRoute.Category(categoryId))
                },
            )
        }

        composable(
            route = NavRoute.Category.route,
            arguments = NavRoute.Category.arguments,
        ) {
            CategoryScreen(
                viewModel = hiltViewModel(),
                categoryId = it.categoryId ?: 0,
                onCompleted = navActions.back,
            )
        }

        composable(
            route = NavRoute.Expense.route,
            arguments = NavRoute.Expense.arguments,
        ) {
            ExpenseScreen(
                viewModel = hiltViewModel(),
                back = navActions.back,
                expenseId = it.expenseId ?: 0,
                onCompleted = navActions.back,
            )
        }
    }
}