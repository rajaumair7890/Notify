package com.codingWithUmair.app.notify.ui.screens.textNote

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.codingWithUmair.app.notify.NotifyApplication
import com.codingWithUmair.app.notify.data.NotifyRepository
import com.codingWithUmair.app.notify.model.Note
import com.codingWithUmair.app.notify.model.NoteType
import kotlinx.coroutines.launch


class TextNoteViewModel(
	private val notifyRepository: NotifyRepository
) : ViewModel() {

	var title by mutableStateOf("")

	fun updateTitle(newTitle: String){
		title = newTitle
	}

	var description by mutableStateOf("")

	fun updateDescription(newDescription: String){
		description = newDescription
	}

	var currentColorsIndex by mutableStateOf(0)

	fun updateCurrentColors(newColorsIndex: Int){
		currentColorsIndex = newColorsIndex
	}

	private var initialNoteId = 0

	fun updateInitialUiState(note: Note){
		title = note.title
		description = note.description ?: ""
		initialNoteId = note.id
		currentColorsIndex = note.colorsIndex
	}

	fun addOrUpdateNoteInDataBase(): Boolean{
		return if(description.isNotEmpty()){
			viewModelScope.launch {
				if(initialNoteId == 0){
					notifyRepository.addNoteToDataBase(
						Note(
							title = title,
							description = description,
							noteType = NoteType.Text,
							colorsIndex = currentColorsIndex
						)
					)
				}else{
					notifyRepository.updateNote(
						Note(
							id = initialNoteId,
							title = title,
							description = description,
							noteType = NoteType.Text,
							colorsIndex = currentColorsIndex
						)
					)
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
				TextNoteViewModel(
					application.container.notifyRepository
				)
			}
		}
	}
}