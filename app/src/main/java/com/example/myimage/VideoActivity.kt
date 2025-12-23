package com.example.myimage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import java.io.File
import java.io.FileOutputStream

class VideoActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VideoAdapter
    private val videoFiles = mutableListOf<File>()

    private val PICK_VIDEO = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        adapter = VideoAdapter(videoFiles) { file ->
            confirmDelete(file)
        }
        recyclerView.adapter = adapter

        // ✅ PICK ONLY VIDEOS
        findViewById<Button>(R.id.btnAddVideo).setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "video/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            startActivityForResult(intent, PICK_VIDEO)
        }

        loadVideos()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != PICK_VIDEO || resultCode != RESULT_OK || data == null) return

        if (data.clipData != null) {
            for (i in 0 until data.clipData!!.itemCount) {
                saveVideo(data.clipData!!.getItemAt(i).uri)
            }
        } else if (data.data != null) {
            saveVideo(data.data!!)
        }

        loadVideos()
    }

    // ✅ SAVE VIDEO ONLY
    private fun saveVideo(uri: Uri) {
        val dir = File(filesDir, "my_videos")
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, "VID_${System.currentTimeMillis()}.mp4")
        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
    }

    // ✅ LOAD ONLY VIDEOS
    private fun loadVideos() {
        videoFiles.clear()
        File(filesDir, "my_videos").listFiles()?.let {
            videoFiles.addAll(it)
        }
        adapter.notifyDataSetChanged()
    }

    private fun confirmDelete(file: File) {
        AlertDialog.Builder(this)
            .setTitle("Delete Video")
            .setMessage("Do you want to delete this video?")
            .setPositiveButton("Delete") { _, _ ->
                if (file.delete()) {
                    loadVideos()
                    Toast.makeText(this, "Video Deleted", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
