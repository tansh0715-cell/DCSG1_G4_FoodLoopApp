package com.example.assignment.data.repository

import android.content.Context
import com.example.assignment.data.local.AppDatabase
import com.example.assignment.data.remote.SupabaseClient
import com.example.assignment.model.User

class UserRepository(private val context: Context) {
    private val userDao = AppDatabase.getInstance(context).userDao()

    suspend fun loginWithEmail(email: String, password: String): Result<User> {
        return try {
            val remoteUser = SupabaseClient.loginWithEmail(email, password)
            if (remoteUser != null) {
                userDao.insertUser(remoteUser)
                Result.success(remoteUser)
            } else {
                val localUser = userDao.getUser(email)
                if (localUser != null && localUser.password == password) {
                    Result.success(localUser)
                } else {
                    Result.failure(Exception("Invalid email or password"))
                }
            }
        } catch (e: Exception) {
            val localUser = userDao.getUser(email)
            if (localUser != null && localUser.password == password) {
                Result.success(localUser)
            } else {
                Result.failure(Exception("Invalid email or password"))
            }
        }
    }

    suspend fun registerUser(user: User): Result<Unit> {
        return try {
            val exists = SupabaseClient.checkUserExists(user.email)
            if (exists) {
                return Result.failure(Exception("Email already registered"))
            }
            val success = SupabaseClient.registerUser(user)
            if (success) {
                userDao.insertUser(user)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Registration failed"))
            }
        } catch (e: Exception) {
            userDao.insertUser(user)
            Result.success(Unit)
        }
    }

    suspend fun loginWithGoogle(user: User): Result<User> {
        return try {
            val localUser = userDao.getUser(user.email)
            if (localUser != null) {
                Result.success(localUser)
            } else {
                val exists = SupabaseClient.checkUserExists(user.email)
                if (exists) {
                    val remoteUser = SupabaseClient.getUserInfo(user.email)
                    if (remoteUser != null) {
                        userDao.insertUser(remoteUser)
                        Result.success(remoteUser)
                    } else {
                        Result.failure(Exception("Failed to fetch user info"))
                    }
                } else {
                    val success = SupabaseClient.registerUser(user)
                    if (success) {
                        userDao.insertUser(user)
                        Result.success(user)
                    } else {
                        Result.failure(Exception("Failed to register user"))
                    }
                }
            }
        } catch (e: Exception) {
            val localUser = userDao.getUser(user.email)
            if (localUser != null) {
                Result.success(localUser)
            } else {
                Result.failure(Exception("Network error and user not found locally"))
            }
        }
    }

    suspend fun getUser(email: String): User? = userDao.getUser(email)
}