package com.codingWithUmair.app.notify.ui.screens.listNote

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codingWithUmair.app.notify.R
import com.codingWithUmair.app.notify.model.Note
import com.codingWithUmair.app.notify.model.NoteCheckListItem
import com.codingWithUmair.app.notify.ui.screens.utils.NoteBackground


@Composable
fun ListNoteEditScreen(
	note: Note? = null,
	isDarkTheme: Boolean,
	onBackClick: () -> Unit
){

	val viewModel: ListNoteViewModel = viewModel(factory = ListNoteViewModel.factory)

	val context = LocalContext.current

	LaunchedEffect(Unit){
		if (note != null){
			viewModel.updateInitialUiState(note)
		}
	}

	fun handleBackClick(){
		if (viewModel.addOrUpdateListNoteInDataBase()){
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
		NoteCheckList(
			listOfNoteCheckListItem = viewModel.listOfItem,
			onListItemCheckStateChange = {viewModel.updateCurrentSelectedListItemCheckState()},
			onListItemDescriptionChange = viewModel::updateCurrentSelectedListItemDescription,
			addNewListItem = viewModel::addNewItemToList,
			onCurrentSelectedListItemChange = viewModel::updateCurrentSelectedListItem,
			onItemDeleteClick = viewModel::deleteItem
		)
	}
}



@Composable
fun NoteCheckList(
	listOfNoteCheckListItem: List<NoteCheckListItem>,
	onItemDeleteClick: () -> Unit,
	addNewListItem: () -> Unit,
	onCurrentSelectedListItemChange: (NoteCheckListItem) -> Unit,
	onListItemDescriptionChange: (String) -> Unit,
	onListItemCheckStateChange: (Boolean) -> Unit,
	modifier: Modifier = Modifier
){
	LazyColumn(
		modifier = modifier
	){
		items(listOfNoteCheckListItem.filter { !it.isChecked }){listItem ->
			NoteCheckListItemLayout(
				noteCheckListItem = listItem,
				onListItemCheckStateChange = onListItemCheckStateChange,
				onListItemDescriptionChange = onListItemDescriptionChange,
				onCurrentSelectedListItemChange = onCurrentSelectedListItemChange,
				onItemDeleteClick = onItemDeleteClick
			)
		}

		item{
			Spacer(modifier = Modifier.size(12.dp))
		}

		item {
			AddNewListItemLayout(addNewListItem = addNewListItem)
		}

		if(!listOfNoteCheckListItem.none { it.isChecked }){
			item{
				Spacer(modifier = Modifier.size(12.dp))
			}

			item{
				Text(
					text = stringResource(id = R.string.checked_items), fontWeight = FontWeight.SemiBold
				)
			}
		}

		items(listOfNoteCheckListItem.filter { it.isChecked }){listItem ->
			NoteCheckListItemLayout(
				noteCheckListItem = listItem,
				onListItemCheckStateChange = onListItemCheckStateChange,
				onListItemDescriptionChange = onListItemDescriptionChange,
				onCurrentSelectedListItemChange = onCurrentSelectedListItemChange,
				onItemDeleteClick = onItemDeleteClick
			)
		}
	}
}

@Composable
fun NoteCheckListItemLayout(
	noteCheckListItem: NoteCheckListItem,
	onItemDeleteClick: () -> Unit,
	onCurrentSelectedListItemChange: (NoteCheckListItem) -> Unit,
	onListItemCheckStateChange: (Boolean) -> Unit,
	onListItemDescriptionChange: (String) -> Unit,
	modifier: Modifier = Modifier
){
	OutlinedTextField(
		value = noteCheckListItem.description,
		onValueChange = onListItemDescriptionChange,
		singleLine = true,
		textStyle = TextStyle.Default.copy(textDecoration = if (noteCheckListItem.isChecked) TextDecoration.LineThrough else TextDecoration.Underline),
		colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.Transparent, focusedBorderColor = Color.Transparent),
		modifier = modifier
			.fillMaxWidth()
			.onFocusChanged {
				if (it.isFocused) {
					onCurrentSelectedListItemChange(noteCheckListItem)
				}
			},
		leadingIcon = {
			Checkbox(
				checked = noteCheckListItem.isChecked,
				onCheckedChange = {
					onCurrentSelectedListItemChange(noteCheckListItem)
					onListItemCheckStateChange(it)
				}
			)
		},
		trailingIcon = {
			IconButton(
				onClick = {
					onCurrentSelectedListItemChange(noteCheckListItem)
					onItemDeleteClick()
				}
			){
				Icon(
					Icons.Outlined.Delete,
					stringResource(id = R.string.delete_item)
				)
			}
		}
	)
}


@Composable
fun AddNewListItemLayout(
	addNewListItem: () -> Unit,
	modifier: Modifier = Modifier
){
	Row(
		modifier = modifier
			.fillMaxWidth()
			.padding(horizontal = 6.dp, vertical = 12.dp)
			.clickable(onClick = addNewListItem),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(6.dp)
	) {

		Icon(Icons.Default.Add, stringResource(id = R.string.add_new_list_item))

		Text(
			text = stringResource(id = R.string.add_new_list_item),
			fontWeight = FontWeight.SemiBold
		)
	}
}

@Preview
@Composable
fun Preview(){
	NoteCheckListItemLayout(
		noteCheckListItem = NoteCheckListItem(false, "Buy Milk"),
		onItemDeleteClick = { /*TODO*/ },
		onCurrentSelectedListItemChange = {},
		onListItemCheckStateChange = {},
		onListItemDescriptionChange = {}
	)
}