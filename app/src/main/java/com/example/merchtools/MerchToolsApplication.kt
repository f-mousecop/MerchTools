package com.example.merchtools

import android.app.Application
import android.os.StrictMode
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for Merch Tools used for attaching Hilt to the [Application]
 * object's lifecycle and for dependency injection.
 * */
@HiltAndroidApp
class MerchToolsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setupStrictMode()

    }

    /*
     * We need to enable strict mode for debug builds in order to detect potential
     * memory leaks, given the logged "A resource failed to call close." Logcat warning
     */
    private fun setupStrictMode() {
        if (BuildConfig.DEBUG) {
            val threadPolicy = StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build()

            val vmPolicy = StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .build()

            StrictMode.setThreadPolicy(threadPolicy)
            StrictMode.setVmPolicy(vmPolicy)
        }
    }
}