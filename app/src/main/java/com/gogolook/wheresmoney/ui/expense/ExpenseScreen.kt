package com.gogolook.wheresmoney.ui.expense

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.gogolook.wheresmoney.ui.components.Toolbar
import com.gogolook.wheresmoney.ui.components.ToolbarAction
import com.gogolook.wheresmoney.ui.main.dateFormatter
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
    back: () -> Unit = {},
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
        back = back,
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseView(
    expense: Expense?,
    categories: List<Category>,
    back: () -> Unit = {},
    onSave: (expense: Expense) -> Unit
) {
    val shouldShowDatePicker = remember { mutableStateOf(false) }
    val shouldShowCategoryPicker = remember { mutableStateOf(false) }
    val shouldShowAmountCalculator = remember { mutableStateOf(false) }
    val date = remember { mutableStateOf(Date()) }
    val categoryId = remember { mutableStateOf(0) }
    val amount = remember { mutableStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Toolbar(
                headerAction = ToolbarAction(
                    image = Icons.Outlined.ArrowBack,
                    onClick = { back() }
                ),
                title = "Expense",
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValues)
                .background(LocalColors.current.background)
                .padding(16.dp)
        ) {
            ListItem(
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .background(Color.White, MaterialTheme.shapes.small)
                    .clickable { },
                headlineContent = { Text(text = expense?.name ?: "") },
                overlineContent = { Text(text = "Name") },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent,
                )
            )
            ListItem(
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .background(Color.White, MaterialTheme.shapes.small)
                    .clickable { shouldShowCategoryPicker.value = true },
                headlineContent = {
                    Text(
                        text = expense?.category?.name ?: "",
                        color = Color(expense?.category?.color ?: 0)
                    )
                },
                overlineContent = { Text(text = "Category") },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent,
                )
            )
            ListItem(
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .background(Color.White, MaterialTheme.shapes.small)
                    .clickable(onClick = { shouldShowAmountCalculator.value = true }),
                headlineContent = { Text(text = expense?.amount.toString()) },
                overlineContent = { Text(text = "Amount") },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent,
                )
            )
            ListItem(
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .background(Color.White, MaterialTheme.shapes.small)
                    .clickable { shouldShowDatePicker.value = true },
                headlineContent = {
                    Text(
                        text = dateFormatter.format(
                            expense?.date ?: Date().time
                        )
                    )
                },
                overlineContent = { Text(text = "Date") },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent,
                )
            )
            PrimaryStandardButton(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .align(Alignment.End),
                text = "✔",
                onClick = {
                    onSave(expense!!)
                }
            )
        }
    }

    AnimatedVisibility(visible = shouldShowDatePicker.value) {
        AlertDialog(onDismissRequest = { shouldShowDatePicker.value = false }) {
            DatePicker(expense?.date) {
                date.value = it
                shouldShowDatePicker.value = false
            }
        }
    }
    AnimatedVisibility(visible = shouldShowCategoryPicker.value) {
        AlertDialog(onDismissRequest = { shouldShowCategoryPicker.value = false }) {
            CategoryPicker(categories, expense?.category) {
                categoryId.value = it.id
                shouldShowCategoryPicker.value = false
            }
        }
    }
    AnimatedVisibility(visible = shouldShowAmountCalculator.value) {
        AlertDialog(onDismissRequest = { shouldShowAmountCalculator.value = false }) {
            AmountCalculator(amount.value) {
                amount.value = it
                shouldShowAmountCalculator.value = false
            }
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
            .background(LocalColors.current.surfacePrimary, MaterialTheme.shapes.small)
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
                    onItemClicked = { selectedCategory = category }
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
            .background(Color.White, MaterialTheme.shapes.small)
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