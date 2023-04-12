package com.alura.concord.database.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

object PreferencesKey {
    val LAST_OPEN_CHAT = longPreferencesKey("last_open_chat")
}



