package com.agermolin.playlistmaker.library.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agermolin.playlistmaker.library.domain.interactor.ICreatePlaylistInteractor
import com.agermolin.playlistmaker.library.domain.interactor.IObservePlaylistDetailInteractor
import com.agermolin.playlistmaker.library.domain.interactor.IUpdatePlaylistInteractor
import com.agermolin.playlistmaker.library.domain.model.PlaylistDetailResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val createPlaylistInteractor: ICreatePlaylistInteractor,
    private val updatePlaylistInteractor: IUpdatePlaylistInteractor,
    private val observePlaylistDetailInteractor: IObservePlaylistDetailInteractor,
) : ViewModel() {

    data class PlaylistEditData(
        val name: String,
        val description: String,
        val coverPath: String?,
    )

    private val _canCreatePlaylist = MutableLiveData(false)
    val canCreatePlaylist: LiveData<Boolean> = _canCreatePlaylist

    private val _coverImageUri = MutableLiveData<Uri?>(null)
    val coverImageUri: LiveData<Uri?> = _coverImageUri

    private val _isEditMode = MutableLiveData(false)
    val isEditMode: LiveData<Boolean> = _isEditMode

    private val _editData = MutableLiveData<PlaylistEditData?>()
    val editData: LiveData<PlaylistEditData?> = _editData

    private val playlistNameState = MutableLiveData("")
    private val playlistDescriptionState = MutableLiveData("")
    private var editingPlaylistId: Long? = null
    private var initialName: String = ""
    private var initialDescription: String = ""

    private val _saveSuccess = MutableLiveData<String?>()
    val saveSuccess: LiveData<String?> = _saveSuccess

    private val _saveCompleted = MutableLiveData<Long>()
    val saveCompleted: LiveData<Long> = _saveCompleted

    private val _saveError = MutableLiveData<Long>()
    val saveError: LiveData<Long> = _saveError

    fun onPlaylistNameChanged(raw: String) {
        playlistNameState.value = raw
        _canCreatePlaylist.value = raw.trim().isNotEmpty()
    }

    fun onDescriptionChanged(raw: String) {
        playlistDescriptionState.value = raw
    }

    fun setCoverImageUri(uri: Uri?) {
        _coverImageUri.value = uri
    }

    fun hasUnsavedChanges(): Boolean {
        if (_isEditMode.value == true) {
            val name = (playlistNameState.value ?: "").trim()
            val desc = (playlistDescriptionState.value ?: "").trim()
            val hasNewCover = _coverImageUri.value != null
            return hasNewCover || name != initialName || desc != initialDescription
        }
        val name = (playlistNameState.value ?: "").trim().isNotEmpty()
        val desc = (playlistDescriptionState.value ?: "").trim().isNotEmpty()
        val cover = _coverImageUri.value != null
        return name || desc || cover
    }

    fun initEditMode(playlistId: Long) {
        if (editingPlaylistId == playlistId) return
        editingPlaylistId = playlistId
        _isEditMode.value = true
        viewModelScope.launch {
            when (val result = observePlaylistDetailInteractor.observePlaylistDetail(playlistId).first()) {
                is PlaylistDetailResult.Content -> {
                    val playlist = result.playlist
                    initialName = playlist.name
                    initialDescription = playlist.description
                    playlistNameState.value = initialName
                    playlistDescriptionState.value = initialDescription
                    _canCreatePlaylist.value = initialName.isNotBlank()
                    _editData.value = PlaylistEditData(
                        name = initialName,
                        description = initialDescription,
                        coverPath = playlist.coverImagePath,
                    )
                }
                PlaylistDetailResult.NotFound -> {
                    _saveError.postValue(System.nanoTime())
                }
            }
        }
    }

    fun createPlaylist() {
        val name = (playlistNameState.value ?: "").trim()
        if (name.isEmpty()) return
        val description = (playlistDescriptionState.value ?: "").trim()
        val cover = _coverImageUri.value
        viewModelScope.launch {
            try {
                val editId = editingPlaylistId
                if (editId == null) {
                    createPlaylistInteractor.execute(name, description, cover)
                    _saveSuccess.postValue(name)
                } else {
                    updatePlaylistInteractor.updatePlaylist(editId, name, description, cover)
                }
                _saveCompleted.postValue(System.nanoTime())
            } catch (_: Exception) {
                _saveError.postValue(System.nanoTime())
            }
        }
    }

    fun consumeSaveSuccess() {
        _saveSuccess.value = null
    }
}
