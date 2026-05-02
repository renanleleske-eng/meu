package com.videoplayer.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.videoplayer.app.data.FolderItem
import com.videoplayer.app.data.SortOrder
import com.videoplayer.app.data.VideoItem
import com.videoplayer.app.data.VideoRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VideoRepository(application)

    private val _videos = MutableLiveData<List<VideoItem>>()
    val videos: LiveData<List<VideoItem>> = _videos

    private val _folders = MutableLiveData<List<FolderItem>>()
    val folders: LiveData<List<FolderItem>> = _folders

    private val _searchResults = MutableLiveData<List<VideoItem>>()
    val searchResults: LiveData<List<VideoItem>> = _searchResults

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    var currentSortOrder = SortOrder.DATE_DESC
        private set

    fun loadVideos(sortOrder: SortOrder = currentSortOrder) {
        currentSortOrder = sortOrder
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _videos.value = repository.getAllVideos(sortOrder)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadFolders() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _folders.value = repository.getFolders()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            try {
                _searchResults.value = repository.searchVideos(query)
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            }
        }
    }

    fun getVideosByFolder(folderPath: String, callback: (List<VideoItem>) -> Unit) {
        viewModelScope.launch {
            val result = repository.getVideosByFolder(folderPath)
            callback(result)
        }
    }
}
