package com.alura.concord.database.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.alura.concord.database.preferences.PreferencesKey.RECENT_IMAGES
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStoreFiles: DataStore<Preferences> by preferencesDataStore(name = "files")

object PreferencesKey {
    val RECENT_IMAGES = stringPreferencesKey("recent_images")
}



