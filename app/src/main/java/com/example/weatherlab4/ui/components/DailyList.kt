package com.example.weatherlab4.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.weatherlab4.R


data class DailyUi(
    val date: String,
    val tmin: String,
    val tmax: String,
    val popMax: Int,
    val code: Int
)

@Composable
fun DailyList(
    items: List<DailyUi>,
    dateFormatter: (String) -> String
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { d ->
            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = weatherIconForCode(d.code),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )

                    Spacer(Modifier.width(12.dp))

                    Column(Modifier.weight(1f)) {
                        Text(dateFormatter(d.date), style = MaterialTheme.typography.titleMedium)
                        Text("${stringResource(R.string.daily_precipitation)}: ${d.popMax}%")
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(d.tmax, style = MaterialTheme.typography.titleMedium)
                        Text(d.tmin, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}