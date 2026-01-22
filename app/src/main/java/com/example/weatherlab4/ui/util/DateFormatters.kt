package com.example.weatherlab4.ui.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Форматирует ISO-дату (yyyy-MM-dd) в вид:
 *  - RU: "9 Ноября"
 *  - EN: "Nov 9"
 * Если парсинг не удался — возвращает исходную строку (первые 10 символов).
 */
fun formatDateForLocale(iso: String, langCode: String): String {
    val locale = when (langCode.lowercase(Locale.ROOT)) {
        "ru" -> Locale("ru")
        else -> Locale.ENGLISH
    }
    return runCatching {
        val date = LocalDate.parse(iso.take(10))
        val pattern = when (locale.language) {
            "ru" -> "d MMMM"
            else -> "MMM d"
        }
        val raw = date.format(DateTimeFormatter.ofPattern(pattern, locale))
        if (locale.language == "ru") raw.replaceFirstChar { it.titlecase(locale) } else raw
    }.getOrElse { iso.take(10) }
}