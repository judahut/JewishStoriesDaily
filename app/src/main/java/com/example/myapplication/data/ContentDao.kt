package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.data.model.CachedStory
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDao {

    @Query("SELECT * FROM daily_stories WHERE ref = :ref LIMIT 1")
    suspend fun getStory(ref: String): CachedStory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: CachedStory)

    @Query("SELECT * FROM daily_stories WHERE isFavorite = 1")
    fun getFavorites(): Flow<List<CachedStory>>

    @Query("UPDATE daily_stories SET isFavorite = :isFavorite WHERE ref = :ref")
    suspend fun updateFavoriteStatus(ref: String, isFavorite: Boolean)
}