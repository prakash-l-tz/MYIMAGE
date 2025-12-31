package com.example.myimage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.viewpager2.widget.ViewPager2
import androidx.core.content.ContextCompat
import java.io.File

class ViewImageActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var bottomBar: LinearLayout
    private lateinit var mediaList: ArrayList<String>
    private lateinit var adapter: ImagePagerAdapter

    // ✅ STORE LIKED ITEMS
    private val likedPositions = HashSet<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)

        viewPager = findViewById(R.id.viewPager)
        bottomBar = findViewById(R.id.bottomBar)

        val btnLike = findViewById<ImageView>(R.id.btnLike)

        mediaList = intent.getStringArrayListExtra("media_list") ?: arrayListOf()
        val startPosition = intent.getIntExtra("position", 0)

        adapter = ImagePagerAdapter(mediaList) {
            toggleBottomBar()
        }

        viewPager.adapter = adapter
        viewPager.setCurrentItem(startPosition, false)

        // 🔹 UPDATE LIKE COLOR WHEN PAGE CHANGES
        viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    updateLikeIcon(btnLike, position)
                }
            }
        )

        // 🔹 BACK
        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

// 🔹 LIKE BUTTON (WITH TOAST)
        btnLike.setOnClickListener {
            val pos = viewPager.currentItem

            if (likedPositions.contains(pos)) {
                likedPositions.remove(pos)
                updateLikeIcon(btnLike, pos)
                Toast.makeText(this, "Unliked 🤍", Toast.LENGTH_SHORT).show()
            } else {
                likedPositions.add(pos)
                updateLikeIcon(btnLike, pos)
                Toast.makeText(this, "Liked ❤️", Toast.LENGTH_SHORT).show()
            }
        }


        // 🔹 SHARE
        findViewById<ImageView>(R.id.btnShare).setOnClickListener {
            shareCurrent()
        }

        // 🔹 DELETE
        findViewById<ImageView>(R.id.btnDelete).setOnClickListener {
            confirmDelete()
        }

        // INITIAL STATE
        updateLikeIcon(btnLike, startPosition)
    }

    // ---------------- LIKE UI ----------------

    private fun updateLikeIcon(btnLike: ImageView, position: Int) {
        val color = if (likedPositions.contains(position))
            android.R.color.holo_red_dark
        else
            android.R.color.white

        btnLike.setColorFilter(
            ContextCompat.getColor(this, color)
        )
    }

    // ---------------- DELETE ----------------

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle("Delete")
            .setMessage("Are you sure you want to delete this file?")
            .setPositiveButton("Delete") { _, _ ->
                deleteCurrent()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteCurrent() {
        if (mediaList.isEmpty()) return

        val position = viewPager.currentItem
        val file = File(mediaList[position])

        if (file.exists() && file.delete()) {
            mediaList.removeAt(position)
            adapter.notifyItemRemoved(position)

            // REMOVE LIKE STATE ALSO
            likedPositions.remove(position)

            if (mediaList.isEmpty()) {
                finish()
                return
            }

            val newPosition =
                if (position >= mediaList.size) mediaList.size - 1 else position

            viewPager.setCurrentItem(newPosition, true)
            toast("Deleted 🗑")
        } else {
            toast("Delete failed")
        }
    }

    // ---------------- UI ----------------

    fun toggleBottomBar() {
        bottomBar.visibility =
            if (bottomBar.visibility == View.VISIBLE)
                View.GONE else View.VISIBLE
    }

    // ---------------- SHARE ----------------

    private fun shareCurrent() {
        val pos = viewPager.currentItem
        val file = File(mediaList[pos])

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = if (file.extension == "mp4") "video/*" else "image/*"
            putExtra(
                Intent.EXTRA_STREAM,
                FileProvider.getUriForFile(
                    this@ViewImageActivity,
                    "$packageName.provider",
                    file
                )
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
