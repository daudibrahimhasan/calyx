package com.calyx.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.calyx.app.data.models.RankingCategory

/**
 * Category tabs for switching between Most Called and Most Talked.
 */
@Composable
fun CategoryTabs(
    selectedCategory: RankingCategory,
    onCategoryChange: (RankingCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = RankingCategory.entries
    val selectedIndex = categories.indexOf(selectedCategory)

    TabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                color = MaterialTheme.colorScheme.primary
            )
        }
    ) {
        categories.forEachIndexed { index, category ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onCategoryChange(category) },
                text = {
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
