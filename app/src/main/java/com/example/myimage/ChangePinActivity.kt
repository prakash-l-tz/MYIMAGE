package com.example.myimage

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ChangePinActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    private var oldPinVisible = false
    private var newPinVisible = false
    private var confirmPinVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pin)

        prefs = getSharedPreferences("MY_IMAGE_PREF", MODE_PRIVATE)

        // ✅ Correct view types
        val etOldPin = findViewById<EditText>(R.id.etOldPin)
        val etNewPin = findViewById<EditText>(R.id.etNewPin)
        val etConfirmPin = findViewById<EditText>(R.id.etConfirmPin)

        val btnTogglePin1 = findViewById<ImageView>(R.id.btnTogglePin1)
        val btnTogglePin2 = findViewById<ImageView>(R.id.btnTogglePin2)
        val btnTogglePin3 = findViewById<ImageView>(R.id.btnTogglePin3)

        val btnSavePin = findViewById<Button>(R.id.btnSavePin)

        // 👁 OLD PIN TOGGLE
        btnTogglePin1.setOnClickListener {
            oldPinVisible = !oldPinVisible
            togglePin(etOldPin, btnTogglePin1, oldPinVisible)
        }

        // 👁 NEW PIN TOGGLE
        btnTogglePin2.setOnClickListener {
            newPinVisible = !newPinVisible
            togglePin(etNewPin, btnTogglePin2, newPinVisible)
        }

        // 👁 CONFIRM PIN TOGGLE
        btnTogglePin3.setOnClickListener {
            confirmPinVisible = !confirmPinVisible
            togglePin(etConfirmPin, btnTogglePin3, confirmPinVisible)
        }

        // 💾 SAVE PIN
        btnSavePin.setOnClickListener {

            val oldPin = etOldPin.text.toString().trim()
            val newPin = etNewPin.text.toString().trim()
            val confirmPin = etConfirmPin.text.toString().trim()

            val savedPin = prefs.getString("APP_PIN", null)
            if (oldPin.isEmpty() || newPin.isEmpty() || confirmPin.isEmpty()) {
                toast("Please fill all fields ❗")
                return@setOnClickListener
            }
            if (savedPin != null && oldPin != savedPin) {
                toast("Old PIN is incorrect ❌")
                return@setOnClickListener
            }

            if (newPin.length != 4 || confirmPin.length != 4) {
                toast("PIN must be 4 digits")
                return@setOnClickListener
            }

            if (newPin != confirmPin) {
                toast("New PIN & Confirm PIN does not match ❌")
                return@setOnClickListener
            }

            prefs.edit().putString("APP_PIN", newPin).apply()
            toast("PIN updated successfully 🔐")

            startActivity(Intent(this, PinActivity::class.java))
            finish()
        }
    }

    // 🔁 Reusable function
    private fun togglePin(
        editText: EditText,
        imageView: ImageView,
        visible: Boolean
    ) {
        if (visible) {
            editText.transformationMethod = null
            imageView.setImageResource(R.drawable.eye_visible)
        } else {
            editText.transformationMethod =
                PasswordTransformationMethod.getInstance()
            imageView.setImageResource(R.drawable.eye_invisible)
        }
        editText.setSelection(editText.text.length)
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
