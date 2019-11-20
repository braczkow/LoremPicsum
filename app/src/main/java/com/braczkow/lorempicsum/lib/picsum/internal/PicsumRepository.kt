package com.braczkow.lorempicsum.lib.picsum.internal

import android.content.Context
import com.braczkow.lorempicsum.lib.picsum.PicsumEntry
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject
import javax.inject.Singleton

interface PicsumRepository {
    fun addImages(picsum: List<PicsumEntry>, pageNo: Int)
    fun getPiclist(): Observable<List<PicsumEntry>>
    fun getPagesFetched(): Int
}

@Singleton
class PicsumRepositoryImpl @Inject constructor(private val context: Context) :
    PicsumRepository {

    data class PicsListStorage(val picsum: List<PicsumEntry>)
    private val PAGES_FETCHED_KEY = "PAGES_FETCHED_KEY"

    private val gson = Gson()

    private val prefs by lazy {
        context.getSharedPreferences(context.packageName + "_prefs", Context.MODE_PRIVATE)
    }

    private val piclistPublisher = BehaviorSubject.createDefault(loadPicList())

    override fun addImages(picsum: List<PicsumEntry>, pageNo: Int) {
        val storage =
            PicsListStorage(
                loadPicList() + picsum
            )

        prefs
            .edit()
            .putString(PicsListStorage::class.java.simpleName, gson.toJson(storage))
            .putInt(PAGES_FETCHED_KEY, pageNo)
            .apply()

        piclistPublisher.onNext(storage.picsum)
    }

    override fun getPiclist(): Observable<List<PicsumEntry>> = piclistPublisher

    override fun getPagesFetched() = prefs.getInt(PAGES_FETCHED_KEY, 0)

    private fun loadPicList(): List<PicsumEntry> {
        if (prefs.contains(PicsListStorage::class.java.simpleName)) {
            return gson.fromJson(prefs.getString(PicsListStorage::class.java.simpleName, ""), PicsListStorage::class.java).picsum
        } else {
            return listOf()
        }
    }

}
