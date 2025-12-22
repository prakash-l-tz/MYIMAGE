package com.example.myimage

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class PinActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        prefs = getSharedPreferences("MY_IMAGE_PREF", MODE_PRIVATE)

        val etPin = findViewById<EditText>(R.id.etPin)
        val btnUnlock = findViewById<Button>(R.id.btnUnlock)
        val btnChangePin = findViewById<Button>(R.id.btnChangePin)
        val txtForgotPin = findViewById<TextView>(R.id.txtForgotPin)

        // 🔐 UNLOCK
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

        // 🔒 CHANGE PIN (Normal)
        btnChangePin.setOnClickListener {
            startActivity(Intent(this, ChangePinActivity::class.java))
        }

        // ❓ FORGOT PIN (WhatsApp)
        txtForgotPin.setOnClickListener {
            startActivity(Intent(this, ForgotPinActivity::class.java))
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
