package com.example.myimage

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


        val changepin = findViewById<LinearLayout>(R.id.changepin)
        val resetpin = findViewById<LinearLayout>(R.id.resetpin)


        changepin.setOnClickListener {
            val intent = Intent(this, ChangePinActivity::class.java)
            startActivity(intent)
        }
        resetpin.setOnClickListener {
            val intent = Intent(this, ResetPINActivity::class.java)
            startActivity(intent)
        }

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

    }
}