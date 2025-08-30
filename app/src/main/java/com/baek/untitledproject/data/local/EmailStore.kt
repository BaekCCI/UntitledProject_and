package com.baek.untitledproject.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.baek.untitledproject.ui.login.AuthEntry
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject


private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class EmailStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val EMAIL = stringPreferencesKey("last_email")

    }

    suspend fun saveEmail(email: String) {
        context.dataStore.edit { pref ->
            pref[Keys.EMAIL] = email
        }
    }

    val flow: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[Keys.EMAIL] }

    suspend fun getEmail(): String? = flow.firstOrNull()

    suspend fun clear() {
        context.dataStore.edit { prefs ->
            prefs.remove(Keys.EMAIL)
        }
    }
}