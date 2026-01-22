package com.example.weatherlab4.ui.screens.home

import android.Manifest
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.weatherlab4.R
import com.example.weatherlab4.ui.UnitFormatter
import com.example.weatherlab4.ui.components.DailyList
import com.example.weatherlab4.ui.components.DailyUi
import com.example.weatherlab4.ui.components.WeatherTopBar
import com.example.weatherlab4.ui.components.weatherIconForCode
import com.example.weatherlab4.ui.navigation.Routes
import com.example.weatherlab4.ui.util.formatDateForLocale
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun HomeScreen(nav: NavController, vm: HomeViewModel = hiltViewModel()) {
    val st by vm.state.collectAsState()
    val lang = LocalConfiguration.current.locales[0]?.language ?: "en"
    val context = LocalContext.current
    val perms = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val settings = st.settings

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if (perms.allPermissionsGranted) vm.refreshFromGps()
                else perms.launchMultiplePermissionRequest()
            }) { Icon(Icons.Default.GpsFixed, contentDescription = stringResource(R.string.home_my_location)) }
        },
        topBar = {
            WeatherTopBar(
                title = st.cityTitle,
                isGps = st.isGps,
                onSearchClick = { nav.navigate(Routes.SEARCH) },
                onGpsClick = {
                    if (perms.allPermissionsGranted) vm.refreshFromGps()
                    else perms.launchMultiplePermissionRequest()
                }
            )
        }
    ) { inner ->
        val scroll = rememberScrollState()
        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .verticalScroll(scroll)
                .imePadding()
                .navigationBarsPadding()
                .padding(16.dp)
                .animateContentSize()
        ) {
            if (st.loading) { LinearProgressIndicator(Modifier.fillMaxWidth()) }
            st.error?.let { Text(stringResource(R.string.home_error, it), color = MaterialTheme.colorScheme.error) }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = weatherIconForCode(st.currentCode),
                    contentDescription = st.currentDesc,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        UnitFormatter.formatTemperature(st.currentTemp, settings),
                        style = MaterialTheme.typography.displayMedium
                    )
                    Text(st.currentDesc, style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(Modifier.height(8.dp))
            Text("${stringResource(R.string.home_humidity)}: ${st.humidity?.toString() ?: "—"}% • ${stringResource(R.string.home_wind)}: ${UnitFormatter.formatWind(st.wind, settings, context)}")
            Text("${stringResource(R.string.home_pressure)}: ${UnitFormatter.formatPressure(st.pressure, settings, context)} • ${stringResource(R.string.home_visibility)}: ${st.visibility?.let { stringResource(R.string.home_visibility_value).format(it) } ?: "—"}")
            Text("${stringResource(R.string.home_sunrise)}: ${st.sunrise} • ${stringResource(R.string.home_sunset)}: ${st.sunset}")

            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.home_hourly_forecast), style = MaterialTheme.typography.titleMedium)
            LazyRow(contentPadding = PaddingValues(vertical = 8.dp)) {
                items(st.hourly) { h ->
                    ElevatedCard(Modifier.padding(end = 12.dp)) {
                        Column(
                            Modifier
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                .animateItemPlacement(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = weatherIconForCode(h.code),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Column {
                                    Text(h.time, style = MaterialTheme.typography.labelLarge)
                                    Text(
                                        UnitFormatter.formatTemperature(h.temp, settings),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text("${stringResource(R.string.home_precipitation)} ${h.pop}%")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            val dailyUi = remember(st.daily, settings) { 
                st.daily.map { d ->
                    DailyUi(
                        date = d.date,
                        tmin = UnitFormatter.formatTemperature(d.tmin, settings),
                        tmax = UnitFormatter.formatTemperature(d.tmax, settings),
                        popMax = d.popMax,
                        code = d.code
                    )
                }
            }

            DailyList(
                items = dailyUi,
                dateFormatter = { iso -> formatDateForLocale(iso, lang) }
            )

            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { nav.navigate(Routes.FAVORITES) }) { Text(stringResource(R.string.home_favorite_cities)) }
                Button(onClick = { nav.navigate(Routes.SETTINGS) }) { Text(stringResource(R.string.home_settings)) }
            }
            Spacer(Modifier.height(96.dp))
        }
    }
}
