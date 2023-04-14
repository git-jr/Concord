package com.alura.concord

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.alura.concord.navigation.ConcordNavHost
import com.alura.concord.ui.theme.ConcordTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ConcordTheme {
                val navController = rememberNavController()
                ConcordNavHost(navController = navController)
                LaunchedEffect(Unit) {
                    navController.addOnDestinationChangedListener { _, _, _ ->
                        val routes = navController.backQueue.map {
                            it.destination.route
                        }
                        Log.i("MainActivity", "onCreate: back stack - $routes")
                    }
                }
            }
        }
    }
}


