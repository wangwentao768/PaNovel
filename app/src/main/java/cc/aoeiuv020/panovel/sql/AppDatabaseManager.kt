package cc.aoeiuv020.panovel.sql

import android.content.Context
import android.support.annotation.VisibleForTesting
import cc.aoeiuv020.panovel.local.BookListData
import cc.aoeiuv020.panovel.sql.db.AppDatabase
import cc.aoeiuv020.panovel.sql.entity.BookList
import cc.aoeiuv020.panovel.sql.entity.BookListItem
import cc.aoeiuv020.panovel.sql.entity.NovelMini

/**
 * 封装一个数据库多个表多个DAO的联用，
 *
 * Created by AoEiuV020 on 2018.04.27-11:52:55.
 */
class AppDatabaseManager(context: Context) {
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val db: AppDatabase = AppDatabase.build(context)


    fun importBookList(bookListData: BookListData): BookList = db.runInTransaction<BookList> {
        val bookList = BookList().apply {
            this.name = bookListData.name
        }
        val id = db.bookListDao().insertBookList(bookList)
        bookList.id = id
        bookListData.list.forEach { novelItem ->
            val type = novelItem.requester.type
            val extra = novelItem.requester.extra
            val novelMiniId = db.novelMiniDao().queryOrNew(type, extra)
                    .id!!
            val bookListItem = BookListItem.new(id, novelMiniId)
            db.bookListDao().insertBookListItem(bookListItem)
        }
        bookList
    }

    fun getAllBookLists(): List<BookList> = db.runInTransaction<List<BookList>> {
        db.bookListDao().getAllBookLists()
    }

    fun getNovelMiniInBookList(bookList: BookList): List<NovelMini> = db.runInTransaction<List<NovelMini>> {
        val id = requireNotNull(bookList.id) {
            "require bookList.id was null,"
        }
        db.bookListDao().getNovelMiniInBookList(id)
    }

    fun renameBookList(bookList: BookList, name: String) = db.runInTransaction {
        val id = requireNotNull(bookList.id) {
            "require bookList.id was null,"
        }
        db.bookListDao().renameBookList(id, name)
    }

    fun removeBookList(bookList: BookList) = db.runInTransaction {
        val id = requireNotNull(bookList.id) {
            "require bookList.id was null,"
        }
        db.bookListDao().removeBookList(id)
    }

    /**
     * id和detailRequester必须有个非空，
     */
    fun putBookshelf(novelMini: NovelMini) = db.runInTransaction {
        novelMini.id?.also { id ->
            db.bookshelfDao().putBookshelfById(id)
            return@runInTransaction
        }
        val type = novelMini.detailRequesterType
        val extra = novelMini.detailRequesterExtra
        if (type != null && extra != null) {
            db.bookshelfDao().putBookshelfByRequester(type, extra)
        } else {
            throw IllegalArgumentException("require id or (type and extra) both null,")
        }
    }

    fun getAllBookshelf(): List<NovelMini> = db.runInTransaction<List<NovelMini>> {
        db.bookshelfDao().getAllBookshelf()
    }

    fun putBookshelf(bookList: BookList) = db.runInTransaction {
        val id = requireNotNull(bookList.id) {
            "require bookList.id was null,"
        }
        db.bookshelfDao().putBookshelfByBookListId(id)
    }

    fun removeBookshelf(bookList: BookList) = db.runInTransaction {
        val id = requireNotNull(bookList.id) {
            "require bookList.id was null,"
        }
        db.bookshelfDao().removeBookshelfByBookListId(id)
    }
}