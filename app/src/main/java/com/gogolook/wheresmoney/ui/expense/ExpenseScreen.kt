package com.gogolook.wheresmoney.ui.expense

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gogolook.wheresmoney.data.Category
import com.gogolook.wheresmoney.data.Expense
import com.gogolook.wheresmoney.ui.components.PrimaryStandardButton
import com.gogolook.wheresmoney.ui.theme.LocalColors
import com.gogolook.wheresmoney.ui.theme.LocalTypography
import java.util.Date

/**
 * Expense screen
 * A composable that provides a screen for user to create or edit an expense
 * @param viewModel: ExpenseViewModel
 * @param expenseId: the id of the expense to be edited, or 0 if creating a new expense
 * @param onCompleted: callback when user finish creating or editing an expense
 */
@Composable
fun ExpenseScreen(
    viewModel: ExpenseViewModel,
    expenseId: Int,
    onCompleted: () -> Unit = {},
) {
    LaunchedEffect(key1 = expenseId) {
        viewModel.fetchCategories()
        viewModel.fetchExpense(expenseId)
    }
    ExpenseView(
        expense = viewModel.expense.value,
        categories = viewModel.categories.value,
        onSave = { expense ->
            viewModel.saveExpense(expense)
            onCompleted()
        }
    )
}

/**
 * Expense view
 * A composable that provides a view with for user to create or edit an expense, you can choose a style
 * 1. A view with toolbar, or
 * 2. A non-full screen view just like a dialog
 * There are four editable fields:
 * 1. Name: the name of the expense
 * 2. Category: the category of the expense (clicking on this field will show a category picker)
 * 3. Date: the date of the expense (clicking on this field will show a date picker)
 * 4. Amount: the amount of the expense
 * @param expense: the expense to be edited, or null if creating a new expense
 * @param categories: the list of categories
 * @param onSave: callback when user finish creating or editing an expense
 */
@Composable
fun ExpenseView(expense: Expense?, categories: List<Category>, onSave: (expense: Expense) -> Unit) {
    val shouldShowDatePicker = remember { mutableStateOf(false) }
    val shouldShowCategoryPicker = remember { mutableStateOf(false) }
    val shouldShowAmountCalculator = remember { mutableStateOf(false) }
    val date = remember { mutableStateOf(Date()) }
    val categoryId = remember { mutableStateOf(0) }
    val amount = remember { mutableStateOf(0) }

    // TODO Implement ExpenseView

    AnimatedVisibility(visible = shouldShowCategoryPicker.value) {
        DatePicker(expense?.date) {
            date.value = it
            shouldShowDatePicker.value = false
        }
    }
    AnimatedVisibility(visible = shouldShowCategoryPicker.value) {
        CategoryPicker(categories, expense?.category) {
            categoryId.value = it.id
            shouldShowCategoryPicker.value = false
        }
    }
    AnimatedVisibility(visible = shouldShowAmountCalculator.value) {
        AmountCalculator(amount.value) {
            amount.value = it
            shouldShowAmountCalculator.value = false
        }
    }
}

/**
 * Amount calculator
 * A composable that provides
 * 1. A text field for user to input amount
 * 2. A number pad supports +-x/ for user to input amount
 * 3. A button to confirm the amount
 * @param defaultAmount: the default amount of the amount calculator
 * @param onPick: callback when user pick an amount
 */
@Composable
fun AmountCalculator(defaultAmount: Int, onPick: (amount: Int) -> Unit) {

}

/**
 * Category picker
 * A composable that provides
 * 1. A single choice list of categories
 * 2. A button to confirm the category
 * @param categories: the list of categories
 * @param defaultCategory: the default category of the category picker
 * @param onPick: callback when user pick a category
 */
@Composable
fun CategoryPicker(
    categories: List<Category>,
    defaultCategory: Category?,
    onPick: (category: Category) -> Unit
) {
    Column(
        modifier = Modifier
            .background(LocalColors.current.surfacePrimary, MaterialTheme.shapes.large)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var selectedCategory by remember { mutableStateOf(defaultCategory) }

        LazyColumn(
            modifier = Modifier
        ) {
            items(items = categories) { category ->
                CategoryItem(
                    category = category,
                    isSelected = category == selectedCategory,
                    onItemClicked = { selectedCategory = category}
                )
            }
        }
        PrimaryStandardButton(
            modifier = Modifier
                .padding(top = 32.dp)
                .align(Alignment.End),
            text = "OK",
            onClick = {
                onPick(selectedCategory ?: categories[0])
            }
        )
    }
}

@Composable
private fun CategoryItem(category: Category, isSelected: Boolean, onItemClicked: () -> Unit) {
    ListItem(
        modifier = Modifier
            .padding(vertical = 6.dp)
            .background(Color.White, MaterialTheme.shapes.large)
            .clickable { onItemClicked() },
        headlineContent = {
            Text(
                modifier = Modifier,
                text = category.name,
                color = Color(category.color)
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
        ),
        trailingContent = {
            if (isSelected) {
                Text(
                    modifier = Modifier
                        .background(LocalColors.current.primary, CircleShape)
                        .size(24.dp)
                        .padding(horizontal = 4.dp),
                    text = "✔",
                    color = LocalColors.current.onSurfaceHighlightPrimary,
                    fontSize = LocalTypography.current.m4.fontSize
                )
            }
        }
    )
}

/**
 * Date picker
 * A composable that provides
 * 1. A date picker with year, month, day (do not use 3rd-party library)
 * 2. A button to confirm the date
 * @param defaultDate: the default date of the date picker
 * @param onPick: callback when user pick a date
 */
@Composable
fun DatePicker(defaultDate: Date?, onPick: (date: Date) -> Unit) {

}