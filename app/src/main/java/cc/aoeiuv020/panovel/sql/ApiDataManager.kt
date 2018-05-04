package cc.aoeiuv020.panovel.sql

import android.content.Context
import cc.aoeiuv020.panovel.api.*
import cc.aoeiuv020.panovel.local.bookId
import cc.aoeiuv020.panovel.sql.entity.RequesterData
import cc.aoeiuv020.panovel.sql.entity.toSql
import io.reactivex.Observable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import org.jetbrains.anko.error
import org.jetbrains.anko.verbose

/**
 * Created by AoEiuV020 on 2018.05.04-19:00:27.
 */
class ApiDataManager(@Suppress("UNUSED_PARAMETER") context: Context) : AnkoLogger {
    fun getNovelDetail(requester: DetailRequester): NovelDetail {
        return NovelContext.getNovelContextByUrl(requester.url)
                .getNovelDetail(requester)
    }

    fun getChapters(requester: ChaptersRequester): List<NovelChapter> {
        return NovelContext.getNovelContextByUrl(requester.url)
                .getNovelChaptersAsc(requester)
    }

    fun fuzzySearch(name: String): Observable<RequesterData> {
        return Observable.create<RequesterData> { em ->
            fun next(novelItem: NovelItem) {
                debug { "search result <${novelItem.bookId}>" }
                em.onNext(novelItem.requester.toSql())
            }
            NovelContext.getNovelContexts().forEach { context ->
                debug { "search ${context.getNovelSite().name}" }
                try {
                    // 模糊搜索，fuzzy search,
                    context.getNovelList(context.searchNovelName(name).requester).filter {
                        verbose { it.novel }
                        name in it.novel.name
                    }.forEach { next(it.novel) }
                } catch (e: Exception) {
                    // 一个网站搜索失败不抛异常，
                    error("${context.getNovelSite().name}.$name", e)
                }
            }
        }
    }
}