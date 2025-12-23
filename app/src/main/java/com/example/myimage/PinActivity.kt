package com.example.myimage

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener

class PinActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    private var isPinVisible = false
    private lateinit var btnTogglePin: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        prefs = getSharedPreferences("MY_IMAGE_PREF", MODE_PRIVATE)

        val etPin = findViewById<EditText>(R.id.etPin)
        val btnUnlock = findViewById<Button>(R.id.btnUnlock)
        val btnChangePin = findViewById<Button>(R.id.btnChangePin)
        btnTogglePin = findViewById(R.id.btnTogglePin)
        val txtForgot = findViewById<TextView>(R.id.txtForgot)

        disablePinUI()

        // 🔁 Forgot PIN → Reset Screen
        txtForgot.setOnClickListener {
            startActivity(Intent(this, ForgotPIN::class.java))
        }

        // 👁 Show / Hide PIN
        btnTogglePin.setOnClickListener {
            if (btnTogglePin.isEnabled) {
                isPinVisible = !isPinVisible
                togglePin(etPin, btnTogglePin, isPinVisible)
            }
        }

        // ✍ Enable eye button only when PIN typed
        etPin.addTextChangedListener {
            btnTogglePin.isEnabled = !it.isNullOrEmpty()
            btnTogglePin.alpha = if (it.isNullOrEmpty()) 0.4f else 1f
        }
//        btnTogglePin.setOnClickListener {
//            isPinVisible = !isPinVisible
//
//            if (isPinVisible) {
//                etPin.transformationMethod = null
//                btnTogglePin.setImageResource(R.drawable.eye_visible)
//            } else {
//                etPin.transformationMethod = PasswordTransformationMethod.getInstance()
//                btnTogglePin.setImageResource(R.drawable.eye_invisible)
//            }
//            etPin.setSelection(etPin.text.length)
//        }

        // 🔐 Unlock / Set PIN
        btnUnlock.setOnClickListener {
            val pin = etPin.text.toString().trim()
            val savedPin = prefs.getString("APP_PIN", null)

            if (pin.length != 4) {
                toast("Enter 4 digit PIN")
                return@setOnClickListener
            }

            if (savedPin == null) {
                // First time set PIN
                prefs.edit().putString("APP_PIN", pin).apply()
                toast("PIN Set Successfully 🔐")
                openMain()
            } else if (savedPin == pin) {
                openMain()
            } else {
                toast("Wrong PIN ❌")
                etPin.text.clear()
            }
        }

        // 🔄 Change PIN
        btnChangePin.setOnClickListener {
            startActivity(Intent(this, ChangePinActivity::class.java))
        }
    }
    private fun disablePinUI() {
        btnTogglePin.isEnabled = false

        btnTogglePin.alpha = 0.4f
    }
    private fun togglePin(editText: EditText, imageView: ImageView, visible: Boolean) {
        if (visible) {
            editText.transformationMethod = null
            imageView.setImageResource(R.drawable.eye_visible)
        } else {
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            imageView.setImageResource(R.drawable.eye_invisible)
        }
        editText.setSelection(editText.text.length)
    }
    override fun onResume() {
        super.onResume()
        findViewById<EditText>(R.id.etPin).text.clear()
    }

    private fun openMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
