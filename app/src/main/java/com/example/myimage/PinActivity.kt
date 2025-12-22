package com.example.myimage

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class PinActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private var isPinVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        prefs = getSharedPreferences("MY_IMAGE_PREF", MODE_PRIVATE)

        val etPin = findViewById<EditText>(R.id.etPin)
        val btnUnlock = findViewById<Button>(R.id.btnUnlock)
        val btnChangePin = findViewById<Button>(R.id.btnChangePin)
        val btnTogglePin = findViewById<ImageView>(R.id.btnTogglePin)

        // 👁️ SHOW / HIDE PIN
        btnTogglePin.setOnClickListener {
            isPinVisible = !isPinVisible

            if (isPinVisible) {
                etPin.transformationMethod = null   // show PIN
                btnTogglePin.setImageResource(R.drawable.eye_visible)
            } else {
                etPin.transformationMethod =
                    android.text.method.PasswordTransformationMethod.getInstance()
                btnTogglePin.setImageResource(R.drawable.eye_invisible)
            }

            etPin.setSelection(etPin.text.length)
        }


        // 🔐 UNLOCK / SET PIN
        btnUnlock.setOnClickListener {
            val pin = etPin.text.toString().trim()
            val savedPin = prefs.getString("APP_PIN", null)

            if (pin.length != 4) {
                toast("Enter 4 digit PIN")
                return@setOnClickListener
            }

            if (savedPin == null) {
                prefs.edit().putString("APP_PIN", pin).apply()
                toast("PIN Set Successfully 💖")
                openMain()
            } else if (savedPin == pin) {
                openMain()
            } else {
                toast("Wrong PIN 💔")
                etPin.text.clear()
            }
        }

        // 🔒 CHANGE PIN
        btnChangePin.setOnClickListener {
            startActivity(Intent(this, ChangePinActivity::class.java))
        }
    }

    private fun openMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
