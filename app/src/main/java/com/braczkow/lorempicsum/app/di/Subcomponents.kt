package com.braczkow.lorempicsum.app.di

import com.braczkow.lorempicsum.ux.main.MainActivity
import dagger.Module

@Module(
    subcomponents = [
        MainActivity.DaggerComponent::class
    ]
)
class Subcomponents {
}