package com.codingWithUmair.app.notify.ui.screens.mainScreen


import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import coil.compose.AsyncImage
import com.codingWithUmair.app.notify.R
import com.codingWithUmair.app.notify.model.Note
import com.codingWithUmair.app.notify.model.NoteType
import com.codingWithUmair.app.notify.model.User
import com.codingWithUmair.app.notify.ui.screens.utils.backgroundColors


@Composable
fun MainScreen(
	user: User,
	allNotes: List<Note>,
	searchTerm: String,
	onSearchTermChange: (String) -> Unit,
	onUserIconClick: (User) -> Unit,
	onNoteClick: (Note) -> Unit,
	onNoteDeleteClick: (Note) -> Unit,
	onDeletionConfirm: () -> Unit,
	onDeletionCancel: () -> Unit,
	onAddNoteClick: () -> Unit,
	onAddDrawingClick: () -> Unit,
	onAddCheckListClick: () -> Unit,
	onAddImageClick: () -> Unit,
	onTakePhotoClick: () -> Unit,
	isDarkTheme: Boolean,
	modifier: Modifier = Modifier
){
	var addImagePopUpState by remember{ mutableStateOf(false) }

	var confirmDeleteDialogState by remember{ mutableStateOf(false) }

	Scaffold(
		bottomBar = {
			NotifyBottomBar(
				onAddImageClick = { addImagePopUpState = true },
				onAddCheckListClick = onAddCheckListClick,
				onAddDrawingClick = onAddDrawingClick
			)
		},
		floatingActionButton = {
			FloatingActionButton(
				onClick = onAddNoteClick,
				elevation = FloatingActionButtonDefaults.elevation(12.dp)
			) {
				Icon(
					Icons.Default.Add,
					tint = MaterialTheme.colorScheme.onPrimaryContainer,
					contentDescription = stringResource(
						id = R.string.add_a_new_note
					)
				)
			}
		},
		floatingActionButtonPosition = FabPosition.End,
	){paddingValues ->
		Box(modifier = modifier.fillMaxSize()){
			Column(
				modifier = modifier.padding(paddingValues)
			) {
				SearchBar(
					searchTerm = searchTerm,
					onSearchTermChange = onSearchTermChange,
					user = user,
					onUserIconClick = onUserIconClick,
					modifier = modifier
						.fillMaxWidth()
						.padding(top = 6.dp, bottom = 6.dp, start = 12.dp, end = 12.dp)
				)

				NotesGrid(
					notes = allNotes,
					onNoteClick = onNoteClick,
					onNoteDeleteClick = {
						confirmDeleteDialogState = true
						onNoteDeleteClick(it)
					},
					isDarkTheme = isDarkTheme
				)
			}
			if(addImagePopUpState){
				Popup(
					alignment = Alignment.Center,
					onDismissRequest = {addImagePopUpState = false}
				) {
					AddImageNotePopUp(
						onChooseImageClick = {
							addImagePopUpState = false
							onAddImageClick()
						},
						onTakePhotoClick = {
							addImagePopUpState = false
							onTakePhotoClick()
						}
					)
				}
			}
			if (confirmDeleteDialogState){
				ConfirmDeleteDialog(
					onDismiss = {
						confirmDeleteDialogState = false
						onDeletionCancel()
					},
					onDeletionConfirm = {
						confirmDeleteDialogState = false
						onDeletionConfirm()
					}
				)
			}
		}
	}

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesGrid(
	notes: List<Note>,
	onNoteClick: (Note) -> Unit,
	onNoteDeleteClick: (Note) -> Unit,
	isDarkTheme: Boolean,
	modifier: Modifier = Modifier
){
	LazyVerticalStaggeredGrid(
		columns = StaggeredGridCells.Fixed(2),
		contentPadding = PaddingValues(8.dp),
		modifier = modifier
	){
		items(notes){ note ->
			NoteCard(
				note = note,
				onNoteClick = onNoteClick,
				onNoteDeleteClick = onNoteDeleteClick,
				isDarkTheme = isDarkTheme,
				modifier = modifier.padding(8.dp)
			)
		}
	}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
	note: Note,
	onNoteClick: (Note) -> Unit,
	onNoteDeleteClick: (Note) -> Unit,
	isDarkTheme: Boolean,
	modifier: Modifier = Modifier
){
	OutlinedCard(
		modifier = modifier
			.combinedClickable(
				onClick = { onNoteClick(note) },
				onLongClick = { onNoteDeleteClick(note) }
			),
		colors = CardDefaults.cardColors(
			containerColor = if(isDarkTheme) backgroundColors[note.colorsIndex].second else backgroundColors[note.colorsIndex].first
		),
		shape = RoundedCornerShape(25.dp)
	){
		if(note.title.isNotEmpty()){
			Text(
				text = note.title,
				fontWeight = FontWeight.Bold,
				modifier = Modifier.padding(12.dp)
			)
		}

		when(note.noteType) {
			NoteType.Text -> {
				Text(
					text = note.description ?: "",
					maxLines = 10,
					overflow = TextOverflow.Ellipsis,
					modifier = Modifier.padding(12.dp)
				)
			}

			NoteType.List -> {
				note.list?.forEach {
					ListNoteLayout(isChecked = it.isChecked, description = it.description)
				}
			}

			NoteType.Image -> {
				ImageNoteLayout(note = note)
			}
		}
	}
}


@Composable
fun SearchBar(
	searchTerm: String,
	onSearchTermChange: (String) -> Unit,
	user: User,
	onUserIconClick: (User) -> Unit,
	modifier: Modifier = Modifier
){
	OutlinedTextField(
		value = searchTerm,
		onValueChange = onSearchTermChange,
		placeholder = {
			Text(text = stringResource(id = R.string.search_place_holder))
		},
		leadingIcon = {
			Icon(
				imageVector = Icons.Outlined.Search,
				contentDescription = stringResource(id = R.string.search_place_holder),
				tint = MaterialTheme.colorScheme.primary
			)
		},
		trailingIcon = {
			IconButton(onClick = { onUserIconClick(user) }) {
				if (user.picture != null){
					Icon(
						bitmap = user.picture.asImageBitmap(),
						contentDescription = user.name
					)
				}else{
					/*
					Icon(
						painter = painterResource(id = R.drawable.outline_account_circle_24),
						contentDescription = user.name,
						tint = MaterialTheme.colorScheme.primary
					)

					 */
				}
			}
		},
		shape = RoundedCornerShape(50.dp),
		colors = OutlinedTextFieldDefaults.colors(
			focusedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
			unfocusedContainerColor =  MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
			focusedBorderColor = Color.Transparent,
			unfocusedBorderColor = Color.Transparent
		),
		modifier = modifier
	)
}

@Composable
fun NotifyBottomBar(
	onAddImageClick: () -> Unit,
	onAddCheckListClick: () -> Unit,
	onAddDrawingClick: () -> Unit,
	modifier: Modifier = Modifier
) {
	Surface(
		color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
	) {
		Row(
			modifier = modifier.fillMaxWidth()
		) {
			Spacer(modifier = Modifier.width(12.dp))
			IconButton(
				onClick = onAddImageClick,
				modifier = Modifier.padding(vertical = 4.dp)
			) {
				Icon(
					painterResource(id = R.drawable.outline_image_24),
					contentDescription = stringResource(id = R.string.add_image),
					tint = MaterialTheme.colorScheme.primary
				)
			}
			IconButton(
				onClick = onAddDrawingClick,
				modifier = Modifier.padding(vertical = 4.dp)
			) {
				Icon(
					painterResource(id = R.drawable.outline_brush_24),
					contentDescription = stringResource(id = R.string.add_drawing),
					tint = MaterialTheme.colorScheme.primary
				)
			}
			IconButton(
				onClick = onAddCheckListClick,
				modifier = Modifier.padding(vertical = 4.dp)
			) {
				Icon(
					painterResource(id = R.drawable.outline_checklist_24),
					contentDescription = stringResource(id = R.string.add_checkList),
					tint = MaterialTheme.colorScheme.primary
				)
			}
		}
	}
}

@Composable
private fun ListNoteLayout(
	isChecked: Boolean,
	description: String
){
	Row(
		modifier = Modifier.fillMaxWidth().padding(12.dp)
	) {
		Checkbox(checked = isChecked, onCheckedChange = null)
		Text(
			text = description,
			textDecoration = if(isChecked) TextDecoration.LineThrough else TextDecoration.None
		)
	}
}

@Composable
fun ImageNoteLayout(
	note: Note,
	modifier: Modifier = Modifier
){
	val context = LocalContext.current
	val file = context.filesDir.listFiles()?.firstOrNull {
		it.canRead() && it.isFile && it.nameWithoutExtension == note.imageUrl
	}
	AsyncImage(model = file, contentDescription = null, modifier = modifier)
}

@Composable
fun AddImageNotePopUp(
	onChooseImageClick: () -> Unit,
	onTakePhotoClick: () -> Unit,
	modifier: Modifier = Modifier
){
	Surface(
		modifier = modifier
			.fillMaxWidth()
			.padding(24.dp),
		color = MaterialTheme.colorScheme.surfaceVariant,
		shape = RoundedCornerShape(25.dp),
		tonalElevation = 12.dp,
		shadowElevation = 12.dp
	) {
		Column(
			horizontalAlignment = Alignment.Start,
			verticalArrangement = Arrangement.Center,
			modifier = Modifier.padding(12.dp)
		) {
			Text(text = stringResource(id = R.string.add_image), style = MaterialTheme.typography.headlineLarge)
			Spacer(modifier = Modifier.size(12.dp))
			AddImageNotePopupRowItem(
				icon = painterResource(id = R.drawable.outline_image_24),
				text = stringResource(id = R.string.choose_image),
				onClick = onChooseImageClick
			)
			Spacer(modifier = Modifier.size(24.dp))
			AddImageNotePopupRowItem(
				icon = painterResource(id = R.drawable.outline_photo_camera_24),
				text = stringResource(id = R.string.take_photo),
				onClick = onTakePhotoClick
			)
		}
	}
}

@Composable
private fun AddImageNotePopupRowItem(
	icon: Painter,
	text: String,
	onClick: () -> Unit,
	modifier: Modifier = Modifier
){
	Row(
		modifier = modifier
			.fillMaxWidth()
			.clickable(onClick = onClick) ,
		horizontalArrangement = Arrangement.Start,
		verticalAlignment = Alignment.CenterVertically
	) {
		Icon(
			icon, contentDescription = text,
			modifier = Modifier.padding(horizontal = 12.dp),
			tint = MaterialTheme.colorScheme.primary
		)
		Text(
			text = text,
			style = MaterialTheme.typography.labelMedium,
			color = MaterialTheme.colorScheme.primary
		)
	}
}

@Composable
fun ConfirmDeleteDialog(
	onDismiss: () -> Unit,
	onDeletionConfirm: () -> Unit,
	modifier: Modifier = Modifier
){
	AlertDialog(
		modifier = modifier,
		onDismissRequest = onDismiss,
		confirmButton = {
			TextButton(onClick = onDeletionConfirm) {
				Text(text = stringResource(id = R.string.delete))
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(text = stringResource(id = R.string.cancel))
			}
		},
		title = {
			Text(text = stringResource(id = R.string.confirm_deletion))
		}
	)
}

