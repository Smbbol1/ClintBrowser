package com.jhaiian.clint.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.jhaiian.clint.settings.datasaver.DataSaverMode
import com.jhaiian.clint.util.LocaleHelper
import com.jhaiian.clint.ui.theme.SystemDarkState
import com.jhaiian.clint.util.loadMeasurementSystemPreference
import java.lang.ref.WeakReference

class ClintApplication : Application() {

    private var _currentActivity: WeakReference<Activity>? = null
    val currentActivity: Activity? get() = _currentActivity?.get()

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.wrapContext(base))
    }

        override fun onCreate() {
        super.onCreate()
        
                                // --- NUEVO: Arranque asíncrono del WebView (Prepara Chromium en segundo plano) ---
        if (androidx.webkit.WebViewFeature.isFeatureSupported(androidx.webkit.WebViewFeature.START_SAFE_BROWSING)) {
            // 1. Creamos la configuración con un hilo de fondo para las tareas de inicio
            val config = androidx.webkit.WebViewStartUpConfig.Builder(
                java.util.concurrent.Executors.newSingleThreadExecutor()
            ).build()
            
            // 2. Iniciamos el WebView de forma asíncrona
            androidx.webkit.WebViewCompat.startUpWebView(
                this,
                config,
                object : androidx.webkit.WebViewOutcomeReceiver<
                    androidx.webkit.WebViewStartUpResult, 
                    androidx.webkit.WebViewStartupException
                > {
                    override fun onResult(result: androidx.webkit.WebViewStartUpResult) {
                        // El WebView se ha inicializado correctamente en segundo plano
                    }
                    override fun onError(error: androidx.webkit.WebViewStartupException) {
                        // Error al inicializar (no crítico, el WebView se inicializará cuando se use)
                    }
                }
            )
        }
        // ----------------------------------------------------------------------------
        
        applyNightMode()
        SystemDarkState.install(this)
        loadMeasurementSystemPreference(this)
        DataSaverMode.register(this)
        com.jhaiian.clint.privacy.PrivacyGuard.install(this)
        com.jhaiian.clint.downloads.ClintDownloadManager.init(this)
        com.jhaiian.clint.quiver.QuiverGuardAutoUpdater.schedule(this)
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {
                _currentActivity = WeakReference(activity)
            }
            override fun onActivityPaused(activity: Activity) {
                if (_currentActivity?.get() === activity) _currentActivity = null
            }
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {
                if (_currentActivity?.get() === activity) _currentActivity = null
            }
        })
    }

    fun applyNightMode() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val theme = prefs.getString("app_theme", "system") ?: "system"
        AppCompatDelegate.setDefaultNightMode(
            when (theme) {
                "dark" -> AppCompatDelegate.MODE_NIGHT_YES
                "light" -> AppCompatDelegate.MODE_NIGHT_NO
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }
}
