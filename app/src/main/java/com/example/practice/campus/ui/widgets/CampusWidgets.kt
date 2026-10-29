package com.example.practice.campus.ui.widgets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.practice.campus.model.Community
import com.example.practice.campus.model.Project
import com.example.practice.campus.ui.theme.*

@Composable
fun ClubIcon(clubId: String, modifier: Modifier = Modifier) {
    val icon = when (clubId) {
        "cyber" -> Icons.Default.Security
        "ai" -> Icons.Default.AutoAwesome
        "mobile" -> Icons.Default.PhoneAndroid
        "web" -> Icons.Default.Language
        "cloud" -> Icons.Default.Cloud
        else -> Icons.Default.Code
    }
    val tint = when (clubId) {
        "cyber" -> CampusRed
        "ai", "cloud" -> Color(0xFF8038F3)
        "mobile" -> Color(0xFF0099CC)
        else -> CampusBlue
    }
    Box(modifier.size(44.dp).background(tint.copy(alpha = 0.10f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(25.dp))
    }
}

@Composable
fun JoinedBadge() {
    Text("Joined ✓", color = CampusRed, fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
        modifier = Modifier.background(Color(0xFFFFF1F2), RoundedCornerShape(12.dp))
            .padding(horizontal = 9.dp, vertical = 4.dp))
}

@Composable
fun StatusBadge(completed: Boolean) {
    val textColor = if (completed) Color(0xFF14833B) else Color(0xFFB86B00)
    val background = if (completed) Color(0xFFDCFCE7) else Color(0xFFFFF2BF)
    Text(if (completed) "● Completed" else "● In Progress",
        color = textColor, fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
        modifier = Modifier.background(background, RoundedCornerShape(12.dp))
            .padding(horizontal = 9.dp, vertical = 4.dp))
}

@Composable
fun TechBadges(techStack: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        for (index in techStack.indices step 2) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                TechBadge(techStack[index])
                if (index + 1 < techStack.size) TechBadge(techStack[index + 1])
            }
        }
    }
}

@Composable
fun TechBadge(tech: String) {
    Text(tech, color = Color(0xFF475569), fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.background(Color(0xFFF0F4F8), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp))
}

@Composable
fun SectionTitle(title: String, trailing: String = "", projects: Boolean = false) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(if (projects) Icons.Default.ContentCopy else Icons.Default.Star,
            contentDescription = null, tint = CampusBlue, modifier = Modifier.size(17.dp))
        Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        if (trailing.isNotEmpty()) Text(trailing, color = CampusBlue, fontSize = 9.sp)
    }
}

@Composable
fun JoinedClubCard(community: Community, projectCount: Int, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, CampusBorder),
        colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ClubIcon(community.id, Modifier.size(36.dp))
            Column(Modifier.weight(1f)) {
                Text(community.name, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("${community.subtitle} · $projectCount Proyek", color = CampusMuted, fontSize = 10.sp)
            }
            JoinedBadge()
        }
    }
}

@Composable
fun CommunityCard(community: Community, onClick: () -> Unit, onJoinClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, CampusBorder),
        colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ClubIcon(community.id)
                Column(Modifier.weight(1f)) {
                    Text(community.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("${community.subtitle} · ${community.members} Mahasiswa",
                        fontSize = 10.sp, color = CampusMuted)
                }
            }
            Text(community.description, fontSize = 12.sp, lineHeight = 17.sp, color = Color(0xFF5B6777))
            HorizontalDivider(color = CampusBorder)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(if (community.joined) Icons.Default.LockOpen else Icons.Default.Lock,
                        contentDescription = null, modifier = Modifier.size(13.dp),
                        tint = if (community.joined) CampusBlue else CampusMuted)
                    Text(if (community.joined) "Unlocked · Buka Proyek →" else "Terkunci (Gate Akses)",
                        fontSize = 10.sp, fontWeight = FontWeight.Medium,
                        color = if (community.joined) CampusBlue else CampusMuted)
                }
                if (community.joined) {
                    OutlinedButton(onClick = onJoinClick, border = BorderStroke(1.dp, CampusRed),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CampusRed)) {
                        Text("✓ Joined", fontSize = 11.sp)
                    }
                } else {
                    Button(onClick = onJoinClick,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)) {
                        Text("+ Join", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ProjectCard(project: Project, clubName: String, showClub: Boolean,
                showDelete: Boolean, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp), border = BorderStroke(1.dp, CampusBorder),
        colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top) {
                Box(Modifier.size(40.dp).background(Color(0xFFDCEAFF), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Android, contentDescription = null, tint = CampusBlue)
                }
                Column(Modifier.weight(1f)) {
                    if (showClub) Text(clubName, fontSize = 10.sp, color = CampusBlue, fontWeight = FontWeight.Bold)
                    Text(project.title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(project.description, fontSize = 11.sp, color = CampusMuted,
                        maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 15.sp)
                }
                if (showDelete) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.Remove, contentDescription = "Hapus ${project.title}",
                            tint = CampusRed, modifier = Modifier.size(28.dp)
                                .background(Color(0xFFFFE2E5), CircleShape).padding(6.dp))
                    }
                }
            }
            TechBadges(project.techStack)
            HorizontalDivider(color = CampusBorder)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text("ID: ${project.id}", color = CampusMuted, fontSize = 10.sp,
                    modifier = Modifier.weight(1f))
                StatusBadge(project.completed)
            }
        }
    }
}

@Composable
fun EmptyMessage(message: String) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, CampusBorder),
        colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Text(message, color = CampusMuted, fontSize = 13.sp, modifier = Modifier.padding(20.dp))
    }
}
