package com.example.myimage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class ViewVideoActivity : AppCompatActivity() {

    private lateinit var adapter: VideoPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_video)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        val videoList =
            intent.getStringArrayListExtra("video_list") ?: return

        val startPosition =
            intent.getIntExtra("position", 0)

        // 🔴 PASS BACK ACTION HERE
        adapter = VideoPagerAdapter(videoList) {
            finish() // ← this closes VideoActivity
        }

        viewPager.adapter = adapter
        viewPager.setCurrentItem(startPosition, false)

        viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    adapter.stopVideo()
                }
            }
        )
    }

    override fun onPause() {
        super.onPause()
        adapter.stopVideo()
    }
}
