package com.example.proyectofinal

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.proyectofinal.viewmodel.NoteViewModel
import com.example.proyectofinal.data.Note
import com.example.proyectofinal.data.Reminder
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(navController: NavHostController, viewModel: NoteViewModel, note: Note) {
    var title by remember { mutableStateOf(note.title) }
    var description by remember { mutableStateOf(note.description) }
    var classification by remember { mutableStateOf(note.classification) }
    var isCompleted by remember { mutableStateOf(note.isCompleted) }
    var dueDate by remember { mutableStateOf("") }
    var dueTime by remember { mutableStateOf("") }

    // Mantén una variable para el recordatorio seleccionado
    var selectedReminder by remember { mutableStateOf<Reminder?>(null) }

    // Para manejar la edición y eliminación de recordatorios
    val reminders = viewModel.getRemindersForNote(note.id).observeAsState(emptyList())

    // Para agregar o actualizar un recordatorio
    val saveReminder = {
        if (selectedReminder != null) {
            // Si hay un recordatorio seleccionado, actualízalo
            val updatedReminder = selectedReminder!!.copy(
                dueDate = dueDate,
                dueTime = dueTime
            )
            viewModel.updateReminder(updatedReminder) // Actualiza el recordatorio
        } else {
            // Si no hay recordatorio seleccionado, agrega uno nuevo
            val newReminder = Reminder(
                noteId = note.id,
                dueDate = dueDate,
                dueTime = dueTime
            )
            viewModel.insertReminder(newReminder) // Inserta un nuevo recordatorio
        }
        // Resetea los campos después de guardar
        dueDate = ""
        dueTime = ""
        selectedReminder = null // Limpiar el recordatorio seleccionado
    }

    // Para eliminar un recordatorio
    val deleteReminder = { reminder: Reminder ->
        viewModel.deleteReminder(reminder) // Elimina el recordatorio de la base de datos
    }

    // Para editar un recordatorio
    val editReminder = { reminder: Reminder ->
        // Setea el recordatorio como el seleccionado y actualiza los campos
        selectedReminder = reminder
        dueDate = reminder.dueDate
        dueTime = reminder.dueTime
    }

    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                actions = {
                    Text(
                        text = "Guardar",
                        color = Color(0xff649562),
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable {
                                // Guardar la nota actualizada en la base de datos
                                val updatedNote = note.copy(
                                    title = title,
                                    description = description,
                                    classification = classification,
                                    isCompleted = isCompleted
                                )
                                CoroutineScope(Dispatchers.IO).launch {
                                    viewModel.update(updatedNote) // Actualizar la nota
                                    withContext(Dispatchers.Main) {
                                        // Una vez que la nota esté actualizada, volvemos a la pantalla anterior
                                        navController.navigateUp()
                                    }
                                }
                            },
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    Text(
                        text = "Cancelar",
                        color = Color.Gray,
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable { navController.navigateUp() },
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) { innerPadding ->
        // Quitar verticalScroll aquí
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Campos de texto para título y descripción
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Nombre") },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 4.dp)
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 4.dp)
            )

            // Campos adicionales solo para tareas
            if (classification == "TASK") {
                Spacer(modifier = Modifier.height(16.dp))

                // Fecha
                Button(onClick = {
                    val calendar = Calendar.getInstance()
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            calendar.set(year, month, dayOfMonth)
                            dueDate = dateFormat.format(calendar.time)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Text(text = if (dueDate.isEmpty()) "Fecha" else "Fecha: $dueDate")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hora
                Button(onClick = {
                    val calendar = Calendar.getInstance()
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            calendar.set(Calendar.MINUTE, minute)
                            dueTime = timeFormat.format(calendar.time)
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                }) {
                    Text(text = if (dueTime.isEmpty()) "Hora" else "Hora: $dueTime")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Agregar o actualizar recordatorio
                Button(onClick = saveReminder) {
                    Text(text = "Guardar Recordatorio")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar los recordatorios existentes
                Text(text = "Recordatorios", fontWeight = FontWeight.Bold)
                LazyColumn {
                    items(reminders.value) { reminder ->
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Text("Fecha: ${reminder.dueDate} Hora: ${reminder.dueTime}")
                            Row {
                                // Botón para editar
                                Button(onClick = { editReminder(reminder) }) {
                                    Text("Editar")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                // Botón para eliminar
                                Button(onClick = { deleteReminder(reminder) }) {
                                    Text("Eliminar")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = { isCompleted = it }
                )
                Text(text = "Marcar como completada", modifier = Modifier.padding(start = 8.dp))
            }

            // Botón para eliminar la nota/tarea
            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.delete(note) // Eliminar la nota de la base de datos
                        withContext(Dispatchers.Main) {
                            navController.navigateUp() // Regresar a la pantalla anterior
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "Eliminar", color = Color.White)
            }
        }
    }
}
