package com.example.myimage

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import java.io.File

class ImagePagerAdapter(
    private val mediaList: List<String>
) : RecyclerView.Adapter<ImagePagerAdapter.VH>() {

    private var currentVideo: VideoView? = null
    private var currentIsVideo = false

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val photo: PhotoView = view.findViewById(R.id.photoView)
        val video: VideoView = view.findViewById(R.id.videoView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_media, parent, false)
        )
    }

    override fun getItemCount() = mediaList.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val file = File(mediaList[position])
        val isVideo = file.extension.lowercase() == "mp4"

        // stop old video
        currentVideo?.pause()
        currentVideo = null

        currentIsVideo = isVideo

        if (isVideo) {
            holder.photo.visibility = View.GONE
            holder.video.visibility = View.VISIBLE

            holder.video.setVideoURI(Uri.fromFile(file))
            holder.video.start()

            currentVideo = holder.video
        } else {
            holder.video.visibility = View.GONE
            holder.photo.visibility = View.VISIBLE

            Glide.with(holder.photo.context)
                .load(file)
                .into(holder.photo)
        }
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

    fun isCurrentItemVideo(position: Int): Boolean {
        return mediaList[position].lowercase().endsWith(".mp4")
    }
}
