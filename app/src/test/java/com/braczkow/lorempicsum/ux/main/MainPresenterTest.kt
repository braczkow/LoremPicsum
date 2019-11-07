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

class MainPresenterTest {

    @MockK
    lateinit var picsumApi: PicsumApi

    @MockK(relaxed = true)
    lateinit var picsumRepository: PicsumRepository

    @MockK(relaxed = true)
    lateinit var view: MainActivity.MainView

    val lifecycle = LifecycleRegistry(mockk())

    val sf = TestSchedulerFactory()

    lateinit var presenter: MainPresenter

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        presenter = MainPresenter(
            picsumApi,
            picsumRepository,
            sf,
            view,
            lifecycle
        )

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
            picsumApi.getPicsList()
        } returns Single.just(list)


        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)

        verify {
            picsumRepository.addImages(list)
        }
    }

    @Test
    fun `when Repo has empty list, presenter stores the piclist`() {
        every {
            picsumRepository.getPiclist()
        } returns Observable.just(listOf())

        every {
            picsumApi.getPicsList()
        } returns Single.just(listOf())


        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)

        verify {
            picsumApi.getPicsList()
        }
    }

    @Test
    fun `when Repo has non-empty list, new images are not fetched`() {
        every {
            picsumRepository.getPiclist()
        } returns Observable.just(listOf(
            PicsumApi.ListEntry("1", "author", "200", "200", "url", "download_url")
        ))

        every {
            picsumApi.getPicsList()
        } returns Single.just(listOf())


        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)

        verify(exactly = 0) {
            picsumApi.getPicsList()
        }
    }

}