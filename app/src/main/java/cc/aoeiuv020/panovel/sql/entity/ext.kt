package cc.aoeiuv020.panovel.sql.entity

import cc.aoeiuv020.panovel.api.ChaptersRequester
import cc.aoeiuv020.panovel.api.DetailRequester
import cc.aoeiuv020.panovel.api.Requester

/**
 * Created by AoEiuV020 on 2018.04.29-18:48:10.
 */

val NovelMini.detailRequester: DetailRequester
    get() = Requester.deserialization(detailRequesterType!!, detailRequesterExtra!!) as DetailRequester
val NovelDetail.detailRequester: DetailRequester
    get() = Requester.deserialization(detailRequesterType!!, detailRequesterExtra!!) as DetailRequester
val NovelDetail.chaptersRequester: ChaptersRequester
    get() = Requester.deserialization(detailRequesterType!!, detailRequesterExtra!!) as ChaptersRequester

fun NovelDetail.toData(): NovelDetailData = NovelDetailData(
        novelMiniData = NovelMiniData(
                detailRequester = detailRequester,
                bookshelf = bookshelf!!,
                chapterReadAt = chapterReadAt!!,
                textReadAt = textReadAt!!
        ),
        chaptersRequester = chaptersRequester,
        name = name!!,
        author = author!!,
        site = site!!,
        introduction = introduction!!
)

