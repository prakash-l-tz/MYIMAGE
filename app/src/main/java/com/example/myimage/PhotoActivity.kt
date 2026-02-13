package com.example.myimage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
    private lateinit var toolbar: MaterialToolbar

    private val PICK_IMAGE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (adapter.isSelectionMode) {
                    adapter.clearSelection()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        adapter = ImageAdapter(imageFiles) { files, isSelectionMode ->
            if (isSelectionMode) {
                toolbar.title = "${adapter.selectedItemCount} selected"
                toolbar.menu.findItem(R.id.action_delete).isVisible = adapter.selectedItemCount > 0
            } else {
                if (files.isNotEmpty()) {
                    confirmDelete(files)
                } else {
                    toolbar.title = "PHOTO"
                    toolbar.menu.findItem(R.id.action_delete)?.isVisible = false
                }
            }
        }
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.btnAddImage).setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            startActivityForResult(intent, PICK_IMAGE)
        }

        loadImages()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_photo, menu)
        menu?.findItem(R.id.action_delete)?.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_delete) {
            adapter.deleteSelected()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
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

    private fun loadImages() {
        imageFiles.clear()
        File(filesDir, "my_images").listFiles()?.let {
            imageFiles.addAll(it)
        }
        adapter.notifyDataSetChanged()
    }

    private fun confirmDelete(files: List<File>) {
        AlertDialog.Builder(this)
            .setTitle("Delete Images")
            .setMessage("Do you want to delete ${files.size} images?")
            .setPositiveButton("Delete") { _, _ ->
                files.forEach { it.delete() }
                loadImages()
                adapter.clearSelection()
                Toast.makeText(this, "Images Deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
