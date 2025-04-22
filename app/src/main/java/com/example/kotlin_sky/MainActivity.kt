package com.example.kotlin_sky

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView

class MainActivity : AppCompatActivity() {

    val villes = arrayOf("Paris", "Londres", "New York", "Tokyo", "Sydney")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinner: Spinner = findViewById(R.id.spinnerVilles)
        val resultat: TextView = findViewById(R.id.meteoResultat)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, villes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val ville = villes[position]
                val meteo = "Il fait beau à $ville ☀️"
                resultat.text = meteo
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                resultat.text = "Sélectionne une ville"
            }
        }
    }
}
