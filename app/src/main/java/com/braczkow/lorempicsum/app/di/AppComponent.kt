package com.braczkow.lorempicsum.app.di

import com.braczkow.lorempicsum.lib.picsum.di.PicsumModule
import com.braczkow.lorempicsum.lib.util.di.UtilsModule
import com.braczkow.lorempicsum.ux.main.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        ViewModelModule::class,
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

    fun mainActivity(): MainActivity.DaggerComponent.Builder
}