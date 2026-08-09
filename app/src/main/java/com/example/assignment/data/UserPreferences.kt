package com.example.assignment.data

import android.content.Context
import android.content.SharedPreferences
import com.example.assignment.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object UserPreferences {
    private const val PREFS_NAME = "UserPrefs"
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        if (!::prefs.isInitialized) {
            prefs =context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    private fun getPrefs(): SharedPreferences {
        if (!::prefs.isInitialized) {
            throw IllegalStateException("SharedPreferences not initialized. Call UserPreferences.init(context) first.")
        }
        return prefs
    }

    fun saveUser(user: User) {
        with(getPrefs().edit()){
            putString("password_${user.email}", user.password)
            putString("type_${user.email}", user.type)
            putString("name_${user.email}", user.name)
            putString("phone_${user.email}", user.phone)
            user.restaurant?.let { putString("restaurant_${user.email}", it) }
            user.address?.let { putString("address_${user.email}", it) }
            apply()
        }
    }

    fun saveLicense(email: String, licenseUri: String) {
        getPrefs().edit().putString("license_$email", licenseUri).apply()
    }

    fun getUser(email: String): User? {
        val password = getPrefs().getString("password_$email", null) ?: return null
        val type = getPrefs().getString("type_$email", "FoodSaver") ?: "FoodSaver"
        val name = getPrefs().getString("name_$email", "") ?: ""
        val phone = getPrefs().getString("phone_$email", "") ?: ""
        val restaurant = getPrefs().getString("restaurant_$email", null)
        val address = getPrefs().getString("address_$email", null)
        return User( name = name, email = email, password = password, type = type, phone = phone, restaurant = restaurant, address = address)
    }

    fun checkUserExists(email: String): Boolean {
        return getPrefs().getString("password_$email", null) != null
    }

    fun getUserType(email: String): String {
        return getPrefs().getString("type_$email", "FoodSaver") ?: "FoodSaver"
    }

    fun getPassword(email: String): String {
        return getPrefs().getString("password_$email", "") ?: ""
    }

    fun  clear() {
        getPrefs().edit().clear().apply()
    }

    suspend fun saveUserSuspend(user: User) = withContext(Dispatchers.IO){
        saveUser(user)
    }

    suspend fun getUserSuspend(email: String): User? = withContext(Dispatchers.IO){
        getUser(email)
    }

    suspend fun checkUserExistsSuspend(email: String): Boolean = withContext(Dispatchers.IO){
        checkUserExists(email)
    }
}
