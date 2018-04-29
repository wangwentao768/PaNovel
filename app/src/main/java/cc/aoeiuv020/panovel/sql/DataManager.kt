package cc.aoeiuv020.panovel.sql

import android.content.Context
import android.support.annotation.VisibleForTesting
import cc.aoeiuv020.panovel.api.NovelContext
import cc.aoeiuv020.panovel.sql.entity.NovelDetailData
import cc.aoeiuv020.panovel.sql.entity.NovelMiniData
import cc.aoeiuv020.panovel.sql.entity.detailRequester
import cc.aoeiuv020.panovel.sql.entity.toData

/**
 * 封装多个数据库的联用，
 * 隐藏所有数据库实体，这里进出的都是专用的kotlin数据类，
 *
 * Created by AoEiuV020 on 2018.04.28-16:53:14.
 */
object DataManager {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    lateinit var app: AppDatabaseManager
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    lateinit var cache: CacheDatabaseManager

    @Synchronized
    fun init(context: Context) {
        if (!::app.isInitialized) {
            app = AppDatabaseManager(context)
        }
        if (!::cache.isInitialized) {
            cache = CacheDatabaseManager(context)
        }
    }

    fun loadBookshelf(): List<NovelMiniData> {
        return app.getAllBookshelf().map {
            NovelMiniData(
                    detailRequester = it.detailRequester,
                    bookshelf = it.bookshelf!!,
                    chapterReadAt = it.chapterReadAt!!,
                    textReadAt = it.textReadAt!!
            )
        }
    }

    fun getNovelDetail(novelMiniData: NovelMiniData): NovelDetailData {
        return cache.queryByDetailRequester(novelMiniData)
                ?.toData()
                ?: run {
                    NovelContext.getNovelContextByUrl(novelMiniData.detailRequester.url)
                            .getNovelDetail(novelMiniData.detailRequester)
                            .let {
                                NovelDetailData(
                                        novelMiniData = novelMiniData,
                                        chaptersRequester = it.requester,
                                        name = it.novel.name,
                                        author = it.novel.author,
                                        site = it.novel.site,
                                        introduction = it.introduction
                                )
                            }
                }
    }
}