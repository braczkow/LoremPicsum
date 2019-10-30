package com.braczkow.lorempicsum.ux.main

import androidx.lifecycle.Lifecycle
import com.braczkow.lorempicsum.lib.picsum.PicsumApi
import com.braczkow.lorempicsum.lib.picsum.PicsumRepository
import com.braczkow.lorempicsum.lib.util.DoOnStart
import com.braczkow.lorempicsum.lib.util.DoOnStop
import com.braczkow.lorempicsum.lib.util.SchedulersFactory
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import javax.inject.Inject

class MainPresenter @Inject constructor(
    private val picsumApi: PicsumApi,
    private val picsumRepository: PicsumRepository,
    private val sf: SchedulersFactory,
    private val view: MainActivity.MainView,
    private val lifecycle: Lifecycle
) {
    val items = mutableListOf<PicsumApi.ListEntry>()

    init {
        view.onEndOfScroll {
            Timber.d("End of scroll detected!")
            loadNewImages()
        }

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
                        view.showLoading()
                        loadNewImages()
                    } else {
                        view.hideLoading()
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
                view.hideLoading()
                picsumRepository.savePiclist(it)
            }, {
                Timber.e("Failed to getPiclist: $it")
                view.hideLoading()
            })

        DoOnStop(lifecycle) {
            disposable.dispose()
        }
    }
}