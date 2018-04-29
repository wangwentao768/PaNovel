package cc.aoeiuv020.panovel.sql.entity

import cc.aoeiuv020.panovel.api.DetailRequester

/**
 *
 * Created by AoEiuV020 on 2018.04.29-18:33:45.
 */
data class NovelMiniData(
        val detailRequester: DetailRequester,
        val bookshelf: Boolean = false,
        val chapterReadAt: Int = 0,
        val textReadAt: Int = 0
)