package com.example.myimage

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AudioAdapter(
    private val audioList: MutableList<AudioItem>
) : RecyclerView.Adapter<AudioAdapter.AudioViewHolder>() {

    private var mediaPlayer: MediaPlayer? = null
    private var currentPlayingPosition = -1

    class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val audioName: TextView = itemView.findViewById(R.id.txtAudioName)
        val playPauseBtn: ImageView = itemView.findViewById(R.id.btnPlayPause)
        val deleteBtn: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_audio, parent, false)
        return AudioViewHolder(view)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val item = audioList[position]
        holder.audioName.text = item.name

        // Set correct icon state
        if (currentPlayingPosition == position) {
            holder.playPauseBtn.setImageResource(android.R.drawable.ic_media_pause)
        } else {
            holder.playPauseBtn.setImageResource(android.R.drawable.ic_media_play)
        }

        // ▶ PLAY / ⏸ PAUSE
        holder.playPauseBtn.setOnClickListener {
            if (currentPlayingPosition == position) {
                stopAudio()
                notifyItemChanged(position)
            } else {
                val oldPosition = currentPlayingPosition
                playAudio(holder, item, position)
                if (oldPosition != -1) notifyItemChanged(oldPosition)
                notifyItemChanged(position)
            }
        }

        // 🗑 DELETE
        holder.deleteBtn.setOnClickListener {
            stopAudio()
            audioList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, audioList.size)
        }
    }

    override fun getItemCount(): Int = audioList.size

    private fun playAudio(holder: AudioViewHolder, item: AudioItem, position: Int) {
        stopAudio()

        mediaPlayer = MediaPlayer().apply {
            setDataSource(holder.itemView.context, item.uri)
            prepare()
            start()
            setOnCompletionListener {
                stopAudio()
                notifyItemChanged(position)
            }
        }

        currentPlayingPosition = position
    }

    private fun stopAudio() {
        mediaPlayer?.release()
        mediaPlayer = null
        val oldPosition = currentPlayingPosition
        currentPlayingPosition = -1
        if (oldPosition != -1) notifyItemChanged(oldPosition)
    }

    fun releasePlayer() {
        stopAudio()
    }
}
