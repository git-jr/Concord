package com.alura.concord

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.compose.rememberNavController
import com.alura.concord.extensions.showMessage
import com.alura.concord.navigation.ConcordNavHost
import com.alura.concord.ui.theme.ConcordTheme
import com.google.android.gms.common.ConnectionResult.SERVICE_MISSING
import com.google.android.gms.common.ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED
import com.google.android.gms.common.ConnectionResult.SUCCESS
import com.google.android.gms.common.GoogleApiAvailability
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val servicesAvailable =
            GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)

        when (servicesAvailable) {
            SUCCESS -> {
                showMessage("Google Play Services Disponivel")
                Log.i("Services", "Disponivel")

            }

            SERVICE_MISSING -> {
                showMessage("Google Play Services NÃO Disponivel")
                Log.i("Services", "NÃO")

            }

            SERVICE_VERSION_UPDATE_REQUIRED -> {
                showMessage("Atualizar Google Play Services")
                Log.i("Services", "Atualizar")
            }
        }


//        filesDir.listFiles()?.forEach {
//            Log.i("files", "onCreate interno: ${it.name}")
//        }
//
//        getExternalFilesDir(null)?.listFiles()?.forEach {
//            Log.i("files", "onCreate externo: ${it.name}")
//            Log.i("files", "onCreate externo: ${it.freeSpace}")
//        }
//
//        getExternalFilesDir(null)?.listFiles()?.forEach {
//            Log.i("files", "onCreate externo: ${it.name}")
//            Log.i("files", "onCreate externo: ${it.freeSpace}")
//        }


//        getExternalStorageDirectory()?.listFiles()?.forEach {
//            Log.i("files", "Arquivos: ${it.path}")
//        }

//        getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)?.listFiles()?.forEach {
//            System.out.println("Downloads: ${it.path}")
//            Log.i("files", "Downloads: ${it.path}")
//        }

        setContent {
            ConcordTheme {
                val navController = rememberNavController()
                ConcordNavHost(navController = navController)
            }
        }
    }
}


