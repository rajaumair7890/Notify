package com.codingWithUmair.app.notify.ui.screens.drawingNote

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.codingWithUmair.app.notify.NotifyApplication
import com.codingWithUmair.app.notify.data.NotifyRepository
import com.codingWithUmair.app.notify.fileSystem.LocalFileStorageRepository
import com.codingWithUmair.app.notify.model.Note
import com.codingWithUmair.app.notify.model.NoteType
import kotlinx.coroutines.launch

class DrawingViewModel(
	private val localFileStorageRepository: LocalFileStorageRepository,
	private val notifyRepository: NotifyRepository
): ViewModel() {

	var currentImageBitmap: ImageBitmap? by mutableStateOf(null)

	fun updateCurrentImageBitmap(imageBitmap: ImageBitmap){
		currentImageBitmap = imageBitmap
	}

	var title by mutableStateOf("")

	fun updateTitle(newTitle: String){
		title = newTitle
	}

	var currentColorsIndex by mutableStateOf(0)

	fun updateCurrentColors(newColorsIndex: Int){
		currentColorsIndex = newColorsIndex
	}

	private var initialNoteId = 0
	private var initialNoteImageUrl = ""

	fun updateInitialUiState(note: Note){
		viewModelScope.launch {
			initialNoteId = note.id
			title = note.title
			initialNoteImageUrl = note.imageUrl.toString()
			currentImageBitmap = note.imageUrl?.let {
				localFileStorageRepository.loadImageFromInternalStorage(
					it
				).first().asImageBitmap()
			}
			currentColorsIndex = note.colorsIndex
		}
	}

	fun addOrUpdateDrawingNoteInDataBase(imageBitmap: ImageBitmap): Boolean{

		val fileName = title + System.currentTimeMillis().toString()

		return if(
			localFileStorageRepository.saveImageToInternalStorage(
				fileName, imageBitmap.asAndroidBitmap()
			)
		){
			viewModelScope.launch {
				if(initialNoteId == 0){
					notifyRepository.addNoteToDataBase(
						Note(
							title = title,
							imageUrl = fileName,
							noteType = NoteType.Image,
							colorsIndex = currentColorsIndex
						)
					)
				}else{
					notifyRepository.updateNote(
						Note(
							id = initialNoteId,
							title = title,
							imageUrl = fileName,
							noteType = NoteType.Image,
							colorsIndex = currentColorsIndex
						)
					)
					localFileStorageRepository.deleteImageFromInternalStorage(initialNoteImageUrl)
				}
			}
			true
		}else{
			false
		}
	}

	companion object{
		val factory = viewModelFactory {
			initializer {
				val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as NotifyApplication)
				DrawingViewModel(
					application.container.localFileStorageRepository,
					application.container.notifyRepository
				)
			}
		}
	}

}