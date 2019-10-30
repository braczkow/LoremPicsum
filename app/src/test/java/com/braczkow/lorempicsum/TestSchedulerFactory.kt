package com.braczkow.lorempicsum

import com.braczkow.lorempicsum.lib.util.SchedulersFactory
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class TestSchedulerFactory : SchedulersFactory {
    override fun io(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun main(): Scheduler {
        return Schedulers.trampoline()
    }
}