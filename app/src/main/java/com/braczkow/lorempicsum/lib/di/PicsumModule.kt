package com.braczkow.lorempicsum.lib.di

import com.braczkow.lorempicsum.BuildConfig
import com.braczkow.lorempicsum.lib.picsum.PicsumApi
import com.braczkow.lorempicsum.lib.picsum.PicsumRepository
import com.braczkow.lorempicsum.lib.picsum.PicsumRepositoryImpl
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
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

    @Provides
    fun providePicsumRepository(impl: PicsumRepositoryImpl): PicsumRepository = impl
}