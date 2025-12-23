package com.example.myapplication.mvi

import com.example.myapplication.AppUiState

fun ContentReducer(oldState: ContentState, action: ContentAction): ContentState {
    return when (action) {
        is ContentAction.LoadDailyStory -> oldState.copy(
            uiState = AppUiState.Loading
        )

        is ContentAction.StoryLoaded -> oldState.copy(
            dailyStory = action.story,
            uiState = AppUiState.Success(action.story),
            error = null
        )
        is ContentAction.StoryError -> oldState.copy(
            error = action.message,
            uiState = AppUiState.Error(Exception(action.message))
        )
        is ContentAction.FavoritesLoaded -> {
            val newFavorites = action.favorites
            var newState = oldState.copy(favorites = newFavorites)


            if (oldState.uiState is AppUiState.Success) {
                val currentStory = oldState.uiState.story
                val isFavNow = newFavorites.any { it.ref == currentStory.ref }
                if (currentStory.isFavorite != isFavNow) {
                    val updatedStory = currentStory.copy(isFavorite = isFavNow)
                    newState = newState.copy(uiState = AppUiState.Success(updatedStory))
                }
            }
            newState
        }

        else -> oldState
    }
}