package com.example.proyectofinal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.data.Note
import com.example.proyectofinal.data.NoteDatabase
import com.example.proyectofinal.data.NoteRepository
import com.example.proyectofinal.data.ReminderRepository
import com.example.proyectofinal.data.Reminder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import com.example.proyectofinal.NotificationWorker

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository
    private val reminderRepository: ReminderRepository  // Aquí añadimos el ReminderRepository
    val allNotes: LiveData<List<Note>>

    init {
        val noteDao = NoteDatabase.getDatabase(application).noteDao()
        val reminderDao = NoteDatabase.getDatabase(application)
            .reminderDao()  // Asegúrate de que existe el dao para Reminder
        repository = NoteRepository(noteDao)
        reminderRepository =
            ReminderRepository(reminderDao)  // Inicializamos el repository de recordatorios
        allNotes = repository.allNotes
    }

    //AÑADIR NOTAS
    fun insert(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        val insertedNoteId = repository.insert(note)
    }

    //AÑADIR RECORDATORIO
    fun insertReminder(reminder: Reminder) {
        CoroutineScope(Dispatchers.IO).launch {
            reminderRepository.insert(reminder)
        }
    }

    fun update(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(note)
    }

    fun delete(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(note)
        val reminders = reminderRepository.getRemindersForNote(note.id).value
        reminders?.forEach { reminder ->
            reminderRepository.delete(reminder)
        }
    }

    fun getNoteById(id: Int): LiveData<Note> {
        return repository.getNoteById(id)
    }

    fun getRemindersForNote(noteId: Int): LiveData<List<Reminder>> {
        return reminderRepository.getRemindersForNote(noteId)
    }

    fun getAllReminders(): List<Reminder> {
        return reminderRepository.getAllReminders()
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            reminderRepository.delete(reminder)
        }
    }

    // Actualizar recordatorio
    fun updateReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            reminderRepository.update(reminder)
        }
    }
}