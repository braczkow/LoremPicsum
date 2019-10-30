package com.braczkow.lorempicsum.lib.util

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SchedulersFactory {
    fun io() = Schedulers.io()
    fun main() = AndroidSchedulers.mainThread()
}