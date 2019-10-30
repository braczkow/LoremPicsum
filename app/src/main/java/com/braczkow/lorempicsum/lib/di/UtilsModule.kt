package com.braczkow.lorempicsum.lib.di

import com.braczkow.lorempicsum.lib.util.SchedulersFactory
import dagger.Module
import dagger.Provides

@Module
class UtilsModule {
    @Provides
    fun provideSchedulersFactory(): SchedulersFactory = SchedulersFactory()
}