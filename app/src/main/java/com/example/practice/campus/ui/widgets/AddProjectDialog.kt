package com.example.practice.campus.ui.widgets

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.practice.campus.ui.theme.*
import com.example.practice.campus.viewmodel.CampusUiState

@Composable
fun AddProjectDialog(uiState: CampusUiState, scrollState: ScrollState,
                     onTitleChange: (String) -> Unit, onDescriptionChange: (String) -> Unit,
                     onTechClick: (String) -> Unit, onSave: () -> Unit, onCancel: () -> Unit) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.fillMaxWidth().verticalScroll(scrollState).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Add Project", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                val community = uiState.getClub(uiState.formClubId)
                Text("Komunitas: ${community?.name ?: ""} (${community?.subtitle ?: ""})",
                    color = CampusMuted, fontSize = 11.sp)
                Text("Judul Proyek *", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(value = uiState.projectTitle, onValueChange = onTitleChange,
                    placeholder = { Text("Nama proyek", fontSize = 12.sp) },
                    singleLine = true, shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth())
                Text("Deskripsi Proyek (Minimal 3 Baris) *", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(value = uiState.projectDescription, onValueChange = onDescriptionChange,
                    placeholder = { Text("Jelaskan proyek yang dikerjakan", fontSize = 12.sp) },
                    minLines = 3, maxLines = 5, shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth())
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Tech Stack (Pilihan Multi-Selection) *", fontSize = 11.sp,
                        fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("${uiState.selectedTechStack.size} Dipilih", color = CampusBlue, fontSize = 10.sp)
                }
                Column {
                    for (index in uiState.techOptions.indices step 2) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            TechChoice(uiState.techOptions[index], uiState.selectedTechStack, onTechClick)
                            if (index + 1 < uiState.techOptions.size) {
                                TechChoice(uiState.techOptions[index + 1], uiState.selectedTechStack, onTechClick)
                            }
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onCancel) { Text("Cancel") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = onSave, enabled = uiState.canSave) { Text("Save") }
                }
            }
        }
    }
}

@Composable
fun TechChoice(tech: String, selectedTech: List<String>, onClick: (String) -> Unit) {
    val selected = selectedTech.contains(tech)
    FilterChip(selected = selected, onClick = { onClick(tech) },
        label = { Text(if (selected) "✓ $tech" else "+ $tech", fontSize = 10.sp) })
}
