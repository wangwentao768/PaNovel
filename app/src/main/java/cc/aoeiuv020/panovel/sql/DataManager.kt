package cc.aoeiuv020.panovel.sql

import android.content.Context
import android.support.annotation.VisibleForTesting
import cc.aoeiuv020.panovel.local.BookListData
import cc.aoeiuv020.panovel.sql.db.AppDatabase
import cc.aoeiuv020.panovel.sql.db.CacheDatabase
import cc.aoeiuv020.panovel.sql.entity.BookList
import cc.aoeiuv020.panovel.sql.entity.BookListItem
import cc.aoeiuv020.panovel.sql.entity.NovelMini

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

    fun getAllBookLists(): List<BookList> = app.runInTransaction<List<BookList>> {
        app.bookListDao().getAllBookLists()
    }

    fun getNovelMiniInBookList(bookList: BookList): List<NovelMini> = app.runInTransaction<List<NovelMini>> {
        val id = bookList.id
                ?: throw IllegalArgumentException("require id was null,")
        app.bookListDao().getNovelMiniInBookList(id)
    }

    fun renameBookList(bookList: BookList, name: String) = app.runInTransaction {
        val id = bookList.id
                ?: throw IllegalArgumentException("require BookList.id was null,")
        app.bookListDao().renameBookList(id, name)
    }

    fun removeBookList(bookList: BookList) = app.runInTransaction {
        val id = bookList.id
                ?: throw IllegalArgumentException("require BookList.id was null,")
        app.bookListDao().removeBookList(id)
    }

    /**
     * id和detailRequester必须有个非空，
     */
    fun putBookshelf(novelMini: NovelMini) = app.runInTransaction {
        novelMini.id?.also { id ->
            app.bookshelfDao().putBookshelfById(id)
            return@runInTransaction
        }
        val type = novelMini.detailRequesterType
        val extra = novelMini.detailRequesterExtra
        if (type != null && extra != null) {
            app.bookshelfDao().putBookshelfByRequester(type, extra)
        } else {
            throw IllegalArgumentException("require id or (type and extra) both null,")
        }
    }

    fun getAllBookshelf(): List<NovelMini> = app.runInTransaction<List<NovelMini>> {
        app.bookshelfDao().getAllBookshelf()
    }

    fun putBookshelf(bookList: BookList) = app.runInTransaction {
        val id = bookList.id
                ?: throw IllegalArgumentException("require id was null,")
        app.bookshelfDao().putBookshelfByBookListId(id)
    }

    fun removeBookshelf(bookList: BookList) = app.runInTransaction {
        val id = bookList.id
                ?: throw IllegalArgumentException("require id was null,")
        app.bookshelfDao().removeBookshelfByBookListId(id)
    }
}