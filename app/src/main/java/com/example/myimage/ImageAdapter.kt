package com.example.myimage

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File

class ImageAdapter(
    private val files: List<File>,
    private val onDelete: (File) -> Unit
) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val videoIcon: ImageView = view.findViewById(R.id.videoIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = files[position]
        val isVideo = file.extension.lowercase() == "mp4"

        // ▶ show play icon on video
        holder.videoIcon.visibility = if (isVideo) View.VISIBLE else View.GONE

        Glide.with(holder.imageView.context)
            .load(file)
            .centerCrop()
            .into(holder.imageView)

        // ✅ CLICK → OPEN VIEWPAGER (IMAGE + VIDEO)
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ViewImage::class.java)

            intent.putStringArrayListExtra(
                "media_list",
                ArrayList(files.map { it.absolutePath })
            )

            intent.putExtra("position", holder.bindingAdapterPosition)

            holder.itemView.context.startActivity(intent)
        }

        // 🗑 LONG CLICK → DELETE
        holder.itemView.setOnLongClickListener {
            onDelete(file)
            true
        }
    }

    override fun getItemCount() = files.size
}
