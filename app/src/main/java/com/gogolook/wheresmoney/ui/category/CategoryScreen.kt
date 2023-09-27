package com.gogolook.wheresmoney.ui.category

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gogolook.wheresmoney.data.Category
import com.gogolook.wheresmoney.ui.components.PrimaryStandardButton
import com.gogolook.wheresmoney.ui.theme.LocalColors

/**
 * Category screen
 * A composable that provides a screen for user to create or edit a category
 * @param viewModel: CategoryViewModel
 * @param categoryId: the id of the category to be edited, or 0 if creating a new category
 * @param onCompleted: callback when user finish creating or editing a category
 */
@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel,
    categoryId: Int,
    onCompleted: () -> Unit = {},
) {
    LaunchedEffect(key1 = categoryId) {
        viewModel.fetchCategory(categoryId)
    }
    CategoryView(viewModel.category.value, onSave = { category ->
        viewModel.saveCategory(category)
        onCompleted()
    })
}

/**
 * Category view
 * A composable that provides a view for user to create or edit a category, you can choose a style
 * 1. A view with toolbar, or
 * 2. A non-full screen view just like a dialog
 * There are two editable fields:
 * 1. Name: the name of the category
 * 2. Color: the color of the category (clicking on this field will show a color picker)
 * @param category: the category to be edited, or null if creating a new category
 * @param onSave: callback when user finish creating or editing a category
 */
@Composable
fun CategoryView(category: Category?, onSave: (category: Category) -> Unit) {
    val shouldShowColorPicker = remember { mutableStateOf(false) }
    val color = remember { mutableStateOf(Color.Red) }

    // TODO Implement CategoryView

    AnimatedVisibility(visible = shouldShowColorPicker.value) {
        ColorPicker(defaultColor = color.value, onPick = {
            color.value = it
            shouldShowColorPicker.value = false
        })
    }
}

/**
 * Color picker
 * A composable that provides
 * 1. Three Slider for each RGB value, and
 * 2. A preview of the color
 * 3. A button to confirm the color
 * @param defaultColor: the default color of the color picker
 * @param onPick: callback when user pick a color
 */
@Composable
fun ColorPicker(defaultColor: Color? = null, onPick: (color: Color) -> Unit) {
    var r by remember { mutableFloatStateOf(defaultColor?.red ?: 0f) }
    var g by remember { mutableFloatStateOf(defaultColor?.green ?: 0f) }
    var b by remember { mutableFloatStateOf(defaultColor?.blue ?: 0f) }
    var selectedColor by remember { mutableStateOf(defaultColor ?: Color.Black) }

    Column(
        modifier = Modifier
            .background(LocalColors.current.surfacePrimary)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SliderWithText("R", LocalColors.current.red, r) {
            r = it
            selectedColor = Color(r, g, b)
        }
        SliderWithText("G", LocalColors.current.green, g) {
            g = it
            selectedColor = Color(r, g, b)
        }
        SliderWithText("B", LocalColors.current.blue, b) {
            b = it
            selectedColor = Color(r, g, b)
        }
        Box(
            modifier = Modifier
                .width(150.dp)
                .height(75.dp)
                .padding(top = 16.dp)
                .background(selectedColor)
        )
        Text(
            modifier = Modifier.padding(top = 6.dp),
            text = selectedColor.toHex(),
            fontSize = 20.sp,
            color = LocalColors.current.onSurfaceVariant
        )
        PrimaryStandardButton(
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.End),
            text = "OK",
            onClick = {
                onPick(selectedColor)
            }
        )
    }
}

@Composable
fun SliderWithText(
    text: String,
    textColor: Color,
    defaultValue: Float,
    onValueChange: (value: Float) -> Unit
) {
    var value by remember { mutableFloatStateOf(defaultValue) }
    Row(
        modifier = Modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = text,
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        Slider(
            modifier = Modifier.weight(9f),
            value = value,
            onValueChange = {
                value = it
                onValueChange(it)
            },
            valueRange = 0f..1f,
            colors = SliderDefaults.colors(
                thumbColor = LocalColors.current.primary,
                activeTrackColor = LocalColors.current.primary,
                inactiveTrackColor = LocalColors.current.onSurfaceVariant,
            )
        )
    }
}

fun Color.toHex(): String {
    return String.format(
        "#%02X%02X%02X",
        (this.red * 255).toInt(),
        (this.green * 255).toInt(),
        (this.blue * 255).toInt()
    )
}