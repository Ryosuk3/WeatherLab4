package com.example.weatherlab4.data.prefs

import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import android.content.Context
import com.example.weatherlab4.domain.model.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val KEY_TEMP = stringPreferencesKey("u_temp")
    private val KEY_WIND = stringPreferencesKey("u_wind")
    private val KEY_PRESS = stringPreferencesKey("u_press")
    private val KEY_LANG = stringPreferencesKey("lang")
    private val KEY_REFRESH = intPreferencesKey("refresh")
    private val KEY_GPS_HIGH = booleanPreferencesKey("gps_high")
    private val KEY_LOC_TIMEOUT = intPreferencesKey("loc_timeout")

    val settingsFlow = context.dataStore.data.map { p ->
        Settings(
            temp = TempUnit.valueOf(p[KEY_TEMP] ?: TempUnit.C.name),
            wind = WindUnit.valueOf(p[KEY_WIND] ?: WindUnit.MS.name),
            pressure = PressureUnit.valueOf(p[KEY_PRESS] ?: PressureUnit.HPA.name),
            language = p[KEY_LANG] ?: "ru",
            refreshMinutes = p[KEY_REFRESH] ?: 60,
            gpsAccuracyHigh = p[KEY_GPS_HIGH] ?: true,
            locationTimeoutSec = p[KEY_LOC_TIMEOUT] ?: 10
        )
    }

    suspend fun save(s: Settings) = context.dataStore.edit { e ->
        e[KEY_TEMP] = s.temp.name; e[KEY_WIND] = s.wind.name; e[KEY_PRESS] = s.pressure.name
        e[KEY_LANG] = s.language; e[KEY_REFRESH] = s.refreshMinutes
        e[KEY_GPS_HIGH] = s.gpsAccuracyHigh; e[KEY_LOC_TIMEOUT] = s.locationTimeoutSec
    }
}