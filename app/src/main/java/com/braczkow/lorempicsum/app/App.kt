package com.braczkow.lorempicsum.app

import android.app.Application
import com.braczkow.lorempicsum.BuildConfig
import timber.log.Timber

class App : Application(){
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}