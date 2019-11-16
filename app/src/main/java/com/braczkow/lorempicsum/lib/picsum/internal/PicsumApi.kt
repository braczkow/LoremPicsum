package com.braczkow.lorempicsum.lib.picsum.internal

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface PicsumApi {

    data class ListEntry(
        val id: String,
        val author: String,
        val width: String,
        val height: String,
        val url: String,
        val download_url: String
    )

    @GET("v2/list")
    fun getPicsList(
        @Query("page") page: Int? = null
    ): Single<List<ListEntry>>
}