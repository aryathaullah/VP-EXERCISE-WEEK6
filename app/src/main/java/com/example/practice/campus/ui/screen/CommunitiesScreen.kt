package com.example.practice.campus.ui.screen

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.practice.campus.ui.widgets.CommunityCard
import com.example.practice.campus.ui.widgets.EmptyMessage
import com.example.practice.campus.viewmodel.CampusUiState

@Composable
fun CommunitiesScreen(uiState: CampusUiState, scrollState: ScrollState,
                      onClubClick: (String) -> Unit, onJoinClick: (String) -> Unit,
                      onSearchChange: (String) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(scrollState).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        val clubs = uiState.getVisibleClubs()
        if (clubs.isEmpty()) EmptyMessage("Komunitas tidak ditemukan")
        for (club in clubs) {
            CommunityCard(club, onClick = { onClubClick(club.id) }, onJoinClick = { onJoinClick(club.id) })
        }
    }
}
