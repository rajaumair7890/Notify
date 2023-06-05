package com.codingWithUmair.app.notify.model

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class User(
	val id: Int = 0,
	val name: String = "",
	val picture: Bitmap? = null
)

@Entity(tableName = "note")
data class Note(
	@PrimaryKey(autoGenerate = true)
	val id: Int = 0,
	val title: String = "",
	val description: String? = null,
	@ColumnInfo(name = "image_url")
	val imageUrl: String? = null,
	val list: List<NoteCheckListItem>? = null,
	val noteType: NoteType,
	val colorsIndex: Int = 0
)

data class NoteCheckListItem(
	val isChecked: Boolean,
	val description: String
)

enum class NoteType{
	Text, List, Image
}
