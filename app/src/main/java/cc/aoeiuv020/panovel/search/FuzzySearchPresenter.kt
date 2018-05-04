package cc.aoeiuv020.panovel.search

import cc.aoeiuv020.panovel.base.item.DefaultItemListPresenter
import cc.aoeiuv020.panovel.sql.DataManager
import cc.aoeiuv020.panovel.util.async
import org.jetbrains.anko.debug
import org.jetbrains.anko.error

/**
 *
 * Created by AoEiuV020 on 2017.10.22-18:18:58.
 */
class FuzzySearchPresenter : DefaultItemListPresenter<FuzzySearchActivity>() {
    var name: String? = null
    private var author: String? = null

    fun search(name: String, author: String? = null) {
        this.name = name
        this.author = author
        searchActual(name, author)
    }

    private fun searchActual(name: String, author: String?) {
        debug { "search <$name, $author>" }
        DataManager.fuzzySearch(name).async().subscribe({ item ->
            view?.addNovel(item)
        }, { e ->
            val message = "搜索小说失败，"
            error(message, e)
            view?.showError(message, e)
            view?.showOnComplete()
        }, {
            view?.showOnComplete()
        }).let { addDisposable(it) }
    }

    override fun refresh() {
        name?.let { nameNonnull ->
            searchActual(nameNonnull, author)
        } ?: view?.showOnComplete()
    }
}