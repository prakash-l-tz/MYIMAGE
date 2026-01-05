package com.example.myimage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import java.io.File
import java.io.FileOutputStream

class PhotoActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddImage: Button

    // Image picker launcher
    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { saveImageToFolder(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        recyclerView = findViewById(R.id.recyclerView)
        btnAddImage = findViewById(R.id.btnAddImage)

        // Toolbar back
        toolbar.setNavigationOnClickListener { finish() }

        // RecyclerView basic setup
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        // Add Image button
        btnAddImage.setOnClickListener {
            imagePicker.launch("image/*")
        }

        // Ensure folder exists
        createImageFolder()
    }

    // Create folder
    private fun createImageFolder(): File {
        val folder = File(getExternalFilesDir(null), "MyImages")
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return folder
    }

    // Save selected image
    private fun saveImageToFolder(uri: Uri) {
        val folder = createImageFolder()
        val fileName = getFileName(uri)
        val file = File(folder, fileName)

        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }

        Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show()

        // Later: refresh RecyclerView here
    }

    // Get original file name
    private fun getFileName(uri: Uri): String {
        var name = "IMG_${System.currentTimeMillis()}.jpg"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst() && index != -1) {
                name = it.getString(index)
            }
        }
        return name
    }
}
