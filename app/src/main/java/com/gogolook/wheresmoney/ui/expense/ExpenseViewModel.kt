package com.gogolook.wheresmoney.ui.expense

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gogolook.wheresmoney.data.Category
import com.gogolook.wheresmoney.data.CategoryRepository
import com.gogolook.wheresmoney.data.Expense
import com.gogolook.wheresmoney.data.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val expenseRepository: ExpenseRepository,
) : ViewModel() {

    val categories: MutableState<List<Category>> = mutableStateOf(emptyList())
    val expense: MutableState<Expense?> = mutableStateOf(null)

    suspend fun fetchCategories() {
        categories.value = withContext(Dispatchers.IO) {
            categoryRepository.getAll()
        }
    }

    suspend fun fetchExpense(expenseId: Int) {
        if (expenseId == 0) return
        expense.value = withContext(Dispatchers.IO) {
            expenseRepository.get(expenseId)
        }
    }

    fun saveExpense(expense: Expense) {
        viewModelScope.launch(Dispatchers.IO) {
            if (expense.id != 0) {
                expenseRepository.update(expense)
            } else {
                expenseRepository.insert(expense)
            }
        }
    }
}