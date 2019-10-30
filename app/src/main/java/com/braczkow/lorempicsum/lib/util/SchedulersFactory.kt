package com.braczkow.lorempicsum.lib.util

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

interface SchedulersFactory {
    fun io() = Schedulers.io()
    fun main() = AndroidSchedulers.mainThread()
}

class SchedulersFactoryImpl: SchedulersFactory