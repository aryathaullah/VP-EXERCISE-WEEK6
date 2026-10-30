package com.example.practice.campus.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.practice.campus.ui.screen.ClubProjectsScreen
import com.example.practice.campus.ui.screen.CommunitiesScreen
import com.example.practice.campus.ui.screen.DashboardScreen
import com.example.practice.campus.ui.screen.ProjectDetailScreen
import com.example.practice.campus.ui.theme.CampusBackground
import com.example.practice.campus.ui.theme.CampusBorder
import com.example.practice.campus.ui.theme.CampusMuted
import com.example.practice.campus.ui.theme.CampusNavy
import com.example.practice.campus.ui.widgets.AddProjectDialog
import com.example.practice.campus.viewmodel.CampusViewModel

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    campusViewModel: CampusViewModel = viewModel()
) {
    val uiState by campusViewModel.uiState.collectAsStateWithLifecycle()
    val entry by navController.currentBackStackEntryAsState()
    val destination = entry?.destination

    val isDashboard = destination?.hasRoute<DashboardRoute>() ?: true
    val isCommunities = destination?.hasRoute<CommunitiesRoute>() == true
    val isClubProjects = destination?.hasRoute<ClubProjectsRoute>() == true
    val isProjectDetail = destination?.hasRoute<ProjectDetailRoute>() == true

    val isTopLevel = isDashboard || isCommunities

    val clubId = if (isClubProjects) {
        try {
            entry?.toRoute<ClubProjectsRoute>()?.clubId ?: ""
        } catch (_: Exception) {
            ""
        }
    } else ""

    val community = uiState.getClub(clubId)
    val snackbarState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.showAccessMessage) {
        if (uiState.showAccessMessage) {
            snackbarState.showSnackbar(
                message = "Harap bergabung ke komunitas terlebih dahulu!",
                actionLabel = "OK",
                duration = SnackbarDuration.Short
            )
            campusViewModel.dismissAccessMessage()
        }
    }

    val dashboardScroll = rememberScrollState()
    val communitiesScroll = rememberScrollState()
    val dialogScroll = rememberScrollState()

    val openClub: (String) -> Unit = { id ->
        if (uiState.getClub(id)?.joined == true) {
            if (isDashboard) navigateToTab(navController, CommunitiesRoute)
            navController.navigate(ClubProjectsRoute(id)) { launchSingleTop = true }
        } else {
            campusViewModel.showAccessMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CampusBackground,
        snackbarHost = {
            SnackbarHost(snackbarState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = CampusNavy,
                    contentColor = Color.White,
                    actionColor = Color(0xFF93C5FD),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        },
        topBar = {
            Surface(
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.statusBarsPadding()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!isTopLevel) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                            }
                        } else {
                            Spacer(modifier = Modifier.width(12.dp))
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = when {
                                    isDashboard -> "CampusTech Hub"
                                    isCommunities -> "Communities Directory"
                                    isClubProjects -> "${community?.name ?: "Club"} Projects"
                                    else -> "Project Detail"
                                },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (!isProjectDetail) {
                                Text(
                                    text = when {
                                        isDashboard -> "Student Profile & Portfolio Dashboard"
                                        isCommunities -> "Pilih komunitas untuk membuka Club Projects"
                                        else -> community?.subtitle ?: ""
                                    },
                                    fontSize = 10.sp,
                                    color = CampusMuted
                                )
                            }
                        }
                        if (isDashboard) {
                            Box(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .size(28.dp)
                                    .background(Color(0xFF5031DD), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("AP", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        if (isCommunities) {
                            IconButton(onClick = { campusViewModel.toggleSearch() }) {
                                Icon(Icons.Default.Search, contentDescription = "Cari komunitas")
                            }
                        }
                    }
                    HorizontalDivider(color = CampusBorder)
                }
            }
        },
        bottomBar = {
            if (isTopLevel) {
                NavigationBar(containerColor = Color.White) {
                    NavigationBarItem(
                        selected = isDashboard,
                        onClick = { navigateToTab(navController, DashboardRoute) },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Dashboard", fontSize = 11.sp) }
                    )
                    NavigationBarItem(
                        selected = isCommunities,
                        onClick = { navigateToTab(navController, CommunitiesRoute) },
                        icon = { Icon(Icons.Default.Groups, contentDescription = null) },
                        label = { Text("Communities", fontSize = 11.sp) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DashboardRoute,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            composable<DashboardRoute> {
                DashboardScreen(
                    uiState = uiState,
                    scrollState = dashboardScroll,
                    onClubClick = openClub,
                    onProjectClick = { id ->
                        val project = uiState.getProject(id)
                        if (project != null) {
                            openClub(project.clubId)
                            if (uiState.getClub(project.clubId)?.joined == true) {
                                navController.navigate(ProjectDetailRoute(id))
                            }
                        }
                    }
                )
            }
            composable<CommunitiesRoute> {
                CommunitiesScreen(
                    uiState = uiState,
                    scrollState = communitiesScroll,
                    onClubClick = openClub,
                    onJoinClick = { campusViewModel.toggleJoin(it) },
                    onSearchChange = { campusViewModel.onSearchChange(it) }
                )
            }
            composable<ClubProjectsRoute> { backStackEntry ->
                val routeArgs: ClubProjectsRoute = backStackEntry.toRoute()
                val selectedId = routeArgs.clubId
                val scrollState = rememberScrollState()
                ClubProjectsScreen(
                    community = uiState.getClub(selectedId),
                    projects = uiState.getClubProjects(selectedId),
                    scrollState = scrollState,
                    onAddClick = { campusViewModel.openProjectDialog(selectedId) },
                    onProjectClick = { id -> navController.navigate(ProjectDetailRoute(id)) },
                    onDelete = { campusViewModel.deleteProject(it) }
                )
            }
            composable<ProjectDetailRoute> { backStackEntry ->
                val routeArgs: ProjectDetailRoute = backStackEntry.toRoute()
                val projectId = routeArgs.projectId
                val project = uiState.getProject(projectId)
                val projectClub = uiState.getClub(project?.clubId ?: "")
                val scrollState = rememberScrollState()
                ProjectDetailScreen(
                    project = project,
                    clubName = projectClub?.name ?: "",
                    scrollState = scrollState,
                    onStatusChange = { campusViewModel.setProjectCompleted(projectId, it) }
                )
            }
        }
    }
    if (uiState.showProjectDialog && isClubProjects) {
        AddProjectDialog(
            uiState = uiState,
            scrollState = dialogScroll,
            onTitleChange = { campusViewModel.onTitleChange(it) },
            onDescriptionChange = { campusViewModel.onDescriptionChange(it) },
            onTechClick = { campusViewModel.toggleTech(it) },
            onSave = { campusViewModel.saveProject() },
            onCancel = { campusViewModel.cancelProjectDialog() }
        )
    }
}

fun navigateToTab(navController: NavHostController, route: Any) {
    navController.navigate(route) {
        popUpTo<DashboardRoute> { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
