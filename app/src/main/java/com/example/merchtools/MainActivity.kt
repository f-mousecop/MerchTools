package com.example.merchtools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.merchtools.ui.theme.MerchToolsTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for MerchTools application
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MerchToolsTheme {
                MerchToolsApp()
            }
        }
    }
}