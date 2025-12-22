package com.example.myimage


import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import java.io.File

class ViewImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)

        val imageView = findViewById<PhotoView>(R.id.fullImageView)
        val videoView = findViewById<VideoView>(R.id.videoView)

        val path = intent.getStringExtra("image_path") ?: return
        val file = File(path)

        if (file.extension.lowercase() == "mp4") {
            // SHOW VIDEO
            videoView.visibility = VideoView.VISIBLE
            imageView.visibility = PhotoView.GONE

            videoView.setVideoURI(Uri.fromFile(file))
            videoView.setMediaController(MediaController(this))
            videoView.start()
        } else {
            // SHOW IMAGE WITH ZOOM
            imageView.visibility = PhotoView.VISIBLE
            videoView.visibility = VideoView.GONE

            Glide.with(this)
                .load(file)
                .into(imageView)
        }
    }
}

