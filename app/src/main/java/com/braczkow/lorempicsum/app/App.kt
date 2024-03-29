package com.braczkow.lorempicsum.app

import android.app.Application
import com.braczkow.lorempicsum.BuildConfig
import com.braczkow.lorempicsum.app.di.AppComponent
import com.braczkow.lorempicsum.app.di.AppModule
import com.braczkow.lorempicsum.app.di.DaggerAppComponent
import timber.log.Timber

class App : Application(){
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        appComponent = DaggerAppComponent
            .builder()
            .appModule(AppModule(applicationContext))
            .build()
    }

    companion object {
        private lateinit var appComponent : AppComponent
        fun dagger() = appComponent
    }
}