package com.braczkow.lorempicsum.lib.picsum.usecase

import com.braczkow.lorempicsum.lib.picsum.internal.PicsumApi
import com.braczkow.lorempicsum.lib.picsum.internal.PicsumRepository
import com.braczkow.lorempicsum.lib.util.SchedulersFactory
import io.reactivex.Completable
import timber.log.Timber
import javax.inject.Inject

interface FetchImages {
    fun execute(): Completable
}

class FetchImagesImpl @Inject constructor(
    private val picsumApi: PicsumApi,
    private val picsumRepository: PicsumRepository,
    private val sf: SchedulersFactory
) : FetchImages {
    override fun execute() = Completable.create { emitter ->
        val requestPage = picsumRepository.getPagesFetched() + 1

        picsumApi.getPicsList(requestPage)
            .subscribeOn(sf.io())
            .observeOn(sf.main())
            .subscribe({
                Timber.d("Success geting picslist! size: ${it.size}")
                picsumRepository.addImages(it, requestPage)
                emitter.takeIf { !it.isDisposed } ?.apply { onComplete() }
            }, {
                Timber.e("Failed to getPiclist: $it")
                emitter.takeIf { !it.isDisposed } ?.apply { onError(it) }
            })
    }
}



