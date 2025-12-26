package com.example.myapplication.data.repository

import android.content.Context
import androidx.room.Room
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.model.CachedStory
import com.example.myapplication.data.model.DailyStory
import com.example.myapplication.data.model.StoryMetadata
import com.example.myapplication.data.network.ApiService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.Locale

class ContentRepository(
    private val context: Context,
    private val api: ApiService
) {

    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "daily-talmud-db"
    )
        .fallbackToDestructiveMigration()
        .build()

    private val contentDao = db.contentDao()

    private val metadataList: List<StoryMetadata> by lazy {
        val jsonString = context.assets.open("stories.json").bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<StoryMetadata>>() {}.type
        Gson().fromJson(jsonString, listType)
    }

    val favorites: Flow<List<DailyStory>> = contentDao.getFavorites().map { list ->
        val isSystemHebrew = Locale.getDefault().language == "he"
        list.map { it.toDomainModel(isSystemHebrew) }
    }

    suspend fun getDailyStory(dayOffset: Int, isSystemHebrew: Boolean): DailyStory {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, dayOffset)
        val targetDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        val safeIndex = Math.floorMod(targetDayOfYear, metadataList.size)
        val metadata = metadataList[safeIndex]

        val targetRef = if (isSystemHebrew) {
            "Steinsaltz on ${metadata.ref}"
        } else {
            metadata.ref
        }

        val cached = contentDao.getStory(targetRef)
        if (cached != null) {
            return cached.toDomainModel(isSystemHebrew)
        }

        var hebrewText: List<String> = emptyList()
        var englishText: List<String> = emptyList()
        var sourceTitle = "Sefaria"
        var heRefDisplay = ""

        if (isSystemHebrew) {
            val response = api.getText(targetRef, version = null)
            heRefDisplay = response.heRef ?: ""
            hebrewText = response.versions.find { it.language == "he" }?.text ?: emptyList()
        } else {
            val hebrewResponse = api.getText(targetRef, version = null)
            heRefDisplay = hebrewResponse.heRef ?: ""
            hebrewText = hebrewResponse.versions.find { it.language == "he" }?.text ?: emptyList()

            try {
                val englishResponse = api.getText(targetRef, version = "english")
                val bestEnglishVersion = englishResponse.versions
                    .filter { it.language == "en" }
                    .maxByOrNull { it.text.size }

                englishText = bestEnglishVersion?.text ?: emptyList()
                if (bestEnglishVersion != null) {
                    sourceTitle = bestEnglishVersion.versionTitle
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val newStory = CachedStory(
            ref = targetRef,
            titleEn = metadata.titleEn,
            titleHe = metadata.titleHe,
            descriptionEn = metadata.descEn,
            descriptionHe = metadata.descHe,
            heRef = heRefDisplay,
            hebrewText = hebrewText,
            englishText = englishText,
            englishSource = sourceTitle,
            isFavorite = false
        )
        contentDao.insertStory(newStory)

        return newStory.toDomainModel(isSystemHebrew)
    }

    suspend fun toggleFavorite(story: DailyStory) {
        contentDao.updateFavoriteStatus(story.ref, !story.isFavorite)
    }
}