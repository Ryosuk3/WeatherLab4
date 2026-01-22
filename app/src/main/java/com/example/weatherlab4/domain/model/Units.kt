package com.example.weatherlab4.domain.model


enum class TempUnit { C, F }
enum class WindUnit { MS, KMH, MPH }
enum class PressureUnit { MMHG, HPA, INHG }

data class Settings(
    val temp: TempUnit = TempUnit.C,
    val wind: WindUnit = WindUnit.MS,
    val pressure: PressureUnit = PressureUnit.HPA,
    val language: String = "ru",
    val refreshMinutes: Int = 60,
    val gpsAccuracyHigh: Boolean = true,
    val locationTimeoutSec: Int = 10
)