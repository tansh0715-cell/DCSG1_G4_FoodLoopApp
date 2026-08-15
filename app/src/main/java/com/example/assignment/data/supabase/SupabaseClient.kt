package com.example.assignment.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.gotrue.GoTrue
import io.ktor.websocket.WebSocketDeflateExtension.Companion.install

val supabase: SupabaseClient = createSupabaseClient(
    supabaseUrl = "https://wjwqgmfebufideenvmdh.supabase.co",
    supabaseKey = "sb_publishable_O1HHTeS7ekjk5slHixWqHg_A4NZVL0k"
){
    install(Postgrest)
    install(GoTrue)
}