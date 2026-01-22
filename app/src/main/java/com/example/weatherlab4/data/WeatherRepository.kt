package com.example.weatherlab4.data

import android.util.Log
import com.example.weatherlab4.data.db.FavDao
import com.example.weatherlab4.data.db.FavoriteCity
import com.example.weatherlab4.data.network.OpenMeteoApi
import com.example.weatherlab4.data.network.ForecastResponse
import com.example.weatherlab4.data.prefs.SettingsDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.math.*
import com.example.weatherlab4.data.network.NominatimApi
import com.example.weatherlab4.data.network.GeocodeResponse
import com.example.weatherlab4.data.network.GeocodeItem
import kotlinx.coroutines.flow.MutableStateFlow

@Singleton
class WeatherRepository @Inject constructor(
    @Named("forecastApi") private val forecastApi: OpenMeteoApi,
    @Named("geocodeApi") private val geocodeApi: OpenMeteoApi,
    @Named("nominatimApi") private val nominatimApi: NominatimApi,
    private val favDao: FavDao,
    private val settings: SettingsDataStore,
    private val cache: LastLocationCache
) {
    suspend fun forecast(lat: Double, lon: Double): ForecastResponse =
        forecastApi.forecast(lat, lon)

    suspend fun geocode(query: String) = geocodeApi.geocode(query)
    suspend fun reverse(lat: Double, lon: Double, lang: String = "ru"): GeocodeResponse {
        return try {
            val r = nominatimApi.reverse(lat, lon, lang = lang)

            val name = r.address?.city
                ?: r.address?.town
                ?: r.address?.village
                ?: r.display_name
                ?: "Unknown"

            GeocodeResponse(
                results = listOf(
                    GeocodeItem(
                        id = null,
                        name = name,
                        latitude = lat,
                        longitude = lon,
                        country = r.address?.country,
                        admin1 = r.address?.state,
                        timezone = null
                    )
                )
            )
        } catch (t: Throwable) {
            Log.e("WeatherRepo", "reverse error for $lat,$lon", t)
            GeocodeResponse(results = emptyList())
        }
    }

    fun favorites(): Flow<List<FavoriteCity>> = favDao.favorites()
    suspend fun addFavorite(city: FavoriteCity) = favDao.insert(city)
    suspend fun removeFavorite(city: FavoriteCity) = favDao.delete(city)

    val settingsFlow = settings.settingsFlow

    suspend fun saveLastLocation(lat: Double, lon: Double) = cache.save(lat, lon)
    suspend fun lastLocation(): Pair<Double, Double>? = cache.read()

    suspend fun needsUpdateForShift(newLat: Double, newLon: Double): Boolean {
        val old = cache.read() ?: return true
        return haversineKm(old.first, old.second, newLat, newLon) > 5.0
    }

    private fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat/2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon/2).pow(2.0)
        return 2 * R * asin(min(1.0, sqrt(a)))
    }



}