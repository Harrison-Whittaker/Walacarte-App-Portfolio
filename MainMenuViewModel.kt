package com.example.sous.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sous.data.entities.ShoppingWeekWithEntries
import com.example.sous.data.repositories.DataStoreRepository
import com.example.sous.data.repositories.ShoppingListRepository
import com.example.sous.data.repositories.WeeklyMenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainMenuViewModel(
    private val weeklyMenuRepository: WeeklyMenuRepository,
    private val shoppingListRepository: ShoppingListRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {
    private val _shoppingList = MutableStateFlow<ShoppingWeekWithEntries?>(null)
    val shoppingList: StateFlow<ShoppingWeekWithEntries?> = _shoppingList

    private val _weeklyMenu = MutableStateFlow<List<MenuRecipeCardState>?>(null)
    val weeklyMenu: StateFlow<List<MenuRecipeCardState>?> = _weeklyMenu

    val showTutorialDialog: StateFlow<Boolean> = dataStoreRepository.shouldShowTutorial
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    init {
        refreshLatestLists()
    }

    fun markTutorialAsSeen() {
        viewModelScope.launch {
            dataStoreRepository.markTutorialShown()
        }
    }

    fun refreshLatestLists() {
        viewModelScope.launch {
            _shoppingList.value = shoppingListRepository.getLatestShoppingList().firstOrNull()
            _weeklyMenu.value = weeklyMenuRepository.getLatestWeeklyMenu().firstOrNull()
        }
    }
}