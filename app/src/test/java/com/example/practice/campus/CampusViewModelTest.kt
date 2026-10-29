package com.example.practice.campus

import com.example.practice.campus.viewmodel.CampusViewModel
import org.junit.Assert.*
import org.junit.Test

class CampusViewModelTest {
    private fun addProject(vm: CampusViewModel, clubId: String, title: String) {
        vm.openProjectDialog(clubId)
        vm.onTitleChange(title)
        vm.onDescriptionChange("Deskripsi proyek mahasiswa")
        vm.toggleTech("Kotlin")
        vm.saveProject()
    }

    @Test
    fun initialStateHasSixClubsAndZeroCounters() {
        val state = CampusViewModel().uiState.value
        assertEquals(6, state.communities.size)
        assertEquals(0, state.getJoinedClubs().size)
        assertEquals(0, state.projects.size)
        assertTrue(state.techOptions.size >= 5)
        assertFalse(state.canSave)
    }

    @Test
    fun joinAndLeaveUpdateCounterWithoutDuplicates() {
        val vm = CampusViewModel()
        vm.toggleJoin("gdsc")
        vm.toggleJoin("cyber")
        assertEquals(2, vm.uiState.value.getJoinedClubs().size)
        vm.toggleJoin("gdsc")
        assertEquals(1, vm.uiState.value.getJoinedClubs().size)
        vm.toggleJoin("gdsc")
        assertEquals(2, vm.uiState.value.getJoinedClubs().size)
    }

    @Test
    fun unjoinedClubCannotOpenFormOrSaveProject() {
        val vm = CampusViewModel()
        vm.openProjectDialog("gdsc")
        assertFalse(vm.uiState.value.showProjectDialog)
        vm.onTitleChange("Project")
        vm.onDescriptionChange("Description")
        vm.toggleTech("Kotlin")
        vm.saveProject()
        assertTrue(vm.uiState.value.projects.isEmpty())
    }

    @Test
    fun saveRequiresNonBlankTitleDescriptionAndAtLeastOneTech() {
        val vm = CampusViewModel()
        vm.toggleJoin("gdsc")
        vm.openProjectDialog("gdsc")
        vm.onTitleChange("   ")
        vm.onDescriptionChange("Description")
        vm.toggleTech("Kotlin")
        assertFalse(vm.uiState.value.canSave)
        vm.saveProject()
        assertTrue(vm.uiState.value.projects.isEmpty())
        vm.onTitleChange("Project")
        vm.onDescriptionChange(" \n ")
        assertFalse(vm.uiState.value.canSave)
        vm.onDescriptionChange("Description")
        vm.toggleTech("Kotlin")
        assertFalse(vm.uiState.value.canSave)
        vm.toggleTech("Kotlin")
        assertTrue(vm.uiState.value.canSave)
        vm.saveProject()
        assertEquals(1, vm.uiState.value.projects.size)
    }

    @Test
    fun multiSelectionCanBeToggledAndInvalidTechIsIgnored() {
        val vm = CampusViewModel()
        vm.toggleTech("Kotlin")
        vm.toggleTech("Room")
        vm.toggleTech("Firebase")
        vm.toggleTech("Not an option")
        assertEquals(3, vm.uiState.value.selectedTechStack.size)
        vm.toggleTech("Room")
        assertEquals(listOf("Kotlin", "Firebase"), vm.uiState.value.selectedTechStack)
    }

    @Test
    fun saveTrimsInputAndPreservesAllSelectedTechnologies() {
        val vm = CampusViewModel()
        vm.toggleJoin("gdsc")
        vm.openProjectDialog("gdsc")
        vm.onTitleChange("  Smart Campus Navigation  ")
        vm.onDescriptionChange("  Sistem navigasi kampus.  ")
        vm.toggleTech("Kotlin")
        vm.toggleTech("Room")
        vm.saveProject()
        val project = vm.uiState.value.projects[0]
        assertEquals("Smart Campus Navigation", project.title)
        assertEquals("Sistem navigasi kampus.", project.description)
        assertEquals(listOf("Kotlin", "Room"), project.techStack)
        assertFalse(project.completed)
        assertFalse(vm.uiState.value.showProjectDialog)
        assertEquals("", vm.uiState.value.projectTitle)
        vm.saveProject()
        assertEquals(1, vm.uiState.value.projects.size)
    }

    @Test
    fun projectsAreSeparatedByClubButDashboardCountsAll() {
        val vm = CampusViewModel()
        vm.toggleJoin("gdsc")
        vm.toggleJoin("cyber")
        addProject(vm, "gdsc", "Navigation")
        addProject(vm, "gdsc", "Attendance")
        addProject(vm, "cyber", "Security Audit")
        assertEquals(3, vm.uiState.value.projects.size)
        assertEquals(2, vm.uiState.value.getClubProjects("gdsc").size)
        assertEquals(1, vm.uiState.value.getClubProjects("cyber").size)
        assertTrue(vm.uiState.value.getClubProjects("ai").isEmpty())
    }

    @Test
    fun statusChangesSharedProjectWithoutChangingOtherFieldsOrProjects() {
        val vm = CampusViewModel()
        vm.toggleJoin("gdsc")
        addProject(vm, "gdsc", "Navigation")
        addProject(vm, "gdsc", "Attendance")
        val before = vm.uiState.value.projects[0]
        vm.setProjectCompleted(before.id, true)
        assertEquals(before.copy(completed = true), vm.uiState.value.getProject(before.id))
        assertTrue(vm.uiState.value.getClubProjects("gdsc")[0].completed)
        assertFalse(vm.uiState.value.projects[1].completed)
        vm.setProjectCompleted(before.id, false)
        assertEquals(before, vm.uiState.value.getProject(before.id))
    }

    @Test
    fun deleteUpdatesCountsAndIdsAreNotReused() {
        val vm = CampusViewModel()
        vm.toggleJoin("gdsc")
        addProject(vm, "gdsc", "First")
        val firstId = vm.uiState.value.projects[0].id
        vm.deleteProject(firstId)
        assertTrue(vm.uiState.value.projects.isEmpty())
        assertTrue(vm.uiState.value.getClubProjects("gdsc").isEmpty())
        assertNull(vm.uiState.value.getProject(firstId))
        addProject(vm, "gdsc", "Second")
        assertNotEquals(firstId, vm.uiState.value.projects[0].id)
    }

    @Test
    fun cancelAndReopenKeepDraftForSameClub() {
        val vm = CampusViewModel()
        vm.toggleJoin("gdsc")
        vm.openProjectDialog("gdsc")
        vm.onTitleChange("Draft")
        vm.onDescriptionChange("Belum selesai")
        vm.toggleTech("Kotlin")
        vm.cancelProjectDialog()
        assertFalse(vm.uiState.value.showProjectDialog)
        assertTrue(vm.uiState.value.projects.isEmpty())
        vm.openProjectDialog("gdsc")
        assertEquals("Draft", vm.uiState.value.projectTitle)
        assertEquals("Belum selesai", vm.uiState.value.projectDescription)
        assertEquals(listOf("Kotlin"), vm.uiState.value.selectedTechStack)
    }

    @Test
    fun differentClubStartsWithCleanDraft() {
        val vm = CampusViewModel()
        vm.toggleJoin("gdsc")
        vm.toggleJoin("cyber")
        vm.openProjectDialog("gdsc")
        vm.onTitleChange("Draft GDSC")
        vm.cancelProjectDialog()
        vm.openProjectDialog("cyber")
        assertEquals("cyber", vm.uiState.value.formClubId)
        assertEquals("", vm.uiState.value.projectTitle)
    }

    @Test
    fun leavingClubKeepsExistingProjectsAndRejoiningRestoresAccess() {
        val vm = CampusViewModel()
        vm.toggleJoin("gdsc")
        addProject(vm, "gdsc", "Navigation")
        vm.toggleJoin("gdsc")
        assertEquals(0, vm.uiState.value.getJoinedClubs().size)
        assertEquals(1, vm.uiState.value.projects.size)
        vm.openProjectDialog("gdsc")
        assertFalse(vm.uiState.value.showProjectDialog)
        vm.toggleJoin("gdsc")
        assertEquals(1, vm.uiState.value.getClubProjects("gdsc").size)
    }

    @Test
    fun searchIgnoresCaseAndPreservesMembershipState() {
        val vm = CampusViewModel()
        vm.toggleJoin("gdsc")
        vm.onSearchChange("  gdSC ")
        assertEquals(1, vm.uiState.value.getVisibleClubs().size)
        assertTrue(vm.uiState.value.getVisibleClubs()[0].joined)
        vm.onSearchChange("not found")
        assertTrue(vm.uiState.value.getVisibleClubs().isEmpty())
        assertEquals(1, vm.uiState.value.getJoinedClubs().size)
    }

    @Test
    fun unknownIdsDoNotModifyExistingData() {
        val vm = CampusViewModel()
        vm.toggleJoin("gdsc")
        addProject(vm, "gdsc", "Navigation")
        val before = vm.uiState.value
        vm.toggleJoin("unknown")
        vm.deleteProject("unknown")
        vm.setProjectCompleted("unknown", true)
        assertEquals(before, vm.uiState.value)
    }
}
