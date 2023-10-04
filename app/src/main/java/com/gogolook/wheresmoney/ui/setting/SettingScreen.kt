package com.gogolook.wheresmoney.ui.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gogolook.wheresmoney.ui.components.FloatingActionButton
import com.gogolook.wheresmoney.ui.components.Toolbar
import com.gogolook.wheresmoney.ui.components.ToolbarAction
import com.gogolook.wheresmoney.ui.theme.LocalColors

/**
 * A composable that provides
 * 1. category list
 * 2. floating button to callback createCategory()
 * 3. click on the category item will callback updateCategory()
 * 4. a toolbar with back button and title
 * @param viewModel the view model for category list.
 * @param back the callback when back button is clicked
 * @param createCategory the callback when create category button is clicked
 * @param updateCategory the callback when category item is clicked
 */
@Composable
fun SettingScreen(
    viewModel: SettingViewModel,
    back: () -> Unit = {},
    createCategory: () -> Unit = {},
    updateCategory: (categoryId: Int) -> Unit = {},
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.fetchCategories()
    }
    val categories = viewModel.categories.value

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Toolbar(
                headerAction = ToolbarAction(
                    image = Icons.Outlined.ArrowBack,
                    onClick = { back() }
                ),
                title = "Categories",
            )
        },
        floatingActionButton = {
            FloatingActionButton(icon = Icons.Outlined.Add) {
                createCategory()
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(LocalColors.current.background)
                .padding(16.dp)
        ) {
            items(items = categories) { category ->
                ListItem(
                    modifier = Modifier
                        .padding(vertical = 6.dp)
                        .clip(MaterialTheme.shapes.small)
                        .clickable { updateCategory(category.id) },
                    headlineContent = {
                        Text(
                            text = category.name,
                            color = Color(category.color)
                        )
                    }
                )
            }
        }
    }
}