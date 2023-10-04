package com.gogolook.wheresmoney.ui.expense

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gogolook.wheresmoney.data.Category
import com.gogolook.wheresmoney.data.Expense
import com.gogolook.wheresmoney.ui.components.FloatingActionButton
import com.gogolook.wheresmoney.ui.components.PrimaryStandardButton
import com.gogolook.wheresmoney.ui.components.Toolbar
import com.gogolook.wheresmoney.ui.components.ToolbarAction
import com.gogolook.wheresmoney.ui.main.dateFormatter
import com.gogolook.wheresmoney.ui.theme.LocalColors
import java.util.Calendar
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
    val name = remember { mutableStateOf("") }
    val date = remember { mutableStateOf(Date()) }
    val categoryId = remember { mutableIntStateOf(0) }
    val amount = remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = expense) {
        name.value = expense?.name ?: ""
        date.value = expense?.date ?: Date()
        categoryId.intValue = expense?.categoryId ?: 0
        amount.intValue = expense?.amount ?: 0
    }

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
        },
        floatingActionButton = {
            FloatingActionButton(icon = Icons.Outlined.Check) {
                onSave(
                    Expense(
                        id = expense?.id ?: 0,
                        name = name.value,
                        amount = amount.intValue,
                        categoryId = categoryId.intValue,
                        date = date.value
                    )
                )
            }
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
                    .clip(MaterialTheme.shapes.small),
                headlineContent = {
                    TextField(
                        value = name.value,
                        onValueChange = { name.value = it },
                        modifier = Modifier,
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                        )
                    )
                },
                overlineContent = { Text(text = "Name") },
            )
            ListItem(
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .clip(MaterialTheme.shapes.small)
                    .clickable { shouldShowCategoryPicker.value = true },
                headlineContent = {
                    Text(
                        text = categories.find { it.id == categoryId.intValue }?.name ?: "",
                        color = categories.find { it.id == categoryId.intValue }?.color?.let {
                            Color(
                                it
                            )
                        } ?: Color.Transparent
                    )
                },
                overlineContent = { Text(text = "Category") },
            )
            ListItem(
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .clip(MaterialTheme.shapes.small)
                    .clickable(onClick = { shouldShowAmountCalculator.value = true }),
                headlineContent = { Text(text = amount.intValue.toString()) },
                overlineContent = { Text(text = "Amount") },
            )
            ListItem(
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .clip(MaterialTheme.shapes.small)
                    .clickable { shouldShowDatePicker.value = true },
                headlineContent = {
                    Text(
                        text = dateFormatter.format(date.value)
                    )
                },
                overlineContent = { Text(text = "Date") },
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
                categoryId.intValue = it.id
                shouldShowCategoryPicker.value = false
            }
        }
    }
    AnimatedVisibility(visible = shouldShowAmountCalculator.value) {
        AlertDialog(onDismissRequest = { shouldShowAmountCalculator.value = false }) {
            AmountCalculator(amount.intValue) {
                amount.intValue = it
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
fun AmountCalculator(
    defaultAmount: Int,
    onPick: (amount: Int) -> Unit
) {

    fun calculate(formula: String): Int {
        val numbers = formula.split("+", "-", "*").map { it.toInt() }
        val operators = formula.split("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
            .filter { it.isNotEmpty() }
        var newAmount = 0

        numbers.forEachIndexed { index, number ->
            if (index == 0) {
                newAmount += number
            } else {
                when (operators[index - 1]) {
                    "+" -> newAmount += number
                    "-" -> newAmount -= number
                    "*" -> newAmount *= number
                }
            }
        }
        return newAmount
    }

    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(LocalColors.current.surfacePrimary)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val newAmount = remember { mutableIntStateOf(defaultAmount) }
        val formula = remember { mutableStateOf(newAmount.intValue.toString()) }

        ListItem(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .clip(MaterialTheme.shapes.small)
                .background(LocalColors.current.green200),
            headlineContent = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = formula.value,
                        fontSize = 24.sp,
                    )
                }
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent,
            )
        )

        CalculatorButtonGrid { text ->
            when (text) {
                "=" -> {
                    newAmount.intValue = calculate(formula.value)
                    formula.value = newAmount.intValue.toString()
                }

                "C" -> formula.value = "0"
                "←" -> formula.value = formula.value.dropLast(1)
                else -> formula.value += text
            }
        }

        PrimaryStandardButton(
            modifier = Modifier
                .padding(top = 32.dp)
                .align(Alignment.End),
            onClick = {
                newAmount.intValue = calculate(formula.value)
                onPick(newAmount.intValue)
            }
        )
    }
}

@Composable
fun CalculatorButton(
    text: String,
    onClick: (String) -> Unit
) {
    Button(
        modifier = Modifier
            .padding(4.dp)
            .size(36.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = LocalColors.current.greenA100,
            contentColor = Color.DarkGray
        ),
        contentPadding = PaddingValues(0.dp),
        onClick = { onClick(text) }
    ) {
        Text(text = text, fontSize = 24.sp)
    }
}

@Composable
fun CalculatorButtonGrid(
    onClick: (String) -> Unit
) {
    val buttonTexts =
        listOf("7", "8", "9", "+", "4", "5", "6", "-", "1", "2", "3", "*", "C", "0", "←", "=")
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(4.dp),
    ) {
        items(items = buttonTexts) { buttonText ->
            CalculatorButton(text = buttonText, onClick = onClick)
        }
    }
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
            .clip(MaterialTheme.shapes.small)
            .background(LocalColors.current.surfacePrimary)
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
            onClick = { onPick(selectedCategory ?: categories[0]) }
        )
    }
}

@Composable
private fun CategoryItem(category: Category, isSelected: Boolean, onItemClicked: () -> Unit) {
    ListItem(
        modifier = Modifier
            .padding(vertical = 6.dp)
            .clip(MaterialTheme.shapes.small)
            .clickable { onItemClicked() },
        headlineContent = {
            Text(
                modifier = Modifier,
                text = category.name,
                color = Color(category.color)
            )
        },
        trailingContent = {
            if (isSelected) {
                IconButton(
                    modifier = Modifier
                        .background(LocalColors.current.primary, CircleShape)
                        .size(24.dp),
                    onClick = { },
                    enabled = false,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = LocalColors.current.onSurfaceHighlightPrimary,
                        disabledContentColor = LocalColors.current.onSurfaceHighlightPrimary
                    )
                ) {
                    Icon(Icons.Outlined.Check, contentDescription = null)
                }
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
    val calendar = Calendar.getInstance()
    calendar.time = defaultDate ?: Date()
    val year = remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    val month = remember { mutableIntStateOf(calendar.get(Calendar.MONTH) + 1) }
    val day = remember { mutableIntStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }

    val yearList = (calendar.get(Calendar.YEAR) - 5..calendar.get(Calendar.YEAR) + 5).toList()
    val monthList = (1..12).toList()
    val dayList = (1..calendar.getActualMaximum(Calendar.DAY_OF_MONTH)).toList()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalColors.current.surfacePrimary, MaterialTheme.shapes.small)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DatePickerItem(Modifier.width(100.dp), "Year", year.intValue, yearList) {
                year.intValue = it
            }
            DatePickerItem(Modifier.width(80.dp), "Month", month.intValue, monthList) {
                month.intValue = it
            }
            DatePickerItem(Modifier.width(80.dp), "Day", day.intValue, dayList) {
                day.intValue = it
            }
        }
        PrimaryStandardButton(
            modifier = Modifier
                .padding(top = 32.dp)
                .align(Alignment.End),
            onClick = {
                calendar.set(year.intValue, month.intValue - 1, day.intValue)
                onPick(calendar.time)
            }
        )
    }
}

@Composable
fun DatePickerItem(
    modifier: Modifier,
    type: String,
    defaultItem: Int?,
    items: List<Int>,
    onPick: (Int) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableIntStateOf(defaultItem ?: 0) }
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column {
        OutlinedTextField(
            modifier = modifier,
            value = selectedItem.toString(),
            label = { Text(type) },
            readOnly = true,
            singleLine = true,
            onValueChange = { },
            trailingIcon = {
                Icon(
                    icon,
                    modifier = Modifier.clickable { expanded = !expanded },
                    contentDescription = null
                )
            }
        )
        DropdownMenu(
            modifier = modifier,
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.toString()) },
                    onClick = {
                        expanded = false
                        selectedItem = item
                        onPick(selectedItem)
                    },
                )
            }
        }
    }
}