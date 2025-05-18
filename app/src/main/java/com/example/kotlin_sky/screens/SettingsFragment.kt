package com.example.kotlin_sky.screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.kotlin_sky.R

class SettingsFragment : Fragment() {

    private lateinit var switchUnit: Switch
    private lateinit var switchTheme: Switch

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        switchUnit = view.findViewById(R.id.switchUnit)
        switchTheme = view.findViewById(R.id.switchTheme)

        val prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)

        // Applique les valeurs enregistrées
        switchUnit.isChecked = prefs.getBoolean("isFahrenheit", false)
        switchTheme.isChecked = prefs.getBoolean("darkMode", false)

        // Gestion du switch des unités
        switchUnit.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("isFahrenheit", isChecked).apply()
        }

        // Gestion du switch de thème
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("darkMode", isChecked).apply()

            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )

            requireActivity().recreate()
        }

        return view
    }
}
