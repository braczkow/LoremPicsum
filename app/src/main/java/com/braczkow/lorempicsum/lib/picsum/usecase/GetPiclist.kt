package com.braczkow.lorempicsum.lib.picsum.usecase

import com.braczkow.lorempicsum.lib.picsum.PicsumEntry
import com.braczkow.lorempicsum.lib.picsum.internal.PicsumRepository
import io.reactivex.Observable
import javax.inject.Inject

interface GetPiclist {
    fun execute() : Observable<List<PicsumEntry>>
}

class GetPiclistImpl @Inject constructor(
    private val picsumRepository: PicsumRepository
) : GetPiclist {
    override fun execute(): Observable<List<PicsumEntry>> {
        return picsumRepository.getPiclist()
    }
}