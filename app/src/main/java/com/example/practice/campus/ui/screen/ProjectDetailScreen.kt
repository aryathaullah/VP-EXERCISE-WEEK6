package com.example.practice.campus.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.practice.campus.model.Project
import com.example.practice.campus.ui.theme.*
import com.example.practice.campus.ui.widgets.*

@Composable
fun ProjectDetailScreen(project: Project?, clubName: String, scrollState: ScrollState,
                        onStatusChange: (Boolean) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(scrollState).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)) {
        if (project == null) {
            EmptyMessage("Proyek tidak ditemukan")
        } else {
            Column(Modifier.fillMaxWidth().background(
                Brush.horizontalGradient(listOf(Color(0xFF202D40), Color(0xFF101929))),
                RoundedCornerShape(24.dp)).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(clubName, color = Color(0xFF93C5FD), fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.background(Color(0xFF243F70), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 5.dp))
                Text(project.title, color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
            }
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, CampusBorder),
                colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Mark as Done", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Status:", fontSize = 11.sp, color = CampusMuted)
                            StatusBadge(project.completed)
                        }
                    }
                    Switch(checked = project.completed, onCheckedChange = onStatusChange)
                }
            }
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, CampusBorder),
                colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Deskripsi Lengkap Proyek", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(project.description, fontSize = 13.sp, lineHeight = 21.sp, color = Color(0xFF64748B))
                }
            }
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, CampusBorder),
                colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Tech Stack Terpilih", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    TechBadges(project.techStack)
                }
            }
        }
    }
}
