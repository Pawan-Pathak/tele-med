package com.telemed.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.telemed.demo.di.AppViewModelFactory
import com.telemed.demo.navigation.AppNavHost
import com.telemed.demo.ui.theme.TeleMedTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as TeleMedApp
        val factory = AppViewModelFactory(app.container)

        setContent {
            TeleMedTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(factory = factory)
                }
            }
        }
    }
}

