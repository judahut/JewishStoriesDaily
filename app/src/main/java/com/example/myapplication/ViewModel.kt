package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.text.Html
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.DailyStory
import com.example.myapplication.data.repository.ContentRepository
import com.example.myapplication.data.repository.UserPreferencesRepository
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

class AppViewModel(
    private val contentRepository: ContentRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ContentState())
    val state: StateFlow<ContentState> = _state.asStateFlow()

    private val _dateString = MutableStateFlow("")
    val dateString: StateFlow<String> = _dateString.asStateFlow()

    private var currentDayOffset = 0

    var currentTheme by mutableStateOf(ReaderTheme.Day)
        private set

    var textSizeSp by mutableStateOf(20f)
        private set

    // Helper to check if the phone is in Hebrew
    val isSystemHebrew: Boolean
        get() = Locale.getDefault().language == "he"

    init {
        loadDailyWisdom()
        observeFavorites()
        observePreferences()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            preferencesRepository.userSettingsFlow.collect { settings ->
                currentTheme = settings.theme
                textSizeSp = settings.fontSize
            }
        }
    }

    private fun fireAction(action: ContentAction) {
        _state.value = ContentReducer(_state.value, action)
    }

    fun loadDailyWisdom() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, currentDayOffset)

        // Date Format: If Hebrew, it formats correctly automatically (e.g. "יום ראשון")
        val formatter = SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault())
        _dateString.value = formatter.format(calendar.time)

        fireAction(ContentAction.LoadDailyStory)

        viewModelScope.launch {
            try {
                // PASS THE LANGUAGE FLAG HERE
                val story = contentRepository.getDailyStory(currentDayOffset, isSystemHebrew)
                fireAction(ContentAction.StoryLoaded(story))
            } catch (e: Exception) {
                fireAction(ContentAction.StoryError(e.message ?: "Unknown Error"))
            }
        }
    }

    fun loadSpecificStory(story: DailyStory) {
        fireAction(ContentAction.StoryLoaded(story))
        _dateString.value = if (isSystemHebrew) "נבחר ממועדפים" else "Selected Favorite"
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
            contentRepository.favorites.collect { favoriteStories ->
                fireAction(ContentAction.FavoritesLoaded(favoriteStories))
            }
        }
    }

    fun toggleFavorite(story: DailyStory) {
        viewModelScope.launch {
            contentRepository.toggleFavorite(story)
        }
    }

    fun toggleTheme() {
        val newTheme = when (currentTheme) {
            ReaderTheme.Day -> ReaderTheme.Cream
            ReaderTheme.Cream -> ReaderTheme.Night
            ReaderTheme.Night -> ReaderTheme.Day
        }
        viewModelScope.launch {
            preferencesRepository.updateTheme(newTheme)
        }
    }

    fun increaseFontSize() {
        if (textSizeSp < 40f) {
            viewModelScope.launch {
                preferencesRepository.updateFontSize(textSizeSp + 2f)
            }
        }
    }

    fun decreaseFontSize() {
        if (textSizeSp > 14f) {
            viewModelScope.launch {
                preferencesRepository.updateFontSize(textSizeSp - 2f)
            }
        }
    }

    fun parseHtml(text: String): String {
        return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY).toString()
    }

    fun shareStory(context: Context, story: DailyStory) {
        // Share text depends on the mode
        val cleanText = if (isSystemHebrew) {
            story.hebrewText.joinToString("\n\n") { parseHtml(it) }
        } else {
            story.englishText.joinToString("\n\n") { parseHtml(it) }
        }

        val footer = if (isSystemHebrew) "- נשלח מסיפור תלמוד יומי" else "- Sent from Daily Talmud Tale"
        val shareMessage = "${story.title}\n\n$cleanText\n\n$footer"

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareMessage)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "Share Story"))
    }
}
enum class ReaderTheme(val bg: Color, val text: Color, val icon: Color) {
    Day(Color(0xFFFFFFFF), Color(0xFF111111), Color(0xFF666666)),
    Cream(Color(0xFFF8F1E3), Color(0xFF3E362E), Color(0xFF8D8172)),
    Night(Color(0xFF121212), Color(0xFFE0E0E0), Color(0xFF757575))
}