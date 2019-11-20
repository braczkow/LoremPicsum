package com.braczkow.lorempicsum.ux.main

import android.view.View
import android.widget.ImageView

interface MainNavigation {
    sealed class Destination {
        data class ImageDetails(
            val id: String,
            val downloadUrl: String,
            val author: String,
            val view: View
        ) : Destination()
    }

    fun navigate(to: Destination)
}