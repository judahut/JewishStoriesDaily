package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.DailyStory
import com.example.myapplication.data.repository.ContentRepository
import com.example.myapplication.mvi.ContentAction
import com.example.myapplication.mvi.ContentReducer
import com.example.myapplication.mvi.ContentState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AppViewModel(private val repository: ContentRepository) : ViewModel() {

    private val _state = MutableStateFlow(ContentState())
    val state: StateFlow<ContentState> = _state.asStateFlow()

    private val _dateString = MutableStateFlow("")
    val dateString: StateFlow<String> = _dateString.asStateFlow()

    private var currentDayOffset = 0

    init {
        loadDailyWisdom()
        observeFavorites()
    }

    private fun fireAction(action: ContentAction) {
        _state.value = ContentReducer(_state.value, action)
    }

    fun loadDailyWisdom() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, currentDayOffset)
        val formatter = SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault())
        _dateString.value = formatter.format(calendar.time)

        fireAction(ContentAction.LoadDailyStory)

        viewModelScope.launch {
            try {
                val story = repository.getDailyStory(currentDayOffset)
                fireAction(ContentAction.StoryLoaded(story))
            } catch (e: Exception) {
                fireAction(ContentAction.StoryError(e.message ?: "Unknown Error"))
            }
        }
    }

    fun loadSpecificStory(story: DailyStory) {
        fireAction(ContentAction.StoryLoaded(story))
        _dateString.value = "Selected Favorite"
    }

    fun nextDay() {
        currentDayOffset++
        loadDailyWisdom()
    }

    fun previousDay() {
        currentDayOffset--
        loadDailyWisdom()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            repository.favorites.collect { favoriteStories ->
                fireAction(ContentAction.FavoritesLoaded(favoriteStories))
            }
        }
    }

    fun toggleFavorite(story: DailyStory) {
        viewModelScope.launch {
            repository.toggleFavorite(story)
        }
    }
}