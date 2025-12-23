package com.example.myapplication.mvi

import com.example.myapplication.data.model.DailyStory

sealed class ContentAction {
    object LoadDailyStory : ContentAction()
    data class StoryLoaded(val story: DailyStory) : ContentAction()
    data class StoryError(val message: String) : ContentAction()
    data class ToggleFavorite(val story: DailyStory) : ContentAction()
    data class FavoritesLoaded(val favorites: List<DailyStory>) : ContentAction()
}