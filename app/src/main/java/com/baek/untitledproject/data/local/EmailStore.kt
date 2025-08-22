package com.baek.untitledproject.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.baek.untitledproject.data.local.model.AuthCache
import com.baek.untitledproject.ui.login.AuthEntry
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject


private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class EmailStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val EMAIL = stringPreferencesKey("last_email")
        val ENTRY = stringPreferencesKey("last_auth_entry")
        val SAVED_AT = longPreferencesKey("saved_at")

    }

    val flow: Flow<AuthCache> = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }
        .map { pref ->
            AuthCache(
                email = pref[Keys.EMAIL],
                entry = pref[Keys.ENTRY]?.let { runCatching { AuthEntry.valueOf(it) }.getOrNull() },
                savedAt = pref[Keys.SAVED_AT] ?: 0L
            )
        }

    suspend fun save(email: String, entry: AuthEntry) {
        context.dataStore.edit { prefs ->
            prefs[Keys.EMAIL] = email
            prefs[Keys.ENTRY] = entry.name
            prefs[Keys.SAVED_AT] = System.currentTimeMillis()
        }
    }

    suspend fun get(): AuthCache = flow.first()

    suspend fun clearAuth() {
        context.dataStore.edit { prefs ->
            prefs.remove(Keys.EMAIL)
            prefs.remove(Keys.ENTRY)
            prefs.remove(Keys.SAVED_AT)
        }
    }
}