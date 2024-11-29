package com.example.proyectofinal

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.res.stringResource

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar(
        modifier = Modifier.height(54.dp),
        containerColor = Color(0xFF333333),
        contentColor = Color.White
    ) {
        NavigationBarItem(
            icon = { Spacer(modifier = Modifier.size(0.dp)) },
            label = { Text(stringResource(id = R.string.anadir), color = Color.White) },
            selected = navController.currentBackStackEntry?.destination?.route == "noteDetail",
            onClick = {
                navController.navigate("noteDetail") {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationBarItem(
            icon = { Spacer(modifier = Modifier.size(0.dp)) },
            label = { Text(stringResource(id = R.string.notas), color = Color.White) },
            selected = navController.currentBackStackEntry?.destination?.route == "noteList",
            onClick = {
                navController.navigate("noteList") {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}
