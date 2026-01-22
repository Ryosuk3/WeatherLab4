package com.example.weatherlab4.di

import android.content.Context
import androidx.room.Room
import com.example.weatherlab4.data.db.AppDb
import com.example.weatherlab4.data.db.FavDao
import com.example.weatherlab4.data.prefs.SettingsDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {
    @Provides @Singleton fun db(@ApplicationContext c: Context): AppDb =
        Room.databaseBuilder(c, AppDb::class.java, "weather.db").build()

    @Provides fun favDao(db: AppDb): FavDao = db.favDao()

    @Provides @Singleton fun settings(@ApplicationContext c: Context) = SettingsDataStore(c)
}