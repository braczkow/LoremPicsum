package com.braczkow.lorempicsum.lib.picsum.di

import com.braczkow.lorempicsum.BuildConfig
import com.braczkow.lorempicsum.lib.picsum.internal.PicsumApi
import com.braczkow.lorempicsum.lib.picsum.internal.PicsumRepository
import com.braczkow.lorempicsum.lib.picsum.internal.PicsumRepositoryImpl
import com.braczkow.lorempicsum.lib.picsum.usecase.FetchImages
import com.braczkow.lorempicsum.lib.picsum.usecase.FetchImagesImpl
import com.braczkow.lorempicsum.lib.picsum.usecase.GetPiclist
import com.braczkow.lorempicsum.lib.picsum.usecase.GetPiclistImpl
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module(includes = [ PicsumModule.PicsumBindings::class ])
class PicsumModule {
    @Provides
    fun providePicsumApi(): PicsumApi {
        val httpClient = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            httpClient.addInterceptor(
                HttpLoggingInterceptor(
                    HttpLoggingInterceptor.Logger { message -> println(message) }
                ).setLevel(HttpLoggingInterceptor.Level.BODY))
        }

        return Retrofit.Builder()
            .baseUrl("https://picsum.photos")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .client(httpClient.build())
            .build()
            .create(PicsumApi::class.java)
    }

    @Module
    abstract class PicsumBindings {
        @Binds
        abstract fun bindGetpiclist(impl: GetPiclistImpl) : GetPiclist

        @Binds
        abstract fun bindPicsumRepository(impl: PicsumRepositoryImpl): PicsumRepository

        @Binds
        abstract fun bindFetchImages(impl: FetchImagesImpl): FetchImages
    }
}