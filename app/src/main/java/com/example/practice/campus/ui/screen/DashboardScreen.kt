package com.example.practice.campus.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.practice.campus.ui.theme.*
import com.example.practice.campus.ui.widgets.*
import com.example.practice.campus.viewmodel.CampusUiState

@Composable
fun DashboardScreen(uiState: CampusUiState, scrollState: ScrollState,
                    onClubClick: (String) -> Unit, onProjectClick: (String) -> Unit) {
    val joinedClubs = uiState.getJoinedClubs()
    Column(Modifier.fillMaxSize().verticalScroll(scrollState).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(Modifier.fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(Color(0xFF233A8B), Color(0xFF3479F6))),
                RoundedCornerShape(24.dp)).padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(Modifier.size(60.dp).background(Color.White, CircleShape), contentAlignment = Alignment.Center) {
                Text("AR", color = Color(0xFF24449A), fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("MAHASISWA AKTIF", color = Color.White, fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.background(Color.White.copy(alpha = 0.20f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp))
                Text("ARYA ATHAULLAH RAFI", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                Text("IMT 2025", color = Color(0xFFDEE9FF), fontSize = 10.sp)
                Text("NIM: 1076012510026", color = Color(0xFFDEE9FF), fontSize = 10.sp)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CounterCard(joinedClubs.size, "Joined Clubs", "Komunitas yang Diikuti", false, Modifier.weight(1f))
            CounterCard(uiState.projects.size, "Total Projects", "Seluruh Proyek Dibuat", true, Modifier.weight(1f))
        }
        SectionTitle("Semua Komunitas yang Diikuti (${joinedClubs.size})", "Status: Joined")
        if (joinedClubs.isEmpty()) {
            EmptyMessage("Belum bergabung dengan komunitas")
        } else {
            for (club in joinedClubs) {
                JoinedClubCard(club, uiState.getClubProjects(club.id).size, onClick = { onClubClick(club.id) })
            }
        }
        SectionTitle("Semua Proyek Mahasiswa (${uiState.projects.size})", "Total: ${uiState.projects.size} Proyek", true)
        if (uiState.projects.isEmpty()) {
            EmptyMessage("Belum ada proyek yang dicatat")
        } else {
            for (project in uiState.projects) {
                ProjectCard(project, uiState.getClub(project.clubId)?.name ?: "", true, false,
                    onClick = { onProjectClick(project.id) }, onDelete = {})
            }
        }
    }
}

@Composable
fun CounterCard(count: Int, label: String, subtitle: String, projects: Boolean, modifier: Modifier) {
    Card(modifier, shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, CampusBorder),
        colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(if (projects) Icons.Default.ContentCopy else Icons.Default.Groups,
                contentDescription = null,
                tint = if (projects) Color(0xFF7041B8) else CampusBlue,
                modifier = Modifier.size(36.dp)
                    .background(if (projects) Color(0xFFEDE7FA) else Color(0xFFDCEAFF), RoundedCornerShape(10.dp))
                    .padding(8.dp))
            Text(count.toString(), fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, fontSize = 10.sp, color = CampusMuted)
        }
    }
}
