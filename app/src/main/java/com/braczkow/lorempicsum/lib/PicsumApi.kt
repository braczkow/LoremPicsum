package com.braczkow.lorempicsum.lib

import io.reactivex.Single
import retrofit2.http.GET

interface PicsumApi {

    data class ListResponse(val entries: List<Entry>) {
        data class Entry(val id: String, val author: String, val width: String, val height: String, val url: String, val download_url: String)
    }

    @GET("v2/list")
    fun getPicsList(): Single<ListResponse>
}