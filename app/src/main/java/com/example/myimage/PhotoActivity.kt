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

class PhotoActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageAdapter
    private val imageFiles = mutableListOf<File>()

    private val PICK_IMAGE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        adapter = ImageAdapter(imageFiles) { file ->
            confirmDelete(file)
        }
        recyclerView.adapter = adapter

        // ✅ PICK ONLY IMAGES
        findViewById<Button>(R.id.btnAddImage).setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            startActivityForResult(intent, PICK_IMAGE)
        }

        loadImages()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != PICK_IMAGE || resultCode != RESULT_OK || data == null) return

        if (data.clipData != null) {
            for (i in 0 until data.clipData!!.itemCount) {
                saveImage(data.clipData!!.getItemAt(i).uri)
            }
        } else if (data.data != null) {
            saveImage(data.data!!)
        }

        loadImages()
    }

    // ✅ SAVE IMAGE ONLY
    private fun saveImage(uri: Uri) {
        val dir = File(filesDir, "my_images")
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, "IMG_${System.currentTimeMillis()}.jpg")
        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
    }

    // ✅ LOAD ONLY IMAGES
    private fun loadImages() {
        imageFiles.clear()
        File(filesDir, "my_images").listFiles()?.let {
            imageFiles.addAll(it)
        }
        adapter.notifyDataSetChanged()
    }

    private fun confirmDelete(file: File) {
        AlertDialog.Builder(this)
            .setTitle("Delete Image")
            .setMessage("Do you want to delete this image?")
            .setPositiveButton("Delete") { _, _ ->
                if (file.delete()) {
                    loadImages()
                    Toast.makeText(this, "Image Deleted", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
