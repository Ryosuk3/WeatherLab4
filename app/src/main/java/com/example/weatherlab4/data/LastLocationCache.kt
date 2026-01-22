package com.example.weatherlab4.data

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.store by preferencesDataStore("last_location")

@Singleton
class LastLocationCache @Inject constructor(@ApplicationContext private val ctx: Context) {
    private val K_LAT = doublePreferencesKey("lat")
    private val K_LON = doublePreferencesKey("lon")

    suspend fun save(lat: Double, lon: Double) {
        ctx.store.edit { it[K_LAT] = lat; it[K_LON] = lon }
    }
    suspend fun read(): Pair<Double, Double>? {
        val d = ctx.store.data.map { it[K_LAT] to it[K_LON] }.first()
        return if (d.first != null && d.second != null) Pair(d.first!!, d.second!!) else null
    }
}