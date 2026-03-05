package com.example.myimage

import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.appbar.MaterialToolbar

class ResetPINActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    private lateinit var etEmail: EditText
    private lateinit var etOtp: EditText
    private lateinit var etNewPin: EditText
    private lateinit var etConfirmPin: EditText

    private lateinit var btnSendOtp: Button
    private lateinit var btnVerify: Button
    private lateinit var btnSave: Button

    private lateinit var btnTogglePin1: ImageView
    private lateinit var btnTogglePin2: ImageView

    private var newPinVisible = false
    private var confirmPinVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_pin)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        prefs = getSharedPreferences("MY_IMAGE_PREF", MODE_PRIVATE)

        etEmail = findViewById(R.id.etEmail)
        etOtp = findViewById(R.id.etOtp)
        etNewPin = findViewById(R.id.etNewPin)
        etConfirmPin = findViewById(R.id.etConfirmPin)

        btnSendOtp = findViewById(R.id.btnSendOtp)
        btnVerify = findViewById(R.id.btnVerify)
        btnSave = findViewById(R.id.btnSavePin)

        btnTogglePin1 = findViewById(R.id.btnTogglePin)
        btnTogglePin2 = findViewById(R.id.btnTogglePins)

        disablePinUI()
        etOtp.isEnabled = false
        btnVerify.isEnabled = false

        btnSendOtp.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                toast("Please enter email")
                return@setOnClickListener
            }

            ApiService.sendOtp(email) { success ->
                runOnUiThread {
                    if (success) {
                        toast("OTP Sent to $email")
                        etOtp.isEnabled = true
                        btnVerify.isEnabled = true
                        btnSendOtp.isEnabled = false
                    } else {
                        toast("Failed to send OTP")
                    }
                }
            }
        }

        btnVerify.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val otp = etOtp.text.toString().trim()

            if (otp.isEmpty()) {
                toast("Please enter OTP")
                return@setOnClickListener
            }

            ApiService.verifyOtp(email, otp) { success ->
                runOnUiThread {
                    if (success) {
                        toast("OTP Verified")
                        enablePinUI()
                        etOtp.isEnabled = false
                        btnVerify.isEnabled = false
                    } else {
                        toast("Invalid OTP")
                    }
                }
            }
        }

        btnSave.setOnClickListener {
            val newPin = etNewPin.text.toString()
            val confirmPin = etConfirmPin.text.toString()

            if (newPin.length != 4) {
                toast("PIN must be 4 digits")
                return@setOnClickListener
            }

            if (newPin != confirmPin) {
                toast("PINs do not match")
                return@setOnClickListener
            }

            prefs.edit().putString("APP_PIN", newPin).apply()
            toast("PIN Reset Successfully")
            finish()
        }

        btnTogglePin1.setOnClickListener {
            newPinVisible = !newPinVisible
            togglePin(etNewPin, btnTogglePin1, newPinVisible)
        }

        btnTogglePin2.setOnClickListener {
            confirmPinVisible = !confirmPinVisible
            togglePin(etConfirmPin, btnTogglePin2, confirmPinVisible)
        }
    }

    private fun disablePinUI() {
        etNewPin.isEnabled = false
        etConfirmPin.isEnabled = false
        btnSave.isEnabled = false
    }

    private fun enablePinUI() {
        etNewPin.isEnabled = true
        etConfirmPin.isEnabled = true
        btnSave.isEnabled = true
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

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
