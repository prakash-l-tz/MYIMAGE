package com.example.myimage

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class ViewVideoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_video)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val btnPrev = findViewById<ImageView>(R.id.btnPrev)
        val btnNext = findViewById<ImageView>(R.id.btnNext)
        val btnPlayPause = findViewById<ImageView>(R.id.btnPlayPause)

        val videoList =
            intent.getStringArrayListExtra("video_list") ?: return

        val startPosition =
            intent.getIntExtra("position", 0)

        val adapter = VideoPagerAdapter(videoList)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(startPosition, false)

        // Page change → auto pause previous video
        viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                btnPlayPause.setImageResource(
                    android.R.drawable.ic_media_pause
                )
            }
        })

        btnPrev.setOnClickListener {
            if (viewPager.currentItem > 0) {
                viewPager.currentItem--
            }
        }

        btnNext.setOnClickListener {
            if (viewPager.currentItem < videoList.size - 1) {
                viewPager.currentItem++
            }
        }

        btnPlayPause.setOnClickListener {
            val isPlaying = adapter.togglePlayPause()
            btnPlayPause.setImageResource(
                if (isPlaying)
                    android.R.drawable.ic_media_pause
                else
                    android.R.drawable.ic_media_play
            )
        }
    }

    override fun onPause() {
        super.onPause()
        // Stop video when leaving activity
        (findViewById<ViewPager2>(R.id.viewPager).adapter as? VideoPagerAdapter)
            ?.stopVideo()
    }
}
