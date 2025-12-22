package com.example.myimage

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ChangePinActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pin)

        prefs = getSharedPreferences("MY_IMAGE_PREF", MODE_PRIVATE)

        val etOldPin = findViewById<EditText>(R.id.etOldPin)
        val etNewPin = findViewById<EditText>(R.id.etNewPin)
        val etConfirmPin = findViewById<EditText>(R.id.etConfirmPin)
        val btnSavePin = findViewById<Button>(R.id.btnSavePin)

        btnSavePin.setOnClickListener {

            val oldPin = etOldPin.text.toString()
            val newPin = etNewPin.text.toString()
            val confirmPin = etConfirmPin.text.toString()

            val savedPin = prefs.getString("APP_PIN", null)

            // 🔐 Validate old PIN
            if (savedPin != null && oldPin != savedPin) {
                toast("Old PIN is incorrect ❌")
                return@setOnClickListener
            }

            if (newPin.length != 4 || confirmPin.length != 4) {
                toast("PIN must be 4 digits")
                return@setOnClickListener
            }

            if (newPin != confirmPin) {
                toast("PINs do not match ❌")
                return@setOnClickListener
            }

            // ✅ Save new PIN
            prefs.edit().putString("APP_PIN", newPin).apply()
            toast("PIN updated successfully 🔐")

            // 👉 Go to MainActivity (optional)
            startActivity(Intent(this, PinActivity::class.java))
            finish()
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
