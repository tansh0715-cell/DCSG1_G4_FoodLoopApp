package com.example.assignment

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.assignment.screen.RegisterTypeScreen
import com.example.assignment.ui.theme.AssignmentTheme

class RegisterType : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AssignmentTheme {
                RegisterTypeScreen(
                    onBackClick = { finish() },
                    onSelectSaver = { startActivity(Intent(this, RegisterSaver::class.java)) },
                    onSelectProvider = { startActivity(Intent(this, RegisterProvider::class.java)) }
                )
            }
        }
    }
}

