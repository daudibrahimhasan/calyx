package com.calyx.app.ui.screens.leaderboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.calyx.app.data.models.CallerStats
import com.calyx.app.data.models.RankingCategory
import com.calyx.app.data.models.TimeRange
import com.calyx.app.data.repository.CallLogRepository
import com.calyx.app.data.repository.CallSummary
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

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
    // Remove direct exposing of _callerStatsList to ensure sorting is handled via StateFlow combinations

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

    // Derived sorted list - ONLY recalculates when data or category changes
    private val sortedStats = combine(_callerStatsList, _selectedCategory) { stats, category ->
        when (category) {
            RankingCategory.MOST_CALLED -> stats.sortedBy { it.rankByCount }
            RankingCategory.MOST_TALKED -> stats.sortedBy { it.rankByDuration }
        }
    }
    .flowOn(Dispatchers.Default)
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList<CallerStats>())

    // Derived Top 3
    val topThree = sortedStats.map { stats ->
        Triple(stats.getOrNull(0), stats.getOrNull(1), stats.getOrNull(2))
    }
    .flowOn(Dispatchers.Default)
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Triple<CallerStats?, CallerStats?, CallerStats?>(null, null, null))

    // Derived Rest of List
    val restOfList = sortedStats.map { stats ->
        stats.drop(3)
    }
    .flowOn(Dispatchers.Default)
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList<CallerStats>())

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
                    callerStats = stats, // Passing unsorted list, UI should use sorted flows
                    summary = _summary.value
                )
                
                // Load stats data after main log is loaded and synced
                loadStatsData()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to load call log"
                _uiState.value = _uiState.value.copy(isLoading = false, error = _errorMessage.value)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Backend Repository (lazy to ensure proper initialization order)
    private val backendRepo by lazy { com.calyx.app.data.repository.StatsBackendRepository() }
    
    // Global Stats (lazy to match backendRepo initialization)
    val globalStats: StateFlow<com.calyx.app.data.models.GlobalStats> by lazy { backendRepo.globalStats }

    /**
     * Load data for the Stats screen (heatmap and trends).
     * Also triggers backend sync.
     */
    private fun loadStatsData() {
        viewModelScope.launch {
            try {
                // 0. Start Firebase listener (non-blocking, happens on background thread)
                backendRepo.startListening()
                
                // 1. Load local heatmap data (last 35 days)
                _dailyCallCounts.value = repository.getDailyCallCounts(35)
                
                // 2. Load local weekly trend data
                val thisWeek = repository.getWeeklyCallCounts()
                val lastWeek = repository.getLastWeekCallCounts()
                _thisWeekCalls.value = thisWeek
                _lastWeekCalls.value = lastWeek
                
                // 3. Initiate Backend Sync
                // Get stable User ID (create if not exists)
                val prefs = getApplication<Application>().getSharedPreferences("calyz_prefs", android.content.Context.MODE_PRIVATE)
                var userId = prefs.getString("user_id", null)
                if (userId == null) {
                    userId = java.util.UUID.randomUUID().toString()
                    prefs.edit().putString("user_id", userId).apply()
                }
                
                // Calculate local stats for sync
                val allTimeStats = repository.getCallerStats(TimeRange.ALL_TIME)
                val allTimeSummary = repository.calculateSummary(allTimeStats)
                val localTotalCalls = allTimeSummary.totalCalls
                
                val localTodayCalls = thisWeek.lastOrNull() ?: 0 // Assuming last item is today
                val localWeekCalls = thisWeek.sum()
                
                // Fire and forget sync
                launch {
                    backendRepo.syncStats(
                        context = getApplication(),
                        userId = userId,
                        localTotalCalls = localTotalCalls,
                        localTodayCalls = localTodayCalls, 
                        localWeekCalls = localWeekCalls
                    )
                }
                
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
        // No need to manual update _uiState, sortedStats handles it
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
            // repository.clearAllData() // Removing this to prevent empty state flicker
            loadCallLog()
            loadStatsData()
        }
    }

    /**
     * Get top 10 callers for share poster.
     */
    fun getTopTen(): List<CallerStats> {
        return sortedStats.value.take(10)
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
