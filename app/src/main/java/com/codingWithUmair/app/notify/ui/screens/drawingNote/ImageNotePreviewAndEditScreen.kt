package com.codingWithUmair.app.notify.ui.screens.drawingNote

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import com.codingWithUmair.app.notify.ui.screens.utils.NoteBackground

@Composable
fun ImageNotePreviewAndEditScreen(
	title: String,
	onTitleChange: (String) -> Unit,
	imageBitmap: ImageBitmap?,
	onImageClick: () -> Unit,
	onBackClick: () -> Unit,
	currentColorsIndex: Int,
	onColorsIndexChange: (Int) -> Unit,
	isDarkTheme: Boolean,
	modifier: Modifier = Modifier
){

	BackHandler(true) {
		onBackClick()
	}

	NoteBackground(
		title = title,
		onTitleChange = onTitleChange,
		currentColorsIndex = currentColorsIndex,
		onColorsIndexChange = onColorsIndexChange,
		isDarkTheme = isDarkTheme,
		onBackClick = onBackClick,
		modifier = modifier
	) {
		if (imageBitmap != null) {
			Image(
				bitmap = imageBitmap,
				contentDescription = title,
				modifier = Modifier
					.fillMaxWidth()
					.clickable { onImageClick() }
			)
		}
	}
}