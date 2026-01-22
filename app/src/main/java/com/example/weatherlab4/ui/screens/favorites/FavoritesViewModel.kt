package com.example.weatherlab4.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherlab4.data.WeatherRepository
import com.example.weatherlab4.data.db.FavoriteCity
import com.example.weatherlab4.ui.UnitFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class FavoriteWithTemp(
    val id: Long,
    val name: String,
    val lat: Double,
    val lon: Double,
    val temp: String? = null,
    val loading: Boolean = false
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repo: WeatherRepository
) : ViewModel() {

    private val _ui = MutableStateFlow<List<FavoriteWithTemp>>(emptyList())
    val ui = _ui.asStateFlow()

    init {
        viewModelScope.launch {
            repo.favorites().combine(repo.settingsFlow) { favs, settings ->
                _ui.value = favs.map {
                    FavoriteWithTemp(
                        id = it.id,
                        name = it.name,
                        lat = it.lat,
                        lon = it.lon,
                        loading = true
                    )
                }

                favs.forEach { city ->
                    viewModelScope.launch {
                        try {
                            val forecast = repo.forecast(city.lat, city.lon)
                            val temp = UnitFormatter.formatTemperature(forecast.current.temperature, settings)

                            _ui.update { list ->
                                list.map {
                                    if (it.id == city.id)
                                        it.copy(temp = temp, loading = false)
                                    else it
                                }
                            }
                        } catch (e: Exception) {
                            _ui.update { list ->
                                list.map {
                                    if (it.id == city.id)
                                        it.copy(temp = "—", loading = false)
                                    else it
                                }
                            }
                        }
                    }
                }
            }.collect {}
        }
    }

    fun remove(city: FavoriteCity) = viewModelScope.launch {
        repo.removeFavorite(city)
    }
}
