package com.example.myimage

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class OtpActivity : AppCompatActivity() {

    private lateinit var etMobile: EditText
    private lateinit var btnSendOtp: Button
    private lateinit var layoutOtp: LinearLayout
    private lateinit var btnVerifyOtp: Button
    private lateinit var txtResend: TextView

    private lateinit var otp1: EditText
    private lateinit var otp2: EditText
    private lateinit var otp3: EditText
    private lateinit var otp4: EditText

    private var generatedOtp = "1234" // Demo OTP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        // Views
        etMobile = findViewById(R.id.etMobile)
        btnSendOtp = findViewById(R.id.btnSendOtp)
        layoutOtp = findViewById(R.id.layoutOtp)
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp)
        txtResend = findViewById(R.id.txtResend)

        otp1 = findViewById(R.id.otp1)
        otp2 = findViewById(R.id.otp2)
        otp3 = findViewById(R.id.otp3)
        otp4 = findViewById(R.id.otp4)

        // Send OTP
        btnSendOtp.setOnClickListener {
            val mobile = etMobile.text.toString().trim()

            if (mobile.length != 10) {
                toast("Enter valid mobile number")
                return@setOnClickListener
            }

            layoutOtp.visibility = LinearLayout.VISIBLE
            btnVerifyOtp.visibility = Button.VISIBLE
            txtResend.visibility = TextView.VISIBLE

            toast("OTP sent to +91 $mobile")
            startResendTimer()
        }

        // Verify OTP
        btnVerifyOtp.setOnClickListener {
            val enteredOtp =
                otp1.text.toString() +
                        otp2.text.toString() +
                        otp3.text.toString() +
                        otp4.text.toString()

            if (enteredOtp.length != 4) {
                toast("Enter complete OTP")
                return@setOnClickListener
            }

            if (enteredOtp == generatedOtp) {
                toast("OTP Verified ✅")
            } else {
                toast("Invalid OTP ❌")
            }
        }

        // Resend OTP
        txtResend.setOnClickListener {
            toast("OTP resent 🔁")
            startResendTimer()
        }

        setupOtpInputs()
    }

    // ================= OTP AUTO MOVE =================

    private fun setupOtpInputs() {
        moveNext(otp1, otp2)
        moveNext(otp2, otp3)
        moveNext(otp3, otp4)
    }

    private fun moveNext(current: EditText, next: EditText) {
        current.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    next.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // ================= RESEND TIMER =================

    private fun startResendTimer() {
        txtResend.isEnabled = false
        txtResend.text = "Resend OTP in 30s"

        object : CountDownTimer(30000, 1000) {
            override fun onTick(ms: Long) {
                txtResend.text = "Resend OTP in ${ms / 1000}s"
            }

            override fun onFinish() {
                txtResend.text = "Resend OTP"
                txtResend.isEnabled = true
            }
        }.start()
    }

    // ================= TOAST =================

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
