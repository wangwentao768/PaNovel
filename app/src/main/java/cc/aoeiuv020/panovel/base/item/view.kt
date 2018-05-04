package cc.aoeiuv020.panovel.base.item

import cc.aoeiuv020.panovel.IView
import cc.aoeiuv020.panovel.sql.entity.NovelDetail
import cc.aoeiuv020.panovel.sql.entity.NovelStatus

/**
 *
 * Created by AoEiuV020 on 2017.11.22-10:48:04.
 */
interface BaseItemListView : IView {
    fun showError(message: String, e: Throwable)
}

interface SmallItemView : IView {
    fun showDetail(novelDetail: NovelDetail)
    fun showStatus(novelStatus: NovelStatus)
}

interface BigItemView : SmallItemView {
}