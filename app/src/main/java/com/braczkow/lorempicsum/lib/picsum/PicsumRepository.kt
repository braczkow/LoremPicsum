package com.braczkow.lorempicsum.lib.picsum

import android.content.Context
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

interface PicsumRepository {
    fun savePiclist(list: List<PicsumApi.ListEntry>)
    fun getPiclist(): Observable<List<PicsumApi.ListEntry>>
}

@Singleton
class PicsumRepositoryImpl @Inject constructor(private val context: Context) : PicsumRepository {

    data class PicsListStorage(val list: List<PicsumApi.ListEntry>)

    private val gson = Gson()

    private val prefs by lazy {
        context.getSharedPreferences(context.packageName + "_prefs", Context.MODE_PRIVATE)
    }

    private val piclistPublisher = BehaviorSubject.createDefault(loadPicList())

    private val LIST_PREFS_NAME = "LIST_PREFS_NAME"

    override fun savePiclist(list: List<PicsumApi.ListEntry>) {
        prefs
            .edit()
            .putString(LIST_PREFS_NAME, gson.toJson(PicsListStorage(list)))
            .apply()

        piclistPublisher.onNext(list)
    }

    override fun getPiclist(): Observable<List<PicsumApi.ListEntry>> = piclistPublisher

    private fun loadPicList(): List<PicsumApi.ListEntry> {
        return prefs.getString(LIST_PREFS_NAME, null)?.let {
            val storage = gson.fromJson(it, PicsListStorage::class.java)
            storage.list
        } ?: listOf<PicsumApi.ListEntry>()
    }

}

