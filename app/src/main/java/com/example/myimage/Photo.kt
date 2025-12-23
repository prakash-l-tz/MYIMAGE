package com.example.myimage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.FileOutputStream

class Photo : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageAdapter
    private val mediaFiles = mutableListOf<File>()

    private val PICK_MEDIA = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        adapter = ImageAdapter(mediaFiles) { file ->
            confirmDelete(file)
        }
        recyclerView.adapter = adapter

        // Back
        findViewById<ImageView>(R.id.Image_back).setOnClickListener {
            finish()
        }

        // Add media
        findViewById<ImageView>(R.id.btnAddImage).setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            startActivityForResult(intent, PICK_MEDIA)
        }

        loadMedia()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != PICK_MEDIA || resultCode != RESULT_OK || data == null) return

        if (data.clipData != null) {
            for (i in 0 until data.clipData!!.itemCount) {
                saveMedia(data.clipData!!.getItemAt(i).uri)
            }
        } else if (data.data != null) {
            saveMedia(data.data!!)
        }

        loadMedia()
    }

    private fun saveMedia(uri: Uri) {
        val type = contentResolver.getType(uri) ?: return
        when {
            type.startsWith("image") -> saveFile(uri, "my_images", "IMG_", ".jpg")
            type.startsWith("video") -> saveFile(uri, "my_videos", "VID_", ".mp4")
        }
    }

    private fun saveFile(uri: Uri, folder: String, prefix: String, ext: String) {
        val dir = File(filesDir, folder)
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, "$prefix${System.currentTimeMillis()}$ext")
        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
    }

    private fun loadMedia() {
        mediaFiles.clear()
        File(filesDir, "my_images").listFiles()?.let { mediaFiles.addAll(it) }
        File(filesDir, "my_videos").listFiles()?.let { mediaFiles.addAll(it) }
        adapter.notifyDataSetChanged()
    }

    private fun confirmDelete(file: File) {
        AlertDialog.Builder(this)
            .setTitle("Delete")
            .setMessage("Delete this file?")
            .setPositiveButton("Delete") { _, _ ->
                if (file.delete()) {
                    loadMedia()
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
