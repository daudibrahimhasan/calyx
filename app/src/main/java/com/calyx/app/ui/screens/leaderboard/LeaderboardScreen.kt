package com.calyx.app.ui.screens.leaderboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calyx.app.R
import com.calyx.app.data.models.RankingCategory
import com.calyx.app.ui.components.*
import com.calyx.app.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Main leaderboard screen with green gradient header.
 * 
 * Design Spec - Top Bar:
 * - Background: Linear gradient (#2BB15D → #4EC651)
 * - Height: 120dp
 * - "Leaderboard" | Lufga Bold, 28sp, White
 * - Refresh icon (top right) | 24dp, White
 * - Glass overlay effect with subtle blur
 * - Shadow: 0 4dp 12dp rgba(28, 156, 112, 0.2)
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

    // Header slide-in animation
    var headerVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        headerVisible = true
    }
    
    val headerOffset by animateFloatAsState(
        targetValue = if (headerVisible) 0f else -120f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "headerOffset"
    )
    
    val headerAlpha by animateFloatAsState(
        targetValue = if (headerVisible) 1f else 0f,
        animationSpec = tween(350, easing = FastOutSlowInEasing),
        label = "headerAlpha"
    )

    // Rotating refresh icon animation
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
            // Custom gradient header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        translationY = headerOffset
                        alpha = headerAlpha
                    }
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp),
                        ambientColor = Color(0x331C9C70),
                        spotColor = Color(0x331C9C70)
                    )
                    .background(CalyxGradients.headerGradient)
                    .statusBarsPadding()
                    .height(120.dp)
            ) {
                // Glass overlay effect
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Leaderboard",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    IconButton(
                        onClick = { 
                            viewModel.refreshData()
                        },
                        enabled = !uiState.isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            modifier = Modifier
                                .size(24.dp)
                                .then(
                                    if (isRefreshing) {
                                        Modifier.graphicsLayer {
                                            rotationZ = refreshRotation
                                        }
                                    } else Modifier
                                ),
                            tint = Color.White
                        )
                    }
                }
            }

            // Content area with category and time tabs (still on header gradient)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CalyxGradients.headerGradient)
            ) {
                Column {
                    // Category tabs (Most Called / Most Talked)
                    CategoryTabs(
                        selectedCategory = selectedCategory,
                        onCategoryChange = { viewModel.switchCategory(it) }
                    )

                    // Time filter tabs (Weekly / All Time)
                    TimeFilterTabs(
                        selectedRange = selectedTimeRange,
                        onRangeChange = { viewModel.switchTimeRange(it) }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Main content area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
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
                            summary = summary,
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
    summary: com.calyx.app.data.repository.CallSummary,
    selectedCategory: RankingCategory
) {
    val (first, second, third) = viewModel.getTopThree()
    val restOfList = viewModel.getRestOfList()

    // Staggered entrance animation for list items
    var listVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        listVisible = true
    }

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

        // Section divider
        if (restOfList.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = 1.dp,
                        color = VibrantGreen.copy(alpha = 0.2f)
                    )
                    Text(
                        text = "Rankings",
                        style = MaterialTheme.typography.labelMedium,
                        color = SecondaryText,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = 1.dp,
                        color = VibrantGreen.copy(alpha = 0.2f)
                    )
                }
            }
        }

        // Rest of the list (4th place onwards) with staggered animation
        items(
            items = restOfList,
            key = { it.phoneNumber }
        ) { caller ->
            val index = restOfList.indexOf(caller)
            var itemVisible by remember { mutableStateOf(false) }
            
            LaunchedEffect(listVisible) {
                if (listVisible) {
                    delay(index * 60L)
                    itemVisible = true
                }
            }
            
            val itemAlpha by animateFloatAsState(
                targetValue = if (itemVisible) 1f else 0f,
                animationSpec = tween(300, easing = FastOutSlowInEasing),
                label = "itemAlpha"
            )
            
            val itemOffset by animateFloatAsState(
                targetValue = if (itemVisible) 0f else 20f,
                animationSpec = tween(300, easing = FastOutSlowInEasing),
                label = "itemOffset"
            )
            
            RankedListItem(
                callerStats = caller,
                category = selectedCategory,
                onClick = { /* Future: show caller details */ },
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .graphicsLayer {
                        alpha = itemAlpha
                        translationY = itemOffset
                    }
            )
        }
    }
}
