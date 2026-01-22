package com.example.weatherlab4.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.store by preferencesDataStore("search_history")

@Singleton
class SearchHistoryStore @Inject constructor(@ApplicationContext private val ctx: Context) {
    private val KEY = stringPreferencesKey("items")
    suspend fun add(q: String) {
        val cur = list().toMutableList()
        cur.remove(q)
        cur.add(0, q)
        while (cur.size > 5) cur.removeLast()
        ctx.store.edit { it[KEY] = cur.joinToString("|") }
    }
    suspend fun clear() { ctx.store.edit { it.remove(KEY) } }
    suspend fun list(): List<String> = ctx.store.data.map { it[KEY].orEmpty() }
        .first().split("|").filter { it.isNotBlank() }
}