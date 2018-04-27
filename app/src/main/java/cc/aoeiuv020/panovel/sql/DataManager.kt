package cc.aoeiuv020.panovel.sql

import android.content.Context
import android.support.annotation.VisibleForTesting
import cc.aoeiuv020.panovel.local.BookListData
import cc.aoeiuv020.panovel.sql.db.AppDatabase
import cc.aoeiuv020.panovel.sql.db.CacheDatabase
import cc.aoeiuv020.panovel.sql.entity.BookList
import cc.aoeiuv020.panovel.sql.entity.BookListItem

/**
 *
 * Created by AoEiuV020 on 2018.04.27-11:52:55.
 */
object DataManager {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    lateinit var app: AppDatabase
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    lateinit var cache: CacheDatabase

    @Synchronized
    fun init(context: Context) {
        if (!::app.isInitialized) {
            app = AppDatabase.build(context)
        }
        if (!::cache.isInitialized) {
            cache = CacheDatabase.build(context)
        }
    }

    fun importBookList(bookListData: BookListData): BookList = app.runInTransaction<BookList> {
        val bookList = BookList().apply {
            this.name = bookListData.name
        }
        val id = app.bookListDao().insertBookList(bookList)
        bookList.id = id
        bookListData.list.forEach { novelItem ->
            val type = novelItem.requester.type
            val extra = novelItem.requester.extra
            val novelMiniId = app.novelMiniDao().queryOrNew(type, extra)
                    .id!!
            val bookListItem = BookListItem.new(id, novelMiniId)
            app.bookListDao().insertBookListItem(bookListItem)
        }
        bookList
    }

    fun renameBookList(bookList: BookList, name: String) {
        val id = bookList.id
                ?: throw IllegalArgumentException("require BookList.id was null,")
        return app.bookListDao().renameBookList(id, name)
    }

    fun removeBookList(bookList: BookList) {
        val id = bookList.id
                ?: throw IllegalArgumentException("require BookList.id was null,")
        return app.bookListDao().removeBookList(id)
    }
}