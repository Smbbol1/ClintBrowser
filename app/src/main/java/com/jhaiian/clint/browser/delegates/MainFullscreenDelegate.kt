package com.jhaiian.clint.browser.delegates
import com.jhaiian.clint.browser.webview.*
import com.jhaiian.clint.browser.MainActivity

import android.view.View
import android.webkit.WebChromeClient
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

internal fun MainActivity.onShowCustomView(view: View, callback: WebChromeClient.CustomViewCallback) {
    if (fullscreenView != null) { callback.onCustomViewHidden(); return }
    fullscreenCallback = callback
    fullscreenView = view
    
    // --- INICIO DEL CAMBIO: Centrar el video en la pantalla vertical ---
    val params = android.widget.FrameLayout.LayoutParams(
        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
        android.view.ViewGroup.LayoutParams.WRAP_CONTENT // <-- Para que no se estire verticalmente
    )
    params.gravity = android.view.Gravity.CENTER // <-- Para que se centre en el medio de la pantalla
    fullscreenContainerView.addView(view, params)
    // --- FIN DEL CAMBIO ---

    uiState.isFullscreen = true
    val ctrl = androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)
    ctrl.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
    ctrl.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    applyFullscreenOrientation(view, retry = true)
}

private fun MainActivity.applyFullscreenOrientation(view: View, retry: Boolean) {
    // Forzamos la pantalla a quedarse en vertical (Portrait) sin importar el video.
    // Esto hará que el video se centre en la pantalla con bandas negras.
    requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
    
    // (Opcional) Si quieres que el usuario pueda girar el teléfono manualmente,
    // cambia SCREEN_ORIENTATION_PORTRAIT por SCREEN_ORIENTATION_USER_PORTRAIT
}

internal fun MainActivity.exitFullscreen() {
    fullscreenCallback?.onCustomViewHidden()
    fullscreenCallback = null
    fullscreenView?.let { fullscreenContainerView.removeView(it) }
    fullscreenView = null
    uiState.isFullscreen = false
    bottomBarAnimator2?.cancel()
    uiState.topBarFraction = 0f
    uiState.bottomBarFraction = 0f
    nestedScrollActive = false
    canvasTouchActive = false
    hasWebBottomNav = false
    updateMainContentInsets()
    swipeRefreshView.isEnabled = true
    val ctrl = androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)
    ctrl.show(WindowInsetsCompat.Type.navigationBars())
    applySystemUiVisibility()
    
    // --- NUEVO: Recuperar el foco del WebView para que vuelva a responder a los toques ---
    tabManager.activeTab?.webView?.let { webView ->
        webView.requestFocus()
        webView.onResume()
        webView.resumeTimers()
        // Forzamos un pequeño refresco para que la página vuelva a ser interactiva
        webView.postDelayed({
            webView.clearFocus()
            webView.requestFocus()
        }, 100)
    }
    // ---------------------------------------------------------------------------------

    window.decorView.post {
        requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        uiState.topBarFullHeightPx = 0
        uiState.bottomBarFullHeightPx = 0
    }
}

internal fun MainActivity.applySystemUiVisibility() {
    val hideStatusBar = prefs.getBoolean("hide_status_bar", false)
    val hideSystemNavigation = prefs.getBoolean("hide_system_navigation", false)
    uiState.hideStatusBar = hideStatusBar
    uiState.hideSystemNavigation = hideSystemNavigation
    val controller = androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)
    if (hideStatusBar) {
        controller.hide(WindowInsetsCompat.Type.statusBars())
    } else {
        controller.show(WindowInsetsCompat.Type.statusBars())
    }
    if (hideSystemNavigation) {
        controller.hide(WindowInsetsCompat.Type.navigationBars())
    } else {
        controller.show(WindowInsetsCompat.Type.navigationBars())
    }
}
