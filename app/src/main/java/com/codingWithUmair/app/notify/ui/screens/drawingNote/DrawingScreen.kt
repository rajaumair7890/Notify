package com.codingWithUmair.app.notify.ui.screens.drawingNote

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.codingWithUmair.app.notify.R
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.orchestra.colorpicker.AlphaSlideBar
import com.skydoves.orchestra.colorpicker.BrightnessSlideBar
import com.skydoves.orchestra.colorpicker.ColorPicker
import io.getstream.sketchbook.Sketchbook
import io.getstream.sketchbook.rememberSketchbookController

@Composable
fun DrawingScreen(
	onBackClick: (ImageBitmap) -> Unit,
	modifier: Modifier = Modifier,
	imageBitmap: ImageBitmap? = null,
){
	val controller = rememberSketchbookController()
	var showColorPickerDialog by remember { mutableStateOf(false) }
	var isDottedLine by rememberSaveable{ mutableStateOf(false) }

	LaunchedEffect(Unit) {
		controller.setPaintStrokeWidth(23f)
		controller.setPaintColor(Color.Blue)
		if(imageBitmap != null){
			controller.setImageBitmap(imageBitmap)
		}else{
			val bitmap = ImageBitmap(720, 1080)
			val canvas = Canvas(bitmap)
			val paint = Paint()
			paint.color = Color.White
			canvas.drawRect(0f, 0f, 720F, 1080F, paint )
			controller.setImageBitmap(bitmap)
		}
	}

	BackHandler(true){
		onBackClick(controller.getSketchbookBitmap())
	}

	Scaffold(
		modifier = modifier,
		topBar = {
			DrawingTopBar(
				onBackClick = {
					onBackClick(controller.getSketchbookBitmap())
				},
				undoEnabled = controller.canUndo.value,
				onUndo = { controller.undo() },
				redoEnabled = controller.canRedo.value,
				onRedo = { controller.redo() },
				onClearCanvasClick = {
					controller.clear()
				}
			)
		},
		bottomBar = {
			DrawingBottomBar(
				onPenClick = {
					controller.setEraseMode(false)
					controller.setPaintShader(null)
				},
				onBrushClick = {
					controller.setEraseMode(false)
					controller.setLinearShader(rainbowColorList())
				},
				onEraseClick = {
					controller.setEraseMode(true)
				},
				onChangeStrokeWidthClick = {
					controller.setPaintStrokeWidth(it)
					controller.setEraseRadius(it)
				},
				onChangeColorClick = { showColorPickerDialog = !showColorPickerDialog },
				onDashedLineClick = {
					isDottedLine = if(isDottedLine){
						controller.setPathEffect(PathEffect.cornerPathEffect(60f))
						false
					}else{
						controller.setPathEffect(PathEffect.dashPathEffect(floatArrayOf(20f, 40f), 40f))
						true
					}
				},
				isDottedLine = isDottedLine,
				currentSelectedColor = controller.currentPaintColor.value
			)
		}
	) {
		if(showColorPickerDialog){
			DrawingColorPickerDialog(
				onDismissRequest = { showColorPickerDialog = !showColorPickerDialog },
				initialColor = controller.currentPaintColor.value,
				onColorSelected = { controller.setPaintColor(it) }
			)
		}
		Sketchbook(
			modifier = Modifier,
			controller = controller
		)
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DrawingTopBar(
	onBackClick: () -> Unit,
	undoEnabled: Boolean,
	onUndo: () -> Unit,
	redoEnabled: Boolean,
	onRedo: () -> Unit,
	onClearCanvasClick: () -> Unit,
	modifier: Modifier = Modifier
){
	TopAppBar(
		modifier = modifier,
		title = {},
		navigationIcon = {
			IconButton(onClick = onBackClick) {
				Icon(
					Icons.Default.ArrowBack,
					contentDescription = stringResource(id = R.string.back)
				)
			}
		},
		actions = {

			IconButton(
				enabled = undoEnabled,
				onClick = onUndo
			) {
				Icon(
					painter = painterResource(id = R.drawable.baseline_undo_24),
					contentDescription = stringResource(id = R.string.undo)
				)
			}

			IconButton(
				enabled = redoEnabled,
				onClick = onRedo
			) {
				Icon(
					painter = painterResource(id = R.drawable.baseline_redo_24),
					contentDescription = stringResource(id = R.string.undo)
				)
			}

			IconButton(onClick = onClearCanvasClick) {
				Icon(
					Icons.Outlined.Delete,
					contentDescription = stringResource(id = R.string.clear_canvas)
				)
			}
		}
	)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun DrawingBottomBar(
	onPenClick: () -> Unit,
	onBrushClick: () -> Unit,
	onEraseClick: () -> Unit,
	onChangeStrokeWidthClick: (Float) -> Unit,
	onChangeColorClick: () -> Unit,
	isDottedLine: Boolean,
	onDashedLineClick: () -> Unit,
	currentSelectedColor: Color,
	modifier: Modifier = Modifier
){
	var drawingMode by rememberSaveable{ mutableStateOf(DrawingMode.PEN) }
	var widthSelector by remember { mutableStateOf(false) }

	BottomAppBar(
		modifier = modifier
	) {
		AnimatedContent(targetState = widthSelector) {
			if (it){
				DrawingWidthSelector(
					onChangeStrokeWidthClick = {width ->
						onChangeStrokeWidthClick(width)
						widthSelector = false
					},
					color = currentSelectedColor
				)
			}else{
				BottomBarContent(
					onPenClick = onPenClick,
					onBrushClick = onBrushClick,
					onEraseClick = onEraseClick,
					onChangeStrokeWidthClick = {
						widthSelector = true
					},
					onChangeColorClick = onChangeColorClick,
					drawingMode = drawingMode,
					onDrawingModeChange = {newDrawingMode -> drawingMode = newDrawingMode},
					isDottedLine = isDottedLine,
					onDashedLineClick = onDashedLineClick
				)
			}
		}
	}
}

@Composable
private fun BottomBarContent(
	onPenClick: () -> Unit,
	onBrushClick: () -> Unit,
	onEraseClick: () -> Unit,
	onChangeStrokeWidthClick: () -> Unit,
	onChangeColorClick: () -> Unit,
	onDashedLineClick: () -> Unit,
	isDottedLine: Boolean,
	drawingMode: DrawingMode,
	onDrawingModeChange: (DrawingMode) -> Unit,
	modifier: Modifier = Modifier
){
	Row(
		modifier = modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.spacedBy(6.dp),
		verticalAlignment = Alignment.CenterVertically
	) {

		NavigationBarItem(
			selected = drawingMode == DrawingMode.PEN,
			onClick = {
				onDrawingModeChange(DrawingMode.PEN)
				onPenClick()
			},
			icon = {
				Icon(
					painter = painterResource(id = R.drawable.outline_border_color_24),
					contentDescription = stringResource(id = R.string.pen)
				)
			}
		)

		NavigationBarItem(
			selected = drawingMode == DrawingMode.BRUSH,
			onClick = {
				onDrawingModeChange(DrawingMode.BRUSH)
				onBrushClick()
			},
			icon = {
				Image(
					painter = painterResource(id = R.drawable.output_onlinepngtools__1_),
					contentDescription = stringResource(id = R.string.brush),
					modifier = Modifier.size(24.dp)
				)
			}
		)

		NavigationBarItem(
			selected = drawingMode == DrawingMode.ERASE,
			onClick = {
				onDrawingModeChange(DrawingMode.ERASE)
				onEraseClick()
			},
			icon = {
				Icon(
					painter = painterResource(id = R.drawable.eraser),
					contentDescription = stringResource(id = R.string.erase)
				)
			}
		)

		NavigationBarItem(
			selected = isDottedLine,
			onClick = onDashedLineClick,
			icon = {
				Icon(
					painter = painterResource(id = R.drawable.icons8_dashed_line_30),
					contentDescription = stringResource(id = R.string.dotted_Stroke),
					modifier = Modifier.size(24.dp)
				)
			}
		)

		IconButton(onClick = onChangeStrokeWidthClick) {
			Icon(
				painter = painterResource(id = R.drawable.round_line_weight_24),
				contentDescription = stringResource(id = R.string.Change_brush_Stroke),
				modifier = Modifier.rotate(180f)
			)
		}

		IconButton(onClick = onChangeColorClick) {
			Icon(
				painter = painterResource(R.drawable.outline_color_lens_24),
				contentDescription = stringResource(id = R.string.change_color)
			)
		}
	}
}

@Composable
private fun DrawingWidthSelector(
	onChangeStrokeWidthClick: (Float) -> Unit,
	color: Color,
	modifier: Modifier = Modifier
){
	val widthSelectorList = listOf(10f, 20f, 30f, 40f, 60f, 80f, 100f)

	Row(
		modifier = modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceEvenly,
		verticalAlignment = Alignment.CenterVertically
	){
		widthSelectorList.forEach {
			WidthSelectorItem(
				size = it.dp,
				onClick = onChangeStrokeWidthClick,
				color = color
			)
		}
	}
}

@Composable
private fun WidthSelectorItem(
	size: Dp,
	onClick: (Float) -> Unit,
	color: Color,
	modifier: Modifier = Modifier
){
	Box(
		modifier = modifier
			.background(color, shape = CircleShape)
			.size(size / 2)
			.clickable { onClick(size.value) }
	)
}

@Composable
private fun DrawingColorPickerDialog(
	onDismissRequest: () -> Unit,
	initialColor: Color,
	onColorSelected: (Color) -> Unit,
	modifier: Modifier = Modifier
){
	val (selectedColor, setSelectedColor) = remember { mutableStateOf(ColorEnvelope(0)) }

	Dialog(
		onDismissRequest = onDismissRequest
	){
		Surface(
			modifier = modifier,
			shape = RoundedCornerShape(25.dp),
			tonalElevation = 12.dp,
			shadowElevation = 12.dp
		) {
			ColorPicker(
				modifier = Modifier
					.size(400.dp)
					.padding(horizontal = 12.dp),
				initialColor = initialColor,
				onColorListener = { envelope, _ -> setSelectedColor(envelope) },
				children = {
					Column(
						modifier = Modifier.padding(vertical = 20.dp, horizontal = 6.dp),
						horizontalAlignment = Alignment.CenterHorizontally
					) {
						Box(modifier = Modifier.padding(vertical = 6.dp)) {
							AlphaSlideBar(
								modifier = Modifier
									.fillMaxWidth()
									.height(26.dp)
									.clip(RoundedCornerShape(4.dp)),
								colorPickerView = it
							)
						}
						Box(modifier = Modifier.padding(vertical = 6.dp)) {
							BrightnessSlideBar(
								modifier = Modifier
									.fillMaxWidth()
									.height(26.dp)
									.clip(RoundedCornerShape(4.dp)),
								colorPickerView = it
							)
						}

						Spacer(modifier = Modifier.height(8.dp))

						Button(onClick = {
							onColorSelected(Color(selectedColor.color))
							onDismissRequest()
						}) {
							Text(text = stringResource(id = R.string.select))
						}
					}
				}
			)
		}
	}
}

private fun rainbowColorList(): List<Color>{
	return listOf(
		Color.Red,
		Color.Yellow,
		Color.Cyan,
		Color.Green,
		Color.Magenta,
		Color.Blue,
		Color.Red,
		Color.Yellow,
		Color.Green,
		Color.Cyan
	)
}

private enum class DrawingMode{
	PEN, ERASE, BRUSH
}
