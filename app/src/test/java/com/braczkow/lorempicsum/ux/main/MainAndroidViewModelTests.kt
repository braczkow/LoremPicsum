package com.braczkow.lorempicsum.ux.main

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import com.braczkow.lorempicsum.TestSchedulerFactory
import com.braczkow.lorempicsum.lib.picsum.PicsumApi
import com.braczkow.lorempicsum.lib.picsum.PicsumRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.reactivex.schedulers.TestScheduler
import org.junit.rules.TestRule
import org.junit.Rule
import java.util.concurrent.TimeUnit


class MainAndroidViewModelTests {

    @get:Rule
    public val rule: TestRule = InstantTaskExecutorRule()

    @MockK
    lateinit var picsumApi: PicsumApi

    @MockK(relaxed = true)
    lateinit var picsumRepository: PicsumRepository


    val lifecycle = LifecycleRegistry(mockk())

    val sf = TestSchedulerFactory()

    lateinit var vm: AndroidViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)


    }

    fun makeTestee() {
        vm = AndroidViewModel(picsumApi, picsumRepository, sf)
    }

    private fun makeEntry(id: String) = PicsumApi.ListEntry(id, "author", "200", "200", "url", "download_url")

    @Test
    fun `when Repo has empty list, presenter fetches images`() {
        every {
            picsumRepository.getPiclist()
        } returns Observable.just(listOf())

        val list = listOf(
            makeEntry("1"),
            makeEntry("2")
        )
        every {
            picsumApi.getPicsList(any())
        } returns Single.just(list)


        makeTestee()


        verify {
            picsumRepository.getPiclist()
        }
    }

    @Test
    fun `when Repo has empty list, presenter stores the piclist`() {
        every {
            picsumRepository.getPiclist()
        } returns Observable.just(listOf())

        val list = listOf(
            makeEntry("1"),
            makeEntry("2")
        )
        every {
            picsumApi.getPicsList(any())
        } returns Single.just(list)


        makeTestee()
        sf.scheduler.triggerActions()

        verify {
            picsumRepository.addImages(list, 1)
        }
    }

    @Test
    fun `when Repo has non-empty list, new images are not fetched automatically`() {
        every {
            picsumRepository.getPiclist()
        } returns Observable.just(listOf(
            PicsumApi.ListEntry("1", "author", "200", "200", "url", "download_url")
        ))

        every {
            picsumApi.getPicsList()
        } returns Single.just(listOf())


        makeTestee()
        sf.scheduler.triggerActions()

        verify(exactly = 0) {
            picsumApi.getPicsList()
        }
    }

    @Test
    fun `when images are requested, first request is not throttled`() {
        every {
            picsumRepository.getPiclist()
        } returns Observable.just(listOf(
            PicsumApi.ListEntry("1", "author", "200", "200", "url", "download_url")
        ))

        every {
            picsumApi.getPicsList(any())
        } returns Single.just(listOf())


        makeTestee()

        vm.requestImages()

        sf.scheduler.triggerActions()

        verify(exactly = 1) {
            picsumApi.getPicsList(any())
        }
    }

    @Test
    fun `when images are requested, subsequent requests are throttled`() {
        every {
            picsumRepository.getPiclist()
        } returns Observable.just(listOf(
            PicsumApi.ListEntry("1", "author", "200", "200", "url", "download_url")
        ))

        every {
            picsumApi.getPicsList(any())
        } returns Single.just(listOf())


        makeTestee()

        vm.requestImages()
        vm.requestImages()
        vm.requestImages()
        sf.scheduler.triggerActions()

        verify(exactly = 1) {
            picsumApi.getPicsList(any())
        }
    }

    @Test
    fun `when images are requested, subsequent requests are throttled for some time`() {
        every {
            picsumRepository.getPiclist()
        } returns Observable.just(listOf(
            PicsumApi.ListEntry("1", "author", "200", "200", "url", "download_url")
        ))

        every {
            picsumApi.getPicsList(any())
        } returns Single.just(listOf())


        makeTestee()

        vm.requestImages()
        vm.requestImages()
        vm.requestImages()
        sf.scheduler.triggerActions()

        verify(exactly = 1) {
            picsumApi.getPicsList(any())
        }

        sf.scheduler.advanceTimeBy(10, TimeUnit.SECONDS)
        vm.requestImages()
        sf.scheduler.triggerActions()


        verify(exactly = 2) {
            picsumApi.getPicsList(any())
        }
    }


}