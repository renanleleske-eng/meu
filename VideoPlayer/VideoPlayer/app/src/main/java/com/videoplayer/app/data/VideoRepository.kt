package com.videoplayer.app.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VideoRepository(private val context: Context) {

    suspend fun getAllVideos(sortOrder: SortOrder = SortOrder.DATE_DESC): List<VideoItem> =
        withContext(Dispatchers.IO) {
            val videos = mutableListOf<VideoItem>()

            val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.BUCKET_ID,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.MIME_TYPE
            )

            val order = when (sortOrder) {
                SortOrder.DATE_DESC -> "${MediaStore.Video.Media.DATE_ADDED} DESC"
                SortOrder.DATE_ASC -> "${MediaStore.Video.Media.DATE_ADDED} ASC"
                SortOrder.NAME_ASC -> "${MediaStore.Video.Media.TITLE} ASC"
                SortOrder.NAME_DESC -> "${MediaStore.Video.Media.TITLE} DESC"
                SortOrder.SIZE_DESC -> "${MediaStore.Video.Media.SIZE} DESC"
                SortOrder.DURATION_DESC -> "${MediaStore.Video.Media.DURATION} DESC"
            }

            val selection = "${MediaStore.Video.Media.DURATION} >= ?"
            val selectionArgs = arrayOf("1000") // mínimo 1 segundo

            context.contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                order
            )?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
                val displayNameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
                val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                val bucketNameCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)
                val dateAddedCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
                val dateModifiedCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)
                val widthCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)
                val heightCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)
                val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                    val path = cursor.getString(dataCol) ?: ""
                    val folderPath = path.substringBeforeLast("/")
                    val folderName = cursor.getString(bucketNameCol) ?: folderPath.substringAfterLast("/")

                    videos.add(
                        VideoItem(
                            id = id,
                            title = cursor.getString(titleCol) ?: "",
                            displayName = cursor.getString(displayNameCol) ?: "",
                            duration = cursor.getLong(durationCol),
                            size = cursor.getLong(sizeCol),
                            path = path,
                            uri = uri,
                            folderName = folderName,
                            folderPath = folderPath,
                            dateAdded = cursor.getLong(dateAddedCol),
                            dateModified = cursor.getLong(dateModifiedCol),
                            width = cursor.getInt(widthCol),
                            height = cursor.getInt(heightCol),
                            mimeType = cursor.getString(mimeCol) ?: "video/mp4"
                        )
                    )
                }
            }

            videos
        }

    suspend fun getFolders(): List<FolderItem> = withContext(Dispatchers.IO) {
        val videos = getAllVideos()
        videos.groupBy { it.folderPath }
            .map { (path, items) ->
                FolderItem(
                    name = items.first().folderName,
                    path = path,
                    videoCount = items.size,
                    totalDuration = items.sumOf { it.duration },
                    firstVideoUri = items.firstOrNull()?.uri
                )
            }
            .sortedBy { it.name.lowercase() }
    }

    suspend fun searchVideos(query: String): List<VideoItem> = withContext(Dispatchers.IO) {
        getAllVideos().filter { video ->
            video.title.contains(query, ignoreCase = true) ||
            video.displayName.contains(query, ignoreCase = true) ||
            video.folderName.contains(query, ignoreCase = true)
        }
    }

    suspend fun getVideosByFolder(folderPath: String): List<VideoItem> = withContext(Dispatchers.IO) {
        getAllVideos().filter { it.folderPath == folderPath }
    }
}

enum class SortOrder {
    DATE_DESC, DATE_ASC, NAME_ASC, NAME_DESC, SIZE_DESC, DURATION_DESC
}
