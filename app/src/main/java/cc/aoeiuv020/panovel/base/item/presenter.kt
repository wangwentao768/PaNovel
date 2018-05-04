package cc.aoeiuv020.panovel.base.item

import cc.aoeiuv020.panovel.Presenter
import cc.aoeiuv020.panovel.sql.DataManager
import cc.aoeiuv020.panovel.sql.entity.NovelDetail
import cc.aoeiuv020.panovel.sql.entity.RequesterData
import cc.aoeiuv020.panovel.util.async
import cc.aoeiuv020.panovel.util.suffixThreadName
import io.reactivex.Observable
import org.jetbrains.anko.debug
import org.jetbrains.anko.error

/**
 *
 * Created by AoEiuV020 on 2017.11.22-10:45:36.
 */
abstract class BaseItemListPresenter<V : BaseItemListView, out T : SmallItemPresenter<*>> : Presenter<V>() {
    var refreshTime = 0L

    abstract fun refresh()

    fun forceRefresh() {
        refreshTime = System.currentTimeMillis()
        refresh()
    }

    abstract fun subPresenter(): T
}

abstract class DefaultItemListPresenter<V : BaseItemListView>
    : BaseItemListPresenter<V, DefaultItemPresenter>() {
    override fun subPresenter(): DefaultItemPresenter = DefaultItemPresenter(this)
}

class DefaultItemPresenter(itemListPresenter: BaseItemListPresenter<*, *>)
    : BigItemPresenter<DefaultItemViewHolder<*>>(itemListPresenter)

abstract class SmallItemPresenter<T : SmallItemView>(protected val itemListPresenter: BaseItemListPresenter<*, *>) : Presenter<T>() {
    open val refreshTime: Long
        get() = itemListPresenter.refreshTime

    fun requestDetail(requesterData: RequesterData) {
        debug { "request detail ${requesterData.extra}" }
        Observable.fromCallable {
            suffixThreadName("requestDetail")
            DataManager.queryNovelDetail(requesterData)
        }.async().subscribe({ novelDetail ->
            view?.showDetail(novelDetail)
        }, { e ->
            val message = "读取小说详情失败：${requesterData.extra}"
            error(message, e)
            itemListPresenter.view?.showError(message, e)
        }).let { addDisposable(it, 0) }
    }

    fun requestStatus(novelDetail: NovelDetail) {
        debug { "request detail ${novelDetail.name}" }
        Observable.fromCallable {
            suffixThreadName("requestDetail")
            DataManager.queryNovelStatus(novelDetail)
        }.async().subscribe({ novelStatus ->
            view?.showStatus(novelStatus)
        }, { e ->
            val message = "读取小说阅读状态失败：${novelDetail.name}"
            error(message, e)
            itemListPresenter.view?.showError(message, e)
        }).let { addDisposable(it, 0) }
    }
}

abstract class BigItemPresenter<T : BigItemView>(itemListPresenter: BaseItemListPresenter<*, *>) : SmallItemPresenter<T>(itemListPresenter) {
}