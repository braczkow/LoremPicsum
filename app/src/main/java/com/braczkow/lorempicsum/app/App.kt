package com.braczkow.lorempicsum.app

import android.app.Application
import com.braczkow.lorempicsum.BuildConfig
import com.braczkow.lorempicsum.app.di.AppComponent
import timber.log.Timber

class App : Application(){
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    companion object {
        private lateinit var appComponent : AppComponent
        fun dagger() = appComponent
    }
}