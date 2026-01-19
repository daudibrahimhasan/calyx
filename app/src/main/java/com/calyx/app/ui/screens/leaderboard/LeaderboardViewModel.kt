package com.calyx.app.ui.screens.leaderboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.calyx.app.data.models.CallerStats
import com.calyx.app.data.models.RankingCategory
import com.calyx.app.data.models.TimeRange
import com.calyx.app.data.repository.CallLogRepository
import com.calyx.app.data.repository.CallSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Leaderboard screen.
 * Manages call log data, rankings, and UI state.
 */
class LeaderboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CallLogRepository(application)

    // UI State
    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    // Caller stats list
    private val _callerStatsList = MutableStateFlow<List<CallerStats>>(emptyList())
    val callerStatsList: StateFlow<List<CallerStats>> = _callerStatsList.asStateFlow()

    // Selected category
    private val _selectedCategory = MutableStateFlow(RankingCategory.MOST_CALLED)
    val selectedCategory: StateFlow<RankingCategory> = _selectedCategory.asStateFlow()

    // Selected time range
    private val _selectedTimeRange = MutableStateFlow(TimeRange.ALL_TIME)
    val selectedTimeRange: StateFlow<TimeRange> = _selectedTimeRange.asStateFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Summary statistics
    private val _summary = MutableStateFlow(CallSummary(0, 0L, 0, 0, 0, 0))
    val summary: StateFlow<CallSummary> = _summary.asStateFlow()

    // ========== STATS SCREEN DATA (REAL) ==========
    
    // Daily call counts for heatmap (last 35 days) - List of counts ordered from oldest to newest
    private val _dailyCallCounts = MutableStateFlow<List<Int>>(emptyList())
    val dailyCallCounts: StateFlow<List<Int>> = _dailyCallCounts.asStateFlow()
    
    // This week's call data (Mon-Sun)
    private val _thisWeekCalls = MutableStateFlow(listOf(0, 0, 0, 0, 0, 0, 0))
    val thisWeekCalls: StateFlow<List<Int>> = _thisWeekCalls.asStateFlow()
    
    // Last week's call data (Mon-Sun)
    private val _lastWeekCalls = MutableStateFlow(listOf(0, 0, 0, 0, 0, 0, 0))
    val lastWeekCalls: StateFlow<List<Int>> = _lastWeekCalls.asStateFlow()

    init {
        loadCallLog()
        loadStatsData()
    }

    /**
     * Load call log data and process rankings.
     */
    fun loadCallLog() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val stats = repository.getCallerStats(_selectedTimeRange.value)
                _callerStatsList.value = stats
                _summary.value = repository.calculateSummary(stats)
                
                _uiState.value = LeaderboardUiState(
                    isLoading = false,
                    callerStats = getSortedStats(stats),
                    summary = _summary.value
                )
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load call log"
                _uiState.value = _uiState.value.copy(isLoading = false, error = _errorMessage.value)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Load data for the Stats screen (heatmap and trends).
     */
    private fun loadStatsData() {
        viewModelScope.launch {
            try {
                // Load heatmap data (last 35 days)
                _dailyCallCounts.value = repository.getDailyCallCounts(35)
                
                // Load weekly trend data
                _thisWeekCalls.value = repository.getWeeklyCallCounts()
                _lastWeekCalls.value = repository.getLastWeekCallCounts()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Switch the ranking category.
     */
    fun switchCategory(category: RankingCategory) {
        _selectedCategory.value = category
        _uiState.value = _uiState.value.copy(
            callerStats = getSortedStats(_callerStatsList.value)
        )
    }

    /**
     * Switch the time range filter.
     */
    fun switchTimeRange(range: TimeRange) {
        if (range != _selectedTimeRange.value) {
            _selectedTimeRange.value = range
            loadCallLog()
        }
    }

    /**
     * Refresh data manually.
     */
    fun refreshData() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.clearAllData()
            loadCallLog()
            loadStatsData()
        }
    }

    /**
     * Get sorted stats based on current category.
     */
    private fun getSortedStats(stats: List<CallerStats>): List<CallerStats> {
        return when (_selectedCategory.value) {
            RankingCategory.MOST_CALLED -> stats.sortedBy { it.rankByCount }
            RankingCategory.MOST_TALKED -> stats.sortedBy { it.rankByDuration }
        }
    }

    /**
     * Get top 3 callers for podium display.
     */
    fun getTopThree(): Triple<CallerStats?, CallerStats?, CallerStats?> {
        val sorted = getSortedStats(_callerStatsList.value)
        return Triple(
            sorted.getOrNull(0),
            sorted.getOrNull(1),
            sorted.getOrNull(2)
        )
    }

    /**
     * Get callers ranked 4th and below.
     */
    fun getRestOfList(): List<CallerStats> {
        return getSortedStats(_callerStatsList.value).drop(3)
    }
    
    /**
     * Get top 10 callers for share poster.
     */
    fun getTopTen(): List<CallerStats> {
        return getSortedStats(_callerStatsList.value).take(10)
    }

    override fun onCleared() {
        super.onCleared()
        repository.clearCache()
    }
}

/**
 * UI State for the Leaderboard screen.
 */
data class LeaderboardUiState(
    val isLoading: Boolean = true,
    val callerStats: List<CallerStats> = emptyList(),
    val summary: CallSummary = CallSummary(0, 0L, 0, 0, 0, 0),
    val error: String? = null
)
