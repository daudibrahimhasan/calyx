package com.calyx.app.ui.screens.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calyx.app.data.models.RankingCategory
import com.calyx.app.data.models.TimeRange
import com.calyx.app.ui.components.*

/**
 * Main leaderboard screen displaying call rankings.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedTimeRange by viewModel.selectedTimeRange.collectAsState()
    val summary by viewModel.summary.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Leaderboard",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.refreshData() },
                        enabled = !uiState.isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Category tabs
            CategoryTabs(
                selectedCategory = selectedCategory,
                onCategoryChange = { viewModel.switchCategory(it) }
            )

            // Time filter tabs
            TimeFilterTabs(
                selectedRange = selectedTimeRange,
                onRangeChange = { viewModel.switchTimeRange(it) }
            )

            when {
                uiState.isLoading -> {
                    LoadingState(message = "Analyzing your calls...")
                }
                uiState.error != null -> {
                    EmptyState(
                        title = "Something went wrong",
                        message = uiState.error ?: "Please try again"
                    )
                }
                uiState.callerStats.isEmpty() -> {
                    EmptyState(
                        title = "No calls yet",
                        message = "Make some calls and they'll appear here!"
                    )
                }
                else -> {
                    LeaderboardContent(
                        viewModel = viewModel,
                        summary = summary,
                        selectedCategory = selectedCategory
                    )
                }
            }
        }
    }
}

@Composable
private fun LeaderboardContent(
    viewModel: LeaderboardViewModel,
    summary: com.calyx.app.data.repository.CallSummary,
    selectedCategory: RankingCategory
) {
    val (first, second, third) = viewModel.getTopThree()
    val restOfList = viewModel.getRestOfList()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Stats card
        item {
            StatsCard(
                summary = summary,
                category = selectedCategory,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        // Top 3 podium
        item {
            TopThreePodium(
                firstPlace = first,
                secondPlace = second,
                thirdPlace = third,
                category = selectedCategory,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Divider
        if (restOfList.isNotEmpty()) {
            item {
                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }

        // Rest of the list (4th place onwards)
        items(
            items = restOfList,
            key = { it.phoneNumber }
        ) { caller ->
            RankedListItem(
                callerStats = caller,
                category = selectedCategory,
                onClick = { /* Future: show caller details */ },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    }
}
