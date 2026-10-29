package com.example.practice.campus.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.practice.campus.model.Community
import com.example.practice.campus.model.Project
import com.example.practice.campus.ui.theme.*
import com.example.practice.campus.ui.widgets.*

@Composable
fun ClubProjectsScreen(community: Community?, projects: List<Project>, scrollState: ScrollState,
                       onAddClick: () -> Unit, onProjectClick: (String) -> Unit,
                       onDelete: (String) -> Unit) {
    if (community == null || !community.joined) {
        Box(Modifier.fillMaxSize().padding(16.dp)) {
            EmptyMessage("Harap bergabung ke komunitas terlebih dahulu!")
        }
        return
    }
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().verticalScroll(scrollState).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFFC3DBFF)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE1EEFF))) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text(community.subtitle, color = Color(0xFF24449A),
                            fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("${community.members} Anggota · ${projects.size} Proyek",
                            color = CampusBlue, fontSize = 11.sp)
                    }
                    JoinedBadge()
                }
            }
            SectionTitle("Daftar Proyek Mahasiswa (${projects.size})", projects = true)
            if (projects.isEmpty()) EmptyMessage("Belum ada proyek yang dicatat")
            for (project in projects) {
                ProjectCard(project, community.name, false, true,
                    onClick = { onProjectClick(project.id) }, onDelete = { onDelete(project.id) })
            }
            Spacer(Modifier.height(88.dp))
        }
        ExtendedFloatingActionButton(onClick = onAddClick,
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            text = { Text("Add Project", fontWeight = FontWeight.Bold) },
            containerColor = Color(0xFFDCEAFF), contentColor = Color(0xFF24449A),
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp))
    }
}
