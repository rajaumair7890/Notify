package com.codingWithUmair.app.notify.fileSystem

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class LocalFileStorageRepository(
	private val context: Context
) {

	fun saveImageToInternalStorage(filename: String, bitmap: Bitmap): Boolean{
		return try {
			context.openFileOutput(filename, MODE_PRIVATE).use {stream ->
				val success = bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
				if (!success){
					throw IOException("Couldn't save Bitmap!")
				}
			}
			true
		}catch(e: IOException){
			e.printStackTrace()
			false
		}
	}

	suspend fun loadImageFromInternalStorage(filename: String): List<Bitmap>{
		return withContext(Dispatchers.IO) {
			context.filesDir.listFiles()?.filter {
				it.canRead() && it.isFile && it.nameWithoutExtension == filename
			}?.map{
				val bytes = it.readBytes()
				BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
			} ?: listOf()
		}
	}

	fun deleteImageFromInternalStorage(filename: String): Boolean{
		return try {
			context.deleteFile(filename)
		}catch(e: Exception){
			e.printStackTrace()
			false
		}
	}

	fun getImageBitmapFromContentUri(uri: Uri): Bitmap{
		context.contentResolver.openInputStream(uri).use { inputStream ->
			return BitmapFactory.decodeStream(inputStream)
		}
	}

}