package com.videoplayer.app.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.videoplayer.app.R
import com.videoplayer.app.data.VideoItem
import com.videoplayer.app.databinding.ItemVideoBinding

class VideoListAdapter(
    private val onVideoClick: (VideoItem) -> Unit
) : ListAdapter<VideoItem, VideoListAdapter.VideoViewHolder>(VideoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VideoViewHolder(private val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(video: VideoItem) {
            binding.apply {
                textTitle.text = video.displayName.substringBeforeLast(".")
                textDuration.text = video.durationFormatted
                textSize.text = video.sizeFormatted
                textFolder.text = video.folderName

                if (video.resolutionLabel.isNotEmpty()) {
                    textResolution.text = video.resolutionLabel
                    textResolution.visibility = android.view.View.VISIBLE
                } else {
                    textResolution.visibility = android.view.View.GONE
                }

                Glide.with(imageThumbnail.context)
                    .load(video.uri)
                    .apply(
                        RequestOptions()
                            .centerCrop()
                            .transform(RoundedCorners(12))
                            .placeholder(R.drawable.bg_thumbnail_placeholder)
                            .error(R.drawable.bg_thumbnail_placeholder)
                    )
                    .into(imageThumbnail)

                root.setOnClickListener { onVideoClick(video) }
            }
        }
    }

    class VideoDiffCallback : DiffUtil.ItemCallback<VideoItem>() {
        override fun areItemsTheSame(oldItem: VideoItem, newItem: VideoItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: VideoItem, newItem: VideoItem) = oldItem == newItem
    }
}
