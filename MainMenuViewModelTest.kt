package com.example.sous

import com.example.sous.data.entities.Recipe
import com.example.sous.data.entities.ShoppingWeek
import com.example.sous.data.entities.ShoppingWeekWithEntries
import com.example.sous.data.relations.RecipeWithIngredients
import com.example.sous.data.repositories.DataStoreRepository
import com.example.sous.data.repositories.ShoppingListRepository
import com.example.sous.data.repositories.WeeklyMenuRepository
import com.example.sous.ui.MainMenuViewModel
import com.example.sous.ui.MenuRecipe
import com.example.sous.ui.MenuRecipeCardState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class MainMenuViewModelTest {
    private lateinit var weeklyMenuRepository: WeeklyMenuRepository
    private lateinit var shoppingListRepository: ShoppingListRepository
    private lateinit var dataStoreRepository: DataStoreRepository

    private lateinit var viewModel: MainMenuViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        weeklyMenuRepository = mockk(relaxed = true)
        shoppingListRepository = mockk(relaxed = true)
        dataStoreRepository = mockk(relaxed = true)
    }

    @Test
    fun `init block refreshes lists on startup`() = runTest {
        val fakeMenu = listOf(
            MenuRecipeCardState(
                menuRecipe = MenuRecipe(recipe = RecipeWithIngredients(Recipe(name = "rec1"), listOf()))
            ),
            MenuRecipeCardState(
                menuRecipe = MenuRecipe(recipe = RecipeWithIngredients(Recipe(name = "rec2"), listOf()))
            )
        )
        val fakeShoppingList = ShoppingWeekWithEntries(
            week = ShoppingWeek(1L),
            entries = listOf()
        )

        coEvery { weeklyMenuRepository.getLatestWeeklyMenu() } returns flowOf(fakeMenu)
        coEvery { shoppingListRepository.getLatestShoppingList() } returns flowOf(fakeShoppingList)

        viewModel = MainMenuViewModel(weeklyMenuRepository, shoppingListRepository, dataStoreRepository)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Weekly menu not refreshing on view model initializing", fakeMenu, viewModel.weeklyMenu.value)
        assertEquals("Shopping list not refreshing on view model initializing", fakeShoppingList, viewModel.shoppingList.value)
    }

    @Test
    fun `show tutorial dialogue on install`() = runTest {
        every { dataStoreRepository.shouldShowTutorial } returns flowOf(true)

        viewModel = MainMenuViewModel(weeklyMenuRepository, shoppingListRepository, dataStoreRepository)

        val collectedValues = mutableListOf<Boolean>()
        val job = backgroundScope.launch {
            viewModel.showTutorialDialog.toList(collectedValues)
        }

        assertEquals("Default value for showTutorialDialog is true when it should be false", false, viewModel.showTutorialDialog.value)
        advanceUntilIdle()
        assertEquals("showTutorialDialog should be true when app is just installed", true, viewModel.showTutorialDialog.value)

        job.cancel()
    }

    @Test
    fun `markTutorialAsSeen calls datastore repository`() = runTest {
        viewModel = MainMenuViewModel(weeklyMenuRepository, shoppingListRepository, dataStoreRepository)

        viewModel.markTutorialAsSeen()

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { dataStoreRepository.markTutorialShown() }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}