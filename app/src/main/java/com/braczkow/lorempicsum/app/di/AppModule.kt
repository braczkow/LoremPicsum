package com.braczkow.lorempicsum.app.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.MapKey
import dagger.Module
import dagger.Provides
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.reflect.KClass

@MapKey
@Target(AnnotationTarget.FUNCTION)
annotation class ViewModelKey(
    val value: KClass<out ViewModel>
)

@Module
class AppModule(val context: Context) {
    @Provides
    fun provideContext() = context

    @Provides
    fun provideViewModelFactory(
        providers: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
    ) = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return requireNotNull(providers[modelClass as Class<out ViewModel>]).get() as T
        }
    }
}