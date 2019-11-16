package com.braczkow.lorempicsum.lib.picsum.internal

import com.braczkow.lorempicsum.lib.picsum.PicsumEntry
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface PicsumApi {

    @GET("v2/list")
    fun getPicsList(
        @Query("page") page: Int? = null
    ): Single<List<PicsumEntry>>
}