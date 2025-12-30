package com.example.myimage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar

class ViewVideoActivity : AppCompatActivity() {

    private lateinit var adapter: VideoPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_video)

//        val viewPager1 = findViewById<ViewPager2>(R.id.viewPager)
//
//        viewPager1.setOnClickListener {
//            onBackPressedDispatcher.onBackPressed()
//        }
//        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
//        setSupportActionBar(toolbar)
//
//        toolbar.setNavigationOnClickListener {
//            onBackPressedDispatcher.onBackPressed()
//        }
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)

        val videoList =
            intent.getStringArrayListExtra("video_list") ?: return

        val startPosition =
            intent.getIntExtra("position", 0)
        val adapter = VideoPagerAdapter(videoList) {
            // Back callback
            finish()   // closes activity
        }
        viewPager.adapter = adapter
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()  // This will go back to the previous activity (e.g., gallery/list)
        }

//        adapter = VideoPagerAdapter(videoList)
//        viewPager.adapter = adapter
        viewPager.setCurrentItem(startPosition, false)

        // Stop previous video when swiping
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