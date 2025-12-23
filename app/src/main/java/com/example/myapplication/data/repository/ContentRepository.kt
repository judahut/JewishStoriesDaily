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

class ContentRepository(private val context: Context, private val api: ApiService) {

    private val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "talmud-daily-db"
    )
        .fallbackToDestructiveMigration()
        .build()

    private val dao = db.contentDao()

    val favorites: Flow<List<DailyStory>> = dao.getFavorites()
        .map { list ->
            list.map { it.toDomainModel() }
        }

    suspend fun toggleFavorite(story: DailyStory) {
        dao.updateFavoriteStatus(story.ref, !story.isFavorite)
    }

    private fun getPlaylist(): List<StoryMetadata> {
        val jsonString = context.assets.open("stories.json")
            .bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<StoryMetadata>>() {}.type
        return Gson().fromJson(jsonString, listType)
    }

    suspend fun getDailyStory(dayOffset: Int = 0): DailyStory {
        val playlist = getPlaylist()

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, dayOffset)
        val targetDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        val index = Math.floorMod(targetDayOfYear, playlist.size)
        val targetMetadata = playlist[index]

        val cached = dao.getStory(targetMetadata.ref)
        if (cached != null) {
            return cached.toDomainModel()
        }

        val hebrewResponse = api.getText(targetMetadata.ref)
        val hebrewVersion = hebrewResponse.versions.firstOrNull()
            ?: throw Exception("No Hebrew text found")

        val englishResponse = api.getText(targetMetadata.ref, "english")
        val englishVersion = englishResponse.versions.firstOrNull()
            ?: throw Exception("No English text found")

        val newStoryEntity = CachedStory(
            ref = targetMetadata.ref,
            title = targetMetadata.title,
            description = targetMetadata.desc,
            hebrewText = hebrewVersion.text,
            englishText = englishVersion.text,
            englishSource = englishVersion.versionTitle,
            lastFetched = System.currentTimeMillis()
        )

        dao.insertStory(newStoryEntity)

        return newStoryEntity.toDomainModel()
    }
}