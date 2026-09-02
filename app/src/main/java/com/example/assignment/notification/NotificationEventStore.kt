package com.example.assignment.notification

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.notificationDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "notification_events"
)

class NotificationEventStore(
    private val context: Context
) {

    companion object {

        private val SHOWN_EVENTS = stringSetPreferencesKey(
            "shown_notification_events"
        )
    }

    suspend fun hasBeenShown(
        eventId: String
    ): Boolean {

        val preferences = context.notificationDataStore.data.first()

        val events = preferences[SHOWN_EVENTS] ?: emptySet()

        return eventId in events
    }

    suspend fun markAsShown(
        eventId: String
    ) {

        context.notificationDataStore.edit { preferences ->

            val current = preferences[SHOWN_EVENTS] ?: emptySet()

            preferences[SHOWN_EVENTS] = current + eventId
        }
    }
}