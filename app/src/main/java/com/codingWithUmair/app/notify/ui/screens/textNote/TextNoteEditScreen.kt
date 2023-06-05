package com.codingWithUmair.app.notify.ui.screens.textNote

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codingWithUmair.app.notify.model.Note
import com.codingWithUmair.app.notify.ui.screens.utils.NoteBackground

@Composable
fun TextNoteEditScreen(
	note: Note? = null,
	isDarkTheme: Boolean,
	onBackClick: () -> Unit
){
	val viewModel: TextNoteViewModel = viewModel(factory = TextNoteViewModel.factory)

	val context = LocalContext.current

	LaunchedEffect(Unit){
		if(note != null){
			viewModel.updateInitialUiState(note)
		}
	}

	fun handleBackClick(){
		if (viewModel.addOrUpdateNoteInDataBase()){
			Toast.makeText(context, "Note Saved", Toast.LENGTH_SHORT).show()
		}else{
			Toast.makeText(context, "Empty Note Discarded", Toast.LENGTH_SHORT).show()
		}
		onBackClick()
	}

	BackHandler(true) {
		handleBackClick()
	}

	NoteBackground(
		title = viewModel.title,
		onTitleChange = viewModel::updateTitle,
		currentColorsIndex = viewModel.currentColorsIndex,
		onColorsIndexChange = viewModel::updateCurrentColors,
		isDarkTheme = isDarkTheme,
		onBackClick = { handleBackClick() }
	){
		OutlinedTextField(
			value = viewModel.description,
			onValueChange = viewModel::updateDescription,
			modifier = Modifier
				.fillMaxSize(1f),
			colors = OutlinedTextFieldDefaults.colors(
				unfocusedBorderColor = Color.Transparent,
				focusedBorderColor = Color.Transparent
			)
		)
	}
}