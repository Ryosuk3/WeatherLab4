package com.example.weatherlab4.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherlab4.data.WeatherRepository
import com.example.weatherlab4.location.LocationClient
import com.example.weatherlab4.domain.model.Settings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val loading: Boolean = false,
    val cityTitle: String = "—",
    val isGps: Boolean = false,
    val error: String? = null,

    val currentTemp: Double? = null,
    val currentCode: Int = 0,
    val currentDesc: String = "",
    val humidity: Int? = null,
    val wind: Double? = null,
    val pressure: Double? = null,
    val visibility: Double? = null,
    val sunrise: String = "—",
    val sunset: String = "—",
    val hourly: List<HourItem> = emptyList(),
    val daily: List<DayItem> = emptyList(),

    val settings: Settings = Settings()
)


data class HourItem(val time: String, val temp: Double, val pop: Int, val code: Int)
data class DayItem(
    val date: String,
    val tmin: Double,
    val tmax: Double,
    val popMax: Int,
    val sunrise: String,
    val sunset: String,
    val code: Int
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: WeatherRepository,
    private val loc: LocationClient
) : ViewModel() {

    private val _state = MutableStateFlow(UiState(loading = true))
    val state: StateFlow<UiState> = _state.asStateFlow()

    private var periodicUpdateJob: Job? = null

    init {
        viewModelScope.launch {
            repo.settingsFlow.collect { settings ->
                _state.update { it.copy(settings = settings) }
                startPeriodicUpdates(settings)
            }
        }

        viewModelScope.launch { refreshFromGps(initial = true) }
    }

    private fun startPeriodicUpdates(settings: Settings) {
        periodicUpdateJob?.cancel()
        val refreshIntervalMinutes = settings.refreshMinutes
        if (refreshIntervalMinutes > 0) {
            periodicUpdateJob = viewModelScope.launch {
                while (true) {
                    delay(refreshIntervalMinutes * 60 * 1000L)
                    if (_state.value.isGps) {
                        refreshFromGps()
                    }
                }
            }
        }
    }

    fun refreshFromGps(initial: Boolean = false) = viewModelScope.launch {
        _state.update { it.copy(loading = true, error = null) }
        val s = repo.settingsFlow.first()
        val opt = LocationClient.Options(highAccuracy = s.gpsAccuracyHigh, timeoutSec = s.locationTimeoutSec)
        val location = loc.getCurrentLocation(opt)

        if (location == null) {
            val lastLoc = repo.lastLocation()
            if (lastLoc != null) {
                loadForCoords(lastLoc.first, lastLoc.second, isGps = false)
                _state.update { it.copy(error = "Не удалось обновить GPS. Показаны последние данные.") }
            } else {
                _state.update { it.copy(loading=false, error="Не удалось определить локацию. Разрешите доступ или попробуйте поиск.") }
            }
            return@launch
        }

        val lastLocation = repo.lastLocation()
        if (!initial && lastLocation != null) {
            val distance = calculateDistance(
                lat1 = lastLocation.first,
                lon1 = lastLocation.second,
                lat2 = location.latitude,
                lon2 = location.longitude
            )
            if (distance < 5) {
                _state.update { it.copy(loading = false) }
                return@launch
            }
        }

        loadForCoords(location.latitude, location.longitude, isGps = true)
    }

    fun loadForCoords(lat: Double, lon: Double, isGps: Boolean) = viewModelScope.launch {
        _state.update { it.copy(loading = true, error = null) }
        try {
            val settings = repo.settingsFlow.first()
            val f = repo.forecast(lat, lon)
            val geo = repo.reverse(lat, lon, lang = "ru")

            val name = geo.results
                ?.firstOrNull()
                ?.let { item ->
                    listOfNotNull(item.name, item.admin1, item.country).joinToString(", ")
                }
                ?: "%.3f, %.3f".format(lat, lon)

            repo.saveLastLocation(lat, lon)

            val hourly = f.hourly.time.indices.take(24).map { i ->
                HourItem(
                    time = f.hourly.time[i].substring(11,16),
                    temp = f.hourly.temp[i],
                    pop = f.hourly.pop[i],
                    code = f.hourly.wcode[i]
                )
            }
            val daily = f.daily.time.indices.take(7).map { i ->
                DayItem(
                    date = f.daily.time[i],
                    tmin = f.daily.tmin[i],
                    tmax = f.daily.tmax[i],
                    popMax = f.daily.popMax[i],
                    sunrise = f.daily.sunrise[i].substring(11,16),
                    sunset = f.daily.sunset[i].substring(11,16),
                    code = f.daily.wcode[i]
                )
            }

            _state.update {
                it.copy(
                    loading = false,
                    cityTitle = name,
                    isGps = isGps,
                    error = null,
                    settings = settings,
                    currentTemp = f.current.temperature,
                    currentDesc = codeToText(f.current.wcode),
                    currentCode = f.current.wcode,
                    humidity = f.current.humidity,
                    wind = f.current.windSpeed,
                    pressure = f.current.pressure,
                    visibility = f.current.visibility / 1000.0,
                    sunrise = daily.firstOrNull()?.sunrise ?: "—",
                    sunset = daily.firstOrNull()?.sunset ?: "—",
                    hourly = hourly,
                    daily = daily
                )
            }
        } catch (e: Exception) {
            _state.update { it.copy(loading=false, error = e.message ?: "Ошибка загрузки") }
        }
    }

    fun setManualLocation(lat: Double, lon: Double) {
        Log.d("HomeVM", "setManualLocation called: $lat, $lon")
        loadForCoords(lat, lon, isGps = false)
    }

    private fun codeToText(code: Int): String = when (code) {
        0 -> "Ясно"; 1,2 -> "Переменная облачность"; 3 -> "Пасмурно"
        45,48 -> "Туман"; 51,53,55 -> "Морось"; 61,63,65 -> "Дождь"; 71,73,75 -> "Снег"
        80,81,82 -> "Ливень"; 95,96,99 -> "Гроза"; else -> "Погода"
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371
        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }
}
