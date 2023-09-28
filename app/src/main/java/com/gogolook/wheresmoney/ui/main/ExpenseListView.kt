package com.gogolook.wheresmoney.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gogolook.wheresmoney.data.Expense
import com.gogolook.wheresmoney.ui.theme.LocalColors
import java.text.SimpleDateFormat
import java.util.Locale

val dateFormatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

/**
 * A composable that provides expense list, click on the expense item will callback onExpenseClick().
 * @param viewModel the view model for expense list.
 * @param onExpenseClick the callback when expense item is clicked
 */
@Composable
fun ExpenseListView(viewModel: ExpenseListViewModel, onExpenseClick: (expenseId: Int) -> Unit) {
    LaunchedEffect(key1 = Unit) {
        viewModel.fetchExpenses()
    }
    val expenses = viewModel.expenses.value

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LocalColors.current.background)
            .padding(16.dp)
    ) {
        items(items = expenses) { expense ->
            ExpenseListItem(
                expense = expense,
                onExpenseClick = { onExpenseClick(expense.id) }
            )
        }
    }
}

@Composable
fun ExpenseListItem(expense: Expense, onExpenseClick: () -> Unit) {
    ListItem(
        modifier = Modifier
            .padding(vertical = 6.dp)
            .background(Color.White, MaterialTheme.shapes.small)
            .clickable { onExpenseClick() },
        headlineContent = { Text(text = expense.name) },
        leadingContent = { Text(text = "\$${expense.amount}") },
        trailingContent = {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = expense.category?.name ?: "",
                    color = Color(expense.category?.color ?: 0)
                )
                Text(text = dateFormatter.format(expense.date))
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}