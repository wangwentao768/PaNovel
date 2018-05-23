package cc.aoeiuv020.panovel.data

import android.content.Context
import cc.aoeiuv020.panovel.api.NovelChapter
import cc.aoeiuv020.panovel.api.NovelContext
import cc.aoeiuv020.panovel.api.site.*
import cc.aoeiuv020.panovel.data.entity.Novel
import java.net.URL
import java.util.*

/**
 * Created by AoEiuV020 on 2018.05.23-15:55:11.
 */
class ApiManager(ctx: Context) {
    @Suppress("RemoveExplicitTypeArguments")
    private val contexts: List<NovelContext> by lazy {
        listOf(
                Piaotian(), Biquge(), Liudatxt(), Qidian(),
                Dmzz(), Sfacg(), Snwx(), Syxs(),
                Yssm(), Qlyx()
        )
    }
    // 缓存host对应网站上下文的映射，正常来说主页的host
    private val hostMap: MutableMap<String, NovelContext> by lazy {
        contexts.associateByTo(mutableMapOf()) { URL(it.getNovelSite().baseUrl).host }
    }
    private val nameMap by lazy {
        contexts.associateBy { it.getNovelSite().name }
    }

    @Suppress("unused")
    fun getNovelContextByUrl(url: String): NovelContext {
        return hostMap[URL(url).host]
                ?: contexts.firstOrNull { it.check(url) }
                ?: throw IllegalArgumentException("网址不支持: $url")
    }

    private fun getNovelContextByName(name: String): NovelContext {
        return nameMap[name] ?: throw IllegalArgumentException("网站不支持: $name")
    }

    fun context(novel: Novel): NovelContext {
        return getNovelContextByName(novel.site)
    }

    fun updateNovelDetail(novel: Novel) {
        val novelDetail = context(novel).getNovelDetail(novel.detail)
        novel.image = novelDetail.image
        novel.introduction = novelDetail.introduction
        if (novelDetail.update != null) {
            novel.updateTime = requireNotNull(novelDetail.update)
        }
    }

    fun requestNovelChapters(novel: Novel): List<NovelChapter> {
        val list = context(novel).getNovelChaptersAsc(novel.nChapters)
        novel.apply {
            // 是否真的有更新，
            val hasNew = list.size > chaptersCount
            chaptersCount = list.size
            // 多余的警告，反馈了，
            // https://youtrack.jetbrains.com/issue/KT-24557
            @Suppress("RemoveRedundantCallsOfConversionMethods")
            lastChapterName = list.lastOrNull()?.name.toString()
            if (readAtChapterIndex == 0) {
                // 阅读至第一章代表没阅读过，保存第一章的章节名，
                @Suppress("RemoveRedundantCallsOfConversionMethods")
                readAtChapterName = list.firstOrNull()?.name.toString()
            }
            // 如果有更新时间，就存起来，
            list.lastOrNull()?.update?.let {
                updateTime = it
            }
            checkUpdateTime = Date()
            if (hasNew) {
                receiveUpdateTime = checkUpdateTime
            }
        }
        return list
    }

    fun getDetailUrl(novel: Novel): String {
        return context(novel).getNovelDetailUrl(novel.detail)
    }
}