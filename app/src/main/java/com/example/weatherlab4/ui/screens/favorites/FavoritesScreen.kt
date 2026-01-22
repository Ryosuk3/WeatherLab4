package com.example.weatherlab4.ui.screens.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.weatherlab4.R
import com.example.weatherlab4.data.db.FavoriteCity
import com.example.weatherlab4.ui.navigation.Routes
import com.example.weatherlab4.ui.screens.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    nav: NavController,
    vm: FavoritesViewModel = hiltViewModel(),
    homeVm: HomeViewModel = hiltViewModel(nav.getBackStackEntry(Routes.HOME))
) {
    val items by vm.ui.collectAsState()

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.favorites_title)) },
                navigationIcon = {
                    IconButton(onClick = { nav.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.favorites_back)
                        )
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
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(items) { city ->

                ElevatedCard(
                    onClick = {
                        homeVm.setManualLocation(city.lat, city.lon)
                        nav.popBackStack()
                    }
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        // ---------- Левая часть: Название ----------
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                city.name,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        // Центральная часть - температура
                        Column(
                            modifier = Modifier.padding(end = 12.dp),
                            horizontalAlignment = androidx.compose.ui.Alignment.End
                        ) {
                            if (city.loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    city.temp ?: "—",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }

                        // Правая часть - кнопка удалить
                        TextButton(
                            onClick = {
                                vm.remove(
                                    FavoriteCity(
                                        id = city.id,
                                        name = city.name,
                                        lat = city.lat,
                                        lon = city.lon
                                    )
                                )
                            }
                        ) {
                            Text(stringResource(R.string.favorites_delete))
                        }
                    }
                }
            }
        }
    }
}