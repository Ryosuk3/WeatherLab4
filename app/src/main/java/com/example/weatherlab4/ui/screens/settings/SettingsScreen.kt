package com.example.weatherlab4.ui.screens.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.weatherlab4.R
import com.example.weatherlab4.data.prefs.SettingsDataStore
import com.example.weatherlab4.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val store: SettingsDataStore
) : ViewModel() {
    val settings = store.settingsFlow
    suspend fun save(s: Settings) = store.save(s)

    fun changeLanguage(lang: String) = viewModelScope.launch {
        save(settings.first().copy(language = lang))
        val appLocale = LocaleListCompat.forLanguageTags(lang)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    nav: NavController,
    vm: SettingsViewModel = hiltViewModel()
) {
    val s by vm.settings.collectAsState(initial = Settings())
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = { nav.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(id = R.string.settings_back))
                    }
                }
            )
        }
    ) { inner ->

        LazyColumn(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // температура
            item { Text(stringResource(id = R.string.settings_temperature), style = MaterialTheme.typography.titleMedium) }
            item {
                SegmentedEnum(
                    items = TempUnit.values(),
                    selected = s.temp,
                    label = { it.name },
                ) { sel ->
                    scope.launch { vm.save(s.copy(temp = sel)) }
                }
            }

            // ветер
            item { Text(stringResource(id = R.string.settings_wind_speed), style = MaterialTheme.typography.titleMedium) }
            item {
                SegmentedEnum(
                    items = WindUnit.values(),
                    selected = s.wind,
                    label = { it.name }
                ) { sel ->
                    scope.launch { vm.save(s.copy(wind = sel)) }
                }
            }

            // давление
            item { Text(stringResource(id = R.string.settings_pressure), style = MaterialTheme.typography.titleMedium) }
            item {
                SegmentedEnum(
                    items = PressureUnit.values(),
                    selected = s.pressure,
                    label = { it.name }
                ) { sel ->
                    scope.launch { vm.save(s.copy(pressure = sel)) }
                }
            }

            // язык
            item { Text(stringResource(id = R.string.settings_language), style = MaterialTheme.typography.titleMedium) }
            item {
                Segmented(
                    options = listOf("ru", "en"),
                    selected = s.language
                ) { sel ->
                    vm.changeLanguage(sel)
                }
            }

            // частота обновления
            item { Text(stringResource(id = R.string.settings_update_frequency), style = MaterialTheme.typography.titleMedium) }
            item {
                val actualRefreshMinutes = s.refreshMinutes
                var sliderValue by remember { mutableIntStateOf(actualRefreshMinutes) }

                LaunchedEffect(actualRefreshMinutes) {
                    sliderValue = actualRefreshMinutes
                }

                Column {
                    Slider(
                        value = sliderValue.toFloat(),
                        onValueChange = { sliderValue = it.toInt() },
                        onValueChangeFinished = {
                            scope.launch {
                                vm.save(s.copy(refreshMinutes = sliderValue))
                            }
                        },
                        valueRange = 1f..91f,
                        steps = 18
                    )
                    Text(stringResource(id = R.string.settings_update_frequency_current, sliderValue))
                }
            }

            // gps
            item { Text(stringResource(id = R.string.settings_gps_accuracy), style = MaterialTheme.typography.titleMedium) }
            item {
                Segmented(
                    options = listOf(stringResource(id = R.string.settings_gps_accuracy_high), stringResource(id = R.string.settings_gps_accuracy_balanced)),
                    selected = if (s.gpsAccuracyHigh) stringResource(id = R.string.settings_gps_accuracy_high) else stringResource(id = R.string.settings_gps_accuracy_balanced)
                ) { sel ->
                    scope.launch { vm.save(s.copy(gpsAccuracyHigh = sel == context.getString(R.string.settings_gps_accuracy_high))) }
                }
            }

            // таймаут
            item {
                Column {
                    val actualTimeout = s.locationTimeoutSec
                    var timeout by remember { mutableIntStateOf(actualTimeout) }

                    LaunchedEffect(actualTimeout) {
                        timeout = actualTimeout
                    }
                    Text(stringResource(id = R.string.settings_gps_timeout, timeout), style = MaterialTheme.typography.titleMedium)

                    Slider(
                        value = timeout.toFloat(),
                        onValueChange = { timeout = it.toInt() },
                        onValueChangeFinished = {
                            scope.launch { vm.save(s.copy(locationTimeoutSec = timeout)) }
                        },
                        valueRange = 5f..60f,
                        steps = 10
                    )
                }
            }
        }
    }
}

@Composable
private fun Segmented(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { opt ->
            FilterChip(
                selected = opt == selected,
                onClick = { onSelect(opt) },
                label = { Text(opt) }
            )
        }
    }
}

@Composable
private fun <T : Enum<T>> SegmentedEnum(
    items: Array<T>,
    selected: T,
    label: (T) -> String,
    onSelect: (T) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { item ->
            FilterChip(
                selected = item == selected,
                onClick = { onSelect(item) },
                label = { Text(label(item)) }
            )
        }
    }
}
