package com.example.myimage

import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class VideoPagerAdapter(
    private val videoList: List<String>,
    private val onBack: () -> Unit
) : RecyclerView.Adapter<VideoPagerAdapter.VideoVH>() {

    private var currentVideo: VideoView? = null

    inner class VideoVH(view: View) : RecyclerView.ViewHolder(view) {
        val videoView: VideoView = view.findViewById(R.id.videoView)
        val backBtn: ImageView = view.findViewById(R.id.btnBack)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoVH {
        return VideoVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_media, parent, false)
        )
    }

    override fun getItemCount(): Int = videoList.size

    override fun onBindViewHolder(holder: VideoVH, position: Int) {

        currentVideo?.stopPlayback()

        val file = File(videoList[position])

        val mediaController = MediaController(holder.videoView.context)
        mediaController.setAnchorView(holder.videoView)

        holder.videoView.apply {
            setMediaController(mediaController)
            setVideoURI(Uri.fromFile(file))

            setOnPreparedListener { mp ->
                mp.isLooping = true
                start()
            }

            requestFocus()
        }

        holder.backBtn.setOnClickListener {
            onBack()

        }

        currentVideo = holder.videoView
    }

    fun stopVideo() {
        currentVideo?.stopPlayback()
        currentVideo = null
    }
}
