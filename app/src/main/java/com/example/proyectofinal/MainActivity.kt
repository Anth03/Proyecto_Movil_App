package com.example.proyectofinal

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.proyectofinal.ui.theme.ContactAppTheme
import com.example.proyectofinal.viewmodel.NoteViewModel
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val viewModel: NoteViewModel by viewModels()

    // Registrar el permiso de notificaciones
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (!isGranted) {
            // Manejar el caso en el que el usuario no otorgó el permiso
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDefaultLocale(this)

        // Crear el canal de notificación antes de que se envíen notificaciones
        createNotificationChannel()

        // Solicitar permiso de notificaciones si la versión es Android 13 o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Programar el worker para que se ejecute cada hora, después de que se haya dado el permiso.
        enqueueNotificationWorker()

        setContent {
            ContactAppTheme {
                MainContent(viewModel)
            }
        }
    }

    // Función para crear el canal de notificación
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "your_channel_id"
            val channelName = "Recordatorio"
            val importance = android.app.NotificationManager.IMPORTANCE_DEFAULT
            val channel = android.app.NotificationChannel(channelId, channelName, importance).apply {
                description = "Canal para mostrar recordatorios"
            }

            // Registrar el canal con el sistema
            val notificationManager: android.app.NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun enqueueNotificationWorker() {
        // Crear el PeriodicWorkRequest para ejecutar el NotificationWorker cada hora
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.HOURS)
            .build()

        // Encolar el trabajo en WorkManager
        WorkManager.getInstance(this).enqueue(workRequest)
    }

    private fun setDefaultLocale(context: Context) {
        val defaultLanguage = "es"
        val currentLanguage = Locale.getDefault().language

        if (currentLanguage != "es" && currentLanguage != "en") {
            val locale = Locale(defaultLanguage)
            Locale.setDefault(locale)
            val config = Configuration()
            config.setLocale(locale)
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }
    }
}

@Composable
fun MainContent(viewModel: NoteViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            viewModel = viewModel,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
