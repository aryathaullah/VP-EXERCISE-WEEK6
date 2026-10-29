package com.example.practice.campus

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.Espresso.pressBack
import com.example.practice.campus.navigation.AppNavHost
import com.example.practice.campus.ui.theme.CampusTheme
import com.example.practice.campus.viewmodel.CampusViewModel
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class CampusNavigationTest {
    @get:Rule
    val compose = createComposeRule()

    private fun start(vm: CampusViewModel) {
        compose.setContent { CampusTheme { AppNavHost(campusViewModel = vm) } }
    }

    @Test
    fun unjoinedCommunityIsBlockedAndBottomBarRemainsVisible() {
        start(CampusViewModel())
        compose.onNodeWithText("Communities").performClick()
        compose.onNodeWithText("GDSC").performClick()
        compose.onNodeWithText("Communities Directory").assertIsDisplayed()
        compose.onNodeWithText("Dashboard").assertIsDisplayed()
        compose.onNodeWithText("GDSC Projects").assertDoesNotExist()
        compose.onNodeWithText("Harap bergabung ke komunitas terlebih dahulu!").assertIsDisplayed()
    }

    @Test
    fun childDestinationHidesBottomBarAndBackReturnsToDirectory() {
        val vm = CampusViewModel()
        vm.toggleJoin("gdsc")
        start(vm)
        compose.onNodeWithText("Communities").performClick()
        compose.onNodeWithText("GDSC").performClick()
        compose.onNodeWithText("GDSC Projects").assertIsDisplayed()
        compose.onNodeWithText("Dashboard").assertDoesNotExist()
        compose.onNodeWithContentDescription("Kembali").performClick()
        compose.onNodeWithText("Communities Directory").assertIsDisplayed()
    }

    @Test
    fun dialogValidatesInputAndBackDoesNotDismissIt() {
        val vm = CampusViewModel()
        vm.toggleJoin("gdsc")
        start(vm)
        compose.onNodeWithText("Communities").performClick()
        compose.onNodeWithText("GDSC").performClick()
        compose.onNodeWithText("Add Project").performClick()
        compose.onNodeWithText("Save").performScrollTo().assertIsNotEnabled()
        pressBack()
        compose.onNodeWithText("Cancel").assertExists()
        compose.onAllNodes(hasSetTextAction())[0].performScrollTo().performTextInput("Navigation")
        compose.onAllNodes(hasSetTextAction())[1].performScrollTo().performTextInput("Campus map")
        compose.onNodeWithText("+ Kotlin").performScrollTo().performClick()
        compose.onNodeWithText("Save").performScrollTo().assertIsEnabled().performClick()
        compose.onNodeWithText("Navigation").assertIsDisplayed()
        compose.runOnIdle { assertEquals(1, vm.uiState.value.projects.size) }
    }

    @Test
    fun detailStatusUpdatesListAndDashboardAndDeleteUpdatesCounter() {
        val vm = CampusViewModel()
        vm.toggleJoin("gdsc")
        vm.openProjectDialog("gdsc")
        vm.onTitleChange("Navigation")
        vm.onDescriptionChange("Campus map")
        vm.toggleTech("Kotlin")
        vm.saveProject()
        start(vm)
        compose.onNodeWithText("Communities").performClick()
        compose.onNodeWithText("GDSC").performClick()
        compose.onNodeWithText("Navigation").performClick()
        compose.onNodeWithText("Project Detail").assertIsDisplayed()
        compose.onNode(isToggleable()).performClick()
        compose.onNodeWithText("● Completed").assertIsDisplayed()
        compose.onNodeWithContentDescription("Kembali").performClick()
        compose.onNodeWithText("● Completed").assertIsDisplayed()
        compose.onNodeWithContentDescription("Kembali").performClick()
        compose.onNodeWithText("Dashboard").performClick()
        compose.onNodeWithText("● Completed").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Communities").performClick()
        compose.onNodeWithText("GDSC").performClick()
        compose.onNodeWithContentDescription("Hapus Navigation").performClick()
        compose.onNodeWithText("Belum ada proyek yang dicatat").assertIsDisplayed()
        compose.runOnIdle { assertEquals(0, vm.uiState.value.projects.size) }
    }

    @Test
    fun directoryScrollPositionSurvivesTabChanges() {
        start(CampusViewModel())
        compose.onNodeWithText("Communities").performClick()
        compose.onNodeWithText("Cloud Computing Club").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Dashboard").performClick()
        compose.onNodeWithText("Communities").performClick()
        compose.onNodeWithText("Cloud Computing Club").assertIsDisplayed()
    }

    @Test
    fun cancelledDraftSurvivesNavigationBetweenTabs() {
        val vm = CampusViewModel()
        vm.toggleJoin("gdsc")
        start(vm)
        compose.onNodeWithText("Communities").performClick()
        compose.onNodeWithText("GDSC").performClick()
        compose.onNodeWithText("Add Project").performClick()
        compose.onAllNodes(hasSetTextAction())[0].performScrollTo().performTextInput("Draft Navigation")
        compose.onNodeWithText("Cancel").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Kembali").performClick()
        compose.onNodeWithText("Dashboard").performClick()
        compose.onNodeWithText("Communities").performClick()
        compose.onNodeWithText("GDSC").performClick()
        compose.onNodeWithText("Add Project").performClick()
        compose.onNodeWithText("Draft Navigation").performScrollTo().assertIsDisplayed()
    }

}
