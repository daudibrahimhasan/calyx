package com.calyx.app.ui.screens.leaderboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calyx.app.data.models.RankingCategory
import com.calyx.app.ui.components.*
import com.calyx.app.ui.theme.*

/**
 * Main leaderboard screen with minimized UI for maximum content visibility.
 * 
 * Design Goals:
 * - Reclaim 30% vertical space
 * - Compact toolbar (standard Android style)
 * - 32dp filter pills
 * - 40dp HUD stats strip
 */
// Define custom easing for premium feel
val EaseOutBack = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: LeaderboardViewModel = viewModel(),
    isDarkTheme: Boolean = false
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedTimeRange by viewModel.selectedTimeRange.collectAsState()
    val summary by viewModel.summary.collectAsState()

    // Haptics
    val haptic = LocalHapticFeedback.current
    
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

    // Determine gradients based on theme
    val screenGradient = if (isDarkTheme) CalyzGradients.darkScreenBackgroundGradient else CalyzGradients.screenBackgroundGradient
    val headerGradient = if (isDarkTheme) CalyzGradients.darkHeaderGradient else CalyzGradients.headerGradient

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(screenGradient)
    ) {
        if (uiState.isLoading) {
            LoadingState()
        } else {
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
                        .background(headerGradient)
                        .statusBarsPadding()
                        .height(56.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Title - "calyz"
                        Text(
                            text = "Calyz",
                            fontSize = 30.sp, 
                            fontWeight = FontWeight.Bold,
                            fontFamily = LufgaFontFamily,
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
                        
                        IconButton(
                            onClick = { 
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.refreshData() 
                            },
                            enabled = !uiState.isLoading
                        ) {
                            if (isRefreshing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh",
                                    modifier = Modifier.size(22.dp),
                                    tint = Color.White
                                )
                            }
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
                    onCategoryChange = { 
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.switchCategory(it) 
                    },
                    onTimeRangeChange = { 
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        viewModel.switchTimeRange(it) 
                    }
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
                        .background(MaterialTheme.colorScheme.background) // Theme-aware background
                ) {
                    when {
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
}

@Composable
private fun LeaderboardContent(
    viewModel: LeaderboardViewModel,
    selectedCategory: RankingCategory
) {
    val topThreeValue by viewModel.topThree.collectAsState()
    val topThree = topThreeValue // Rename to avoid shadowing if needed, though here it's fine
    val fullRestOfList by viewModel.restOfList.collectAsState()
    
    // Limit display to 97 contacts below podium (100 total including top 3) in a derived state to avoid re-taking on every recomposition
    val restOfList = remember(fullRestOfList) { fullRestOfList.take(97) }
    
    // Use scroll state for smooth list performance
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp), // Extra space for bottom nav
        // Optimized fling behavior for smoother scrolling
        flingBehavior = ScrollableDefaults.flingBehavior()
    ) {
        // Top 3 Podium
        item(key = "podium") {
            // Animate podium entrance
            val podiumAlpha by animateFloatAsState(
                targetValue = 1f,
                animationSpec = tween(600, easing = EaseOutBack),
                label = "podiumAlpha"
            )
            val podiumScale by animateFloatAsState(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow),
                label = "podiumScale"
            )

            TopThreePodium(
                firstPlace = topThree.first,
                secondPlace = topThree.second,
                thirdPlace = topThree.third,
                category = selectedCategory,
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 8.dp)
                    .graphicsLayer {
                        alpha = podiumAlpha
                        scaleX = podiumScale
                        scaleY = podiumScale
                    }
            )
        }

        // Rankings section
        if (restOfList.isNotEmpty()) {
            // Section header
            item(key = "rankings_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rankings",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${restOfList.size + 3} contacts",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            // Ultra-optimized list items - NO animations for maximum scroll performance
            itemsIndexed(
                items = restOfList,
                key = { _: Int, caller: com.calyx.app.data.models.CallerStats -> caller.phoneNumber }
            ) { _: Int, caller: com.calyx.app.data.models.CallerStats ->
                RankedListItem(
                    callerStats = caller,
                    category = selectedCategory,
                    onClick = { /* Future: show caller details */ },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 3.dp)
                )
            }
        }
    }
}
