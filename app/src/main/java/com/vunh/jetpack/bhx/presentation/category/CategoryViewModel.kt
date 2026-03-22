package com.vunh.jetpack.bhx.presentation.category

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class CategoryUiState(
    val selectedCategoryId: String = "2",
    val availableMainCategories: List<MainCat> = mainCategories,
    val availableSubCategories: List<SubCat> = subCategories
) {
    val selectedSubCategories: List<SubCat>
        get() = availableSubCategories.filter { it.mainId == selectedCategoryId }
}

@HiltViewModel
class CategoryViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    fun selectCategory(categoryId: String) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
    }
}
