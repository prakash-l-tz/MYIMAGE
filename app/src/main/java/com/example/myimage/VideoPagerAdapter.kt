package com.example.myimage

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class VideoPagerAdapter(
    private val videoList: List<String>
) : RecyclerView.Adapter<VideoPagerAdapter.VideoVH>() {

    private var currentVideo: VideoView? = null

    inner class VideoVH(view: View) : RecyclerView.ViewHolder(view) {
        val videoView: VideoView = view.findViewById(R.id.videoView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoVH {
        return VideoVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_video, parent, false)
        )
    }

    override fun getItemCount(): Int = videoList.size

    override fun onBindViewHolder(holder: VideoVH, position: Int) {
        val file = File(videoList[position])

        // Stop previously playing video
        currentVideo?.pause()
        currentVideo = null

        holder.videoView.setVideoURI(Uri.fromFile(file))
        holder.videoView.setOnPreparedListener {
            it.isLooping = true
            holder.videoView.start()
        }

        currentVideo = holder.videoView
    }

    fun togglePlayPause(): Boolean {
        currentVideo?.let {
            return if (it.isPlaying) {
                it.pause()
                false
            } else {
                it.start()
                true
            }
        }
        return false
    }

    fun stopVideo() {
        currentVideo?.pause()
        currentVideo = null
    }
}
