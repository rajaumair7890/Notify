package com.codingWithUmair.app.notify.fileSystem

import android.content.Context
import android.net.Uri

interface NotifyFileProviderRepository{
	fun getImageUri(): Uri
}

class DefaultNotifyFileProviderRepository(private val context: Context): NotifyFileProviderRepository{
	override fun getImageUri() = NotifyFileProvider.getImageUri(context)
}