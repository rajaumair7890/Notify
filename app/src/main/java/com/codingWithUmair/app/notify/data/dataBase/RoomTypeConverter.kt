package com.codingWithUmair.app.notify.data.dataBase

import androidx.room.TypeConverter
import com.codingWithUmair.app.notify.model.NoteCheckListItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RoomTypeConverter {

	@TypeConverter
	fun fromListItemToString(list: List<NoteCheckListItem>): String = Gson().toJson(list)

	@TypeConverter
	fun fromStringToListItem(json: String): List<NoteCheckListItem>{

		val listType = object : TypeToken<List<NoteCheckListItem>>(){}.type

		return Gson().fromJson(json, listType)

	}

}