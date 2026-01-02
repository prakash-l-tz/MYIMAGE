package com.example.myimage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar

class AudioActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var btnAddAudio: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var audioAdapter: AudioAdapter

    private val audioList = mutableListOf<AudioItem>()

    companion object {
        private const val AUDIO_PICK_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        btnAddAudio = findViewById(R.id.btnAddAudio)
        recyclerView = findViewById(R.id.AudioSave)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        audioAdapter = AudioAdapter(audioList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = audioAdapter
    }

    private fun setupClickListeners() {
        btnAddAudio.setOnClickListener {
            openAudioPicker()
        }
    }

    private fun openAudioPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "audio/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, AUDIO_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUDIO_PICK_CODE && resultCode == Activity.RESULT_OK) {
            val uri: Uri = data?.data ?: return

            val name = getFileName(uri)
            audioList.add(AudioItem(uri, name))
            audioAdapter.notifyItemInserted(audioList.size - 1)
        }
    }

    private fun getFileName(uri: Uri): String {
        var name = "Audio File"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst()) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }
    override fun onDestroy() {
        super.onDestroy()
        audioAdapter.releasePlayer()
    }

}
