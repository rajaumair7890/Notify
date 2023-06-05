package com.codingWithUmair.app.notify.data

import android.content.Context
import com.codingWithUmair.app.notify.data.dataBase.NotifyDataBase
import com.codingWithUmair.app.notify.fileSystem.DefaultNotifyFileProviderRepository
import com.codingWithUmair.app.notify.fileSystem.LocalFileStorageRepository
import com.codingWithUmair.app.notify.fileSystem.NotifyFileProviderRepository

interface AppContainer {
	val localFileStorageRepository: LocalFileStorageRepository
	val notifyRepository: NotifyRepository
	val notifyFileProviderRepository: NotifyFileProviderRepository
}

class DefaultAppContainer(private val context: Context): AppContainer{

	override val localFileStorageRepository: LocalFileStorageRepository by lazy {
		LocalFileStorageRepository(context)
	}
	override val notifyRepository: NotifyRepository by lazy {
		OfflineNotifyRepository(NotifyDataBase.getDataBase(context).noteDao())
	}
	override val notifyFileProviderRepository: NotifyFileProviderRepository by lazy{
		DefaultNotifyFileProviderRepository(context)
	}

}