package com.example.weatherlab4.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material.icons.outlined.Thunderstorm
import androidx.compose.material.icons.outlined.WbCloudy
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.example.weatherlab4.R

@Composable
fun weatherIconForCode(code: Int): Painter {
    return painterResource(
        when (code) {
            0 -> R.drawable.weather_sun
            1,2 -> R.drawable.weather_sun_cloud
            3 -> R.drawable.weather_cloud
            45,48 -> R.drawable.weather_fog
            51,53,55,56,57 -> R.drawable.weather_drizzle
            61,63,65,80,81,82 -> R.drawable.weather_rain
            71,73,75,77 -> R.drawable.weather_snow
            95,96,99 -> R.drawable.weather_storm
            else -> R.drawable.weather_cloud
        }
    )
}