package cc.aoeiuv020.panovel.sql.entity

import cc.aoeiuv020.panovel.api.ChaptersRequester
import java.util.*

/**
 * Created by AoEiuV020 on 2018.04.29-18:36:00.
 */
data class NovelDetailData(
        val novelMiniData: NovelMiniData,
        val chaptersRequester: ChaptersRequester,
        val name: String,
        val author: String,
        val site: String,
        val introduction: String,
        val updateTime: Date? = null
)