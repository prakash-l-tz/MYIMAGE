package com.example.myimage

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class ResetPINActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var prefs: SharedPreferences

    private lateinit var etNewPin: EditText
    private lateinit var etConfirmPin: EditText
    private lateinit var btnSave: Button
    private lateinit var btnGoogle: Button
    private lateinit var btnTogglePin1: ImageView
    private lateinit var btnTogglePin2: ImageView

    private var newPinVisible = false
    private var confirmPinVisible = false

    private val RC_SIGN_IN = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reset_pin)

        auth = FirebaseAuth.getInstance()
        prefs = getSharedPreferences("MY_IMAGE_PREF", MODE_PRIVATE)

        etNewPin = findViewById(R.id.etNewPin)
        etConfirmPin = findViewById(R.id.etConfirmPin)
        btnSave = findViewById(R.id.btnSavePin)
        btnGoogle = findViewById(R.id.btnGoogleSignIn)
        btnTogglePin1 = findViewById(R.id.btnTogglePin)
        btnTogglePin2 = findViewById(R.id.btnTogglePins)

        // 🔒 Disable initially
        disablePinUI()

        // 🔹 GOOGLE SIGN-IN CONFIG
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()


        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 👁 NEW PIN TOGGLE
        btnTogglePin1.setOnClickListener {
            if (btnTogglePin1.isEnabled) {
                newPinVisible = !newPinVisible
                togglePin(etNewPin, btnTogglePin1, newPinVisible)
            }
        }

        // 👁 CONFIRM PIN TOGGLE
        btnTogglePin2.setOnClickListener {
            if (btnTogglePin2.isEnabled) {
                confirmPinVisible = !confirmPinVisible
                togglePin(etConfirmPin, btnTogglePin2, confirmPinVisible)
            }
        }

        // ✍ Enable eye icon only when text exists
        etNewPin.addTextChangedListener {
            btnTogglePin1.isEnabled = !it.isNullOrEmpty()
            btnTogglePin1.alpha = if (it.isNullOrEmpty()) 0.4f else 1f
        }

        etConfirmPin.addTextChangedListener {
            btnTogglePin2.isEnabled = !it.isNullOrEmpty()
            btnTogglePin2.alpha = if (it.isNullOrEmpty()) 0.4f else 1f
        }

        // 🔹 GOOGLE SIGN-IN BUTTON
        btnGoogle.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
        }

        // 🔐 SAVE PIN
        btnSave.setOnClickListener {
            val newPin = etNewPin.text.toString().trim()
            val confirmPin = etConfirmPin.text.toString().trim()

            when {
                newPin.isEmpty() || confirmPin.isEmpty() ->
                    toast("Please fill all fields ❗")

                newPin.length != 4 || confirmPin.length != 4 ->
                    toast("PIN must be 4 digits")

                newPin != confirmPin ->
                    toast("PIN does not match")

                else -> {
                    prefs.edit().putString("APP_PIN", newPin).apply()
                    toast("PIN Reset Successfully 🔐")
                    finish()
                }
            }
        }
    }

    // 🔹 GOOGLE RESULT
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            try {
                val account = GoogleSignIn
                    .getSignedInAccountFromIntent(data)
                    .getResult(ApiException::class.java)

                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                toast("Google Sign-In Failed (${e.statusCode})")
            }

        }
    }

    // 🔹 FIREBASE AUTH
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                toast("Google Verified ✅")
                enablePinUI()
            }
            .addOnFailureListener {
                toast("Authentication Failed ❌")
            }
    }

    // 🔒 Disable UI
    private fun disablePinUI() {
        etNewPin.isEnabled = false
        etConfirmPin.isEnabled = false
        btnSave.isEnabled = false
        btnTogglePin1.isEnabled = false
        btnTogglePin2.isEnabled = false

        etNewPin.alpha = 0.4f
        etConfirmPin.alpha = 0.4f
        btnSave.alpha = 0.4f
        btnTogglePin1.alpha = 0.4f
        btnTogglePin2.alpha = 0.4f
    }

    // ✅ Enable UI
    private fun enablePinUI() {
        etNewPin.isEnabled = true
        etConfirmPin.isEnabled = true
        btnSave.isEnabled = true
        btnGoogle.isEnabled = false

        etNewPin.alpha = 1f
        etConfirmPin.alpha = 1f
        btnSave.alpha = 1f
    }

    // 👁 Toggle PIN visibility
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
