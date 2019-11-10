package com.braczkow.lorempicsum

import com.braczkow.lorempicsum.lib.util.SchedulersFactory
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler

class TestSchedulerFactory() : SchedulersFactory {
    val scheduler = TestScheduler()

    override fun io(): Scheduler {
        return scheduler
    }

    override fun main(): Scheduler {
        return scheduler
    }
}