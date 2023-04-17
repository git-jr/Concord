package com.alura.concord

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.alura.concord.navigation.ConcordNavHost
import com.alura.concord.ui.theme.ConcordTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        filesDir.listFiles()?.forEach {
//            Log.i("files", "onCreate interno: ${it.name}")
//        }
//
//        getExternalFilesDir("Stickers")?.listFiles()?.forEach {
//            Log.i("files", "onCreate externo: ${it.name}")
//        }

        setContent {
            ConcordTheme {
                val navController = rememberNavController()
                ConcordNavHost(navController = navController)
            }
        }
    }
}


