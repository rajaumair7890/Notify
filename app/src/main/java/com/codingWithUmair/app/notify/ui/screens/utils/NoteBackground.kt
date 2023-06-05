package com.codingWithUmair.app.notify.ui.screens.utils

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codingWithUmair.app.notify.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteBackground(
	title: String,
	onTitleChange: (String) -> Unit,
	currentColorsIndex: Int,
	onColorsIndexChange: (Int) -> Unit,
	isDarkTheme: Boolean,
	onBackClick: () -> Unit,
	modifier: Modifier = Modifier,
	content: @Composable () -> Unit,
){
	val noteColor = if (isDarkTheme) backgroundColors[currentColorsIndex].second else backgroundColors[currentColorsIndex].first

	Scaffold(
		topBar = {
			TopAppBar(
				title = {},
				navigationIcon = {
					IconButton(onClick = onBackClick) {
						Icon(
							Icons.Default.ArrowBack,
							contentDescription = stringResource(id = R.string.back)
						)
					}
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = noteColor
				)
			)
		},
		bottomBar = {
			BottomAppBar(
				containerColor = noteColor
			){
				ColorBar(
					currentColorsIndex = currentColorsIndex,
					onColorsIndexChange = onColorsIndexChange,
					isDarkTheme = isDarkTheme
				)
			}
		},
		modifier = modifier
	) {
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(it)
				.background(color = noteColor)
		) {
			OutlinedTextField(
				value = title,
				onValueChange = onTitleChange,
				modifier = Modifier.fillMaxWidth(),
				textStyle = TextStyle.Default.copy(
					fontWeight = FontWeight.SemiBold,
					fontSize = 24.sp
				),
				placeholder = { Text(text = stringResource(id = R.string.title)) },
				colors = OutlinedTextFieldDefaults.colors(
					focusedContainerColor = Color.Transparent,
					unfocusedContainerColor = Color.Transparent,
					unfocusedBorderColor = Color.Transparent,
					focusedBorderColor = Color.Transparent
				)
			)
			ElevatedCard(
				modifier = Modifier.heightIn(min = 300.dp).padding(12.dp),
				shape = RoundedCornerShape(25.dp),
				elevation = CardDefaults.elevatedCardElevation(12.dp),
				colors = CardDefaults.elevatedCardColors(
					containerColor = noteColor
				)
			){
				content()
			}
		}
	}
}

@Composable
fun ColorBar(
	currentColorsIndex: Int,
	onColorsIndexChange: (Int) -> Unit,
	isDarkTheme: Boolean,
	modifier: Modifier = Modifier,
){
	LazyRow(
		modifier = modifier.fillMaxWidth(),
		contentPadding = PaddingValues(6.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(6.dp)
	){
		items(backgroundColors){
			ColorRowItem(
				colorsIndex = backgroundColors.indexOf(it),
				onColorsIndexChange = onColorsIndexChange,
				isDarkTheme = isDarkTheme,
				isSelected = currentColorsIndex == backgroundColors.indexOf(it)
			)
		}
	}
}

@Composable
fun ColorRowItem(
	colorsIndex: Int,
	onColorsIndexChange: (Int) -> Unit,
	isDarkTheme: Boolean,
	isSelected: Boolean,
	modifier: Modifier = Modifier
){
	Box(
		modifier = modifier
			.background(
				if (isDarkTheme) backgroundColors[colorsIndex].second else backgroundColors[colorsIndex].first,
				CircleShape
			)
			.size(50.dp)
			.border(1.dp, Color.Black, CircleShape)
			.clickable { onColorsIndexChange(colorsIndex) },
		contentAlignment = Alignment.Center
	){
		if(colorsIndex == 0){
			Icon(
				painter = painterResource(id = R.drawable.outline_format_color_reset_24),
				contentDescription = null,
				modifier = Modifier.matchParentSize()
			)
		}
		if(isSelected){
			Box(
				modifier = Modifier
					.background(Color.Black.copy(0.5f), CircleShape)
					.matchParentSize(),
				contentAlignment = Alignment.Center
			){
				Icon(
					painter = painterResource(id = R.drawable.outline_check_24),
					contentDescription = null,
					modifier = Modifier.matchParentSize()
				)
			}
		}
	}
}

val backgroundColors = listOf(
	Pair(Color.White, Color.Black),
	Pair(Color(250, 175, 168),Color(119, 23, 46)),
	Pair(Color(243, 159, 118),Color(105, 43, 23)),
	Pair(Color(255, 248, 184),Color(124, 74, 3)),
	Pair(Color(226, 246, 211),Color(38, 77, 59)),
	Pair(Color(180, 221, 211),Color(12, 98, 93)),
	Pair(Color(212, 228, 237),Color(37, 99, 119)),
	Pair(Color(174, 204, 220),Color(40, 66, 85)),
	Pair(Color(211, 191, 219), Color(71, 46, 91)),
	Pair(Color(246, 226, 221), Color(245, 57, 79)),
	Pair(Color(233, 227, 212), Color(75, 68, 58)),
	Pair(Color(239, 239, 241), Color(35, 36, 39)),
)

