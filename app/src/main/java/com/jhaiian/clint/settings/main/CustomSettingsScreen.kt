package com.jhaiian.clint.settings.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.preference.PreferenceManager

@Composable
fun CustomSettingsScreen() {
    val context = LocalContext.current
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    
    // Estado para el interruptor
    var isSessionRestoreEnabled by remember { 
        mutableStateOf(prefs.getBoolean("restore_session_enabled", true)) 
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Ajustes Personalizados",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // --- TU INTERRUPTOR ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Recordar conversación al volver",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Carga la última URL visitada (ej. Gemini) al abrir el navegador.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = isSessionRestoreEnabled,
                onCheckedChange = { isChecked ->
                    isSessionRestoreEnabled = isChecked
                    prefs.edit().putBoolean("restore_session_enabled", isChecked).apply()
                }
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
    }
}
