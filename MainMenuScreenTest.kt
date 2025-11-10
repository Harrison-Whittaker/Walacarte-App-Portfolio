package com.example.sous

import androidx.compose.ui.test.junit4.createComposeRule
import com.example.sous.ui.MainMenuViewModel
import org.junit.Rule
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.sous.data.entities.Recipe
import com.example.sous.data.relations.RecipeWithIngredients
import com.example.sous.ui.MenuRecipe
import com.example.sous.ui.MenuRecipeCard
import com.example.sous.ui.MenuRecipeCardState
import com.example.sous.ui.screens.MainMenuScreen
import org.junit.Test
import org.junit.Assert.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

class MainMenuScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val viewModel: MainMenuViewModel = mockk(relaxed = true)

    @Test
    fun cookbookButton_onClick_triggersCallback() {
        var cookbookClicked = false

        every { viewModel.shoppingList } returns MutableStateFlow(null)
        every { viewModel.weeklyMenu } returns MutableStateFlow(null)
        every { viewModel.showTutorialDialog } returns MutableStateFlow(false)

        composeTestRule.setContent {
            MainMenuScreen(
                navigateToCookbook = { cookbookClicked = true },
                navigateToShoppingList = {},
                navigateToQuickList = {},
                navigateToMealPlan = {},
                setDynamicTitle = {},
                setTopBarActions = {},
                mainMenuViewModel = viewModel
            )
        }

        composeTestRule.onNodeWithText("Cookbook").performClick()

        assertTrue("Navigation callback for 'Cookbook' was not triggered", cookbookClicked)
    }

    @Test
    fun shoppingListButton_onClick_triggersCallback() {
        var shoppingListClicked = false

        every { viewModel.shoppingList } returns MutableStateFlow(null)
        every { viewModel.weeklyMenu } returns MutableStateFlow(null)
        every { viewModel.showTutorialDialog } returns MutableStateFlow(false)

        composeTestRule.setContent {
            MainMenuScreen(
                navigateToCookbook = {},
                navigateToShoppingList = { shoppingListClicked = true },
                navigateToQuickList = {},
                navigateToMealPlan = {},
                setDynamicTitle = {},
                setTopBarActions = {},
                mainMenuViewModel = viewModel
            )
        }

        composeTestRule.onNodeWithText("Shopping List").performClick()

        assertTrue("Navigation callback for 'Shopping List' was not triggered", shoppingListClicked)
    }

    @Test
    fun quickListButton_onClick_triggersCallback() {
        var quickListClicked = false

        every { viewModel.shoppingList } returns MutableStateFlow(null)
        every { viewModel.weeklyMenu } returns MutableStateFlow(null)
        every { viewModel.showTutorialDialog } returns MutableStateFlow(false)

        composeTestRule.setContent {
            MainMenuScreen(
                navigateToCookbook = {},
                navigateToShoppingList = {},
                navigateToQuickList = { quickListClicked = true },
                navigateToMealPlan = {},
                setDynamicTitle = {},
                setTopBarActions = {},
                mainMenuViewModel = viewModel
            )
        }

        composeTestRule.onNodeWithText("Quick List").performClick()

        assertTrue("Navigation callback for 'Quick List' was not triggered", quickListClicked)
    }

    @Test
    fun mealPlanButton_onClick_triggersCallback() {
        var mealPlanClicked = false
        val mealPlan = listOf(
            MenuRecipeCardState(
                menuRecipe = MenuRecipe(
                    recipe = RecipeWithIngredients(
                        recipe = Recipe(name = "test"),
                        ingredientRefs = listOf()
                    )
                )
            )
        )

        every { viewModel.shoppingList } returns MutableStateFlow(null)
        every { viewModel.weeklyMenu } returns MutableStateFlow(mealPlan)
        every { viewModel.showTutorialDialog } returns MutableStateFlow(false)

        composeTestRule.setContent {
            MainMenuScreen(
                navigateToCookbook = {},
                navigateToShoppingList = {},
                navigateToQuickList = {},
                navigateToMealPlan = { mealPlanClicked = true },
                setDynamicTitle = {},
                setTopBarActions = {},
                mainMenuViewModel = viewModel
            )
        }

        composeTestRule.onNodeWithText("Meal Plan").performClick()

        assertTrue("Navigation callback for 'Meal Plan' was not triggered", mealPlanClicked)
    }

    @Test
    fun mealPlanButton_isDisabled_whenWeeklyMenuIsEmpty() {
        every { viewModel.weeklyMenu } returns MutableStateFlow(listOf())
        every { viewModel.shoppingList } returns MutableStateFlow(null)
        every { viewModel.showTutorialDialog } returns MutableStateFlow(false)

        composeTestRule.setContent {
            MainMenuScreen(
                navigateToCookbook = {},
                navigateToShoppingList = {},
                navigateToQuickList = {},
                navigateToMealPlan = {},
                setDynamicTitle = {},
                setTopBarActions = {},
                mainMenuViewModel = viewModel
            )
        }

        composeTestRule.onNodeWithText("Meal Plan").assertIsNotEnabled()
    }

    @Test
    fun mealPlanButton_isEnabled_whenWeeklyMenuIsNotEmpty() {
        val mealPlan = listOf(
            MenuRecipeCardState(
                menuRecipe = MenuRecipe(
                    recipe = RecipeWithIngredients(
                        recipe = Recipe(name = "test"),
                        ingredientRefs = listOf()
                    )
                )
            )
        )
        every { viewModel.weeklyMenu } returns MutableStateFlow(mealPlan)
        every { viewModel.shoppingList } returns MutableStateFlow(null)
        every { viewModel.showTutorialDialog } returns MutableStateFlow(false)

        composeTestRule.setContent {
            MainMenuScreen(
                navigateToCookbook = {},
                navigateToShoppingList = {},
                navigateToQuickList = {},
                navigateToMealPlan = {},
                setDynamicTitle = {},
                setTopBarActions = {},
                mainMenuViewModel = viewModel
            )
        }

        composeTestRule.onNodeWithText("Meal Plan").assertIsEnabled()
    }
}