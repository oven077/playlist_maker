package com.agermolin.playlistmaker.library.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agermolin.playlistmaker.library.domain.interactor.ICreatePlaylistInteractor
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val createPlaylistInteractor: ICreatePlaylistInteractor,
) : ViewModel() {

    private val _canCreatePlaylist = MutableLiveData(false)
    val canCreatePlaylist: LiveData<Boolean> = _canCreatePlaylist

    private val _coverImageUri = MutableLiveData<Uri?>(null)
    val coverImageUri: LiveData<Uri?> = _coverImageUri

    private val playlistNameState = MutableLiveData("")
    private val playlistDescriptionState = MutableLiveData("")

    private val _saveSuccess = MutableLiveData<String?>()
    val saveSuccess: LiveData<String?> = _saveSuccess

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
        val name = (playlistNameState.value ?: "").trim().isNotEmpty()
        val desc = (playlistDescriptionState.value ?: "").trim().isNotEmpty()
        val cover = _coverImageUri.value != null
        return name || desc || cover
    }

    fun createPlaylist() {
        val name = (playlistNameState.value ?: "").trim()
        if (name.isEmpty()) return
        val description = (playlistDescriptionState.value ?: "").trim()
        val cover = _coverImageUri.value
        viewModelScope.launch {
            try {
                createPlaylistInteractor.execute(name, description, cover)
                _saveSuccess.postValue(name)
            } catch (_: Exception) {
                _saveError.postValue(System.nanoTime())
            }
        }
    }

    fun consumeSaveSuccess() {
        _saveSuccess.value = null
    }
}
