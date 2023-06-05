package com.codingWithUmair.app.notify.fileSystem

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.codingWithUmair.app.notify.R
import java.io.File

class NotifyFileProvider: FileProvider(R.xml.file_paths){

	companion object{

		fun getImageUri(context: Context): Uri {

			val directory = File(context.cacheDir, "images")

			if (!directory.exists()) {
				directory.mkdir()
			}

			val file = File.createTempFile(
				"selected_image_",
				".jpg",
				directory
			)

			val authority = context.packageName + ".fileProvider"

			return getUriForFile(
				context,
				authority,
				file
			)

		}
	}
}