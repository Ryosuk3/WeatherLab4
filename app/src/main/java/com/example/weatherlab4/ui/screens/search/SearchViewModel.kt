package com.example.weatherlab4.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherlab4.data.WeatherRepository
import com.example.weatherlab4.data.db.FavoriteCity
import com.example.weatherlab4.data.prefs.SearchHistoryStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchState(
    val query: String = "",
    val suggestions: List<Suggestion> = emptyList(),
    val history: List<String> = emptyList()
)
data class Suggestion(val title: String, val lat: Double, val lon: Double, val country: String?)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: WeatherRepository,
    private val history: SearchHistoryStore
) : ViewModel() {
    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    init { viewModelScope.launch { _state.update { it.copy(history = history.list()) } } }

    fun onQuery(q: String) {
        _state.update { it.copy(query = q) }
        viewModelScope.launch {
            if (q.length >= 2) {
                val r = repo.geocode(q)
                _state.update { it.copy(suggestions = r.results.orEmpty().map {
                    Suggestion("${it.name}${it.admin1?.let{a->", $a"}?:""}${it.country?.let{c->", $c"}?:""}",
                        it.latitude, it.longitude, it.country)
                }) }
            } else _state.update { it.copy(suggestions = emptyList()) }
        }
    }

    fun saveToFav(s: Suggestion) = viewModelScope.launch {
        repo.addFavorite(FavoriteCity(name = s.title, lat = s.lat, lon = s.lon, country = s.country))
    }

    fun commitQuery(q: String) = viewModelScope.launch { history.add(q) }
    fun clearHistory() = viewModelScope.launch { history.clear(); _state.update { it.copy(history = emptyList()) } }
}