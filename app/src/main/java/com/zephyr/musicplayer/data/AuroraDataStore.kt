package com.zephyr.musicplayer.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * Single, app-wide DataStore delegate. All repositories MUST use this — declaring a
 * second `Context.dataStore` delegate with the same name in another file crashes the app
 * with "There are multiple DataStores active for the same file".
 */
internal val Context.auroraDataStore: androidx.datastore.core.DataStore<Preferences>
    by preferencesDataStore(name = "aurora_settings")
