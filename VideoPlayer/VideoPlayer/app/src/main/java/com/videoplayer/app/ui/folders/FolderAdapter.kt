package com.videoplayer.app.ui.folders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.videoplayer.app.R
import com.videoplayer.app.data.FolderItem
import com.videoplayer.app.databinding.ItemFolderBinding

class FolderAdapter(
    private val onFolderClick: (FolderItem) -> Unit
) : ListAdapter<FolderItem, FolderAdapter.FolderViewHolder>(FolderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val binding = ItemFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FolderViewHolder(private val binding: ItemFolderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(folder: FolderItem) {
            binding.apply {
                textFolderName.text = folder.name
                textVideoCount.text = "${folder.videoCount} vídeos"
                textDuration.text = folder.totalDurationFormatted

                folder.firstVideoUri?.let { uri ->
                    Glide.with(imageCover.context)
                        .load(uri)
                        .apply(
                            RequestOptions()
                                .centerCrop()
                                .transform(RoundedCorners(16))
                                .placeholder(R.drawable.bg_thumbnail_placeholder)
                                .error(R.drawable.bg_thumbnail_placeholder)
                        )
                        .into(imageCover)
                }

                root.setOnClickListener { onFolderClick(folder) }
            }
        }
    }

    class FolderDiffCallback : DiffUtil.ItemCallback<FolderItem>() {
        override fun areItemsTheSame(oldItem: FolderItem, newItem: FolderItem) = oldItem.path == newItem.path
        override fun areContentsTheSame(oldItem: FolderItem, newItem: FolderItem) = oldItem == newItem
    }
}
