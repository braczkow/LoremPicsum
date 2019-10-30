package com.braczkow.lorempicsum.ux.main

import androidx.lifecycle.Lifecycle
import com.braczkow.lorempicsum.lib.picsum.PicsumApi
import com.braczkow.lorempicsum.lib.picsum.PicsumRepository
import com.braczkow.lorempicsum.lib.util.DoOnStart
import com.braczkow.lorempicsum.lib.util.DoOnStop
import com.braczkow.lorempicsum.lib.util.SchedulersFactory
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class MainPresenter(
    private val picsumApi: PicsumApi,
    private val picsumRepository: PicsumRepository,
    private val sf: SchedulersFactory,
    private val view: MainActivity.MainView,
    private val lifecycle: Lifecycle
) {
    val items = mutableListOf<PicsumApi.ListEntry>()

    init {
        DoOnStart(lifecycle) {
            val disposables = CompositeDisposable()

            DoOnStop(lifecycle) {
                disposables.dispose()
            }

            disposables.add(picsumRepository
                .getPiclist()
                .subscribe {
                    items.clear()
                    items.addAll(it)

                    if (items.isEmpty()) {
                        loadNewImages()
                    } else {
                        view.refreshItems()
                    }
                })

        }
    }

    private fun loadNewImages() {
        val disposable = picsumApi.getPicsList()
            .subscribeOn(sf.io())
            .observeOn(sf.main())
            .subscribe({
                Timber.d("Success geting picslist! size: ${it.size}")
                picsumRepository.savePiclist(it)
            }, {
                Timber.e("Failed to getPiclist: $it")
            })

        DoOnStop(lifecycle) {
            disposable.dispose()
        }
    }
}