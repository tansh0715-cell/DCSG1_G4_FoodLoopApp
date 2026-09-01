package com.example.assignment.notification

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class StoredNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val ownerId: String,
    val role: String
)

class NotificationHistoryStore(
    private val context: Context
) {

    companion object {

        private const val PREF_NAME =
            "notification_history"

        private const val KEY_NOTIFICATIONS =
            "notifications"

        private const val MAX_NOTIFICATIONS = 50
    }

    private val preferences =
        context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

    fun saveNotification(
        notification: StoredNotification
    ) {

        val current =
            getNotifications().toMutableList()

        // Prevent duplicate notification
        // for the same user.
        if (
            current.any {
                it.id == notification.id &&
                        it.ownerId == notification.ownerId
            }
        ) {
            return
        }

        current.add(
            0,
            notification
        )

        val jsonArray = JSONArray()

        current
            .take(MAX_NOTIFICATIONS)
            .forEach { item ->

                jsonArray.put(
                    JSONObject().apply {

                        put(
                            "id",
                            item.id
                        )

                        put(
                            "title",
                            item.title
                        )

                        put(
                            "message",
                            item.message
                        )

                        put(
                            "timestamp",
                            item.timestamp
                        )

                        put(
                            "ownerId",
                            item.ownerId
                        )

                        put(
                            "role",
                            item.role
                        )
                    }
                )
            }

        preferences
            .edit()
            .putString(
                KEY_NOTIFICATIONS,
                jsonArray.toString()
            )
            .apply()
    }

    fun getNotifications():
            List<StoredNotification> {

        val jsonString =
            preferences.getString(
                KEY_NOTIFICATIONS,
                null
            )
                ?: return emptyList()

        return try {

            val jsonArray =
                JSONArray(jsonString)

            buildList {

                for (
                index in
                0 until jsonArray.length()
                ) {

                    val json =
                        jsonArray.getJSONObject(
                            index
                        )

                    add(
                        StoredNotification(

                            id =
                                json.getString(
                                    "id"
                                ),

                            title =
                                json.getString(
                                    "title"
                                ),

                            message =
                                json.getString(
                                    "message"
                                ),

                            timestamp =
                                json.getLong(
                                    "timestamp"
                                ),

                            // optString keeps old
                            // notifications compatible
                            ownerId =
                                json.optString(
                                    "ownerId",
                                    ""
                                ),

                            role =
                                json.optString(
                                    "role",
                                    ""
                                )
                        )
                    )
                }
            }

        } catch (e: Exception) {

            emptyList()
        }
    }

    fun getNotificationsFor(
        ownerId: String,
        role: String
    ): List<StoredNotification> {

        return getNotifications()
            .filter {
                it.ownerId == ownerId &&
                        it.role == role
            }
    }

    fun clear() {

        preferences
            .edit()
            .remove(KEY_NOTIFICATIONS)
            .apply()
    }
}