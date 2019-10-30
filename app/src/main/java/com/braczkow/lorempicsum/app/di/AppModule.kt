package com.braczkow.lorempicsum.app.di

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class AppModule(val context: Context) {
    @Provides
    fun provideContext() = context
}