package com.example.assignment.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.assignment.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUser(email: String): User?

    @Query("SELECT * FROM users WHERE email = :email")
    fun getUserFlow(email: String): Flow<User?>

    @Query("DELETE FROM users WHERE email = :email")
    suspend fun deleteUser(email: String)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}