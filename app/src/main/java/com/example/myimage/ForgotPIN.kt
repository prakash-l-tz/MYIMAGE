package com.example.myimage

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class ForgotPIN : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_pin)

        val etNewPin = findViewById<EditText>(R.id.etNewPin)
        val etConfirmPin = findViewById<EditText>(R.id.etConfirmPin)
        val btnSave = findViewById<Button>(R.id.btnSavePin)

        btnSave.setOnClickListener {
            val pin1 = etNewPin.text.toString()
            val pin2 = etConfirmPin.text.toString()

            if (pin1.length != 4 || pin2.length != 4) {
                toast("PIN must be 4 digits")
            } else if (pin1 != pin2) {
                toast("PIN does not match")
            } else {
                toast("PIN Changed Successfully 🔐")
                finish()
            }
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}