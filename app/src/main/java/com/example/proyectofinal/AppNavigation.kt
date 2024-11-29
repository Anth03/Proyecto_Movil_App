package com.example.proyectofinal

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinal.viewmodel.NoteViewModel
import androidx.compose.runtime.livedata.observeAsState
import com.example.proyectofinal.ui.NoteDetailScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    viewModel: NoteViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = "noteList", modifier = modifier) {
        composable("noteList") {
            NoteListScreen(navController = navController, viewModel = viewModel)
        }

        // Aquí cambiamos la ruta para que reciba un noteId
        composable("noteDetail/{noteId}") { backStackEntry ->
            // Obtenemos el noteId del argumento
            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()

            // Ahora puedes pasarle el noteId a la pantalla de detalle
            if (noteId != null) {
                NoteDetailScreen(navController = navController, viewModel = viewModel)
            }
        }

        composable("editNote/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
            val note = noteId?.let { viewModel.getNoteById(it).observeAsState().value }
            if (note != null) {
                NoteEditScreen(navController = navController, viewModel = viewModel, note = note)
            }
        }
    }
}
