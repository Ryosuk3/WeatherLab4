package com.example.weatherlab4.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CurrentWeatherCard(
    temp: String,
    desc: String,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(temp, style = MaterialTheme.typography.displaySmall)
            Spacer(Modifier.height(4.dp))
            Text(desc, style = MaterialTheme.typography.titleMedium)
        }
    }
}