package com.example.assignment.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferencesManager(private val context: Context) {

    companion object {
        private val KEY_USER_ROLE = stringPreferencesKey("user_role")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
    }

    suspend fun saveUserData(role: String, userId: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_ROLE] = role
            prefs[KEY_USER_ID] = userId
        }
    }

    fun getUserRoleFlow(): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[KEY_USER_ROLE]
        }
    }

    suspend fun clear() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}