package com.example.sous.ui.screens

import android.graphics.drawable.shapes.Shape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.outlined.PlaylistAdd
import androidx.compose.material.icons.automirrored.outlined.PlaylistAddCheck
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Dining
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sous.R
import com.example.sous.SousScreen
import com.example.sous.data.SousContainer
import com.example.sous.ui.MainMenuViewModel
import com.example.sous.ui.MenuButtonData
import com.example.sous.ui.MenuRecipe
import com.example.sous.ui.MenuRecipeCardState
import com.example.sous.ui.SousViewModelProvider
import com.example.sous.ui.navigation.NavigationDestination
import kotlinx.serialization.json.JsonNull.content

object MainMenuScreenDestination : NavigationDestination {
    override val route: String = SousScreen.MainMenu.name
    override val titleRes: Int = R.string.main_menu
}

@Composable
fun MainMenuScreen(
    navigateToCookbook: () -> Unit,
    navigateToShoppingList: () -> Unit,
    navigateToQuickList: () -> Unit,
    navigateToMealPlan: (List<MenuRecipeCardState>) -> Unit,
    modifier: Modifier = Modifier,
    setDynamicTitle: (String?) -> Unit,
    setTopBarActions: (@Composable RowScope.() -> Unit) -> Unit = {},
    mainMenuViewModel: MainMenuViewModel,
) {
    val shoppingList by mainMenuViewModel.shoppingList.collectAsState()
    val weeklyMenu by mainMenuViewModel.weeklyMenu.collectAsState()
    val appName = stringResource(R.string.app_name)
    var showHelpDialog by remember { mutableStateOf(false) }
    val firstRunTutorial by mainMenuViewModel.showTutorialDialog.collectAsState()

    LaunchedEffect(Unit) {
        setDynamicTitle(appName)
        mainMenuViewModel.refreshLatestLists()
    }

    setTopBarActions {
        IconButton(
            onClick = { showHelpDialog = true },
            modifier = Modifier
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Help"
            )
        }
    }

    val density = LocalDensity.current
    val textStyle = MaterialTheme.typography.headlineMedium
    val iconSizeDp = with(density) { (textStyle.fontSize * 1.5).toDp() }

    val menuButtons = listOf(
        MenuButtonData(
            label = "Cookbook",
            onClick = navigateToCookbook,
            enabled = true,
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                    contentDescription = "Cookbook",
                    modifier = Modifier.size(iconSizeDp)
                )
            }
        ),
        MenuButtonData(
            label = "Shopping List",
            onClick = navigateToShoppingList,
            enabled = true,
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Assignment,
                    contentDescription = "Shopping List",
                    modifier = Modifier.size(iconSizeDp)
                )
            }
        ),
        MenuButtonData(
            label = "Quick List",
            onClick = navigateToQuickList,
            enabled = true,
            icon = {
                Icon(
                    imageVector = Icons.Filled.EditNote,
                    contentDescription = "Quick List",
                    modifier = Modifier.size(iconSizeDp)
                )
            }
        ),
        MenuButtonData(
            label = "Meal Plan",
            onClick = {
                weeklyMenu?.let { navigateToMealPlan(it) }
            },
            enabled = weeklyMenu?.isNotEmpty() == true,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Dining,
                    contentDescription = "Meal Plan",
                    modifier = Modifier.size(iconSizeDp)
                )
            }
        )
    )


    MainMenuScreenContent(
        menuButtons = menuButtons,
        showHelpDialog = (showHelpDialog || firstRunTutorial),
        onDismissHelpDialog = {
            showHelpDialog = false
            mainMenuViewModel.markTutorialAsSeen()
        },
        modifier = modifier
    )
}

@Composable
fun MainMenuScreenContent(
    menuButtons: List<MenuButtonData>,
    showHelpDialog: Boolean,
    onDismissHelpDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (showHelpDialog) {
            AlertDialog(
                onDismissRequest = onDismissHelpDialog,
                title = { Text("Welcome to Walacarte!") },
                text = {
                    TutorialTextContent()
                },
                confirmButton = {
                    TextButton(onClick = onDismissHelpDialog) {
                        Text(
                            text = "Thanks!",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface
            )
        }
        menuButtons.forEach { button ->
            Button(
                onClick = button.onClick,
                shape = MaterialTheme.shapes.large,
                enabled = button.enabled,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp)
            ) {
                button.icon?.invoke()
                Spacer(modifier = Modifier.padding(8.dp))
                Text(
                    text = button.label,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}

@Composable
fun TutorialTextContent() {
    val bulletTextIndent = TextIndent(
        firstLine = (-0.5).em,
    )

    val bulletParagraphStyle = ParagraphStyle(
        textIndent = bulletTextIndent,
        lineHeight = 20.sp // Optional: Improves readability of multi-line items
    )

    Text(
        text = buildAnnotatedString {
            append("This app helps you manage recipes, create meal plans, and automatically build grocery lists!\n\n")
            append("To get started:\n")

            withStyle(bulletParagraphStyle) {
                append("• ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Cookbook")
                }
                append(" to add and manage your own recipes.\n")

                append("• ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Quick List")
                }
                append(" for items you wouldn't normally find in a recipe.\n")

                append("• To create a shopping list, click the ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Shopping List")
                }
                append(" button, tap the '+' in the top right corner, and use your stored recipes and/or your quick list items to create your own shopping list.\n")
            }
            append("If you ever get lost or confused, most screens have an info button in their top right corner.\n\n")
            append("Happy Shopping!")
        }
    )
}

@Preview(showBackground = true)
@Composable
fun MainMenuPreviewScreen() {
    val menuButtons = listOf(
        MenuButtonData("Cookbook", {}, enabled = true),
        MenuButtonData("Create New Shopping List", {}, enabled = true),
        MenuButtonData("Shopping List", {}, enabled = false),
        MenuButtonData("Quick List", {}, enabled = true),
        MenuButtonData("Meal Plan", {}, enabled = false)
    )

    MainMenuScreenContent(
        menuButtons = menuButtons,
        showHelpDialog = false,
        onDismissHelpDialog = {},
        modifier = Modifier
    )
}