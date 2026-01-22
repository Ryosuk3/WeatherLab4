package com.example.weatherlab4.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteCity::class], version = 1, exportSchema = false)
abstract class AppDb : RoomDatabase() {
    abstract fun favDao(): FavDao
}