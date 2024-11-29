package com.example.proyectofinal

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.proyectofinal.data.NoteDatabase
import com.example.proyectofinal.data.Reminder
import com.example.proyectofinal.data.Note
import com.example.proyectofinal.data.NoteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Obtener las instancias de los DAOs
            val reminderDao = NoteDatabase.getDatabase(applicationContext).reminderDao()
            val noteDao = NoteDatabase.getDatabase(applicationContext).noteDao()

            // Obtener todos los recordatorios
            val allReminders = reminderDao.getAllReminders()  // Esto devuelve una lista de recordatorios

            // Iterar sobre todos los recordatorios
            for (reminder in allReminders) {
                // Obtener la nota asociada a este recordatorio
                val note = getNoteById(noteDao, reminder.noteId)
                if (note != null) {
                    // Crear y mostrar la notificación
                    createNotification(reminder, note)
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    // Función suspendida para obtener la nota de la base de datos
    private suspend fun getNoteById(noteDao: NoteDao, noteId: Int): Note? {
        return withContext(Dispatchers.IO) {
            // Acceder al valor de LiveData de manera síncrona
            val liveDataNote = noteDao.getNoteById(noteId)
            // Retornar el valor directo de LiveData
            liveDataNote.value
        }
    }

    private suspend fun createNotification(reminder: Reminder, note: Note) {
        // Crear la notificación
        val notification = NotificationCompat.Builder(applicationContext, "your_channel_id")
            .setSmallIcon(R.drawable.ic_notification)  // Reemplaza con tu ícono adecuado
            .setContentTitle("Recordatorio: ${note.title}")  // Toma el título de la tabla `notes`
            .setContentText("${note.description}\n${reminder.dueDate} ${reminder.dueTime}")  // Toma la descripción de la tabla `notes`
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        // Mostrar la notificación
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.notify(reminder.id, notification)
    }
}
