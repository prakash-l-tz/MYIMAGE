package com.example.myimage

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class ViewImage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val btnPrev = findViewById<ImageView>(R.id.btnPrev)
        val btnNext = findViewById<ImageView>(R.id.btnNext)
        val btnPlayPause = findViewById<ImageView>(R.id.btnPlayPaus)

        val mediaList =
            intent.getStringArrayListExtra("media_list") ?: return

        val startPosition =
            intent.getIntExtra("position", 0)

        val adapter = ImagePagerAdapter(mediaList)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(startPosition, false)

        updateControls(adapter, startPosition, btnPrev, btnNext, btnPlayPause)

        viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateControls(adapter, position, btnPrev, btnNext, btnPlayPause)
                btnPlayPause.setImageResource(android.R.drawable.ic_media_pause)
            }
        })

        btnPrev.setOnClickListener {
            if (viewPager.currentItem > 0) viewPager.currentItem--
        }

        btnNext.setOnClickListener {
            if (viewPager.currentItem < mediaList.size - 1) viewPager.currentItem++
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

    private fun updateControls(
        adapter: ImagePagerAdapter,
        position: Int,
        btnPrev: ImageView,
        btnNext: ImageView,
        btnPlayPause: ImageView
    ) {
        val visible = if (adapter.isCurrentItemVideo(position))
            View.VISIBLE else View.GONE

        btnPrev.visibility = visible
        btnNext.visibility = visible
        btnPlayPause.visibility = visible
    }
}
