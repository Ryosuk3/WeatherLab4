package com.example.weatherlab4.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoApi {
    // Текущая, почасовая, дневная в одном запросе
    @GET("v1/forecast")
    suspend fun forecast(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current") current: String = "temperature_2m,weather_code,relative_humidity_2m,apparent_temperature,pressure_msl,wind_speed_10m,wind_direction_10m,visibility",
        @Query("hourly") hourly: String = "temperature_2m,precipitation_probability,weather_code",
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,sunrise,sunset,precipitation_probability_max,uv_index_max,weathercode",
        @Query("timezone") tz: String = "auto"
    ): ForecastResponse

    // Прямой геокодинг
    @GET("v1/search")
    suspend fun geocode(
        @Query("name") query: String,
        @Query("count") count: Int = 10,
        @Query("language") lang: String = "ru"
    ): GeocodeResponse

    // Обратный геокодинг
    @GET("v1/reverse")
    suspend fun reverse(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("language") lang: String = "ru"
    ): GeocodeResponse
}

@Serializable
data class GeocodeResponse(
    val results: List<GeocodeItem>? = null
)

@Serializable
data class GeocodeItem(
    val id: Int? = null,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String? = null,
    val admin1: String? = null,
    val timezone: String? = null
)

@Serializable
data class ForecastResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val current: CurrentBlock,
    val hourly: HourlyBlock,
    val daily: DailyBlock
)

@Serializable
data class CurrentBlock(
    @SerialName("time") val time: String,
    @SerialName("temperature_2m") val temperature: Double,
    @SerialName("apparent_temperature") val apparent: Double,
    @SerialName("weather_code") val wcode: Int,
    @SerialName("relative_humidity_2m") val humidity: Int,
    @SerialName("pressure_msl") val pressure: Double,
    @SerialName("wind_speed_10m") val windSpeed: Double,
    @SerialName("wind_direction_10m") val windDir: Int,
    @SerialName("visibility") val visibility: Double
)

@Serializable
data class HourlyBlock(
    val time: List<String>,
    @SerialName("temperature_2m") val temp: List<Double>,
    @SerialName("precipitation_probability") val pop: List<Int>,
    @SerialName("weather_code") val wcode: List<Int>
)

@Serializable
data class DailyBlock(
    val time: List<String>,
    @SerialName("temperature_2m_max") val tmax: List<Double>,
    @SerialName("temperature_2m_min") val tmin: List<Double>,
    val sunrise: List<String>,
    val sunset: List<String>,
    @SerialName("precipitation_probability_max") val popMax: List<Int>,
    @SerialName("uv_index_max") val uvMax: List<Double>,
    @SerialName("weathercode")
    val wcode: List<Int>
)