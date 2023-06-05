package com.codingWithUmair.app.notify

import android.app.Application
import com.codingWithUmair.app.notify.data.AppContainer
import com.codingWithUmair.app.notify.data.DefaultAppContainer

class NotifyApplication: Application(){
	lateinit var container: AppContainer

	override fun onCreate() {
		super.onCreate()
		container = DefaultAppContainer(this)
	}
}