package com.example.myapplication.mvi

import com.example.myapplication.AppUiState
import com.example.myapplication.data.model.DailyStory

data class ContentState(
    val dailyStory: DailyStory? = null,
    val error: String? = null,
    val uiState: AppUiState = AppUiState.Loading,
    val favorites: List<DailyStory> = emptyList()
)