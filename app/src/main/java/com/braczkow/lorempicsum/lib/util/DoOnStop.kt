package com.braczkow.lorempicsum.lib.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class DoOnStop (lifecycle: Lifecycle, block: () -> Unit) {
    init {
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            fun onStart() {
                block()
            }
        })
    }
}