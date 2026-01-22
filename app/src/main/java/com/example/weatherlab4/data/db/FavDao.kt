package com.example.weatherlab4.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavDao {
    @Query("SELECT * FROM favorite_cities ORDER BY id DESC LIMIT 10")
    fun favorites(): Flow<List<FavoriteCity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(city: FavoriteCity)

    @Delete
    suspend fun delete(city: FavoriteCity)
}