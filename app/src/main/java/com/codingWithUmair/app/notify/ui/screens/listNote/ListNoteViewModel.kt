package com.codingWithUmair.app.notify.ui.screens.listNote

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.codingWithUmair.app.notify.model.NoteCheckListItem
import com.codingWithUmair.app.notify.model.NoteType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListNoteViewModel(
	private val notifyRepository: NotifyRepository
): ViewModel() {

	var title by mutableStateOf("")

	fun updateTitle(newTitle: String){
		title = newTitle
	}

	var listOfItem = mutableStateListOf<NoteCheckListItem>()

	fun addNewItemToList(){
		listOfItem.add(NoteCheckListItem(false,""))
	}

	var currentColorsIndex by mutableStateOf(0)

	fun updateCurrentColors(newColorsIndex: Int){
		currentColorsIndex = newColorsIndex
	}

	private var initialNoteId = 0

	fun updateInitialUiState(note: Note){
		title = note.title
		note.list?.forEach {
			listOfItem.add(it)
		}
		initialNoteId = note.id
		currentColorsIndex = note.colorsIndex
	}

	private var currentSelectedListItem = MutableStateFlow(NoteCheckListItem(false, ""))
	private var currentSelectedListItemIndex = listOfItem.indexOf(currentSelectedListItem.value)

	fun updateCurrentSelectedListItem(newItem: NoteCheckListItem){
		currentSelectedListItem.value = newItem
		currentSelectedListItemIndex = listOfItem.indexOf(newItem)
	}

	fun deleteItem(){
		listOfItem.remove(listOfItem[currentSelectedListItemIndex])
	}

	fun updateCurrentSelectedListItemCheckState(){
		currentSelectedListItem.update {
			it.copy(
				isChecked = !currentSelectedListItem.value.isChecked
			)
		}
		listOfItem[currentSelectedListItemIndex] = currentSelectedListItem.value
	}

	fun updateCurrentSelectedListItemDescription(newDescription: String){
		currentSelectedListItem.update {
			it.copy(
				description = newDescription
			)
		}
		listOfItem[currentSelectedListItemIndex] = currentSelectedListItem.value
	}

	fun addOrUpdateListNoteInDataBase(): Boolean{
		return if(listOfItem.isNotEmpty()){
			viewModelScope.launch {
				if(initialNoteId == 0){
					notifyRepository.addNoteToDataBase(
						Note(
							title = title,
							list = listOfItem.toList(),
							noteType = NoteType.List,
							colorsIndex = currentColorsIndex
						)
					)
				}else{
					notifyRepository.updateNote(
						Note(
							id = initialNoteId,
							title = title,
							list = listOfItem.toList(),
							noteType = NoteType.List,
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
				ListNoteViewModel(
					application.container.notifyRepository
				)
			}
		}
	}
}