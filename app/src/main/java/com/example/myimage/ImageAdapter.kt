package com.example.myimage


import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.FileProvider
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

        // ▶ Show play icon for videos
        holder.videoIcon.visibility = if (isVideo) View.VISIBLE else View.GONE

        // Load thumbnail (works for image + video)
        Glide.with(holder.imageView.context)
            .load(file)
            .centerCrop()
            .into(holder.imageView)

        // CLICK → OPEN
        holder.itemView.setOnClickListener {
            if (isVideo) {
                openVideo(holder.itemView, file)
            } else {
                val intent = Intent(holder.itemView.context, ViewImageActivity::class.java)
                intent.putExtra("image_path", file.absolutePath)
                holder.itemView.context.startActivity(intent)
            }
        }

        // LONG CLICK → DELETE
        holder.itemView.setOnLongClickListener {
            onDelete(file)
            true
        }
    }

    override fun getItemCount() = files.size

    // ---------------- OPEN VIDEO SAFELY ----------------
    private fun openVideo(view: View, file: File) {
        val context = view.context

        val uri: Uri = FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "video/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(intent)
    }
}
