package com.example.myapplication // <--- THIS LINE IS MISSING

import com.example.myapplication.data.model.DailyStory

sealed class AppUiState {
    object Empty : AppUiState()
    object Loading : AppUiState()
    data class Success(val story: DailyStory) : AppUiState()
    data class Error(val exception: Exception) : AppUiState()
}