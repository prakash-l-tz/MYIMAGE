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
import androidx.appcompat.app.AppCompatActivity
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

//        holder.videoView.setOnClickListener {
//            (holder.itemView.context as? AppCompatActivity)
//                ?.onBackPressedDispatcher
//                ?.onBackPressed()
//        }

        holder.videoView.apply {
            setMediaController(mediaController)
            setVideoURI(Uri.fromFile(file))

            setOnPreparedListener { mp ->
                mp.isLooping = true

                val videoW = mp.videoWidth
                val videoH = mp.videoHeight

                val parentW = (parent as View).width
                val parentH = (parent as View).height

                val videoRatio = videoW.toFloat() / videoH
                val screenRatio = parentW.toFloat() / parentH

                val params = FrameLayout.LayoutParams(0, 0)
                params.gravity = Gravity.CENTER

                if (videoRatio > screenRatio) {
                    params.width = parentW
                    params.height = (parentW / videoRatio).toInt()
                } else {
                    params.height = parentH
                    params.width = (parentH * videoRatio).toInt()
                }

                layoutParams = params
                start()
            }

            requestFocus()
        }


        currentVideo = holder.videoView
        holder.backBtn.setOnClickListener {
            onBack()
        }
    }

    fun stopVideo() {
        currentVideo?.stopPlayback()
        currentVideo = null
    }
}
