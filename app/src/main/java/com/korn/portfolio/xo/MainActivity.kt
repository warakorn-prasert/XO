package com.korn.portfolio.xo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.korn.portfolio.xo.ui.MainScreen
import com.korn.portfolio.xo.ui.theme.XOTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            XOTheme {
                Surface(Modifier.fillMaxSize()) {
                    MainScreen(Modifier.fillMaxSize())
                }
            }
        }
    }
}