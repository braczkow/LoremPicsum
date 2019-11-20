package com.braczkow.lorempicsum.ux.main

import android.content.Context
import com.braczkow.lorempicsum.lib.picsum.PicsumEntry
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.mockito.MockitoAnnotations

class ImagesAdapterTest {

    @MockK
    lateinit var navigation: MainNavigation

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    private fun makePicsumEntry(id: String) = PicsumEntry(
        id, "author $id", "200", "200", id, "download $id"
    )

    private val imagesList = listOf(
        makePicsumEntry("one"),
        makePicsumEntry("two"),
        makePicsumEntry("tree")
    )

    @Test
    fun `bindItem uses download_url to load image`() {
        val testee = ImagesAdapter(navigation)

        val viewMock = mockk<ImagesAdapter.ImageView>(relaxed = true)

        testee.bindItem(viewMock, imagesList[2])

        verify {
            viewMock.loadImage("download tree")
        }
    }

    @Test
    fun `bindItem sets clicks that navigate`() {
        val testee = ImagesAdapter(navigation)

        val viewMock = mockk<ImagesAdapter.ImageView>(relaxed = true)

        val slot = slot<()->Unit>()
        every {
            viewMock.imageClicks(capture(slot))
        } returns Unit

        testee.bindItem(viewMock, imagesList[2])

        slot.captured()

        verify {
            navigation.navigate(any())
        }
    }
}