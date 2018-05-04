package cc.aoeiuv020.panovel.sql.entity

import cc.aoeiuv020.panovel.api.ChaptersRequester
import cc.aoeiuv020.panovel.api.DetailRequester
import cc.aoeiuv020.panovel.api.NovelItem
import cc.aoeiuv020.panovel.api.Requester
import java.util.*
import cc.aoeiuv020.panovel.api.NovelDetail as NovelDetailApi

/**
 * Created by AoEiuV020 on 2018.04.29-18:48:10.
 */

fun Requester.toSql(): RequesterData = RequesterData(type, extra)

@Suppress("UNCHECKED_CAST")
fun <T : Requester> RequesterData.toApi(): T =
        Requester.deserialization(type, extra) as T

fun NovelDetailApi.toSql(): NovelDetail =
        NovelDetail(
                name = this.novel.name,
                author = this.novel.author,
                site = this.novel.site,
                imageUrl = this.bigImg,
                introduction = this.introduction,
                detailRequester = this.novel.requester.toSql(),
                chaptersRequester = this.requester.toSql()
        )

fun NovelDetail.toApi(): NovelDetailApi =
        NovelDetailApi(
                NovelItem(this.name, this.author, this.site, this.detailRequester.toApi<DetailRequester>()),
                this.imageUrl,
                Date(0),
                this.introduction,
                this.chaptersRequester.toApi<ChaptersRequester>()
        )
