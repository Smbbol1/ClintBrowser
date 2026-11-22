package com.jhaiian.clint.settings.main

import android.content.SharedPreferences
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Restore
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.preference.PreferenceManager
import com.jhaiian.clint.settings.common.SettingsRow
import com.jhaiian.clint.settings.common.SettingsScreenScaffold
import com.jhaiian.clint.settings.common.SettingsSection
import com.jhaiian.clint.settings.common.SettingsSectionLabel
import com.jhaiian.clint.ui.ClintSwitch
import com.jhaiian.clint.ui.theme.LocalClintColors

@Composable
fun CustomSettingsScreen() {
    val context = LocalContext.current
    val prefs = remember { PreferenceManager.getDefaultSharedPreferences(context) }

    SettingsScreenScaffold {
        SettingsSectionLabel("Sesión y navegación")
        SettingsSection(LocalClintColors.current.cardBackground) {
            CustomFeatureToggle(
                prefs = prefs,
                prefKey = "restore_session_enabled",
                defaultValue = true,
                icon = Icons.Filled.Restore,
                title = "Recordar conversación al volver",
                summary = "Carga la última URL visitada (ej. Gemini) al abrir el navegador."
            )
        }
    }
}

@Composable
private fun CustomFeatureToggle(
    prefs: SharedPreferences,
    prefKey: String,
    defaultValue: Boolean = false,
    icon: ImageVector = Icons.Filled.Build,
    title: String,
    summary: String
) {
    val colors = LocalClintColors.current
    var isEnabled by remember {
        mutableStateOf(prefs.getBoolean(prefKey, defaultValue))
    }

    SettingsRow(
        icon = icon,
        title = title,
        summary = summary,
        colors = colors,
        onClick = {
            val newValue = !isEnabled
            isEnabled = newValue
            prefs.edit().putBoolean(prefKey, newValue).apply()
        },
        trailing = {
            ClintSwitch(checked = isEnabled)
        }
    )
}
