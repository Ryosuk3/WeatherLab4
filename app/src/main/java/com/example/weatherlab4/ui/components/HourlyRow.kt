package com.example.weatherlab4.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class HourlyUi(val time: String, val temp: String, val pop: Int)

@Composable
fun HourlyRow(items: List<HourlyUi>, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text("Почасовой (24ч)", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
            items(items) { h ->
                ElevatedCard(
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .widthIn(min = 88.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(h.time, style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.height(6.dp))
                        Text(h.temp, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(2.dp))
                        Text("Осадки ${h.pop}%", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}