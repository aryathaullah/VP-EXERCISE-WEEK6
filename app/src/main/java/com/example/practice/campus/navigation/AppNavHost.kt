package com.example.practice.campus.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.practice.campus.ui.screen.*
import com.example.practice.campus.ui.theme.*
import com.example.practice.campus.ui.widgets.AddProjectDialog
import com.example.practice.campus.viewmodel.CampusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(modifier: Modifier = Modifier,
               navController: NavHostController = rememberNavController(),
               campusViewModel: CampusViewModel = viewModel()) {
    val uiState by campusViewModel.uiState.collectAsStateWithLifecycle()
    val entry by navController.currentBackStackEntryAsState()
    val route = entry?.destination?.route ?: Routes.DASHBOARD
    val isTopLevel = route == Routes.DASHBOARD || route == Routes.COMMUNITIES
    val clubId = entry?.arguments?.getString("clubId") ?: ""
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
            if (route == Routes.DASHBOARD) navigateToTab(navController, Routes.COMMUNITIES)
            navController.navigate("club_projects/$id") { launchSingleTop = true }
        } else {
            campusViewModel.showAccessMessage()
        }
    }

    Scaffold(modifier = modifier.fillMaxSize(), containerColor = CampusBackground,
        snackbarHost = {
            SnackbarHost(snackbarState) { data ->
                Snackbar(snackbarData = data, containerColor = CampusNavy,
                    contentColor = Color.White, actionColor = Color(0xFF93C5FD),
                    shape = RoundedCornerShape(16.dp))
            }
        },
        topBar = {
            Column {
                TopAppBar(title = {
                    Column {
                        Text(when (route) {
                            Routes.DASHBOARD -> "CampusTech Hub"
                            Routes.COMMUNITIES -> "Communities Directory"
                            Routes.CLUB_PROJECTS -> "${community?.name ?: "Club"} Projects"
                            else -> "Project Detail"
                        }, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        if (route != Routes.PROJECT_DETAIL) {
                            Text(when (route) {
                                Routes.DASHBOARD -> "Student Profile & Portfolio Dashboard"
                                Routes.COMMUNITIES -> "Pilih komunitas untuk membuka Club Projects"
                                else -> community?.subtitle ?: ""
                            }, fontSize = 10.sp, color = CampusMuted)
                        }
                    }
                }, navigationIcon = {
                    if (!isTopLevel) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                        }
                    }
                }, actions = {
                    if (route == Routes.DASHBOARD) {
                        Box(Modifier.padding(end = 16.dp).size(28.dp)
                            .background(Color(0xFF5031DD), CircleShape), contentAlignment = Alignment.Center) {
                            Text("AP", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (route == Routes.COMMUNITIES) {
                        IconButton(onClick = { campusViewModel.toggleSearch() }) {
                            Icon(Icons.Default.Search, contentDescription = "Cari komunitas")
                        }
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White))
                HorizontalDivider(color = CampusBorder)
            }
        },
        bottomBar = {
            if (isTopLevel) {
                NavigationBar(containerColor = Color.White) {
                    NavigationBarItem(selected = route == Routes.DASHBOARD,
                        onClick = { navigateToTab(navController, Routes.DASHBOARD) },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Dashboard", fontSize = 11.sp) })
                    NavigationBarItem(selected = route == Routes.COMMUNITIES,
                        onClick = { navigateToTab(navController, Routes.COMMUNITIES) },
                        icon = { Icon(Icons.Default.Groups, contentDescription = null) },
                        label = { Text("Communities", fontSize = 11.sp) })
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = Routes.DASHBOARD,
            modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding)) {
            composable(Routes.DASHBOARD) {
                DashboardScreen(uiState, dashboardScroll, onClubClick = openClub,
                    onProjectClick = { id ->
                        val project = uiState.getProject(id)
                        if (project != null) {
                            openClub(project.clubId)
                            if (uiState.getClub(project.clubId)?.joined == true) {
                                navController.navigate("project_detail/$id")
                            }
                        }
                    })
            }
            composable(Routes.COMMUNITIES) {
                CommunitiesScreen(uiState, communitiesScroll, onClubClick = openClub,
                    onJoinClick = { campusViewModel.toggleJoin(it) },
                    onSearchChange = { campusViewModel.onSearchChange(it) })
            }
            composable(Routes.CLUB_PROJECTS,
                arguments = listOf(navArgument("clubId") { type = NavType.StringType })) { backStackEntry ->
                val selectedId = backStackEntry.arguments?.getString("clubId") ?: ""
                val scrollState = rememberScrollState()
                ClubProjectsScreen(uiState.getClub(selectedId), uiState.getClubProjects(selectedId), scrollState,
                    onAddClick = { campusViewModel.openProjectDialog(selectedId) },
                    onProjectClick = { id -> navController.navigate("project_detail/$id") },
                    onDelete = { campusViewModel.deleteProject(it) })
            }
            composable(Routes.PROJECT_DETAIL,
                arguments = listOf(navArgument("projectId") { type = NavType.StringType })) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
                val project = uiState.getProject(projectId)
                val projectClub = uiState.getClub(project?.clubId ?: "")
                val scrollState = rememberScrollState()
                ProjectDetailScreen(project, projectClub?.name ?: "", scrollState,
                    onStatusChange = { campusViewModel.setProjectCompleted(projectId, it) })
            }
        }
    }
    if (uiState.showProjectDialog && route == Routes.CLUB_PROJECTS) {
        AddProjectDialog(uiState, dialogScroll,
            onTitleChange = { campusViewModel.onTitleChange(it) },
            onDescriptionChange = { campusViewModel.onDescriptionChange(it) },
            onTechClick = { campusViewModel.toggleTech(it) },
            onSave = { campusViewModel.saveProject() },
            onCancel = { campusViewModel.cancelProjectDialog() })
    }
}

fun navigateToTab(navController: NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(Routes.DASHBOARD) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
