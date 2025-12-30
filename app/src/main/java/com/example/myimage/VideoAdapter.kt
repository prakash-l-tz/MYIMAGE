package com.example.myimage

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File

class VideoAdapter(
    private val files: List<File>,
    private val onDelete: (File) -> Unit
) : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnail: ImageView = view.findViewById(R.id.imageView)
        val playIcon: ImageView = view.findViewById(R.id.videoIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = files[position]

        holder.playIcon.visibility = View.VISIBLE

        Glide.with(holder.thumbnail.context)
            .load(file)
            .centerCrop()
            .into(holder.thumbnail)

        // ▶ OPEN VIDEO PLAYER
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ViewVideoActivity::class.java)
            intent.putStringArrayListExtra(
                "video_list",
                ArrayList(files.map { it.absolutePath })
            )
            intent.putExtra("position", position)
            holder.itemView.context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener {
            onDelete(file)
            true
        }
    }

    override fun getItemCount(): Int = files.size
}