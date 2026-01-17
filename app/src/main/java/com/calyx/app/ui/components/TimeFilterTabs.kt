package com.calyx.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.calyx.app.data.models.TimeRange

/**
 * Time filter tabs for switching between Weekly and All Time.
 */
@Composable
fun TimeFilterTabs(
    selectedRange: TimeRange,
    onRangeChange: (TimeRange) -> Unit,
    modifier: Modifier = Modifier
) {
    val ranges = TimeRange.entries
    val selectedIndex = ranges.indexOf(selectedRange)

    TabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp),
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        divider = {}
    ) {
        ranges.forEachIndexed { index, range ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onRangeChange(range) },
                modifier = Modifier.height(40.dp),
                text = {
                    Text(
                        text = range.displayName,
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                selectedContentColor = MaterialTheme.colorScheme.secondary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
