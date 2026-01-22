package com.example.weatherlab4.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.GpsFixed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherTopBar(
    title: String,
    isGps: Boolean,
    onSearchClick: () -> Unit,
    onGpsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title + if (isGps) "  📍" else "",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Filled.Search, contentDescription = "Поиск")
            }
            IconButton(onClick = onGpsClick) {
                Icon(Icons.Filled.GpsFixed, contentDescription = "Моя локация")
            }
        },
        modifier = modifier
    )
}