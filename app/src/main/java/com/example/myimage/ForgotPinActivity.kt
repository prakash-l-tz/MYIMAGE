package com.example.myimage

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class ForgotPinActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var auth: FirebaseAuth

    private var verificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var otpVerified = false

    private lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pin)

        prefs = getSharedPreferences("MY_IMAGE_PREF", MODE_PRIVATE)
        auth = FirebaseAuth.getInstance()

        val etMobile = findViewById<EditText>(R.id.etMobile)
        val etCode = findViewById<EditText>(R.id.etCode)
        val etNewPin = findViewById<EditText>(R.id.etNewPin)
        val etConfirmPin = findViewById<EditText>(R.id.etConfirmPin)

        val btnSendOtp = findViewById<Button>(R.id.btnSendOtp)
        val btnVerify = findViewById<Button>(R.id.btnVerify)
        val txtTimer = findViewById<TextView>(R.id.txtTimer)

        // 📩 SEND / RESEND OTP
        btnSendOtp.setOnClickListener {

            val mobile = etMobile.text.toString().trim()

            if (mobile.length != 10) {
                toast("Enter valid mobile number")
                return@setOnClickListener
            }

            btnSendOtp.isEnabled = false
            startTimer(txtTimer, btnSendOtp)

            if (resendToken == null) {
                sendOtp("+91$mobile")
            } else {
                resendOtp("+91$mobile")
            }
        }

        // ✅ VERIFY OTP & RESET PIN
        btnVerify.setOnClickListener {

            if (!otpVerified) {
                val otp = etCode.text.toString().trim()
                if (otp.length != 6) {
                    toast("Enter valid OTP")
                    return@setOnClickListener
                }
                verifyOtp(otp)
                return@setOnClickListener
            }

            val newPin = etNewPin.text.toString().trim()
            val confirmPin = etConfirmPin.text.toString().trim()

            if (newPin.length != 4 || confirmPin.length != 4) {
                toast("PIN must be 4 digits")
                return@setOnClickListener
            }

            if (newPin != confirmPin) {
                toast("PINs do not match")
                return@setOnClickListener
            }

            prefs.edit().putString("APP_PIN", newPin).apply()
            toast("PIN reset successfully 🔐")

            startActivity(Intent(this, PinActivity::class.java))
            finish()
        }
    }

    // ================= OTP FUNCTIONS =================

    private fun sendOtp(phone: String) {

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resendOtp(phone: String) {

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken!!)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyOtp(code: String) {
        val id = verificationId ?: return
        val credential = PhoneAuthProvider.getCredential(id, code)

        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                otpVerified = true
                toast("OTP verified ✅ Now set new PIN")
            }
            .addOnFailureListener {
                toast("Invalid OTP ❌")
            }
    }

    private val callbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}

            override fun onVerificationFailed(e: FirebaseException) {
                toast("OTP failed: ${e.message}")
            }

            override fun onCodeSent(
                id: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                verificationId = id
                resendToken = token
                toast("OTP sent 📩")
            }
        }

    // ================= TIMER =================

    private fun startTimer(txtTimer: TextView, btnSendOtp: Button) {

        txtTimer.visibility = TextView.VISIBLE

        timer = object : CountDownTimer(60000, 1000) {

            override fun onTick(ms: Long) {
                txtTimer.text = "Resend OTP in ${ms / 1000}s"
            }

            override fun onFinish() {
                txtTimer.visibility = TextView.GONE
                btnSendOtp.isEnabled = true
                btnSendOtp.text = "Resend OTP"
            }
        }.start()
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::timer.isInitialized) timer.cancel()
    }
}
