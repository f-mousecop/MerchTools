package com.example.merchtools

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for Merch Tools used for attaching Hilt to the [Application]
 * object's lifecycle and for dependency injection.
 * */
@HiltAndroidApp
class MerchToolsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}