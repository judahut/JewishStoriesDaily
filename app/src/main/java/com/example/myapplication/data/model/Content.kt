package com.example.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_stories")
data class CachedStory(
    @PrimaryKey val ref: String,

    val titleEn: String,
    val titleHe: String?,
    val descriptionEn: String,
    val descriptionHe: String?,

    val heRef: String?, // <--- Added this column

    val hebrewText: List<String>,
    val englishText: List<String>,
    val englishSource: String,
    val lastFetched: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
) {
    fun toDomainModel(isSystemHebrew: Boolean): DailyStory {
        return DailyStory(
            title = if (isSystemHebrew && !titleHe.isNullOrEmpty()) titleHe else titleEn,
            description = if (isSystemHebrew && !descriptionHe.isNullOrEmpty()) descriptionHe else descriptionEn,

            ref = ref,
            heRef = heRef, // <--- Pass it to domain

            hebrewText = hebrewText,
            englishText = englishText,
            englishSource = englishSource,
            isFavorite = isFavorite
        )
    }
}