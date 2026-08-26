package com.example.assignment.data.supabase

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

val supabase = createSupabaseClient (
    supabaseUrl = "https://wjwqgmfebufideenvmdh.supabase.co",
    supabaseKey = "sb_publishable_O1HHTeS7ekjk5slHixWqHg_A4NZVL0k"
){
    install(Auth) {
        scheme = "com.example.assignment"
        host = "login-callback"
    }
    install(Postgrest)
}