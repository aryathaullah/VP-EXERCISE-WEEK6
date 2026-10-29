package com.example.practice.campus.viewmodel

import androidx.lifecycle.ViewModel
import com.example.practice.campus.data.CommunityRepository
import com.example.practice.campus.model.Community
import com.example.practice.campus.model.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CampusUiState(
    val communities: List<Community> = CommunityRepository.getCommunityList(),
    val projects: List<Project> = emptyList(),
    val showProjectDialog: Boolean = false,
    val formClubId: String = "",
    val projectTitle: String = "",
    val projectDescription: String = "",
    val selectedTechStack: List<String> = emptyList(),
    val techOptions: List<String> = listOf(
        "Kotlin", "Jetpack Compose", "Room", "Firebase", "Ktor", "Python", "Google Maps SDK"
    ),
    val searchQuery: String = "",
    val showSearch: Boolean = false,
    val showAccessMessage: Boolean = false
) {
    val canSave: Boolean get() = projectTitle.isNotBlank() &&
        projectDescription.isNotBlank() && selectedTechStack.isNotEmpty()

    fun getJoinedClubs(): List<Community> {
        val result = mutableListOf<Community>()
        for (community in communities) {
            if (community.joined) result.add(community)
        }
        return result
    }

    fun getVisibleClubs(): List<Community> {
        val result = mutableListOf<Community>()
        for (community in communities) {
            if (community.name.contains(searchQuery.trim(), ignoreCase = true) ||
                community.subtitle.contains(searchQuery.trim(), ignoreCase = true)) {
                result.add(community)
            }
        }
        return result
    }

    fun getClub(clubId: String): Community? {
        for (community in communities) {
            if (community.id == clubId) return community
        }
        return null
    }

    fun getClubProjects(clubId: String): List<Project> {
        val result = mutableListOf<Project>()
        for (project in projects) {
            if (project.clubId == clubId) result.add(project)
        }
        return result
    }

    fun getProject(projectId: String): Project? {
        for (project in projects) {
            if (project.id == projectId) return project
        }
        return null
    }
}

class CampusViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CampusUiState())
    val uiState: StateFlow<CampusUiState> = _uiState.asStateFlow()
    private var nextProjectId = 1

    fun toggleJoin(clubId: String) {
        val clubs = _uiState.value.communities.toMutableList()
        for (index in clubs.indices) {
            if (clubs[index].id == clubId) {
                clubs[index] = clubs[index].copy(joined = !clubs[index].joined)
                break
            }
        }
        _uiState.update { it.copy(communities = clubs) }
    }

    fun showAccessMessage() {
        _uiState.update { it.copy(showAccessMessage = true) }
    }

    fun dismissAccessMessage() {
        _uiState.update { it.copy(showAccessMessage = false) }
    }

    fun toggleSearch() {
        _uiState.update { it.copy(showSearch = !it.showSearch) }
    }

    fun onSearchChange(value: String) {
        _uiState.update { it.copy(searchQuery = value) }
    }

    fun openProjectDialog(clubId: String) {
        val state = _uiState.value
        if (state.getClub(clubId)?.joined != true) return
        if (state.formClubId == clubId) {
            _uiState.update { it.copy(showProjectDialog = true) }
        } else {
            _uiState.update {
                it.copy(showProjectDialog = true, formClubId = clubId,
                    projectTitle = "", projectDescription = "", selectedTechStack = emptyList())
            }
        }
    }

    fun cancelProjectDialog() {
        _uiState.update { it.copy(showProjectDialog = false) }
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(projectTitle = value) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(projectDescription = value) }
    }

    fun toggleTech(tech: String) {
        if (!_uiState.value.techOptions.contains(tech)) return
        val selected = _uiState.value.selectedTechStack.toMutableList()
        if (selected.contains(tech)) selected.remove(tech) else selected.add(tech)
        _uiState.update { it.copy(selectedTechStack = selected) }
    }

    fun saveProject() {
        val state = _uiState.value
        if (!state.showProjectDialog || !state.canSave) return
        if (state.getClub(state.formClubId)?.joined != true) return
        val project = Project(
            id = "proj-${state.formClubId}-$nextProjectId",
            clubId = state.formClubId,
            title = state.projectTitle.trim(),
            description = state.projectDescription.trim(),
            techStack = state.selectedTechStack.toList()
        )
        nextProjectId++
        _uiState.update {
            it.copy(projects = it.projects + project, showProjectDialog = false,
                projectTitle = "", projectDescription = "", selectedTechStack = emptyList())
        }
    }

    fun deleteProject(projectId: String) {
        val projects = _uiState.value.projects.toMutableList()
        for (index in projects.indices) {
            if (projects[index].id == projectId) {
                projects.removeAt(index)
                break
            }
        }
        _uiState.update { it.copy(projects = projects) }
    }

    fun setProjectCompleted(projectId: String, completed: Boolean) {
        val projects = _uiState.value.projects.toMutableList()
        for (index in projects.indices) {
            if (projects[index].id == projectId) {
                projects[index] = projects[index].copy(completed = completed)
                break
            }
        }
        _uiState.update { it.copy(projects = projects) }
    }
}
