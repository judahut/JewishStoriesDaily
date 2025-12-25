package com.example.myapplication.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.ReaderTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

data class UserSettings(
    val theme: ReaderTheme,
    val fontSize: Float
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME = stringPreferencesKey("reader_theme")
        val FONT_SIZE = floatPreferencesKey("font_size")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data
        .map { preferences ->
            val themeName = preferences[PreferencesKeys.THEME] ?: ReaderTheme.Day.name
            val fontSize = preferences[PreferencesKeys.FONT_SIZE] ?: 20f

            UserSettings(
                theme = try {
                    ReaderTheme.valueOf(themeName)
                } catch (e: Exception) {
                    ReaderTheme.Day
                },
                fontSize = fontSize
            )
        }

    suspend fun updateTheme(theme: ReaderTheme) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme.name
        }
    }

    suspend fun updateFontSize(size: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FONT_SIZE] = size
        }
    }
}