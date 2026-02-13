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

class VideoActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VideoAdapter
    private val videoFiles = mutableListOf<File>()
    private lateinit var toolbar: MaterialToolbar

    private val PICK_VIDEO = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

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

        adapter = VideoAdapter(videoFiles) { files, isSelectionMode ->
            if (isSelectionMode) {
                toolbar.title = "${adapter.selectedItemCount} selected"
                toolbar.menu.findItem(R.id.action_delete).isVisible = adapter.selectedItemCount > 0
            } else {
                if (files.isNotEmpty()) {
                    confirmDelete(files)
                } else {
                    toolbar.title = "VIDEO"
                    toolbar.menu.findItem(R.id.action_delete)?.isVisible = false
                }
            }
        }
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.btnAddVideo).setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "video/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            startActivityForResult(intent, PICK_VIDEO)
        }

        loadVideos()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_video, menu)
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

    private fun loadVideos() {
        videoFiles.clear()
        File(filesDir, "my_videos").listFiles()?.let {
            videoFiles.addAll(it)
        }
        adapter.notifyDataSetChanged()
    }

    private fun confirmDelete(files: List<File>) {
        AlertDialog.Builder(this)
            .setTitle("Delete Videos")
            .setMessage("Do you want to delete ${files.size} videos?")
            .setPositiveButton("Delete") { _, _ ->
                files.forEach { it.delete() }
                loadVideos()
                adapter.clearSelection()
                Toast.makeText(this, "Videos Deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
