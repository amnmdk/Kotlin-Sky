package com.example.kotlin_sky.screens

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.kotlin_sky.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Appliquer le thème système (clair ou sombre)
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES

        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)

        window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN)

        //Forcer l’affichage de la status bar (icônes foncées si fond clair)
        if (!isDarkMode && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        //Demander permission notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
        }

        setContentView(R.layout.splash_screen)

        // Animation du logo
        val logo = findViewById<ImageView>(R.id.logoImage)
        val animation = AnimationUtils.loadAnimation(this, R.anim.logo_zoom)
        logo.startAnimation(animation)

        // Redirection vers l’écran principal après 2 secondes
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
}
