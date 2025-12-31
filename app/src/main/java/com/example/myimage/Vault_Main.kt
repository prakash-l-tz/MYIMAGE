package com.example.myimage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import com.google.android.material.appbar.MaterialToolbar

class Vault_Main : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vault_main)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val settingLayout = findViewById<LinearLayout>(R.id.Setting)
        val photoLayout = findViewById<LinearLayout>(R.id.photo)
        val videoLayout = findViewById<LinearLayout>(R.id.video)
        val audioLayout = findViewById<LinearLayout>(R.id.Audio)


        settingLayout.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
        photoLayout.setOnClickListener {
            val intent = Intent(this, PhotoActivity::class.java)
            startActivity(intent)
        }
        videoLayout.setOnClickListener {
            val intent = Intent(this, VideoActivity::class.java)
            startActivity(intent)
        }
        audioLayout.setOnClickListener {
            val intent = Intent(this, AudioActivity::class.java)
            startActivity(intent)
        }
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}