package com.braczkow.lorempicsum.app.di

import com.braczkow.lorempicsum.lib.di.PicsumModule
import com.braczkow.lorempicsum.lib.di.UtilsModule
import com.braczkow.lorempicsum.ux.main.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        PicsumModule::class,
        UtilsModule::class
    ]
)
interface AppComponent {
    @Component.Builder
    interface Builder {
        fun build(): AppComponent
        fun appModule(appModule: AppModule): Builder
    }

    fun inject(activity: MainActivity)
}