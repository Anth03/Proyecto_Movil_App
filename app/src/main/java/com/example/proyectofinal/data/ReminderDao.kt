package com.example.proyectofinal.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ReminderDao {

    // Insertar un recordatorio
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(reminder: Reminder): Long

    // Actualizar un recordatorio
    @Update
    fun update(reminder: Reminder): Int

    // Eliminar un recordatorio
    @Delete
    fun delete(reminder: Reminder): Int

    // Obtener todos los recordatorios de una nota (relacionados por noteId)
    @Query("SELECT * FROM reminders WHERE noteId = :noteId ORDER BY dueDate ASC, dueTime ASC")
    fun getRemindersForNote(noteId: Int): LiveData<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: Int): Reminder?

    // Obtener todos los recordatorios
    @Query("SELECT * FROM reminders")
    fun getAllReminders(): List<Reminder>  // Este método es el que necesitas agregar
}
