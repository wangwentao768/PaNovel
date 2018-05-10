package cc.aoeiuv020.panovel.sql

import android.content.Context
import android.support.annotation.VisibleForTesting
import cc.aoeiuv020.panovel.api.DetailRequester
import cc.aoeiuv020.panovel.sql.entity.*
import io.reactivex.Observable
import java.util.*
import cc.aoeiuv020.panovel.api.NovelDetail as NovelDetailApi

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
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    lateinit var api: ApiDataManager

    @Synchronized
    fun init(context: Context) {
        if (!::app.isInitialized) {
            app = AppDatabaseManager(context)
        }
        if (!::cache.isInitialized) {
            cache = CacheDatabaseManager(context)
        }
        if (!::api.isInitialized) {
            api = ApiDataManager(context)
        }
    }

    fun queryNovelDetail(requesterData: RequesterData): NovelDetail {
        return cache.getNovelDetail(requesterData)
                ?: api.getNovelDetail(requesterData.toApi()).let { novelDetailApi ->
                    novelDetailApi.toSql().also { novelDetail ->
                        // 如果小说详情页的更新时间大于保存了的更新时间，
                        // 就更新NovelStatus里的小说更新时间，
                        cache.putNovelDetail(novelDetail)
                        val updateTime = novelDetailApi.update
                        if (updateTime > Date(0))
                            cache.queryOrNewStatus(requesterData).let { novelStatus ->
                                if (novelStatus.updateTime == null || updateTime > novelStatus.updateTime) {
                                    novelStatus.copy(
                                            updateTime = updateTime
                                    ).let {
                                        cache.updateNovelStatus(it)
                                    }
                                }
                            }
                    }
                }
    }

    fun queryNovelStatus(novelDetail: NovelDetail): NovelStatus {
        // 存在就直接返回，
        var novelStatus = cache.getNovelStatus(novelDetail.detailRequester)?.also {
            return it
        } ?: NovelStatus(
                detailRequester = novelDetail.detailRequester
        )
        // 不存在就联网获取章节列表，
        // 然后
        api.getChapters(novelDetail.chaptersRequester.toApi()).takeIf { it.isNotEmpty() }?.let {
            // TODO: 从AppDatabase里拿阅读进度，
            val currentTime = Date()
            novelStatus = novelStatus.copy(
                    chapterNameLast = it.last().name,
                    chapterNameReadAt = it.first().name,
                    updateTime = it.last().update,
                    checkUpdateTime = currentTime,
                    receiveUpdateTime = if (it.size > novelStatus.chaptersCount) currentTime else novelStatus.receiveUpdateTime
            ).also {
                cache.insertNovelStatus(it)
            }
        }
        return novelStatus
    }

    fun listBookshelf(): List<RequesterData> {
        return app.listBookshelf()
    }

    fun addBookshelf(requester: DetailRequester) {
        app.addBookshelf(requester.toSql())
    }

    fun removeBookshelf(requester: DetailRequester) {
        app.removeBookshelf(requester.toSql())
    }

    fun listHistory(): List<RequesterData> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun listBookList(bookListName: String): List<RequesterData> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun fuzzySearch(name: String): Observable<RequesterData> {
        return api.fuzzySearch(name)
    }

    fun containsBookshelf(requester: DetailRequester): Boolean {
        return app.containsBookshelf(requester.toSql())
    }
}
