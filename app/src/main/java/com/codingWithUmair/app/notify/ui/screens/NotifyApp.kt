package com.codingWithUmair.app.notify.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.codingWithUmair.app.notify.model.NoteType
import com.codingWithUmair.app.notify.model.User
import com.codingWithUmair.app.notify.ui.screens.drawingNote.DrawingScreen
import com.codingWithUmair.app.notify.ui.screens.drawingNote.DrawingViewModel
import com.codingWithUmair.app.notify.ui.screens.drawingNote.ImageNotePreviewAndEditScreen
import com.codingWithUmair.app.notify.ui.screens.listNote.ListNoteEditScreen
import com.codingWithUmair.app.notify.ui.screens.mainScreen.MainScreen
import com.codingWithUmair.app.notify.ui.screens.textNote.TextNoteEditScreen

@Composable
fun NotifyApp(){

	val viewModel: NotifyViewModel = viewModel(factory = NotifyViewModel.factory)

	val navController = rememberNavController()

	val context = LocalContext.current

	val isDarkTheme = isSystemInDarkTheme()

	val photoPicker = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.PickVisualMedia(),
		onResult = {uri ->
			if(uri != null){
				viewModel.getImageBitmapFromActivityLauncherUri(uri)
				navController.navigate(NotifyScreensDestinations.AddNewImage.name)
			}
		}
	)

	val cameraLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.TakePicture(),
		onResult = {isSuccess ->
			if(isSuccess){
				viewModel.loadImageFromTempFileUri()
				navController.navigate(NotifyScreensDestinations.AddNewImage.name)
			}
		}
	)

	NavHost(
		navController = navController,
		startDestination = NotifyScreensDestinations.MainScreen.name,
		modifier = Modifier.navigationBarsPadding()
	){

		composable(NotifyScreensDestinations.MainScreen.name){

			MainScreen(
				user = User(),
				allNotes = viewModel.allNotes,
				onUserIconClick = { /*TODO*/ },
				searchTerm = viewModel.searchTerm,
				onSearchTermChange = viewModel::updateSearchTerm,

				onNoteClick = {note ->

					viewModel.updateCurrentSelectedNote(note)

					when(note.noteType){

						NoteType.Text -> {
							navController.navigate(NotifyScreensDestinations.Text.name)
						}

						NoteType.Image -> {
							navController.navigate(NotifyScreensDestinations.Image.name)
						}

						NoteType.List -> {
							navController.navigate(NotifyScreensDestinations.List.name)
						}
					}
				},

				onNoteDeleteClick = {
					viewModel.updateNoteToDelete(it)
				},

				onDeletionConfirm = {
					viewModel.deleteNote()
					viewModel.loadAllNotes()
				},

				onDeletionCancel = {
					viewModel.updateNoteToDelete(null)
				},

				onAddNoteClick = {
					navController.navigate(NotifyScreensDestinations.AddNewText.name)
				},

				onAddImageClick = {
					photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
				},

				onAddDrawingClick = {
					navController.navigate(NotifyScreensDestinations.AddNewDrawing.name)
				},

				onAddCheckListClick = {
					navController.navigate(NotifyScreensDestinations.AddNewList.name)
				},

				onTakePhotoClick = {
					viewModel.getUriForImage()
					cameraLauncher.launch(viewModel.tempCameraImageFileUri)
				},

				isDarkTheme = isDarkTheme
			)
		}

		composable(NotifyScreensDestinations.AddNewText.name){
			TextNoteEditScreen(
				onBackClick = {
					viewModel.loadAllNotes()
					navController.navigateUp()
				},
				isDarkTheme = isDarkTheme
			)
		}

		composable(NotifyScreensDestinations.AddNewImage.name){

			val imageViewModel: DrawingViewModel = viewModel(factory = DrawingViewModel.factory)

			var addNewImageScreenDestinations by rememberSaveable{ mutableStateOf(AddNewImageScreenDestinations.ImagePreview) }

			LaunchedEffect(Unit){
				viewModel.selectedImageBitmap?.let { imageBitmap ->
					imageViewModel.updateCurrentImageBitmap(imageBitmap)
					viewModel.updateSelectedImageBitmap(null)
				}
			}

			when(addNewImageScreenDestinations){

				AddNewImageScreenDestinations.ImagePreview -> {
					ImageNotePreviewAndEditScreen(
						title = imageViewModel.title,
						onTitleChange = imageViewModel::updateTitle,
						imageBitmap = imageViewModel.currentImageBitmap,
						onImageClick = {
							addNewImageScreenDestinations = AddNewImageScreenDestinations.DrawOnImage
						},
						onBackClick = {
							imageViewModel.currentImageBitmap?.let { imageBitmap ->
								if(imageViewModel.addOrUpdateDrawingNoteInDataBase(imageBitmap)){
									Toast.makeText(context, "Drawing Note Saved", Toast.LENGTH_SHORT).show()
								}else{
									Toast.makeText(context, "Note Not Saved", Toast.LENGTH_SHORT).show()
								}
							}
							viewModel.loadAllNotes()
							navController.navigate(route = NotifyScreensDestinations.MainScreen.name){
								popUpTo(NotifyScreensDestinations.MainScreen.name){
									inclusive = true
								}
							}
						},
						isDarkTheme = isDarkTheme,
						currentColorsIndex = imageViewModel.currentColorsIndex,
						onColorsIndexChange = imageViewModel::updateCurrentColors
					)
				}

				AddNewImageScreenDestinations.DrawOnImage -> {
					DrawingScreen(
						onBackClick = { imageBitmap ->
							imageViewModel.updateCurrentImageBitmap(imageBitmap)
							addNewImageScreenDestinations = AddNewImageScreenDestinations.ImagePreview
						},
						imageBitmap = imageViewModel.currentImageBitmap
					)
				}
			}
		}


		composable(NotifyScreensDestinations.AddNewDrawing.name){

			val drawingViewModel: DrawingViewModel = viewModel(factory = DrawingViewModel.factory)

			var drawingScreenDestinations by rememberSaveable{ mutableStateOf(AddNewDrawingScreenDestinations.Drawing) }

			when(drawingScreenDestinations) {
				AddNewDrawingScreenDestinations.Drawing -> {
					DrawingScreen(
						onBackClick = {imageBitmap ->
							drawingViewModel.updateCurrentImageBitmap(imageBitmap)
							drawingScreenDestinations = AddNewDrawingScreenDestinations.DrawingNotePreview
						},
						imageBitmap = drawingViewModel.currentImageBitmap
					)
				}

				AddNewDrawingScreenDestinations.DrawingNotePreview -> {

					ImageNotePreviewAndEditScreen(
						title = drawingViewModel.title,
						onTitleChange = drawingViewModel::updateTitle,
						imageBitmap = drawingViewModel.currentImageBitmap,
						onImageClick = {
							drawingScreenDestinations = AddNewDrawingScreenDestinations.Drawing
						},
						onBackClick = {
							drawingViewModel.currentImageBitmap?.let { imageBitmap ->
								if(drawingViewModel.addOrUpdateDrawingNoteInDataBase(imageBitmap)){
									Toast.makeText(context, "Drawing Note Saved", Toast.LENGTH_SHORT).show()
								}else{
									Toast.makeText(context, "Note Not Saved", Toast.LENGTH_SHORT).show()
								}
							}
							viewModel.loadAllNotes()
							navController.navigate(route = NotifyScreensDestinations.MainScreen.name){
								popUpTo(NotifyScreensDestinations.MainScreen.name){
									inclusive = true
								}
							}
						},
						isDarkTheme = isDarkTheme,
						currentColorsIndex = drawingViewModel.currentColorsIndex,
						onColorsIndexChange = drawingViewModel::updateCurrentColors
					)
				}
			}
		}


		composable(NotifyScreensDestinations.AddNewList.name){
			ListNoteEditScreen(
				onBackClick = {
					viewModel.loadAllNotes()
					navController.navigateUp()
				},
				isDarkTheme = isDarkTheme
			)
		}

		composable(NotifyScreensDestinations.Text.name){
			TextNoteEditScreen(
				note = viewModel.currentSelectedNote,
				onBackClick = {
					viewModel.loadAllNotes()
					navController.navigateUp()
				},
				isDarkTheme = isDarkTheme
			)
		}


		composable(NotifyScreensDestinations.Image.name){

			val imageViewModel: DrawingViewModel = viewModel(factory = DrawingViewModel.factory)
			var imageNotePreviewScreenDestinations by rememberSaveable{ mutableStateOf(ImageNotePreviewScreenDestinations.ImageNotePreview) }

			LaunchedEffect(Unit){
				imageViewModel.updateInitialUiState(viewModel.currentSelectedNote)
			}

			when(imageNotePreviewScreenDestinations){
				ImageNotePreviewScreenDestinations.ImageNotePreview -> {
					ImageNotePreviewAndEditScreen(
						title = imageViewModel.title,
						onTitleChange = imageViewModel::updateTitle,
						imageBitmap = imageViewModel.currentImageBitmap,
						onImageClick = {
							imageNotePreviewScreenDestinations = ImageNotePreviewScreenDestinations.DrawOnImage
						},
						onBackClick = {
							imageViewModel.currentImageBitmap?.let { imageBitmap ->
								if(imageViewModel.addOrUpdateDrawingNoteInDataBase(imageBitmap)){
									Toast.makeText(context, "Drawing Note Saved", Toast.LENGTH_SHORT).show()
								}else{
									Toast.makeText(context, "Note Not Saved", Toast.LENGTH_SHORT).show()
								}
							}
							viewModel.loadAllNotes()
							navController.navigate(route = NotifyScreensDestinations.MainScreen.name){
								popUpTo(NotifyScreensDestinations.MainScreen.name){
									inclusive = true
								}
							}
						},
						isDarkTheme = isDarkTheme,
						currentColorsIndex = imageViewModel.currentColorsIndex,
						onColorsIndexChange = imageViewModel::updateCurrentColors
					)
				}

				ImageNotePreviewScreenDestinations.DrawOnImage -> {
					DrawingScreen(
						onBackClick = { imageBitmap ->
							imageViewModel.updateCurrentImageBitmap(imageBitmap)
							imageNotePreviewScreenDestinations = ImageNotePreviewScreenDestinations.ImageNotePreview
						},
						imageBitmap = imageViewModel.currentImageBitmap
					)
				}
			}
		}



		composable(NotifyScreensDestinations.List.name){
			ListNoteEditScreen(
				note = viewModel.currentSelectedNote,
				onBackClick = {
					viewModel.loadAllNotes()
					navController.navigateUp()
				},
				isDarkTheme = isDarkTheme
			)
		}

	}
}

enum class NotifyScreensDestinations{
	MainScreen,
	List,
	AddNewList,
	Text,
	AddNewText,
	AddNewDrawing,
	AddNewImage,
	Image
}

enum class AddNewDrawingScreenDestinations{
	Drawing, DrawingNotePreview
}

enum class AddNewImageScreenDestinations{
	ImagePreview, DrawOnImage
}

enum class ImageNotePreviewScreenDestinations{
	ImageNotePreview, DrawOnImage
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
	factory: ViewModelProvider.Factory,
	navController: NavHostController
): T{
	val navGraphRoute = destination.parent?.route ?: return viewModel(factory = factory)
	val parentEntry = remember(this){
		navController.getBackStackEntry(navGraphRoute)
	}
	return viewModel(parentEntry, factory = factory)
}
