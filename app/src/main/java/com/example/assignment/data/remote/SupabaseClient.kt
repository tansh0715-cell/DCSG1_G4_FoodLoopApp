package com.example.assignment.data.remote

import com.example.assignment.model.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Provider
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.compose.auth.nativeLogin
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.ktor.client.engine.android.Android

object SupabaseClient {
    private const val SUPABASE_URL = "https://FoodLoopApp.supabase.co"
    private const val SUPABASE_ANON_KEY = "862058602146-1j48593cusehrkuv6iblto72fdfu2b1c.apps.googleusercontent.com"
    private const val GOOGLE_WEB_CLIENT_ID = "862058602146-8404s983uam9712eru2etn5p57cspi15.apps.googleusercontent.com"

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            install(Android)
            install(Postgrest)
            install(Auth)
            install(ComposeAuth) {
                googleNativeLogin(
                    serverClientId = GOOGLE_WEB_CLIENT_ID
                )
            }
        }
    }

    suspend fun loginWithEmail(email: String, password: String): User? {
        return try {
            client.auth.signInWith(email, password)
            val userData = client.postgrest["users"]
                .select()
                .eq("email", email)
                .decodeSingleOrNull<Map<String, Any>>()

            if (userData != null) {
                User(
                    email = userData["email"] as? String ?: email,
                    name = userData["name"] as? String ?: "",
                    password = password,
                    phone = userData["phone"] as? String ?: "",
                    type = userData["type"] as? String ?: "FoodSaver",
                    restaurant = userData["restaurant"] as? String,
                    address = userData["address"] as? String,
                    licenseUri = userData["licenseUri"] as? String
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun registerUser(user: User): Boolean {
        return try {
            client.postgrest["users"].insert(
                mapOf(
                    "email" to user.email,
                    "name" to user.name,
                    "password" to user.password,
                    "phone" to user.phone,
                    "type" to user.type,
                    "restaurant" to user.restaurant,
                    "address" to user.address,
                    "licenseUri" to user.licenseUri
                )
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun checkUserExists(email: String): Boolean {
        return try {
            val result = client.postgrest["users"]
                .select("email")
                .eq("email", email)
                .decodeList<Map<String, String>>()
            result.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserInfo(email: String): User? {
        return try {
            val userData = client.postgrest["users"]
                .select()
                .eq("email", email)
                .decodeSingleOrNull<Map<String, Any>>()

            if (userData != null) {
                User(
                    email = userData["email"] as? String ?: email,
                    name = userData["name"] as? String ?: "",
                    password = "",
                    phone = userData["phone"] as? String ?: "",
                    type = userData["type"] as? String ?: "FoodSaver",
                    restaurant = userData["restaurant"] as? String,
                    address = userData["address"] as? String,
                    licenseUri = userData["licenseUri"] as? String
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}