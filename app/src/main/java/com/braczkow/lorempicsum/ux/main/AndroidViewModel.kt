package com.braczkow.lorempicsum.ux.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.braczkow.lorempicsum.lib.picsum.PicsumApi
import com.braczkow.lorempicsum.lib.picsum.PicsumRepository
import com.braczkow.lorempicsum.lib.util.SchedulersFactory
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AndroidViewModel @Inject constructor(
    private val picsumApi: PicsumApi,
    private val picsumRepository: PicsumRepository,
    private val sf: SchedulersFactory
): ViewModel() {

    val disposables = CompositeDisposable()
    val picsumList : LiveData<List<PicsumApi.ListEntry>> =
        MutableLiveData()
    val isLoading: LiveData<Boolean> = MutableLiveData()

    private val requests = PublishSubject.create<Unit>()

    init {
        Timber.d("AndroidViewModel init")

        requests
            .throttleFirst(5, TimeUnit.SECONDS)
            .observeOn(sf.main())
            .subscribe {
                fetchImages()
            }
            .addTo(disposables)

        picsumRepository
            .getPiclist()
            .subscribe {
                if (it.isEmpty()) {
                    requestImages()
                } else {
                    (picsumList as MutableLiveData).postValue(it)
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

        val requestPage = picsumRepository.getPagesFetched() + 1

        picsumApi.getPicsList(requestPage)
        .subscribeOn(sf.io())
        .observeOn(sf.main())
        .subscribe({
            Timber.d("Success geting picslist! size: ${it.size}")
            setLoading(false)
            picsumRepository.addImages(it, requestPage)
        }, {
            Timber.e("Failed to getPiclist: $it")
            setLoading(false)
        }).apply { disposables.add(this) }
    }

    private fun setLoading(loading: Boolean) {
        (isLoading as MutableLiveData).postValue(loading)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

}