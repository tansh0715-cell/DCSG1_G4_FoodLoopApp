package com.example.assignment.data.supabase

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.ktor.client.engine.cio.CIO

val supabase = createSupabaseClient (
    supabaseUrl = "https://wjwqgmfebufideenvmdh.supabase.co",
    supabaseKey = "sb_publishable_O1HHTeS7ekjk5slHixWqHg_A4NZVL0k"
){
    httpEngine = CIO.create()

    install(Auth) {
        scheme = "com.example.assignment"
        host = "auth-callback"

    }
    install(Postgrest)
    install(Storage)
}