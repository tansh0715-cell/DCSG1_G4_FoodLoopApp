package com.example.assignment.data.supabase

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email


class AuthService {
    suspend fun singUp(email: String, password: String, userData: Map<String, String>): Result<Unit> {
        return runCatching {
            supabase.auth.singUpWith(Email){
                this.email = email
                this.password = password
                this.data = userData
            }
        }
    }

    suspend fun singIn(email: String, password: String): Result<Unit> {
        return runCatching {
            supabase.auth.singInWith(Email){
                this.email = email
                this.password = password
            }
        }
    }

    suspend fun singOut(): Result<Unit> {
        return runCatching {
            supabase.auth.signOut()
        }
    }

    fun getCurrentUser() = supabase.auth.currentUser
}