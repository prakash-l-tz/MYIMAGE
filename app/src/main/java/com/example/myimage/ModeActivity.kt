package com.example.myimage

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import com.google.android.material.appbar.MaterialToolbar

class ModeActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mode)

        prefs = getSharedPreferences("theme_prefs", MODE_PRIVATE)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbars)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val cardAuto = findViewById<CardView>(R.id.cardAuto)
        val cardLight = findViewById<CardView>(R.id.cardLight)
        val cardDark = findViewById<CardView>(R.id.cardDark)

        val radioAuto = findViewById<RadioButton>(R.id.radioAuto)
        val radioLight = findViewById<RadioButton>(R.id.radioLight)
        val radioDark = findViewById<RadioButton>(R.id.radioDark)

        // 🔹 Restore saved mode
        when (prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> selectMode(radioAuto, radioLight, radioDark)
            AppCompatDelegate.MODE_NIGHT_NO -> selectMode(radioLight, radioAuto, radioDark)
            AppCompatDelegate.MODE_NIGHT_YES -> selectMode(radioDark, radioAuto, radioLight)
        }

        // 🔹 AUTO
        cardAuto.setOnClickListener {
            saveAndApplyMode(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
                radioAuto, radioLight, radioDark
            )
        }

        // 🔹 LIGHT
        cardLight.setOnClickListener {
            saveAndApplyMode(
                AppCompatDelegate.MODE_NIGHT_NO,
                radioLight, radioAuto, radioDark
            )
        }

        // 🔹 DARK
        cardDark.setOnClickListener {
            saveAndApplyMode(
                AppCompatDelegate.MODE_NIGHT_YES,
                radioDark, radioAuto, radioLight
            )
        }
    }

    private fun saveAndApplyMode(
        mode: Int,
        selected: RadioButton,
        vararg others: RadioButton
    ) {
        selectMode(selected, *others)

        prefs.edit().putInt("theme_mode", mode).apply()

        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun selectMode(selected: RadioButton, vararg others: RadioButton) {
        selected.isChecked = true
        others.forEach { it.isChecked = false }
    }
}
