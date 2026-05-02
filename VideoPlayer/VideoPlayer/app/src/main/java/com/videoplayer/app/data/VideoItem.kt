package com.videoplayer.app.data

import android.net.Uri

data class VideoItem(
    val id: Long,
    val title: String,
    val displayName: String,
    val duration: Long,        // em milissegundos
    val size: Long,            // em bytes
    val path: String,
    val uri: Uri,
    val folderName: String,
    val folderPath: String,
    val dateAdded: Long,
    val dateModified: Long,
    val width: Int = 0,
    val height: Int = 0,
    val mimeType: String = "video/mp4"
) {
    val durationFormatted: String
        get() {
            val totalSeconds = duration / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            return if (hours > 0) {
                String.format("%d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format("%02d:%02d", minutes, seconds)
            }
        }

    val sizeFormatted: String
        get() = when {
            size >= 1_073_741_824 -> String.format("%.1f GB", size / 1_073_741_824.0)
            size >= 1_048_576 -> String.format("%.1f MB", size / 1_048_576.0)
            size >= 1_024 -> String.format("%.1f KB", size / 1_024.0)
            else -> "$size B"
        }

    val resolutionLabel: String
        get() = when {
            height >= 2160 -> "4K"
            height >= 1080 -> "FHD"
            height >= 720 -> "HD"
            height >= 480 -> "SD"
            else -> ""
        }
}

data class FolderItem(
    val name: String,
    val path: String,
    val videoCount: Int,
    val totalDuration: Long,
    val firstVideoUri: android.net.Uri?
) {
    val totalDurationFormatted: String
        get() {
            val totalSeconds = totalDuration / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            return if (hours > 0) {
                String.format("%d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format("%02d:%02d", minutes, seconds)
            }
        }
}
