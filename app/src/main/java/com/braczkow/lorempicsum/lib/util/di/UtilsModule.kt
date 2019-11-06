package com.braczkow.lorempicsum.lib.util.di

import com.braczkow.lorempicsum.lib.util.SchedulersFactory
import com.braczkow.lorempicsum.lib.util.SchedulersFactoryImpl
import dagger.Module
import dagger.Provides

@Module
class UtilsModule {
    @Provides
    fun provideSchedulersFactory(): SchedulersFactory = SchedulersFactoryImpl()
}