package com.calyx.app.ui.screens.leaderboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calyx.app.data.models.RankingCategory
import com.calyx.app.ui.components.*
import com.calyx.app.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Main leaderboard screen with minimized UI for maximum content visibility.
 * 
 * Design Goals:
 * - Reclaim 30% vertical space
 * - Compact toolbar (standard Android style)
 * - 32dp filter pills
 * - 40dp HUD stats strip
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedTimeRange by viewModel.selectedTimeRange.collectAsState()
    val summary by viewModel.summary.collectAsState()

    // Refresh animation
    var isRefreshing by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "refresh")
    val refreshRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "refreshRotation"
    )

    LaunchedEffect(uiState.isLoading) {
        isRefreshing = uiState.isLoading
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CalyxGradients.screenBackgroundGradient)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ========================================
            // COMPACT TOOLBAR (Standard Android Style)
            // Height: ~56dp, Left-aligned title, Right actions
            // ========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CalyxGradients.headerGradient)
                    .statusBarsPadding()
                    .height(56.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Title - Left aligned, 20sp Bold
                    Text(
                        text = "Leaderboard",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Share button
                    IconButton(
                        onClick = {
                            val topContacts = viewModel.getTopTen()
                            if (topContacts.isNotEmpty()) {
                                com.calyx.app.ui.share.SharePosterGenerator.shareTopContacts(
                                    context = context,
                                    topContacts = topContacts,
                                    category = selectedCategory
                                )
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            modifier = Modifier.size(22.dp),
                            tint = Color.White
                        )
                    }
                    
                    // Refresh button
                    IconButton(
                        onClick = { viewModel.refreshData() },
                        enabled = !uiState.isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            modifier = Modifier
                                .size(22.dp)
                                .then(
                                    if (isRefreshing) {
                                        Modifier.graphicsLayer { rotationZ = refreshRotation }
                                    } else Modifier
                                ),
                            tint = Color.White
                        )
                    }
                    
                    // Settings button
                    IconButton(onClick = { /* TODO: Open settings */ }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.size(22.dp),
                            tint = Color.White
                        )
                    }
                }
            }

            // ========================================
            // COMPACT FILTER STRIP (32dp)
            // Segmented pill + Category tabs in one row
            // ========================================
            CompactFilterStrip(
                selectedCategory = selectedCategory,
                selectedTimeRange = selectedTimeRange,
                onCategoryChange = { viewModel.switchCategory(it) },
                onTimeRangeChange = { viewModel.switchTimeRange(it) }
            )

            // ========================================
            // HUD STATS STRIP (40dp)
            // ========================================
            HudStatsStrip(
                totalCalls = summary.totalCalls,
                totalContacts = summary.uniqueContacts
            )

            // ========================================
            // MAIN CONTENT AREA
            // ========================================
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(BackgroundBase)
            ) {
                when {
                    uiState.isLoading -> {
                        LoadingState(message = "Analyzing your calls...")
                    }
                    uiState.error != null -> {
                        ErrorState(
                            title = "Something went wrong",
                            message = uiState.error ?: "Please try again",
                            actionText = "Retry",
                            onAction = { viewModel.refreshData() }
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
                            selectedCategory = selectedCategory
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardContent(
    viewModel: LeaderboardViewModel,
    selectedCategory: RankingCategory
) {
    val (first, second, third) = viewModel.getTopThree()
    val restOfList = viewModel.getRestOfList()

    // Staggered entrance animation
    var listVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(200)
        listVisible = true
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Top 3 Podium with "Growth" animation
        item {
            TopThreePodium(
                firstPlace = first,
                secondPlace = second,
                thirdPlace = third,
                category = selectedCategory,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Rest of the list as a "Sheet" overlay
        if (restOfList.isNotEmpty()) {
            // Sheet header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Rankings",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = SecondaryText
                    )
                }
            }

            // List items with staggered animation
            items(
                items = restOfList,
                key = { it.phoneNumber }
            ) { caller ->
                val index = restOfList.indexOf(caller)
                var itemVisible by remember { mutableStateOf(false) }
                
                LaunchedEffect(listVisible) {
                    if (listVisible) {
                        delay(index * 40L)
                        itemVisible = true
                    }
                }
                
                val itemAlpha by animateFloatAsState(
                    targetValue = if (itemVisible) 1f else 0f,
                    animationSpec = tween(250, easing = FastOutSlowInEasing),
                    label = "itemAlpha"
                )
                
                val itemOffset by animateFloatAsState(
                    targetValue = if (itemVisible) 0f else 16f,
                    animationSpec = tween(250, easing = FastOutSlowInEasing),
                    label = "itemOffset"
                )
                
                RankedListItem(
                    callerStats = caller,
                    category = selectedCategory,
                    onClick = { /* Future: show caller details */ },
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 3.dp)
                        .graphicsLayer {
                            alpha = itemAlpha
                            translationY = itemOffset
                        }
                )
            }
        }
    }
}
