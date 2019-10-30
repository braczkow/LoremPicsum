package com.braczkow.lorempicsum.lib.di

import com.braczkow.lorempicsum.BuildConfig
import com.braczkow.lorempicsum.lib.PicsumApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

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
            .client(httpClient.build())
            .build()
            .create(PicsumApi::class.java)
    }
}