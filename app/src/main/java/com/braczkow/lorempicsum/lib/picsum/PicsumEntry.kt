package com.braczkow.lorempicsum.lib.picsum

data class PicsumEntry(
    val id: String,
    val author: String,
    val width: String,
    val height: String,
    val url: String,
    val download_url: String
)