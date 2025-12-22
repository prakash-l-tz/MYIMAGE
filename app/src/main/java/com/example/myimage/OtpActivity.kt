package com.example.myimage

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class OtpActivity : AppCompatActivity() {

    private lateinit var etMobile: EditText
    private lateinit var btnSendOtp: Button
    private lateinit var btnVerifyOtp: Button

    private lateinit var otp1: EditText
    private lateinit var otp2: EditText
    private lateinit var otp3: EditText
    private lateinit var otp4: EditText

    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        // Views
        etMobile = findViewById(R.id.etMobile)
        btnSendOtp = findViewById(R.id.btnSendOtp)
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp)

        otp1 = findViewById(R.id.otp1)
        otp2 = findViewById(R.id.otp2)
        otp3 = findViewById(R.id.otp3)
        otp4 = findViewById(R.id.otp4)

        auth = FirebaseAuth.getInstance()

        setupOtpMove()

        // SEND OTP
        btnSendOtp.setOnClickListener {
            val mobile = etMobile.text.toString().trim()

            if (mobile.length != 10) {
                toast("Enter valid mobile number")
                return@setOnClickListener
            }

            sendOtp("+91$mobile")
        }

        // VERIFY OTP
        btnVerifyOtp.setOnClickListener {
            val otp =
                otp1.text.toString() +
                        otp2.text.toString() +
                        otp3.text.toString() +
                        otp4.text.toString()

            if (otp.length != 6) {
                toast("Enter complete OTP")
                return@setOnClickListener
            }

            verifyOtp(otp)
        }
    }

    // ================= FIREBASE OTP =================

    private fun sendOtp(phone: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
        toast("Sending OTP...")
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signIn(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            toast("OTP Failed: ${e.message}")
        }

        override fun onCodeSent(
            id: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            verificationId = id
            toast("OTP Sent")
        }
    }

    private fun verifyOtp(code: String) {
        if (verificationId == null) {
            toast("Please request OTP first")
            return
        }
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signIn(credential)
    }

    private fun signIn(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    toast("OTP Verified ✅")
                    startActivity(Intent(this, ChangePinActivity::class.java))
                    finish()
                } else {
                    toast("Wrong OTP ❌")
                }
            }
    }

    // ================= OTP AUTO MOVE =================

    private fun setupOtpMove() {
        moveNext(otp1, otp2)
        moveNext(otp2, otp3)
        moveNext(otp3, otp4)
    }

    private fun moveNext(current: EditText, next: EditText) {
        current.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) next.requestFocus()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
