package com.codingWithUmair.app.notify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.codingWithUmair.app.notify.ui.screens.NotifyApp
import com.codingWithUmair.app.notify.ui.theme.NotifyTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		WindowCompat.setDecorFitsSystemWindows(window, false)
		super.onCreate(savedInstanceState)
		setContent {
			NotifyTheme {
				NotifyApp()
			}
		}
	}
}
