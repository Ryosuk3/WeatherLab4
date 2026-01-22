package com.example.weatherlab4.ui

import android.content.Context
import com.example.weatherlab4.R
import com.example.weatherlab4.domain.model.PressureUnit
import com.example.weatherlab4.domain.model.Settings
import com.example.weatherlab4.domain.model.TempUnit
import com.example.weatherlab4.domain.model.WindUnit
import java.util.Locale
import kotlin.math.roundToInt

object UnitFormatter {

    fun formatTemperature(tempC: Double?, settings: Settings): String {
        if (tempC == null) return "--°"
        val convertedTemp = when (settings.temp) {
            TempUnit.C -> tempC
            TempUnit.F -> tempC * 9 / 5 + 32
        }
        return "${convertedTemp.roundToInt()}°"
    }

    fun formatWind(windMs: Double?, settings: Settings, context: Context): String {
        if (windMs == null) return "--"
        val (value, unitNameRes) = when (settings.wind) {
            WindUnit.MS -> windMs to R.string.unit_wind_ms
            WindUnit.KMH -> windMs * 3.6 to R.string.unit_wind_kmh
            WindUnit.MPH -> windMs * 2.237 to R.string.unit_wind_mph
        }
        return String.format(Locale.US, "%.1f %s", value, context.getString(unitNameRes))
    }

    fun formatPressure(pressureHpa: Double?, settings: Settings, context: Context): String {
        if (pressureHpa == null) return "--"
        val (value, unitNameRes) = when (settings.pressure) {
            PressureUnit.HPA -> pressureHpa.toDouble() to R.string.unit_pressure_hpa
            PressureUnit.MMHG -> pressureHpa * 0.750062 to R.string.unit_pressure_mmhg
            PressureUnit.INHG -> pressureHpa * 0.02953 to R.string.unit_pressure_inhg
        }
        val format = if (settings.pressure == PressureUnit.HPA) "%.0f" else "%.2f"
        return String.format(Locale.US, "$format %s", value, context.getString(unitNameRes))
    }
}