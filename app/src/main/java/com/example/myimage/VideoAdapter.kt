package com.example.myimage

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File

class VideoAdapter(
    private val files: MutableList<File>,
    private val onAction: (List<File>, Boolean) -> Unit
) : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    private val selectedItems = mutableSetOf<File>()
    var isSelectionMode = false
        private set

    val selectedItemCount: Int
        get() = selectedItems.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val videoIcon: ImageView = view.findViewById(R.id.videoIcon)
        val selectionOverlay: View = view.findViewById(R.id.selectionOverlay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = files[position]
        val isVideo = file.extension.lowercase() == "mp4"

        holder.videoIcon.visibility = if (isVideo) View.VISIBLE else View.GONE

        Glide.with(holder.imageView.context)
            .load(file)
            .centerCrop()
            .into(holder.imageView)

        holder.selectionOverlay.isVisible = selectedItems.contains(file)

        holder.itemView.setOnClickListener {
            if (isSelectionMode) {
                toggleSelection(file)
            } else {
                val intent = Intent(holder.itemView.context, ViewVideoActivity::class.java)
                intent.putStringArrayListExtra("video_list", ArrayList(files.map { it.absolutePath }))
                intent.putExtra("position", holder.bindingAdapterPosition)
                holder.itemView.context.startActivity(intent)
            }
        }

        holder.itemView.setOnLongClickListener {
            if (!isSelectionMode) {
                isSelectionMode = true
                onAction(emptyList(), true)
            }
            toggleSelection(file)
            true
        }
    }

    private fun toggleSelection(file: File) {
        if (selectedItems.contains(file)) {
            selectedItems.remove(file)
        } else {
            selectedItems.add(file)
        }
        notifyItemChanged(files.indexOf(file))
        onAction(selectedItems.toList(), true)
    }

    fun deleteSelected() {
        onAction(selectedItems.toList(), false)
    }

    fun clearSelection() {
        selectedItems.clear()
        isSelectionMode = false
        notifyDataSetChanged()
        onAction(emptyList(), false)
    }

    override fun getItemCount() = files.size
}
