package com.codingWithUmair.app.notify.data.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.codingWithUmair.app.notify.model.Note

@Database(entities = [Note::class], version = 1, exportSchema = false)
@TypeConverters(RoomTypeConverter::class)
abstract class NotifyDataBase: RoomDatabase(){

	abstract fun noteDao(): NoteDao

	companion object{

		@Volatile
		private var INSTANCE: NotifyDataBase? = null

		fun getDataBase(context: Context): NotifyDataBase {

			return INSTANCE ?: synchronized(this){
				Room.databaseBuilder(
					context = context,
					klass = NotifyDataBase::class.java,
					name = "notify_database"
				)
					.fallbackToDestructiveMigration()
					.build()
					.also { INSTANCE = it }

			}
		}
	}

}