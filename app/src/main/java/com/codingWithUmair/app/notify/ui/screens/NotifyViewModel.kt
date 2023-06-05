package com.codingWithUmair.app.notify.ui.screens

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.codingWithUmair.app.notify.NotifyApplication
import com.codingWithUmair.app.notify.data.NotifyRepository
import com.codingWithUmair.app.notify.fileSystem.LocalFileStorageRepository
import com.codingWithUmair.app.notify.fileSystem.NotifyFileProviderRepository
import com.codingWithUmair.app.notify.model.Note
import com.codingWithUmair.app.notify.model.NoteType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NotifyViewModel(
	private val notifyRepository: NotifyRepository,
	private val localFileStorageRepository: LocalFileStorageRepository,
	private val notifyFileProviderRepository: NotifyFileProviderRepository
): ViewModel() {

	var searchTerm by mutableStateOf("")

	fun updateSearchTerm(newSearchTerm: String){
		searchTerm = newSearchTerm
		loadAllNotes()
	}

	var allNotes by mutableStateOf<List<Note>>(listOf())

	fun loadAllNotes(){
		viewModelScope.launch {
			allNotes = if(searchTerm.isEmpty()) notifyRepository.getAllNotes().first() else notifyRepository.getNotesByQuery(searchTerm).first()
		}
	}

	init{
		loadAllNotes()
	}

	var currentSelectedNote by mutableStateOf(Note(noteType = NoteType.Text))

	fun updateCurrentSelectedNote(note: Note){
		currentSelectedNote = note
	}

	var selectedImageBitmap by mutableStateOf<ImageBitmap?>(null)

	fun updateSelectedImageBitmap(bitmap: ImageBitmap?){
		selectedImageBitmap = bitmap
	}

	fun getImageBitmapFromActivityLauncherUri(uri: Uri){
		selectedImageBitmap = localFileStorageRepository.getImageBitmapFromContentUri(uri).asImageBitmap()
	}

	var tempCameraImageFileUri: Uri? = null

	fun getUriForImage(){
		tempCameraImageFileUri = notifyFileProviderRepository.getImageUri()
	}

	fun loadImageFromTempFileUri(){
		selectedImageBitmap = tempCameraImageFileUri?.let {
			localFileStorageRepository.getImageBitmapFromContentUri(
				it
			).asImageBitmap()
		}
	}

	private var noteToDelete: Note? = null

	fun updateNoteToDelete(note: Note?){
		noteToDelete = note
	}

	fun deleteNote(){
		if(noteToDelete != null){
			if(noteToDelete!!.noteType == NoteType.Image){
				if(localFileStorageRepository.deleteImageFromInternalStorage(noteToDelete!!.imageUrl.toString())){
					viewModelScope.launch {
						notifyRepository.deleteNote(noteToDelete!!)
					}
				}
			}else{
				viewModelScope.launch {
					notifyRepository.deleteNote(noteToDelete!!)
				}
			}
			noteToDelete = null
		}
	}

companion object{
		val factory = viewModelFactory {
			initializer {
				val application = (this[APPLICATION_KEY] as NotifyApplication)
				NotifyViewModel(
					application.container.notifyRepository,
					application.container.localFileStorageRepository,
					application.container.notifyFileProviderRepository
				)
			}
		}
	}
}