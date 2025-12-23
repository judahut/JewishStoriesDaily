package com.example.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_stories")
data class CachedStory(
    @PrimaryKey val ref: String,
    val title: String,
    val description: String,
    val hebrewText: List<String>,
    val englishText: List<String>,
    val englishSource: String,
    val lastFetched: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
) {
    fun toDomainModel(): DailyStory {
        return DailyStory(
            title = title,
            description = description,
            ref = ref,
            hebrewText = hebrewText,
            englishText = englishText,
            englishSource = englishSource,
            isFavorite = isFavorite
        )
    }
}