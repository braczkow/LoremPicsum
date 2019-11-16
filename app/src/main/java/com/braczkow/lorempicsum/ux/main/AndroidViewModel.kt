package com.braczkow.lorempicsum.ux.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.braczkow.lorempicsum.lib.picsum.PicsumEntry
import com.braczkow.lorempicsum.lib.picsum.usecase.FetchImages
import com.braczkow.lorempicsum.lib.picsum.usecase.GetPiclist
import com.braczkow.lorempicsum.lib.util.SchedulersFactory
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AndroidViewModel @Inject constructor(
    private val getPiclist: GetPiclist,
    private val fetchImages: FetchImages,
    private val sf: SchedulersFactory
): ViewModel() {

    val disposables = CompositeDisposable()
    val picsumPicsum : LiveData<List<PicsumEntry>> =
        MutableLiveData()
    val isLoading: LiveData<Boolean> = MutableLiveData()

    private val requests = PublishSubject.create<Unit>()

    init {
        Timber.d("AndroidViewModel init")

        requests
            .throttleFirst(5, TimeUnit.SECONDS, sf.io())
            .observeOn(sf.main())
            .subscribe {
                fetchImages()
            }
            .addTo(disposables)

        getPiclist
            .execute()
            .subscribe {
                if (it.isEmpty()) {
                    requestImages()
                } else {
                    (picsumPicsum as MutableLiveData).postValue(it)
                }
            }
            .addTo(disposables)
    }

    fun requestImages() {
        requests.onNext(Unit)
    }
    private fun fetchImages() {
        if (isLoading.value == true) {
            Timber.d("loading in progress, early return")
            return
        }

        setLoading(true)

        fetchImages
            .execute()
            .subscribe({
                Timber.d("Success fetchImage}")
                setLoading(false)
            }, {
                Timber.e("Failed to fetchImage: $it")
                setLoading(false)
            }).addTo(disposables)
    }

    private fun setLoading(loading: Boolean) {
        (isLoading as MutableLiveData).postValue(loading)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

}